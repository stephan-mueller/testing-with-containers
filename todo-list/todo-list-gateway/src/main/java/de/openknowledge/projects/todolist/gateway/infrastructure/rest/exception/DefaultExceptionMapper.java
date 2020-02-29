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
package de.openknowledge.projects.todolist.gateway.infrastructure.rest.exception;

import de.openknowledge.projects.todolist.gateway.infrastructure.domain.error.ApplicationErrorDTO;
import de.openknowledge.projects.todolist.gateway.infrastructure.domain.error.ApplicationErrorsDTO;
import de.openknowledge.projects.todolist.gateway.infrastructure.domain.error.ErrorDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Default exception mapper. Handles all uncaught exceptions. Prevents leaking internal details to the client.
 */
@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultExceptionMapper.class);

  @Override
  public Response toResponse(final Throwable throwable) {
    LOG.debug(throwable.getMessage(), throwable);

    if (throwable instanceof BadRequestException) {
      LOG.warn(throwable.getMessage(), throwable);
      List<ErrorDTO> errors = Collections.singletonList(new ErrorDTO(() -> "UNKNOWN", throwable.getMessage()));
      return Response.status(((BadRequestException) throwable).getResponse().getStatus())
          .type(MediaType.APPLICATION_JSON_TYPE)
          .entity(new ApplicationErrorsDTO(errors))
          .build();
    }

    if (throwable instanceof NotAuthorizedException) {
      LOG.error("Request is not authenticated", throwable);
      return Response.status(Status.UNAUTHORIZED).build();
    }

    if (throwable instanceof ForbiddenException) {
      LOG.error("Request is not authorized", throwable);
      return Response.status(Status.FORBIDDEN).build();
    }

    if (throwable instanceof NotFoundException) {
      LOG.error("Entity was not found", throwable);
      return Response.status(Status.NOT_FOUND).build();
    }

    ApplicationErrorDTO error = new ApplicationErrorDTO(() -> "UNKNOWN", "An unknown error occurred");

    LOG.error(String.format("An unknown error occurred (%s)", error.getUuid()), throwable);

    return Response.status(Status.INTERNAL_SERVER_ERROR)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity(error)
        .build();
  }
}
