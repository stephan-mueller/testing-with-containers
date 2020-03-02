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
package de.openknowledge.projects.todolist.service.application;

import static de.openknowledge.projects.todolist.service.ComposeContainer.COMPOSE_SERVICENAME_DATABASE;
import static de.openknowledge.projects.todolist.service.ComposeContainer.COMPOSE_SERVICENAME_SERVICE;
import static de.openknowledge.projects.todolist.service.ComposeContainer.SERVICE_PORT;

import de.openknowledge.projects.todolist.service.ComposeContainer;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

/**
 * Integration test for the metrics of the resource {@link TodoResource}.
 */
@Disabled
@Testcontainers
public class TodoResourceMetricsIT {

  private static final Logger LOG = LoggerFactory.getLogger(TodoResourceMetricsIT.class);

  @Container
  private static final DockerComposeContainer ENVIRONMENT = ComposeContainer.newContainer()
      .withLogConsumer(COMPOSE_SERVICENAME_DATABASE, new Slf4jLogConsumer(LOG))
      .withLogConsumer(COMPOSE_SERVICENAME_SERVICE, new Slf4jLogConsumer(LOG));

  @Test
  public void getApplicationMetrics() {
    String serviceHost = ENVIRONMENT.getServiceHost("service", SERVICE_PORT);
    Integer servicePort = ENVIRONMENT.getServicePort("service", SERVICE_PORT);

    RestAssured.given()
        .accept(MediaType.TEXT_PLAIN)
        .when()
        .get(UriBuilder.fromPath("todo-list-service")
            .scheme("http")
            .host(serviceHost)
            .port(servicePort)
            .path("api")
            .path("todos")
            .build())
        .then()
        .statusCode(Response.Status.OK.getStatusCode());

    ValidatableResponse response = RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get(UriBuilder.fromPath("metrics")
            .scheme("http")
            .host(serviceHost)
            .port(servicePort)
            .path("application")
            .build())
        .then()
        .statusCode(Response.Status.OK.getStatusCode());

    response
        .rootPath("'todos.TodoResource'")
        .body("size()", Matchers.is(15));

    response
        .rootPath("'todos.createTodo'")
        .body("size()", Matchers.is(15));

    response
        .rootPath("'todos.deleteTodo'")
        .body("size()", Matchers.is(15));

    response
        .rootPath("'todos.getTodo'")
        .body("size()", Matchers.is(15));

    response
        .rootPath("'todos.getTodos'")
        .body("size()", Matchers.is(15));

    response
        .rootPath("'todos.updateTodo'")
        .body("size()", Matchers.is(15));
  }
}
