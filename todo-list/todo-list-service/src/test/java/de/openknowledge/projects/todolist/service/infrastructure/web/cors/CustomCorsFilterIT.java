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

import de.openknowledge.projects.todolist.service.AbstractIntegrationTest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

/**
 * Integration test for the custom CORS filter (configured in the server.xml)
 */
public class CustomCorsFilterIT extends AbstractIntegrationTest {

  private static final Logger LOG = LoggerFactory.getLogger(CustomCorsFilterIT.class);

  @Test
  public void checkCorsHeader() {
    String serviceHost = SERVICE.getContainerIpAddress();
    Integer servicePort = SERVICE.getFirstMappedPort();

    RequestSpecification requestSpecification = new RequestSpecBuilder()
        .setPort(servicePort)
        .setBasePath("todo-list-service")
        .addHeader("ORIGIN", serviceHost + ":" + servicePort)
        .build();

    RestAssured.given(requestSpecification)
        .when()
        .options("/api/todos")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .header("Access-Control-Allow-Credentials", "true")
        .header("Access-Control-Allow-Origin", Matchers.notNullValue())
        .header(HttpHeaders.ALLOW, "DELETE,POST,GET,PUT,OPTIONS,HEAD")
        .log().ifValidationFails(LogDetail.ALL);
  }
}
