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
package de.openknowledge.projects.todolist.gateway.application;

import de.openknowledge.projects.todolist.gateway.AbstractIntegrationTest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.MatchType;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.specification.RequestSpecification;

/**
 * Integration test class for the resource {@link TodoGatewayResource}.
 */
/**
 * EXERCISE 6: TodoGatewayResource integration test with Mockserver (JUnit 5)
 *
 * HOWTO:
 * 1. add Network to link the two testcontainers
 * 2. add MockServerContainer
 * 3. add GenericContainer with todo-list-gateway image
 * 4. start MockServerContainer and GatewayContainer manually
 * 5. get host and port from gateway container
 */
public class TodoGatewayResourceIT extends AbstractIntegrationTest {

  private static final Logger LOG = LoggerFactory.getLogger(TodoGatewayResourceIT.class);

  private static RequestSpecification requestSpecification;

  /**
   * HOWTO:
   * 5. get port from gateway container
   * - set port to container mapped port
   */
  @BeforeAll
  public static void setUpRequestSpecification() {
    requestSpecification = new RequestSpecBuilder()
        .setPort(GATEWAY.getFirstMappedPort())
        .setBasePath("todo-list-gateway")
        .build();
  }

  @Test
  public void createTodoShouldReturn201() {
    new MockServerClient(MOCKSERVER.getContainerIpAddress(), MOCKSERVER.getFirstMappedPort())
        .when(HttpRequest.request()
                  .withMethod(HttpMethod.POST)
                  .withHeaders(
                      Header.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                  )
                  .withPath("/todo-list-service/api/todos")
                  .withBody(JsonBody.json("{" + System.lineSeparator()
                                          + "  \"title\": \"clean fridge\"," + System.lineSeparator()
                                          + "  \"description\": \"It's a mess\"," + System.lineSeparator()
                                          + "  \"dueDate\": \"2018-01-01T12:34:56Z\"" + System.lineSeparator()
                                          + "}")))
        .respond(HttpResponse.response()
                     .withStatusCode(Status.CREATED.getStatusCode())
                     .withHeader(Header.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                     .withBody(JsonBody.json("{" + System.lineSeparator()
                                             + "  \"id\": 1000," + System.lineSeparator()
                                             + "  \"title\": \"clean fridge\"," + System.lineSeparator()
                                             + "  \"description\": \"It's a mess\"," + System.lineSeparator()
                                             + "  \"dueDate\": \"2018-01-01T12:34:56Z\"," + System.lineSeparator()
                                             + "  \"done\": false" + System.lineSeparator()
                                             + "}")));

    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\n"
              + "  \"title\": \"clean fridge\",\n"
              + "  \"description\": \"It's a mess\",\n"
              + "  \"dueDate\": \"2018-01-01T12:34:56Z\",\n"
              + "  \"done\": false\n"
              + "}")
        .when()
        .post("/api/todos")
        .then()
        .statusCode(Status.CREATED.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Todo-schema.json"))
        .body("id", Matchers.notNullValue())
        .body("title", Matchers.equalTo("clean fridge"))
        .body("description", Matchers.equalTo("It's a mess"))
        .body("dueDate", Matchers.notNullValue())
        .body("done", Matchers.equalTo(false))
        .log().ifValidationFails();
  }

  @Test
  public void createTodoShouldReturn400ForEmptyRequestBody() {
    new MockServerClient(MOCKSERVER.getContainerIpAddress(), MOCKSERVER.getFirstMappedPort())
        .when(HttpRequest.request()
                  .withMethod(HttpMethod.POST)
                  .withHeaders(
                      Header.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                  )
                  .withPath("/todo-list-service/api/todos")
                  .withBody("{}"))
        .respond(HttpResponse.response()
                     .withStatusCode(Status.BAD_REQUEST.getStatusCode())
                     .withHeader(Header.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                     .withBody(JsonBody.json("{" + System.lineSeparator()
                                             + "    \"errors\": [" + System.lineSeparator()
                                             + "        {" + System.lineSeparator()
                                             + "            \"code\": \"DUE_DATE_IS_NULL\"," + System.lineSeparator()
                                             + "            \"message\": \"Due date is null\"" + System.lineSeparator()
                                             + "        }," + System.lineSeparator()
                                             + "        {" + System.lineSeparator()
                                             + "            \"code\": \"TITLE_IS_NULL\"," + System.lineSeparator()
                                             + "            \"message\": \"Title must not be null\"" + System.lineSeparator()
                                             + "        }" + System.lineSeparator()
                                             + "    ]," + System.lineSeparator()
                                             + "    \"timestamp\": \"2020-02-24T15:15:07.491+01:00\"," + System.lineSeparator()
                                             + "    \"uuid\": \"a0f68294-14b8-4011-b613-79eb90cef3e0\"" + System.lineSeparator()
                                             + "}")));

    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{}")
        .when()
        .post("/api/todos")
        .then()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(2))
        .log().ifValidationFails();
  }

  @Test
  public void deleteTodoShouldReturn204() {
    new MockServerClient(MOCKSERVER.getContainerIpAddress(), MOCKSERVER.getFirstMappedPort())
        .when(HttpRequest.request()
                  .withMethod(HttpMethod.DELETE)
                  .withPath("/todo-list-service/api/todos/1"))
        .respond(HttpResponse.response()
                     .withStatusCode(Status.NO_CONTENT.getStatusCode()));

    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("todoId", 1L)
        .when()
        .delete("/api/todos/{todoId}")
        .then()
        .statusCode(Status.NO_CONTENT.getStatusCode())
        .log().ifValidationFails();
  }

  @Test
  public void deleteTodoShouldReturn404ForUnknownTodo() {
    new MockServerClient(MOCKSERVER.getContainerIpAddress(), MOCKSERVER.getFirstMappedPort())
        .when(HttpRequest.request()
                  .withMethod(HttpMethod.DELETE)
                  .withPath("/todo-list-service/api/todos/999"))
        .respond(HttpResponse.response()
                     .withStatusCode(Status.NOT_FOUND.getStatusCode()));

    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("todoId", 999L)
        .when()
        .delete("/api/todos/{todoId}")
        .then()
        .statusCode(Status.NOT_FOUND.getStatusCode())
        .log().ifValidationFails();
  }

  @Test
  public void getTodoShouldReturn200() {
    new MockServerClient(MOCKSERVER.getContainerIpAddress(), MOCKSERVER.getFirstMappedPort())
        .when(HttpRequest.request()
                  .withMethod(HttpMethod.GET)
                  .withPath("/todo-list-service/api/todos/1"))
        .respond(HttpResponse.response()
                     .withStatusCode(Status.OK.getStatusCode())
                     .withHeader(Header.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                     .withBody(JsonBody.json("{" + System.lineSeparator()
                                             + "  \"id\": 1," + System.lineSeparator()
                                             + "  \"title\": \"clean fridge\"," + System.lineSeparator()
                                             + "  \"description\": \"It's a mess\"," + System.lineSeparator()
                                             + "  \"dueDate\": \"2018-01-01T12:34:56Z\"," + System.lineSeparator()
                                             + "  \"done\": false" + System.lineSeparator()
                                             + "}")));

    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("todoId", 1L)
        .when()
        .get("/api/todos/{todoId}")
        .then()
        .statusCode(Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Todo-schema.json"))
        .body("id", Matchers.equalTo(1))
        .body("title", Matchers.equalTo("clean fridge"))
        .body("description", Matchers.equalTo("It's a mess"))
        .body("dueDate", Matchers.notNullValue())
        .body("done", Matchers.equalTo(false))
        .log().ifValidationFails();
  }

  @Test
  public void getTodoShouldReturn404ForUnknownTodo() {
    new MockServerClient(MOCKSERVER.getContainerIpAddress(), MOCKSERVER.getFirstMappedPort())
        .when(HttpRequest.request()
                  .withMethod(HttpMethod.GET)
                  .withPath("/todo-list-service/api/todos/999"))
        .respond(HttpResponse.response()
                     .withStatusCode(Status.NOT_FOUND.getStatusCode()));

    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("todoId", 999L)
        .when()
        .get("/api/todos/{todoId}")
        .then()
        .statusCode(Status.NOT_FOUND.getStatusCode())
        .log().ifValidationFails();
  }

  @Test
  public void getTodosShouldReturn200() {
    new MockServerClient(MOCKSERVER.getContainerIpAddress(), MOCKSERVER.getFirstMappedPort())
        .when(HttpRequest.request()
                  .withMethod(HttpMethod.GET)
                  .withPath("/todo-list-service/api/todos"))
        .respond(HttpResponse.response()
                     .withStatusCode(Status.OK.getStatusCode())
                     .withHeader(Header.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                     .withBody(JsonBody.json("[" + System.lineSeparator()
                                             + "  {" + System.lineSeparator()
                                             + "    \"id\": 1," + System.lineSeparator()
                                             + "    \"title\": \"clean fridge\"," + System.lineSeparator()
                                             + "    \"description\": \"It's a mess\"," + System.lineSeparator()
                                             + "    \"dueDate\": \"2018-01-01T12:34:56Z\"," + System.lineSeparator()
                                             + "    \"done\": false" + System.lineSeparator()
                                             + "  }," + System.lineSeparator()
                                             + "  {" + System.lineSeparator()
                                             + "    \"title\": \"clean bathroom\"," + System.lineSeparator()
                                             + "    \"description\": \"It's really dirty :(\"," + System.lineSeparator()
                                             + "    \"dueDate\": \"2018-01-02T10:30:00Z\"," + System.lineSeparator()
                                             + "    \"done\": true" + System.lineSeparator()
                                             + "  }" + System.lineSeparator()
                                             + "]")));

    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/api/todos")
        .then()
        .statusCode(Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Todos-schema.json"))
        .body("size()", Matchers.is(2))
        .log().ifValidationFails();
  }

  @Test
  public void updateTodoShouldReturn204() {
    new MockServerClient(MOCKSERVER.getContainerIpAddress(), MOCKSERVER.getFirstMappedPort())
        .when(HttpRequest.request()
                  .withMethod(HttpMethod.PUT)
                  .withHeaders(
                      Header.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                  )
                  .withPath("/todo-list-service/api/todos/2")
                  .withBody(JsonBody.json("{" + System.lineSeparator()
                                          + "  \"title\": \"clean bathroom\"," + System.lineSeparator()
                                          + "  \"description\": \"It's really dirty :(\"," + System.lineSeparator()
                                          + "  \"dueDate\": \"2018-01-02T10:30:00Z\"," + System.lineSeparator()
                                          + "  \"done\": true" + System.lineSeparator()
                                          + "}", MatchType.STRICT)))
        .respond(HttpResponse.response()
                     .withStatusCode(Status.NO_CONTENT.getStatusCode()));

    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .pathParam("todoId", 2L)
        .body("{\n"
              + "  \"title\": \"clean bathroom\",\n"
              + "  \"description\": \"It's really dirty :(\",\n"
              + "  \"dueDate\": \"2018-01-02T10:30:00Z\",\n"
              + "  \"done\": true\n"
              + "}")
        .when()
        .put("/api/todos/{todoId}")
        .then()
        .statusCode(Status.NO_CONTENT.getStatusCode())
        .log().ifValidationFails();
  }

  @Test
  public void updateTodoShouldReturn400ForEmptyRequestBody() {
    new MockServerClient(MOCKSERVER.getContainerIpAddress(), MOCKSERVER.getFirstMappedPort())
        .when(HttpRequest.request()
                  .withMethod(HttpMethod.PUT)
                  .withHeaders(
                      Header.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                  )
                  .withPath("/todo-list-service/api/todos/1")
                  .withBody("{}"))
        .respond(HttpResponse.response()
                     .withStatusCode(Status.BAD_REQUEST.getStatusCode())
                     .withHeader(Header.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                     .withBody(JsonBody.json("{" + System.lineSeparator()
                                             + "    \"errors\": [" + System.lineSeparator()
                                             + "        {" + System.lineSeparator()
                                             + "            \"code\": \"DUE_DATE_IS_NULL\"," + System.lineSeparator()
                                             + "            \"message\": \"Due date is null\"" + System.lineSeparator()
                                             + "        }," + System.lineSeparator()
                                             + "        {" + System.lineSeparator()
                                             + "            \"code\": \"TITLE_IS_NULL\"," + System.lineSeparator()
                                             + "            \"message\": \"Title must not be null\"" + System.lineSeparator()
                                             + "        }" + System.lineSeparator()
                                             + "    ]," + System.lineSeparator()
                                             + "    \"timestamp\": \"2020-02-24T15:15:07.491+01:00\"," + System.lineSeparator()
                                             + "    \"uuid\": \"a0f68294-14b8-4011-b613-79eb90cef3e0\"" + System.lineSeparator()
                                             + "}")));

    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .pathParam("todoId", 1L)
        .body("{}")
        .when()
        .put("/api/todos/{todoId}")
        .then()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(2))
        .log().ifValidationFails();
  }

  @Test
  public void updateTodoShouldReturn404ForUnknownTodo() {
    new MockServerClient(MOCKSERVER.getContainerIpAddress(), MOCKSERVER.getFirstMappedPort())
        .when(HttpRequest.request()
                  .withMethod(HttpMethod.PUT)
                  .withHeaders(
                      Header.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                  )
                  .withPath("/todo-list-service/api/todos/999")
                  .withBody(JsonBody.json("{" + System.lineSeparator()
                                          + "  \"title\": \"clean bathroom\"," + System.lineSeparator()
                                          + "  \"description\": \"It's really dirty :(\"," + System.lineSeparator()
                                          + "  \"dueDate\": \"2018-01-02T10:30:00Z\"," + System.lineSeparator()
                                          + "  \"done\": true" + System.lineSeparator()
                                          + "}", MatchType.STRICT)))
        .respond(HttpResponse.response()
                     .withStatusCode(Status.NOT_FOUND.getStatusCode()));

    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .pathParam("todoId", 999L)
        .body("{\n"
              + "  \"title\": \"clean bathroom\",\n"
              + "  \"description\": \"It's really dirty :(\",\n"
              + "  \"dueDate\": \"2018-01-02T10:30:00Z\",\n"
              + "  \"done\": true\n"
              + "}")
        .when()
        .put("/api/todos/{todoId}")
        .then()
        .statusCode(Status.NOT_FOUND.getStatusCode())
        .log().ifValidationFails();
  }
}
