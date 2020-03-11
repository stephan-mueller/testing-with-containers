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
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.Network;

/**
 * Provides testcontainers for integration tests.
 */
public abstract class AbstractIntegrationTest {

  private static final String MOCKSERVER_NETWORK_ALIAS = "mockserver";
  private static final Integer MOCKSERVER_EXPOSED_PORT = 1080;

  private static final String ENV_SERVICE_HOST = "SERVICE_HOST";
  private static final String ENV_SERVICE_PORT = "SERVICE_PORT";

  private static final Integer EXPOSED_PORT = 9080;

  /**
   * HOWTO:
   * 1. add Network to link the two testcontainers
   */
  private static final Network NETWORK = Network.newNetwork();

  /**
   * 2. add MockServerContainer
   * - instantiate MockServerContainer
   * - set network
   * - add network alias "mockserver"
   * - add log consumer to receive container logs
   */
  protected static final MockServerContainer MOCKSERVER = new MockServerContainer()
      .withNetwork(NETWORK)
      .withNetworkAliases(MOCKSERVER_NETWORK_ALIAS);

  /**
   * 3. add GenericContainer with todo-list-gateway image
   * - instantiate GenericContainer with todo-list-gateway image
   * - set depends on MockServerContainer
   * - set network
   * - set environment variables "SERVICE_HOST" and "SERVICE_PORT" for todo-list-service client (makes http calls to MockServer)
   * - add log consumer to receive container logs
   *
   * HINT 1: use service image "testing-with-containers/todo-list-gateway:0" (requires to run "mvn clean package" before)
   * HINT 2: use "SERVICE_HOST" = "mockserver", "SERVICE_PORT" = "1080" as environment settings
   */
  protected static final GenericContainer<?> GATEWAY = new GenericContainer("testing-with-containers/todo-list-gateway:0")
      .withExposedPorts(EXPOSED_PORT)
      .dependsOn(MOCKSERVER)
      .withNetwork(NETWORK)
      .withEnv(ENV_SERVICE_HOST, MOCKSERVER_NETWORK_ALIAS)
      .withEnv(ENV_SERVICE_PORT, MOCKSERVER_EXPOSED_PORT.toString());

  /**
   * 4. start MockServerContainer and GatewayContainer manually
   */
  static {
    MOCKSERVER.start();
    GATEWAY.start();
  }
}