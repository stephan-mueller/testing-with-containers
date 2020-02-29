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

import de.openknowledge.projects.todolist.gateway.application.TodoApplicationService;
import de.openknowledge.projects.todolist.gateway.application.TodoListServiceClient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;

/**
 * Test class for the service {@link TodoApplicationService}.
 */
@ExtendWith(MockitoExtension.class)
public class TodoApplicationServiceTest {

  @InjectMocks
  private TodoApplicationService service;

  @Mock
  private TodoListServiceClient client;

  @Mock
  private Response response;

  @Test
  public void createTodo() {
    Mockito.when(response.getStatus()).thenReturn(Response.Status.CREATED.getStatusCode());

    Mockito.when(client.createTodo(Mockito.anyString())).thenReturn(response);
    Response response = service.createTodo("{\n"
                                           + "  \"title\": \"clean fridge\",\n"
                                           + "  \"description\": \"It's a mess\",\n"
                                           + "  \"dueDate\": \"2018-01-01T12:34:56Z\"\n"
                                           + "}");
    assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

    Mockito.verify(client).createTodo(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(client);
  }

  @Test
  public void deleteTodo() {
    Mockito.when(response.getStatus()).thenReturn(Response.Status.NO_CONTENT.getStatusCode());

    Mockito.when(client.deleteTodo(Mockito.anyLong())).thenReturn(response);
    Response response = service.deleteTodo(1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

    Mockito.verify(client).deleteTodo(Mockito.anyLong());
    Mockito.verifyNoMoreInteractions(client);
  }

  @Test
  public void getTodo() {
    Mockito.when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());

    Mockito.when(client.getTodo(Mockito.anyLong())).thenReturn(response);
    Response response = service.getTodo(1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

    Mockito.verify(client).getTodo(Mockito.anyLong());
    Mockito.verifyNoMoreInteractions(client);
  }

  @Test
  public void getTodos() {
    Mockito.when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());

    Mockito.when(client.getTodos()).thenReturn(response);
    Response response = service.getTodos();
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

    Mockito.verify(client).getTodos();
    Mockito.verifyNoMoreInteractions(client);
  }

  @Test
  public void updateTodo() {
    Mockito.when(response.getStatus()).thenReturn(Response.Status.NO_CONTENT.getStatusCode());

    Mockito.when(client.updateTodo(Mockito.anyLong(), Mockito.anyString())).thenReturn(response);
    Response response = service.updateTodo(1L, "{\n"
                                               + "  \"title\": \"clean fridge\",\n"
                                               + "  \"description\": \"It's a mess\",\n"
                                               + "  \"dueDate\": \"2018-01-01T12:34:56Z\"\n"
                                               + "  \"done\": true\n"
                                               + "}");
    assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

    Mockito.verify(client).updateTodo(Mockito.anyLong(), Mockito.anyString());
    Mockito.verifyNoMoreInteractions(client);
  }
}