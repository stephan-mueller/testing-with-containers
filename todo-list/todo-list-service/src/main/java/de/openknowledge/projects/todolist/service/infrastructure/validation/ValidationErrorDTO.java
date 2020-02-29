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
package de.openknowledge.projects.todolist.service.infrastructure.validation;

import static org.apache.commons.lang3.Validate.notNull;

import de.openknowledge.projects.todolist.service.infrastructure.domain.error.ErrorCode;
import de.openknowledge.projects.todolist.service.infrastructure.domain.error.ErrorDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import javax.validation.ConstraintViolation;
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
      Set<Class<? extends Payload>> payloads = constraintViolation.getConstraintDescriptor().getPayload();
      if (isConstraintWithValidationErrorPayload(payloads)) {
        Class<? extends Payload> clazz = payloads.stream().filter(ValidationErrorPayload.PREDICATE).findFirst().get();
        errorPayload = (ValidationErrorPayload) clazz.newInstance();
      }
    } catch (InstantiationException | IllegalAccessException e) {
      LOG.error(e.getMessage(), e);
    }

    return errorPayload;
  }

  private static boolean isConstraintWithValidationErrorPayload(Set<Class<? extends Payload>> payloads) {
    return payloads.stream().anyMatch(ValidationErrorPayload.PREDICATE);
  }

  private static String extractErrorMessage(final ConstraintViolation constraintViolation) {
    return constraintViolation.getMessage();
  }

  @Override
  public String toString() {
    return "ValidationErrorDTO{{"
           + "code='" + getCode() + '\''
           + ", message='" + getMessage() + '\''
           + '}' + super.toString();
  }
}
