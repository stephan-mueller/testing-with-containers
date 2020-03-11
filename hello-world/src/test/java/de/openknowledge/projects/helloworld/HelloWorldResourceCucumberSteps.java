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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

/**
 * Step Definitions for the cucumber test {@link HelloWorldResourceCucumberIT}.
 */
public class HelloWorldResourceCucumberSteps {

  private RequestSpecification requestSpecification;

  private String name;

  private io.restassured.response.Response response;

  /**
   * HOWTO:
   * 4. get port from container
   * - set port to container mapped port
   */
  @Before
  public void beforeScenario() {
    requestSpecification = new RequestSpecBuilder()
        .setPort(HelloWorldResourceCucumberTestContainerBaseClass.getContainer().getFirstMappedPort())
        .setBasePath("hello-world")
        .build();
  }

  @Given("^a user$")
  public void given_a_user() {
    this.name = "";
  }

  @Given("^a user with name \"([^\"]*)\"$")
  public void given_a_user_with_name(String name) {
    this.name = name;
  }

  @When("the user calls the service")
  public void when_the_user_calls_the_service() {
    response = RestAssured.given(requestSpecification)
        .accept(MediaType.TEXT_PLAIN)
        .when()
        .get("/api/hello");
  }

  @When("^the user calls the service with his name$")
  public void when_the_user_calls_the_service_with_his_name() {
    response = RestAssured.given(requestSpecification)
        .accept(MediaType.TEXT_PLAIN)
        .pathParam("name", name)
        .when()
        .get("/api/hello/{name}");
  }

  @Then("the response is \"([^\"]*)\"$")
  public void then_the_response_is(String text) {
    response.then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.TEXT_PLAIN)
        .body(Matchers.equalTo(text));
  }
}
