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
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

@ApplicationScoped
public class TodoListServiceClientProducer {

  @Inject
  @ConfigProperty(name = "todo-list-service.host")
  private String host;

  @Inject
  @ConfigProperty(name = "todo-list-service.port")
  private Integer port;

  @Produces
  @RestClient
  @RequestScoped
  public TodoListServiceClient create() {
    return RestClientBuilder.newBuilder()
        .baseUri(UriBuilder.fromUri("todo-list-service")
                     .scheme("http")
                     .host(host)
                     .port(port)
                     .build())
        .connectTimeout(3000, TimeUnit.MILLISECONDS)
        .readTimeout(3000, TimeUnit.MILLISECONDS)
        .build(TodoListServiceClient.class);
  }
}
