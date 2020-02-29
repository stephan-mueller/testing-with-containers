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
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import de.openknowledge.projects.todolist.service.domain.Todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

/**
 * Test class for the builder {@link Todo.TodoBuilder}.
 */
public class TodoBuilderTest {

  private Todo.TodoBuilder builder;

  @BeforeEach
  public void setUp() {
    builder = Todo.newBuilder();
  }

  @Test
  public void build() {
    Todo todo = builder.withTitle("clean fridge")
        .withDescription("It's a mess")
        .withDueDate(OffsetDateTime.now().minusDays(1))
        .build();

    assertThat(todo.getTitle()).isEqualTo("clean fridge");
    assertThat(todo.getDescription()).isEqualTo("It's a mess");
    assertThat(todo.getDueDate()).isBefore(OffsetDateTime.now());
    assertThat(todo.getDone()).isFalse();
  }

  @Test
  public void buildShouldFailForIllegalArgumentException() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> builder.build())
        .withMessageStartingWith("BeanValidation failed, reasons: [")
        .withNoCause();
  }

  @Test
  public void withDueDateShouldFailForMissingValue() {
    assertThatNullPointerException()
        .isThrownBy(() -> builder.withDueDate(null))
        .withMessage("dueDate must not be null")
        .withNoCause();
  }

  @Test
  public void withTitleShouldFailForMissingValue() {
    assertThatNullPointerException()
        .isThrownBy(() -> builder.withTitle(null))
        .withMessage("title must not be null")
        .withNoCause();
  }
}
