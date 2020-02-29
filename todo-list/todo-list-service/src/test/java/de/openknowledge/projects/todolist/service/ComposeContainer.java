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

import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

/**
 * Provides a docker compose container for integration tests
 */
public final class ComposeContainer {

  public static final String COMPOSE_SERVICENAME_DATABASE = "database";
  public static final String COMPOSE_SERVICENAME_SERVICE = "service";

  public static final int DATABASE_PORT = 5432;
  public static final int SERVICE_PORT = 9080;

  private DockerComposeContainer environment = new DockerComposeContainer(new File("./docker-compose.yml"))
      .withExposedService(COMPOSE_SERVICENAME_DATABASE, DATABASE_PORT)
      .withExposedService(COMPOSE_SERVICENAME_SERVICE, SERVICE_PORT);

  private ComposeContainer() {
    super();
  }

  public static DockerComposeContainer newContainer() {
    return new ComposeContainer().environment;
  }
}
