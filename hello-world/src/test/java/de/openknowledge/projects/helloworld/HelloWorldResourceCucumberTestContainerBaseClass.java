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

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.io.File;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import cucumber.api.event.ConcurrentEventListener;
import cucumber.api.event.EventHandler;
import cucumber.api.event.EventPublisher;
import cucumber.api.event.TestRunFinished;
import cucumber.api.event.TestRunStarted;

/**
 * Base class which starts a testcontainer for the cucumber test.
 */
public class HelloWorldResourceCucumberTestContainerBaseClass implements ConcurrentEventListener {

  /**
   * TODO:
   * 2. add Generic Container with ImageFromDockerfile
   *    + add Dockerfile
   *    + add JAR
   *    + set exposed port
   *
   * HINT: use withFileFromFile()
   */
  private static final GenericContainer<?> CONTAINER = new GenericContainer();

  private static URI uri;

  @Override
  public void setEventPublisher(EventPublisher eventPublisher) {
    eventPublisher.registerHandlerFor(TestRunStarted.class, setup);
    eventPublisher.registerHandlerFor(TestRunFinished.class, teardown);
  }

  private EventHandler<TestRunStarted> setup = event -> {
    beforeAll();
    setUpUri();
  };

  /**
   * TODO:
   * 3. call start/stop
   * - start container
   */
  private void beforeAll() {
  }

  /**
   * TODO:
   * 4. get host and port from container
   * - set host to container ip address
   * - set port to container mapped port
   */
  private void setUpUri() {
    uri = UriBuilder.fromPath("hello-world")
        .scheme("http")
        //.host(...)
        //.port(...)
        .build();
  }

  private EventHandler<TestRunFinished> teardown = event -> afterAll();

  /*
   * TODO:
   * 3. call start/stop
   * - stop container
   */
  private void afterAll() {
  }

  public static URI getUri() {
    return uri;
  }
}

