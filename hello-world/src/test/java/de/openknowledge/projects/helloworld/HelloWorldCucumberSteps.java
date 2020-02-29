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

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;

/**
 * Step Definitions for the cucumber test {@link HelloWorldCucumberIT}.
 */
public class HelloWorldCucumberSteps {

  private URI uri;

  private String name;

  private io.restassured.response.Response response;

  @Before
  public void beforeScenario() {
    uri = HelloWorldCucumberTestContainerBaseClass.getUri();
  }

  @Given("^a user$")
  public void given_a_user() {
    this.name = name;
  }

  @Given("^a user with name \"([^\"]*)\"$")
  public void given_a_user_with_name(String name) {
    this.name = name;
  }

  @When("the user calls the service")
  public void when_the_user_calls_the_service() {
    response = RestAssured.given()
        .accept(MediaType.TEXT_PLAIN)
        .when()
        .get(UriBuilder.fromUri(uri).path("api").path("hello").build());
  }

  @When("^the user calls the service with his name$")
  public void when_the_user_calls_the_service_with_his_name() {
    response = RestAssured.given()
        .accept(MediaType.TEXT_PLAIN)
        .when()
        .get(UriBuilder.fromUri(uri).path("api").path("hello").path(name).build());
  }

  @Then("the response is \"([^\"]*)\"$")
  public void then_the_response_is(String text) {
    response.then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.TEXT_PLAIN)
        .body(Matchers.equalTo(text));
  }
}
