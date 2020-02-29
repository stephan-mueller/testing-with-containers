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

import de.openknowledge.projects.todolist.service.domain.Todo;

import org.mockito.Mockito;

import java.time.OffsetDateTime;

/**
 * Test data builder for the entity {@link Todo}.
 */
public class TestTodos {

  public static Todo newDefaultTodo() {
    Todo todo = Mockito.spy(Todo.newBuilder()
                                .withTitle("clean fridge")
                                .withDescription("It's a mess")
                                .withDueDate(OffsetDateTime.now().minusDays(1))
                                .build());

    Mockito.lenient().doReturn(1L).when(todo).getId();

    return todo;
  }
}
