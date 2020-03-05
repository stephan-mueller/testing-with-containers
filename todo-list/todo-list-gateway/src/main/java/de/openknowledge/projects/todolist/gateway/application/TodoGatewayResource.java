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

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * A resource that provides access to the todo-list-service.
 */
@Path("todos")
@Timed(name = "todos", unit = MetricUnits.MILLISECONDS, description = "Metrics of the TodoGatewayResource", absolute = true)
public class TodoGatewayResource {

  private static final Logger LOG = LoggerFactory.getLogger(TodoGatewayResource.class);

  @Inject
  private TodoGatewayApplicationService repository;

  @POST
  public Response createTodo(final String newTodo) {
    LOG.info("Request createTodo of todo {}", newTodo);
    Response response = repository.createTodo(newTodo);
    return Response.fromResponse(response).build();
  }

  @DELETE
  @Path("/{id}")
  public Response deleteTodo(@PathParam("id") final Long todoId) {
    LOG.info("Request deleteTodo todo with id {}", todoId);
    Response response = repository.deleteTodo(todoId);
    return Response.fromResponse(response).build();
  }

  @GET
  @Path("/{id}")
  public Response getTodo(@PathParam("id") final Long todoId) {
    LOG.info("Request todo with id {}", todoId);
    Response response = repository.getTodo(todoId);
    return Response.fromResponse(response).build();
  }

  @GET
  @Operation(description = "Find all todos")
  public Response getTodos() {
    LOG.info("Request all todos");
    Response response = repository.getTodos();
    return Response.fromResponse(response).build();
  }

  @PUT
  @Path("/{id}")
  public Response updateTodo(@PathParam("id") final Long todoId, final String modifiedTodo) {
    LOG.info("Request updateTodo todo with id {} ({})", todoId, modifiedTodo);
    Response response = repository.updateTodo(todoId, modifiedTodo);
    return Response.fromResponse(response).build();
  }
}
