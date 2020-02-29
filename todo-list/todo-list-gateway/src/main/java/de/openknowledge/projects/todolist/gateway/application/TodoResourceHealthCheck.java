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
package de.openknowledge.projects.todolist.gateway.application;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * Health check for the resource {@link TodoResource}.
 */
@Readiness
@ApplicationScoped
public class TodoResourceHealthCheck implements HealthCheck {

  @Inject
  @ConfigProperty(name = "default.http.port")
  private Integer port;

  @Inject
  @ConfigProperty(name = "app.context.root")
  private String contextRoot;

  @Override
  public HealthCheckResponse call() {
    HealthCheckResponseBuilder builder = HealthCheckResponse.named(TodoResource.class.getSimpleName());

    Response response = ClientBuilder.newClient()
        .target(getResourceUri())
        .request()
        .header("ORIGIN", String.format("%s:%d", "localhost", port))
        .options();

    boolean up = Response.Status.OK.equals(response.getStatusInfo().toEnum());

    if (up) {
      builder.withData("resource", "available").up();
    } else {
      builder.withData("resource", "not available").down();
    }

    return builder.build();
  }

  private URI getResourceUri() {
    return UriBuilder.fromPath(contextRoot)
        .path(JaxRsActivator.class.getAnnotation(ApplicationPath.class).value())
        .path(TodoResource.class.getAnnotation(Path.class).value())
        .scheme("http")
        .host("localhost")
        .port(port)
        .build();
  }
}
