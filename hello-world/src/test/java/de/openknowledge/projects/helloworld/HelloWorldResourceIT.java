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
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

/**
 * Integration test for the resource {@link HelloWorldResource}.
 */
/**
 * EXERCISE 1: HelloWorld integration test with testcontainer on-the-fly (JUnit 5)
 *
 * HOWTO:
 * 1. add @Testcontainers annotation to test class
 * 2. add Generic Container with ImageFromDockerfile & use DockerfileBuilder
 * 3. add log consumer to receive container logs
 * 4. get port from container
 */
@Testcontainers
public class HelloWorldResourceIT {

  private static final Logger LOG = LoggerFactory.getLogger(HelloWorldResourceIT.class);

  /**
   * HOWTO:
   * 2. add Generic Container with ImageFromDockerfile & use DockerfileBuilder
   * - add @Container annotation
   * - instantiate GenericContainer with ImageFromDockerfile
   * - use DockerfileBuilder to
   *    + define Docker base image (openjdk)
   *    + copy runnable jar to /opt
   *    + define expose port
   *    + define entry point that starts the runnable jar
   * - set expose port
   *
   * 3. add log consumer to receive container logs
   *
   * HINT: use Slf4jLogConsumer
   */
  @Container
  private static final GenericContainer<?> CONTAINER = new GenericContainer(
      new ImageFromDockerfile().withDockerfileFromBuilder(builder -> builder
          .from("openjdk:8-jre-alpine")
          .add("target/hello-world.jar", "/opt/hello-world.jar")
          .expose(9080)
          .entryPoint("exec java -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true -jar /opt/hello-world.jar")
          .build())
          .withFileFromFile("target/hello-world.jar", new File("target/hello-world.jar")))
      .withExposedPorts(9080)
      .withLogConsumer(new Slf4jLogConsumer(LOG));

  private static RequestSpecification requestSpecification;

  /*
   * HOWTO:
   * 4. get port from container
   * - set port to container mapped port
   */
  @BeforeAll
  public static void setUpUri() {
    requestSpecification = new RequestSpecBuilder()
        .setPort(CONTAINER.getFirstMappedPort())
        .setBasePath("hello-world")
        .build();
  }

  @Test
  public void sayHello() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.TEXT_PLAIN)
        .when()
        .get("/api/hello")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.TEXT_PLAIN)
        .body(Matchers.equalTo("Hello World!"));
  }

  @Test
  public void sayHelloWorld() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.TEXT_PLAIN)
        .pathParam("name", "Stephan")
        .when()
        .get("/api/hello/{name}")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.TEXT_PLAIN)
        .body(Matchers.equalTo("Hello Stephan!"));
  }
}
