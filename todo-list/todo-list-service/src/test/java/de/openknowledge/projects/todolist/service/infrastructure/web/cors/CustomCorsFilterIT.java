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
package de.openknowledge.projects.todolist.service.infrastructure.web.cors;

import static de.openknowledge.projects.todolist.service.ComposeContainer.COMPOSE_SERVICENAME_DATABASE;
import static de.openknowledge.projects.todolist.service.ComposeContainer.COMPOSE_SERVICENAME_SERVICE;
import static de.openknowledge.projects.todolist.service.ComposeContainer.SERVICE_PORT;

import de.openknowledge.projects.todolist.service.ComposeContainer;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import io.restassured.RestAssured;

/**
 * Integration test for the custom CORS filter (configured in the server.xml)
 */
@Testcontainers
public class CustomCorsFilterIT {

  private static final Logger LOG = LoggerFactory.getLogger(CustomCorsFilterIT.class);

  @Container
  public static DockerComposeContainer environment = ComposeContainer.newContainer()
      .withLogConsumer(COMPOSE_SERVICENAME_DATABASE, new Slf4jLogConsumer(LOG))
      .withLogConsumer(COMPOSE_SERVICENAME_SERVICE, new Slf4jLogConsumer(LOG));

  @Test
  public void checkCorsHeader() {
    String serviceHost = environment.getServiceHost("service", SERVICE_PORT);
    Integer servicePort = environment.getServicePort("service", SERVICE_PORT);

    RestAssured.given()
        .header("ORIGIN", serviceHost + ":" + servicePort)
        .when()
        .options(UriBuilder.fromPath("todo-list-service")
                     .path("api")
                     .path("todos")
                     .scheme("http")
                     .host(serviceHost)
                     .port(servicePort)
                     .build())
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .header("Access-Control-Allow-Credentials", "true")
        .header("Access-Control-Allow-Origin", Matchers.notNullValue())
        .header(HttpHeaders.ALLOW, "DELETE,POST,GET,PUT,OPTIONS,HEAD");
  }
}
