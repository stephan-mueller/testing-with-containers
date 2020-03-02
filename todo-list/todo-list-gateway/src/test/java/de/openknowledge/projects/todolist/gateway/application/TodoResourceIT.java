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

import static de.openknowledge.projects.todolist.gateway.GatewayContainer.ENV_SERVICE_HOST;
import static de.openknowledge.projects.todolist.gateway.GatewayContainer.ENV_SERVICE_PORT;

import de.openknowledge.projects.todolist.gateway.GatewayContainer;

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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;

/**
 * Integration test class for the resource {@link TodoResource}.
 */
@Testcontainers
public class TodoResourceIT {

  private static final Logger LOG = LoggerFactory.getLogger(TodoResourceIT.class);

  private static final String MOCKSERVER_NETWORK_ALIAS = "mockserver";
  private static final Integer MOCKSERVER_EXPOSED_PORT = 1080;

  private static final Network NETWORK = Network.newNetwork();

  @Container
  private static final MockServerContainer MOCKSERVER = new MockServerContainer()
      .withNetwork(NETWORK)
      .withNetworkAliases(MOCKSERVER_NETWORK_ALIAS)
      .withLogConsumer(new Slf4jLogConsumer(LOG));

  @Container
  private static final GenericContainer<?> GATEWAY = GatewayContainer.newContainer()
      .dependsOn(MOCKSERVER)
      .withNetwork(NETWORK)
      .withEnv(ENV_SERVICE_HOST, MOCKSERVER_NETWORK_ALIAS)
      .withEnv(ENV_SERVICE_PORT, MOCKSERVER_EXPOSED_PORT.toString())
      .withLogConsumer(new Slf4jLogConsumer(LOG));

  private static URI uri;

  @BeforeAll
  public static void setUpUri() {
    uri = UriBuilder.fromPath("todo-list-gateway")
        .scheme("http")
        .host(GATEWAY.getContainerIpAddress())
        .port(GATEWAY.getFirstMappedPort())
        .build();
  }

  private URI getListUri() {
    return UriBuilder.fromUri(uri).path("api").path("todos").build();
  }

  private URI getSingleItemUri(final Long todoId) {
    return UriBuilder.fromUri(getListUri()).path("{id}").build(todoId);
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

    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\n"
              + "  \"title\": \"clean fridge\",\n"
              + "  \"description\": \"It's a mess\",\n"
              + "  \"dueDate\": \"2018-01-01T12:34:56Z\",\n"
              + "  \"done\": false\n"
              + "}")
        .when()
        .post(getListUri())
        .then()
        .statusCode(Status.CREATED.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Todo-schema.json"))
        .body("id", Matchers.notNullValue())
        .body("title", Matchers.equalTo("clean fridge"))
        .body("description", Matchers.equalTo("It's a mess"))
        .body("dueDate", Matchers.notNullValue())
        .body("done", Matchers.equalTo(false));
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

    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{}")
        .when()
        .post(getListUri())
        .then()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(2));
  }

  @Test
  public void deleteTodoShouldReturn204() {
    new MockServerClient(MOCKSERVER.getContainerIpAddress(), MOCKSERVER.getFirstMappedPort())
        .when(HttpRequest.request()
                  .withMethod(HttpMethod.DELETE)
                  .withPath("/todo-list-service/api/todos/1"))
        .respond(HttpResponse.response()
                     .withStatusCode(Status.NO_CONTENT.getStatusCode()));

    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .delete(getSingleItemUri(1L))
        .then()
        .statusCode(Status.NO_CONTENT.getStatusCode());
  }

  @Test
  public void deleteTodoShouldReturn404ForUnknownTodo() {
    new MockServerClient(MOCKSERVER.getContainerIpAddress(), MOCKSERVER.getFirstMappedPort())
        .when(HttpRequest.request()
                  .withMethod(HttpMethod.DELETE)
                  .withPath("/todo-list-service/api/todos/999"))
        .respond(HttpResponse.response()
                     .withStatusCode(Status.NOT_FOUND.getStatusCode()));

    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .delete(getSingleItemUri(999L))
        .then()
        .statusCode(Status.NOT_FOUND.getStatusCode());
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

    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get(getSingleItemUri(1L))
        .then()
        .statusCode(Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Todo-schema.json"))
        .body("id", Matchers.equalTo(1))
        .body("title", Matchers.equalTo("clean fridge"))
        .body("description", Matchers.equalTo("It's a mess"))
        .body("dueDate", Matchers.notNullValue())
        .body("done", Matchers.equalTo(false));
  }

  @Test
  public void getTodoShouldReturn404ForUnknownTodo() {
    new MockServerClient(MOCKSERVER.getContainerIpAddress(), MOCKSERVER.getFirstMappedPort())
        .when(HttpRequest.request()
                  .withMethod(HttpMethod.GET)
                  .withPath("/todo-list-service/api/todos/999"))
        .respond(HttpResponse.response()
                     .withStatusCode(Status.NOT_FOUND.getStatusCode()));

    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get(getSingleItemUri(999L))
        .then()
        .statusCode(Status.NOT_FOUND.getStatusCode());
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

    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get(getListUri())
        .then()
        .statusCode(Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Todos-schema.json"))
        .body("size()", Matchers.is(2));
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

    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\n"
              + "  \"title\": \"clean bathroom\",\n"
              + "  \"description\": \"It's really dirty :(\",\n"
              + "  \"dueDate\": \"2018-01-02T10:30:00Z\",\n"
              + "  \"done\": true\n"
              + "}")
        .when()
        .put(getSingleItemUri(2L))
        .then()
        .statusCode(Status.NO_CONTENT.getStatusCode());
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

    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{}")
        .when()
        .put(getSingleItemUri(1L))
        .then()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(2))
    ;
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

    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\n"
              + "  \"title\": \"clean bathroom\",\n"
              + "  \"description\": \"It's really dirty :(\",\n"
              + "  \"dueDate\": \"2018-01-02T10:30:00Z\",\n"
              + "  \"done\": true\n"
              + "}")
        .when()
        .put(getSingleItemUri(999L))
        .then()
        .statusCode(Status.NOT_FOUND.getStatusCode());
  }
}
