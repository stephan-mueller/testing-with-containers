/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.openknowledge.projects.todolist.gateway.infrastructure.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Set;

import javax.validation.MessageInterpolator;
import javax.validation.Payload;
import javax.validation.Validation;

/**
 * Custom message interpolator for bean validation.
 *
 * If no custom message or custom message template is specified at the constraint, the constraint is checked for a {@link
 * ValidationErrorPayload} being present. If so, the errorcode specified in the payload custom is used as a message template (key) to look
 * up the corresponding message from the ValidationMessages.properties.
 *
 * If neither any custom message, message template or ValidationErrorPayload is specified at the constraint, the default message will be
 * returned.
 *
 * @see MessageInterpolator (https://beanvalidation.org/2.0/spec/#validationapi-message-customresolution)
 */
public class CustomValidationErrorPayloadMessageInterpolator implements MessageInterpolator {

  private static final Logger LOG = LoggerFactory.getLogger(CustomValidationErrorPayloadMessageInterpolator.class);

  private final MessageInterpolator delegate;

  /**
   * Default constructor required for XML configuration.
   */
  public CustomValidationErrorPayloadMessageInterpolator() {
    this(Validation.byDefaultProvider().configure().getDefaultMessageInterpolator());
  }

  public CustomValidationErrorPayloadMessageInterpolator(final MessageInterpolator delegate) {
    super();
    this.delegate = delegate;
  }

  @Override
  public String interpolate(String message, Context context) {
    return this.delegate.interpolate(getMessage(message, context), context);
  }

  @Override
  public String interpolate(String message, Context context, Locale locale) {
    return this.delegate.interpolate(getMessage(message, context), context, locale);
  }

  private String getMessage(final String message, final Context context) {
    if (isConstraintWithCustomMessage(message)) {
      return message;
    }

    if (isConstraintWithCustomMessageTemplate(message)) {
      return message;
    }

    if (isConstraintWithValidationErrorPayload(context)) {
      return getValidationErrorPayloadMessage(context);
    }

    return message;
  }

  private boolean isConstraintWithCustomMessage(final String message) {
    return !message.startsWith("{") && !message.endsWith("}");
  }

  private boolean isConstraintWithCustomMessageTemplate(final String message) {
    return message.startsWith("{")
           && message.endsWith("}")
           && !message.contains("javax.validation.constraints.")
           && !message.contains("org.hibernate.validator.constraints.");
  }

  private boolean isConstraintWithValidationErrorPayload(final Context context) {
    Set<Class<? extends Payload>> payloads = context.getConstraintDescriptor().getPayload();
    return payloads.stream().anyMatch(ValidationErrorPayload.PREDICATE);
  }

  private String getValidationErrorPayloadMessage(final Context context) {
    String message = "UNKNOWN";
    try {
      Set<Class<? extends Payload>> payloads = context.getConstraintDescriptor().getPayload();
      Class<? extends Payload> clazz = payloads.stream().filter(ValidationErrorPayload.PREDICATE).findFirst().get();
      ValidationErrorPayload payload = (ValidationErrorPayload) clazz.newInstance();
      message = "{" + payload.getErrorCode() + "}";
    } catch (InstantiationException | IllegalAccessException e) {
      LOG.error(e.getMessage(), e);
    }
    return message;
  }
}
