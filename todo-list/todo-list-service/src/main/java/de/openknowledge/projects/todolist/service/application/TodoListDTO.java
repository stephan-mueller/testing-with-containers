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

import static org.apache.commons.lang3.Validate.notNull;

import de.openknowledge.projects.todolist.service.domain.Todo;
import de.openknowledge.projects.todolist.service.infrastructure.domain.value.AbstractValueObject;
import de.openknowledge.projects.todolist.service.infrastructure.rest.xml.OffsetDateTimeAdapter;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A DTO that represents a {@link Todo} in a list.
 */
@Schema
@XmlRootElement
public class TodoListDTO extends AbstractValueObject {

  @Schema(example = "1000")
  @XmlElement
  private Long id;

  @Schema(example = "clean fridge")
  @XmlElement
  private String title;

  @Schema(example = "2018-01-01T12:34:56.000Z")
  @XmlElement
  @XmlJavaTypeAdapter(OffsetDateTimeAdapter.class)
  private OffsetDateTime dueDate;

  @Schema(example = "false")
  @XmlElement
  private Boolean done;

  public TodoListDTO() {
    super();
  }

  public TodoListDTO(final Todo todo) {
    this();
    notNull(todo, "todo must not be null");
    this.id = todo.getId();
    this.title = todo.getTitle();
    this.dueDate = todo.getDueDate();
    this.done = todo.getDone();
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public OffsetDateTime getDueDate() {
    return dueDate;
  }

  public Boolean getDone() {
    return done;
  }

  @Override
  protected Object[] values() {
    return new Object[]{id, title, dueDate, done};
  }

  @Override
  public String toString() {
    return "TodoListDTO{" +
           "id=" + id +
           ", title='" + title + '\'' +
           ", dueDate=" + dueDate +
           ", done=" + done +
           '}';
  }
}
