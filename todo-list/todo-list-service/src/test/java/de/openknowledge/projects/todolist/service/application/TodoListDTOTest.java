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
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import de.openknowledge.projects.todolist.service.domain.TestTodos;
import de.openknowledge.projects.todolist.service.domain.Todo;

import org.junit.jupiter.api.Test;

/**
 * Test class for the DTO {@link TodoListDTO}.
 */
public class TodoListDTOTest {

  @Test
  public void instantiationShouldFailForMissingValue() {
    assertThatNullPointerException()
        .isThrownBy(() -> new TodoFullDTO(null))
        .withMessage("todo must not be null")
        .withNoCause();
  }

  @Test
  public void instantiationShouldSucceed() {
    Todo defaultTodo = TestTodos.newDefaultTodo();
    TodoListDTO todo = new TodoListDTO(defaultTodo);
    assertThat(todo.getId()).isNotNull();
    assertThat(todo.getTitle()).isEqualTo(defaultTodo.getTitle());
    assertThat(todo.getDueDate()).isEqualTo(defaultTodo.getDueDate());
    assertThat(todo.getDone()).isEqualTo(defaultTodo.getDone());
  }
}
