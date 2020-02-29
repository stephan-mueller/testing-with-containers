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
package de.openknowledge.projects.todolist.service.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import de.openknowledge.projects.todolist.service.infrastructure.domain.builder.AbstractBuilder;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

/**
 * Test class for the entity {@link Todo}.
 */
public class TodoTest {

  @Test
  public void newBuilder() {
    assertThat(Todo.newBuilder()).isInstanceOf(AbstractBuilder.class);
  }

  @Test
  public void updateTodo() {
    Todo todo = TestTodos.newDefaultTodo();
    todo.updateTodo(todo.getTitle(), null, todo.getDueDate(), true);

    assertThat(todo.getTitle()).isEqualTo("clean fridge");
    assertThat(todo.getDescription()).isNullOrEmpty();
    assertThat(todo.getDueDate()).isBefore(OffsetDateTime.now());
    assertThat(todo.getDone()).isTrue();
  }

  @Test
  public void updateTodoShouldFailForMissingDueDate() {
    Todo todo = TestTodos.newDefaultTodo();

    assertThatNullPointerException()
        .isThrownBy(() -> todo.updateTodo(todo.getTitle(), todo.getDescription(), null, todo.getDone()))
        .withMessage("dueDate must not be null")
        .withNoCause();
  }

  @Test
  public void updateTodoShouldFailForMissingTitle() {
    Todo todo = TestTodos.newDefaultTodo();

    assertThatNullPointerException()
        .isThrownBy(() -> todo.updateTodo(null, todo.getDescription(), todo.getDueDate(), todo.getDone()))
        .withMessage("title must not be null")
        .withNoCause();
  }
}
