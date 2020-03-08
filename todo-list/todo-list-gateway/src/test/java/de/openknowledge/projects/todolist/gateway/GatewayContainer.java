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
package de.openknowledge.projects.todolist.gateway;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * Provides a generic container for integration tests
 */
public final class GatewayContainer {

  public static final String ENV_SERVICE_HOST = "SERVICE_HOST";
  public static final String ENV_SERVICE_PORT = "SERVICE_PORT";

  public static final Integer EXPOSED_PORT = 19080;

  private final GenericContainer<?> container = new GenericContainer("testing-with-containers/todo-list-gateway:0")
      .withExposedPorts(EXPOSED_PORT)
      .waitingFor(Wait.forListeningPort());

  private GatewayContainer() {
    super();
  }

  public static GenericContainer<?> newContainer() {
    return new GatewayContainer().container;
  }
}
