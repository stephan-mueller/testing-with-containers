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

import static org.assertj.core.api.Assertions.assertThat;

import de.openknowledge.projects.todolist.gateway.infrastructure.domain.error.ApplicationErrorDTO;
import de.openknowledge.projects.todolist.gateway.infrastructure.domain.error.ApplicationErrorsDTO;
import de.openknowledge.projects.todolist.gateway.infrastructure.domain.error.ErrorDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Test class for the exception mapper {@link DefaultExceptionMapper}.
 */
public class DefaultExceptionMapperTest {

  private DefaultExceptionMapper exceptionMapper;

  @BeforeEach
  public void setUp() {
    exceptionMapper = new DefaultExceptionMapper();
  }

  @Test
  public void toResponseShouldReturn400ForBadRequestException() {
    Response response = exceptionMapper.toResponse(new BadRequestException("Illegal Argument"));
    assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    assertThat(response.hasEntity()).isTrue();

    ApplicationErrorsDTO applicationErrors = (ApplicationErrorsDTO) response.getEntity();
    assertThat(applicationErrors.getUuid()).isNotNull();
    assertThat(applicationErrors.getTimestamp()).isNotNull();

    List<ErrorDTO> validationErrors = applicationErrors.getErrors();
    assertThat(validationErrors).hasSize(1);

    ErrorDTO error = validationErrors.get(0);
    assertThat(error.getCode()).isEqualTo("UNKNOWN");
    assertThat(error.getMessage()).isEqualTo("Illegal Argument");
  }

  @Test
  public void toResponseShouldReturn401ForNotAuthorizedException() {
    Response response = exceptionMapper.toResponse(new NotAuthorizedException("Unauthorized access"));
    assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    assertThat(response.hasEntity()).isFalse();
  }

  @Test
  public void toResponseShouldReturn403ForForbiddenException() {
    Response response = exceptionMapper.toResponse(new ForbiddenException());
    assertThat(response.getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
    assertThat(response.hasEntity()).isFalse();
  }

  @Test
  public void toResponseShouldReturn404ForNotFoundException() {
    Response response = exceptionMapper.toResponse(new NotFoundException());
    assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    assertThat(response.hasEntity()).isFalse();
  }

  @Test
  public void toResponseShouldReturn500ForIllegalStateException() {
    Response response = exceptionMapper.toResponse(new IllegalStateException("Illegal state occurred"));
    assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    assertThat(response.hasEntity()).isTrue();

    ApplicationErrorDTO error = (ApplicationErrorDTO) response.getEntity();
    assertThat(error.getCode()).isEqualTo("UNKNOWN");
    assertThat(error.getMessage()).isEqualTo("An unknown error occurred");
    assertThat(error.getUuid()).isNotNull();
    assertThat(error.getTimestamp()).isNotNull();
  }

  @Test
  public void toResponseShouldReturn500ForWebApplicationExceptionWithStatusCode500() {
    Response response = exceptionMapper.toResponse(new InternalServerErrorException("Internal error"));
    assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    assertThat(response.hasEntity()).isTrue();

    ApplicationErrorDTO error = (ApplicationErrorDTO) response.getEntity();
    assertThat(error.getCode()).isEqualTo("UNKNOWN");
    assertThat(error.getMessage()).isEqualTo("An unknown error occurred");
    assertThat(error.getUuid()).isNotNull();
    assertThat(error.getTimestamp()).isNotNull();
  }

  @Test
  public void toResponseShouldReturn500ForWebApplicationExceptionWithStatusCode501() {
    Response response = exceptionMapper.toResponse(new WebApplicationException(Response.Status.NOT_IMPLEMENTED.getStatusCode()));
    assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    assertThat(response.hasEntity()).isTrue();

    ApplicationErrorDTO error = (ApplicationErrorDTO) response.getEntity();
    assertThat(error.getCode()).isEqualTo("UNKNOWN");
    assertThat(error.getMessage()).isEqualTo("An unknown error occurred");
    assertThat(error.getUuid()).isNotNull();
    assertThat(error.getTimestamp()).isNotNull();
  }

  @Test
  public void toResponseShouldReturn500ForWebApplicationExceptionWithStatusCode502() {
    Response response = exceptionMapper.toResponse(new WebApplicationException(Response.Status.BAD_GATEWAY.getStatusCode()));
    assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    assertThat(response.hasEntity()).isTrue();

    ApplicationErrorDTO error = (ApplicationErrorDTO) response.getEntity();
    assertThat(error.getCode()).isEqualTo("UNKNOWN");
    assertThat(error.getMessage()).isEqualTo("An unknown error occurred");
    assertThat(error.getUuid()).isNotNull();
    assertThat(error.getTimestamp()).isNotNull();
  }

  @Test
  public void toResponseShouldReturn500ForWebApplicationExceptionWithStatusCode503() {
    Response response = exceptionMapper.toResponse(new ServiceUnavailableException());
    assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    assertThat(response.hasEntity()).isTrue();

    ApplicationErrorDTO error = (ApplicationErrorDTO) response.getEntity();
    assertThat(error.getCode()).isEqualTo("UNKNOWN");
    assertThat(error.getMessage()).isEqualTo("An unknown error occurred");
    assertThat(error.getUuid()).isNotNull();
    assertThat(error.getTimestamp()).isNotNull();
  }

  @Test
  public void toResponseShouldReturn500ForWebApplicationExceptionWithStatusCode504() {
    Response response = exceptionMapper.toResponse(new WebApplicationException(Response.Status.GATEWAY_TIMEOUT.getStatusCode()));
    assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    assertThat(response.hasEntity()).isTrue();

    ApplicationErrorDTO error = (ApplicationErrorDTO) response.getEntity();
    assertThat(error.getCode()).isEqualTo("UNKNOWN");
    assertThat(error.getMessage()).isEqualTo("An unknown error occurred");
    assertThat(error.getUuid()).isNotNull();
    assertThat(error.getTimestamp()).isNotNull();
  }
}
