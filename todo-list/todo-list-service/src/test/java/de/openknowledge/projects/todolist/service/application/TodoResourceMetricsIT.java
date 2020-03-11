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

import de.openknowledge.projects.todolist.service.AbstractIntegrationTest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

/**
 * Integration test for the metrics of the resource {@link TodoResource}.
 */
@Disabled
public class TodoResourceMetricsIT extends AbstractIntegrationTest {

  private static final Logger LOG = LoggerFactory.getLogger(TodoResourceMetricsIT.class);

  @Test
  public void getApplicationMetrics() {
    String serviceHost = SERVICE.getContainerIpAddress();
    Integer servicePort = SERVICE.getFirstMappedPort();

    RequestSpecification requestSpecification = new RequestSpecBuilder()
        .setPort(servicePort)
        .build();

    RestAssured.given(requestSpecification)
        .accept(MediaType.TEXT_PLAIN)
        .when()
        .get("/api/todos")
        .then()
        .statusCode(Response.Status.OK.getStatusCode());

    ValidatableResponse response = RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/metrics/application")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .log().ifValidationFails(LogDetail.ALL);

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
