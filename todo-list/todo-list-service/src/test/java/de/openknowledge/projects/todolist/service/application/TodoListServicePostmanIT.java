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
package de.openknowledge.projects.todolist.service.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

import java.io.IOException;

/**
 * Integration test class for the todo-list-service.
 *
 * Uses newman-cli in a docker container to run a postman collection against the todo-list-service.
 */
@Disabled
public class TodoListServicePostmanIT {

  private static final Logger LOG = LoggerFactory.getLogger(TodoListServicePostmanIT.class);

  private static final Network NETWORK = Network.newNetwork();

  @Container
  private static final FixedHostPortGenericContainer<?> DATABASE = new FixedHostPortGenericContainer<>("postgres:12-alpine")
      .withExposedPorts(5432)
      .withFixedExposedPort(5432, 5432)
      .withNetwork(NETWORK)
      .withNetworkAliases("DATABASE")
      .withEnv("POSTGRES_DB", "postgres")
      .withEnv("POSTGRES_USER", "postgres")
      .withEnv("POSTGRES_PASSWORD", "postgres")
      .withClasspathResourceMapping("docker/1-schema.sql", "docker-entrypoint-initdb.d/1-schema.sql", BindMode.READ_ONLY)
      .withClasspathResourceMapping("docker/2-data.sql", "docker-entrypoint-initdb.d/2-data.sql", BindMode.READ_ONLY)
      .waitingFor(Wait.forLogMessage(".*server started.*", 1));

  @Container
  private static final FixedHostPortGenericContainer<?> SERVICE = new FixedHostPortGenericContainer<>("testing-with-containers/todo-list-service:0")
          .withExposedPorts(9080)
          .withFixedExposedPort(9080, 9080)
          .withNetwork(NETWORK)
          .withNetworkAliases("SERVICE")
          .dependsOn(DATABASE);

  @Container
  private static final GenericContainer<?> NEWMAN = new GenericContainer<>("postman/newman:4.6.0-alpine")
      .withNetwork(NETWORK)
      .dependsOn(DATABASE, SERVICE)
      .withClasspathResourceMapping("postman/todo-list-service.postman_collection.json","/etc/newman/todo-list-service.postman_collection.json", BindMode.READ_ONLY)
      .withClasspathResourceMapping("postman/todo-list-service.postman_environment.json","/etc/newman/todo-list-service.postman_environment.json", BindMode.READ_ONLY)
      .withLogConsumer(new Slf4jLogConsumer(LOG));

  @Test
  void run() throws IOException, InterruptedException {
    String command = "newman run todo-list-service.postman_collection.json"
                     + " --environment todo-list-service.postman_environment.json"
                     + " --reporters=cli,junit"
                     + " --reporter-junit-export=/results";
    org.testcontainers.containers.Container.ExecResult result = NEWMAN.execInContainer(command);
    Assertions.assertThat(result.getExitCode()).isEqualTo(0);
  }
}
