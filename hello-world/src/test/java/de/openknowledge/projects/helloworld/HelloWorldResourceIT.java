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
package de.openknowledge.projects.helloworld;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import io.restassured.RestAssured;

/**
 * Integration test for the resource {@link HelloWorldResource}.
 */
@Testcontainers
public class HelloWorldResourceIT {

  private static final Logger LOG = LoggerFactory.getLogger(HelloWorldResourceIT.class);

  @Container
  private static final GenericContainer<?> container = new GenericContainer("testing-with-containers/hello-world:0")
      .withExposedPorts(9080)
      .withLogConsumer(new Slf4jLogConsumer(LOG));

  private static URI uri;

  @BeforeAll
  public static void setUpUri() {
    uri = UriBuilder.fromPath("hello-world")
        .scheme("http")
        .host(container.getContainerIpAddress())
        .port(container.getFirstMappedPort())
        .build();
  }

  @Test
  public void sayHello() {
    RestAssured.given()
        .accept(MediaType.TEXT_PLAIN)
        .when()
        .get(UriBuilder.fromUri(uri).path("api").path("hello").build())
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.TEXT_PLAIN)
        .body(Matchers.equalTo("Hello World!"));
  }

  @Test
  public void sayHelloWorld() {
    RestAssured.given()
        .accept(MediaType.TEXT_PLAIN)
        .when()
        .get(UriBuilder.fromUri(uri).path("api").path("hello").path("Stephan").build())
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.TEXT_PLAIN)
        .body(Matchers.equalTo("Hello Stephan!"));
  }
}
