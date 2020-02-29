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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import de.openknowledge.projects.todolist.service.domain.TestTodos;
import de.openknowledge.projects.todolist.service.domain.Todo;
import de.openknowledge.projects.todolist.service.domain.TodoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import javax.ws.rs.core.Response;

/**
 * Test class for the resource {@link TodoResource}.
 */
@ExtendWith(MockitoExtension.class)
public class TodoResourceTest {

  @InjectMocks
  private TodoResource resource;

  @Mock
  private TodoRepository repository;

  @Test
  public void createTodoShouldReturn201() {
    Todo defaultTodo = TestTodos.newDefaultTodo();

    NewTodo newTodo = new NewTodo();
    newTodo.setTitle(defaultTodo.getTitle());
    newTodo.setDescription(defaultTodo.getDescription());
    newTodo.setDueDate(defaultTodo.getDueDate());
    newTodo.setDone(defaultTodo.getDone());

    Mockito.doReturn(defaultTodo).when(repository).create(any(Todo.class));

    Response response = resource.createTodo(newTodo);
    assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

    ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
    verify(repository).create(captor.capture());
    verifyNoMoreInteractions(repository);

    Todo createdTodo = captor.getValue();
    assertThat(createdTodo.getTitle()).isEqualTo(defaultTodo.getTitle());
    assertThat(createdTodo.getDescription()).isEqualTo(defaultTodo.getDescription());
    assertThat(createdTodo.getDueDate()).isEqualTo(defaultTodo.getDueDate());
    assertThat(createdTodo.getDone()).isEqualTo(defaultTodo.getDone());
  }

  @Test
  public void deleteTodoShouldReturn204() {
    Todo defaultTodo = TestTodos.newDefaultTodo();
    Mockito.doNothing().when(repository).delete(any(Todo.class));
    Mockito.doReturn(Optional.of(defaultTodo)).when(repository).find(anyLong());

    Response response = resource.deleteTodo(1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

    verify(repository).delete(any(Todo.class));
    verify(repository).find(anyLong());
    verifyNoMoreInteractions(repository);
  }

  @Test
  public void deleteTodoShouldReturn404ForUnknownTodo() {
    Mockito.doReturn(Optional.empty()).when(repository).find(anyLong());

    Response response = resource.deleteTodo(-1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

    verify(repository).find(anyLong());
    verifyNoMoreInteractions(repository);
  }

  @Test
  public void getTodoShouldReturn200() {
    Todo defaultTodo = TestTodos.newDefaultTodo();
    Mockito.doReturn(Optional.of(defaultTodo)).when(repository).find(anyLong());

    Response response = resource.getTodo(1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

    verify(repository).find(anyLong());
    verifyNoMoreInteractions(repository);
  }

  @Test
  public void getTodoShouldReturn404ForEntityNotFoundException() {
    Mockito.doReturn(Optional.empty()).when(repository).find(anyLong());

    Response response = resource.getTodo(-1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

    verify(repository).find(anyLong());
    verifyNoMoreInteractions(repository);
  }

  @Test
  public void getTodosShouldReturn200() {
    Todo defaultTodo = TestTodos.newDefaultTodo();
    Mockito.doReturn(Collections.singletonList(defaultTodo)).when(repository).findAll();

    Response response = resource.getTodos();
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

    verify(repository).findAll();
    verifyNoMoreInteractions(repository);
  }

  @Test
  public void getTodosShouldReturn204ForEmptyList() {
    Mockito.doReturn(Collections.emptyList()).when(repository).findAll();

    Response response = resource.getTodos();
    assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

    verify(repository).findAll();
    verifyNoMoreInteractions(repository);
  }

  @Test
  public void updateTodoShouldReturn204() {
    Todo defaultTodo = TestTodos.newDefaultTodo();

    ModifiedTodo modifiedTodo = new ModifiedTodo();
    modifiedTodo.setTitle(defaultTodo.getTitle());
    modifiedTodo.setDescription(null);
    modifiedTodo.setDueDate(defaultTodo.getDueDate());
    modifiedTodo.setDone(true);

    Mockito.doReturn(Optional.of(defaultTodo)).when(repository).find(anyLong());
    Mockito.doReturn(defaultTodo).when(repository).update(any(Todo.class));

    Response response = resource.updateTodo(1L, modifiedTodo);
    assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

    ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
    verify(repository).find(anyLong());
    verify(repository).update(captor.capture());
    verifyNoMoreInteractions(repository);

    Todo updatedTodo = captor.getValue();
    assertThat(updatedTodo.getTitle()).isEqualTo(defaultTodo.getTitle());
    assertThat(updatedTodo.getDescription()).isNullOrEmpty();
    assertThat(updatedTodo.getDueDate()).isEqualTo(defaultTodo.getDueDate());
    assertThat(updatedTodo.getDone()).isTrue();
  }

  @Test
  public void updateTodoShouldReturn404ForUnknownTodo() {
    Todo defaultTodo = TestTodos.newDefaultTodo();

    ModifiedTodo modifiedTodo = new ModifiedTodo();
    modifiedTodo.setTitle(defaultTodo.getTitle());
    modifiedTodo.setDescription(null);
    modifiedTodo.setDueDate(defaultTodo.getDueDate());
    modifiedTodo.setDone(true);

    Mockito.doReturn(Optional.empty()).when(repository).find(anyLong());

    Response response = resource.updateTodo(-1L, modifiedTodo);
    assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

    verify(repository).find(anyLong());
    verifyNoMoreInteractions(repository);
  }
}
