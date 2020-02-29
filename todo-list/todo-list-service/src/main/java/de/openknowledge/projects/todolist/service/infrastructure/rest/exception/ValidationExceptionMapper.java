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
package de.openknowledge.projects.todolist.service.infrastructure.rest.exception;

import de.openknowledge.projects.todolist.service.infrastructure.domain.error.ApplicationErrorsDTO;
import de.openknowledge.projects.todolist.service.infrastructure.validation.ValidationErrorDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception Mapper for the {@link ConstraintViolationException} thrown by Bean Validation.
 *
 * @see ConstraintViolationException
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

  private static final Logger LOG = LoggerFactory.getLogger(ValidationExceptionMapper.class);

  @Override
  public Response toResponse(final ConstraintViolationException e) {
    LOG.debug(e.getMessage());

    List<ValidationErrorDTO> errors = e.getConstraintViolations()
        .stream()
        .map(ValidationErrorDTO::new)
        .collect(Collectors.toList());

    LOG.warn("Validation failed. {} constraint violation(s) occurred.", errors.size());

    return Response.status(Response.Status.BAD_REQUEST)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(new ApplicationErrorsDTO(errors))
        .build();
  }
}
