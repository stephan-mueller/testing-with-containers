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

import static org.apache.commons.lang3.Validate.notNull;

import de.openknowledge.projects.todolist.gateway.infrastructure.domain.error.ErrorCode;
import de.openknowledge.projects.todolist.gateway.infrastructure.domain.error.ErrorDTO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.Payload;

/**
 * A DTO that represents a validation error.
 */
public class ValidationErrorDTO extends ErrorDTO {

  private static final Logger LOG = LoggerFactory.getLogger(ValidationErrorDTO.class);

  public ValidationErrorDTO(final ConstraintViolation constraintViolation) {
    super(extractErrorCode(notNull(constraintViolation, "constraintViolation must not be null")),
          extractErrorMessage(notNull(constraintViolation, "constraintViolation must not be null")));
  }

  private static ErrorCode extractErrorCode(final ConstraintViolation constraintViolation) {
    ErrorCode errorPayload = () -> "UNKNOWN";

    try {
      Set<Class<? extends Payload>> payload = constraintViolation.getConstraintDescriptor().getPayload();
      if (!payload.isEmpty()) {
        Class<? extends Payload> clazz = payload.iterator().next();
        errorPayload = (ValidationErrorPayload) clazz.newInstance();
      }
    } catch (InstantiationException | IllegalAccessException e) {
      LOG.error(e.getMessage(), e);
    }

    return errorPayload;
  }

  private static String extractErrorMessage(final ConstraintViolation constraintViolation) {
    String propertyPath = getPropertyPath(constraintViolation);

    StringBuilder sb = new StringBuilder();
    if (StringUtils.isNotBlank(propertyPath) && constraintViolation.getMessageTemplate().startsWith("{javax.validation.constraints")) {
      sb.append(propertyPath).append(' ');
    }

    sb.append(constraintViolation.getMessage());
    return sb.toString();
  }

  private static String getPropertyPath(final ConstraintViolation constraintViolation) {
    StringBuilder sb = new StringBuilder();
    for (Path.Node node : constraintViolation.getPropertyPath()) {
      if (ElementKind.PROPERTY.equals(node.getKind())) {
        if (sb.length() > 0) {
          sb.append('.');
        }
        sb.append(node.getName());
      }
    }

    return sb.toString();
  }

  @Override
  public String toString() {
    return "ValidationErrorDTO{{"
           + "code='" + getCode() + '\''
           + ", message='" + getMessage() + '\''
           + '}' + super.toString();
  }
}
