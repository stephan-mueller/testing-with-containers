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
package de.openknowledge.projects.todolist.gateway.infrastructure.microprofile.health;

import de.openknowledge.projects.todolist.gateway.AbstractIntegrationTest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

/**
 * Integration test for the health check {@link ApplicationHealthCheck}.
 */
public class ApplicationHealthCheckIT extends AbstractIntegrationTest {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationHealthCheckIT.class);

  @Test
  public void checkHealth() {
    RequestSpecification requestSpecification = new RequestSpecBuilder()
        .setPort(GATEWAY.getFirstMappedPort())
        .build();

    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/health")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.OK.getStatusCode())
        .body("status", Matchers.equalTo("UP"))
        .rootPath("checks.find{ it.name == 'application' }")
        .body("status", Matchers.equalTo("UP"))
        .body("data.name", Matchers.equalTo("todo-list-gateway"))
        .body("data.version", Matchers.notNullValue())
        .body("data.createdAt", Matchers.notNullValue());
  }
}
