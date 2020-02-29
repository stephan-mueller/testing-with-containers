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

import java.util.function.Predicate;

import javax.validation.Payload;

/**
 * Provides means for validation error payload.
 */
public abstract class ValidationErrorPayload implements ErrorCode, Payload {

  public static final Predicate<Class<? extends Payload>> PREDICATE =
      clazz -> clazz.getSuperclass().isAssignableFrom(ValidationErrorPayload.class);

  private String errorCode;

  protected ValidationErrorPayload(final String errorCode) {
    this.errorCode = notNull(errorCode, "errorCode must not be null");
  }

  public String getErrorCode() {
    return errorCode;
  }
}
