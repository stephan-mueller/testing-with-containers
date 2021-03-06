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

import de.openknowledge.projects.todolist.service.domain.Todo;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.json.bind.annotation.JsonbNillable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A DTO that represents a full {@link Todo}.
 */
@Schema
@JsonbNillable
@XmlRootElement
public class TodoFullDTO extends TodoListDTO {

  @Schema(example = "It's a mess")
  @XmlElement
  private String description;

  public TodoFullDTO() {
    super();
  }

  public TodoFullDTO(final Todo todo) {
    super(todo);
    this.description = todo.getDescription();
  }

  public String getDescription() {
    return description;
  }

  @Override
  protected Object[] values() {
    return new Object[]{getId(), getTitle(), description, getDueDate(), getDone()};
  }

  @Override
  public String toString() {
    return "TodoFullDTO{" +
           "id=" + getId() +
           ", title='" + getTitle() + '\'' +
           ", description='" + description + '\'' +
           ", dueDate=" + getDueDate() +
           ", done=" + getDone() +
           '}';
  }
}
