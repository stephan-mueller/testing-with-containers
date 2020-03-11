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
package de.openknowledge.projects.todolist.service;

import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

/**
 * Provides testcontainers for integration tests.
 */
public abstract class AbstractIntegrationTest {

  /**
   * HOWTO:
   * 1. add Network to link the two testcontainers
   */
  protected static final Network NETWORK = Network.newNetwork();

  /**
   * 2. add FixedHostGenericContainer with postgres image (name = DATABASE)
   * - instantiate GenericContainer with postgres image
   * - set expose port (5432)
   * - set NETWORK
   * - add NETWORK alias "DATABASE"
   * - add log consumer to receive container logs TODO
   * - add WaitStrategy -> Wait.forLogMessage(".*server started.*", 1)
   */
  protected static final FixedHostPortGenericContainer<?> DATABASE = new FixedHostPortGenericContainer<>("postgres:12-alpine")
      .withExposedPorts(5432)
      .withFixedExposedPort(5432, 5432)
      .withNetwork(NETWORK)
      .withNetworkAliases("DATABASE")
      .withEnv("POSTGRES_DB", "postgres")
      .withEnv("POSTGRES_USER", "postgres")
      .withEnv("POSTGRES_PASSWORD", "postgres")
      .withCopyFileToContainer(MountableFile.forClasspathResource("docker/1-schema.sql"), "/docker-entrypoint-initdb.d/1-schema.sql")
      .waitingFor(
          Wait.forLogMessage(".*server started.*", 1)
      );

  /**
   * 3. add GenericContainer with todo-list-SERVICE image (name = SERVICE)
   * - instantiate GenericContainer with SERVICE image
   * - set expose port (9080)
   * - set NETWORK
   * - set depends on DATABASE container
   * - add log consumer to receive container logs TODO
   * - add WaitStrategy -> Wait.forLogMessage(".*server started.*", 1)
   *
   * HINT: use SERVICE image "testing-with-containers/todo-list-SERVICE:0" (requires to run "mvn clean package" before)
   */
  protected static final GenericContainer SERVICE = new GenericContainer("testing-with-containers/todo-list-service:0")
      .withExposedPorts(9080)
      .withNetwork(NETWORK)
      .dependsOn(DATABASE);

  static {
    DATABASE.start();
    SERVICE.start();
  }
}
