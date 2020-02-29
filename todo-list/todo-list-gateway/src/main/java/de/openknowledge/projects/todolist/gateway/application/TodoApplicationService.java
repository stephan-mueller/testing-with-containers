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

import de.openknowledge.projects.todolist.gateway.infrastructure.domain.service.Service;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 * Service that provides access to the
 */
@Service
public class TodoApplicationService {

  private static Logger LOG = LoggerFactory.getLogger(TodoApplicationService.class);

  @Inject
  @RestClient
  private TodoListServiceClient client;

  public Response createTodo(final String newTodo) {
    LOG.debug("Request createTodo of todo {}", newTodo);
    return client.createTodo(newTodo);
  }

  public Response deleteTodo(final Long todoId) {
    LOG.debug("Request deleteTodo todo with id {}", todoId);
    return client.deleteTodo(todoId);
  }

  public Response getTodo(final Long todoId) {
    LOG.debug("Request todo with id {}", todoId);
    return client.getTodo(todoId);
  }

  public Response getTodos() {
    LOG.debug("Request all todos");
    return client.getTodos();
  }

  public Response updateTodo(final Long todoId, final String modifiedTodo) {
    LOG.info("Request updateTodo todo with id {} ({})", todoId, modifiedTodo);
    return client.updateTodo(todoId, modifiedTodo);
  }
}
