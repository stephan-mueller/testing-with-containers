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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;

/**
 * Test class for the resource {@link TodoGatewayResource}.
 */
@ExtendWith(MockitoExtension.class)
public class TodoGatewayResourceTest {

  @InjectMocks
  private TodoGatewayResource resource;

  @Mock
  private TodoGatewayApplicationService service;

  @Mock
  private Response response;

  @BeforeEach
  void setUpResponse() {
    Mockito.when(response.hasEntity()).thenReturn(true);
    Mockito.when(response.getHeaders()).thenReturn(new MultivaluedHashMap<>());
  }

  @Test
  public void createTodo() {
    Mockito.when(response.getStatus()).thenReturn(Response.Status.CREATED.getStatusCode());
    Mockito.when(response.getEntity()).thenReturn("{\n"
                                                  + "  \"id\": 1000,\n"
                                                  + "  \"title\": \"clean fridge\",\n"
                                                  + "  \"description\": \"It's a mess\",\n"
                                                  + "  \"dueDate\": \"2018-01-01T12:34:56Z\"\n"
                                                  + "  \"done\": false\n"
                                                  + "}");

    Mockito.when(service.createTodo(Mockito.anyString())).thenReturn(response);
    Response response = resource.createTodo("{\n"
                                            + "  \"title\": \"clean fridge\",\n"
                                            + "  \"description\": \"It's a mess\",\n"
                                            + "  \"dueDate\": \"2018-01-01T12:34:56Z\"\n"
                                            + "}");
    assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
    assertThat(response.getEntity()).isEqualTo("{\n"
                                               + "  \"id\": 1000,\n"
                                               + "  \"title\": \"clean fridge\",\n"
                                               + "  \"description\": \"It's a mess\",\n"
                                               + "  \"dueDate\": \"2018-01-01T12:34:56Z\"\n"
                                               + "  \"done\": false\n"
                                               + "}");

    Mockito.verify(service).createTodo(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(service);
  }

  @Test
  public void deleteTodo() {
    Mockito.when(response.getStatus()).thenReturn(Response.Status.NO_CONTENT.getStatusCode());
    Mockito.when(response.getEntity()).thenReturn("");

    Mockito.when(service.deleteTodo(Mockito.anyLong())).thenReturn(response);
    Response response = resource.deleteTodo(1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
    assertThat(response.getEntity()).isEqualTo("");

    Mockito.verify(service).deleteTodo(Mockito.anyLong());
    Mockito.verifyNoMoreInteractions(service);
  }

  @Test
  public void getTodo() {
    Mockito.when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    Mockito.when(response.getEntity()).thenReturn("{\n"
                                                  + "  \"id\": 1000,\n"
                                                  + "  \"title\": \"clean fridge\",\n"
                                                  + "  \"description\": \"It's a mess\",\n"
                                                  + "  \"dueDate\": \"2018-01-01T12:34:56Z\"\n"
                                                  + "  \"done\": false\n"
                                                  + "}");

    Mockito.when(service.getTodo(Mockito.anyLong())).thenReturn(response);
    Response response = resource.getTodo(1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    assertThat(response.getEntity()).isEqualTo("{\n"
                                               + "  \"id\": 1000,\n"
                                               + "  \"title\": \"clean fridge\",\n"
                                               + "  \"description\": \"It's a mess\",\n"
                                               + "  \"dueDate\": \"2018-01-01T12:34:56Z\"\n"
                                               + "  \"done\": false\n"
                                               + "}");

    Mockito.verify(service).getTodo(Mockito.anyLong());
    Mockito.verifyNoMoreInteractions(service);
  }

  @Test
  public void getTodos() {
    Mockito.when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    Mockito.when(response.getEntity()).thenReturn("[{\n"
                                                  + "  \"id\": 1000,\n"
                                                  + "  \"title\": \"clean fridge\",\n"
                                                  + "  \"description\": \"It's a mess\",\n"
                                                  + "  \"dueDate\": \"2018-01-01T12:34:56Z\"\n"
                                                  + "  \"done\": false\n"
                                                  + "}]");

    Mockito.when(service.getTodos()).thenReturn(response);
    Response response = resource.getTodos();
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    assertThat(response.getEntity()).isEqualTo("[{\n"
                                               + "  \"id\": 1000,\n"
                                               + "  \"title\": \"clean fridge\",\n"
                                               + "  \"description\": \"It's a mess\",\n"
                                               + "  \"dueDate\": \"2018-01-01T12:34:56Z\"\n"
                                               + "  \"done\": false\n"
                                               + "}]");

    Mockito.verify(service).getTodos();
    Mockito.verifyNoMoreInteractions(service);
  }

  @Test
  public void updateTodo() {
    Mockito.when(response.getStatus()).thenReturn(Response.Status.NO_CONTENT.getStatusCode());
    Mockito.when(response.getEntity()).thenReturn("");

    Mockito.when(service.updateTodo(Mockito.anyLong(), Mockito.anyString())).thenReturn(response);
    Response response = resource.updateTodo(1L, "{\n"
                                                + "  \"title\": \"clean fridge\",\n"
                                                + "  \"description\": \"It's a mess\",\n"
                                                + "  \"dueDate\": \"2018-01-01T12:34:56Z\"\n"
                                                + "  \"done\": true\n"
                                                + "}");
    assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
    assertThat(response.getEntity()).isEqualTo("");

    Mockito.verify(service).updateTodo(Mockito.anyLong(), Mockito.anyString());
    Mockito.verifyNoMoreInteractions(service);
  }
}