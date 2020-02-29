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

import static org.apache.commons.lang3.Validate.notNull;

import de.openknowledge.projects.todolist.service.infrastructure.domain.builder.AbstractBuilder;
import de.openknowledge.projects.todolist.service.infrastructure.domain.entity.AbstractEntity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * An entity that represents a to-do.
 */
@Entity
@Table(name = "tab_todo")
public class Todo extends AbstractEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tod_id", nullable = false)
  private Long id;

  @NotNull(payload = TodoValidationErrorCodes.TitleIsNull.class)
  @Size(min = 1, max = 80, payload = TodoValidationErrorCodes.InvalidTitleSize.class)
  @Column(name = "tod_title", nullable = false, length = 80)
  private String title;

  @Size(max = 500, payload = TodoValidationErrorCodes.DescriptionTooLong.class)
  @Column(name = "tod_description", length = 500)
  private String description;

  @NotNull(payload = TodoValidationErrorCodes.DueDateIsNull.class)
  @Column(name = "tod_duedate", nullable = false)
  private OffsetDateTime dueDate;

  @NotNull(payload = TodoValidationErrorCodes.DoneIsNull.class)
  @Column(name = "tod_done", nullable = false)
  private Boolean done;

  protected Todo() {
    super();
  }

  public static TodoBuilder newBuilder() {
    return new TodoBuilder();
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public OffsetDateTime getDueDate() {
    return dueDate;
  }

  public Boolean getDone() {
    return done;
  }

  public void updateTodo(final String title, final String description, final OffsetDateTime dueDate, final Boolean done) {
    this.title = notNull(title, "title must not be null");
    this.description = description;
    this.dueDate = notNull(dueDate, "dueDate must not be null");
    this.done = notNull(done, "done must not be null");
  }

  /**
   * Builder for the entity {@link Todo}.
   */
  public static class TodoBuilder extends AbstractBuilder<Todo> {

    public TodoBuilder() {
      this.instance.done = false;
    }

    public TodoBuilder setDone(final Boolean done) {
      this.instance.done = notNull(done, "done must not be null");
      return this;
    }

    public TodoBuilder withDescription(final String description) {
      this.instance.description = description;
      return this;
    }

    public TodoBuilder withDueDate(final OffsetDateTime dueDate) {
      this.instance.dueDate = notNull(dueDate, "dueDate must not be null");
      return this;
    }

    public TodoBuilder withTitle(final String title) {
      this.instance.title = notNull(title, "title must not be null");
      return this;
    }
  }
}
