/*
 * 02.07.2019 AP6T6
 * Copyright (c) 2019 HUK-COBURG. All Rights Reserved.
 * Copyright (C) 2019 open knowledge GmbH
 */
package de.openknowledge.projects.helloworld;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

/**
 * Test class for the resource {@link HelloWorldResource}.
 */
public class HelloWorldResourceTest {

  private HelloWorldResource resource;

  @BeforeEach
  public void setUp() {
    resource = new HelloWorldResource();
  }

  @Test
  public void sayHello() {
    Response response = resource.sayHello("Stephan");
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    assertThat(response.getEntity()).isEqualTo("Hello Stephan!");
  }

  @Test
  public void sayHelloWorld() {
    Response response = resource.sayHelloWorld();
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    assertThat(response.getEntity()).isEqualTo("Hello World!");
  }
}