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

import de.openknowledge.projects.todolist.service.domain.TodoValidationErrorCodes;
import de.openknowledge.projects.todolist.service.infrastructure.domain.value.AbstractValueObject;
import de.openknowledge.projects.todolist.service.infrastructure.rest.xml.OffsetDateTimeAdapter;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Abstract to-do.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractTodo extends AbstractValueObject {

  @Schema(example = "clean fridge", required = true, minLength = 1, maxLength = 80)
  @XmlElement
  @NotNull(groups = {CreateTodoValidationGroup.class, UpdateTodoValidationGroup.class}, payload = TodoValidationErrorCodes.TitleIsNull.class)
  @Size(groups = {CreateTodoValidationGroup.class, UpdateTodoValidationGroup.class}, min = 1, max = 80, payload = TodoValidationErrorCodes.InvalidTitleSize.class)
  private String title;

  @Schema(example = "It's a mess", maxLength = 500)
  @XmlElement
  @Size(groups = {CreateTodoValidationGroup.class, UpdateTodoValidationGroup.class}, max = 500, payload = TodoValidationErrorCodes.DescriptionTooLong.class)
  private String description;

  @Schema(example = "2018-01-01T12:34:56Z", required = true, format = "date-time")
  @XmlElement
  @XmlJavaTypeAdapter(OffsetDateTimeAdapter.class)
  @NotNull(groups = {CreateTodoValidationGroup.class, UpdateTodoValidationGroup.class}, payload = TodoValidationErrorCodes.DueDateIsNull.class)
  private OffsetDateTime dueDate;

  @Schema(example = "false", required = true)
  @XmlElement
  @NotNull(groups = UpdateTodoValidationGroup.class, payload = TodoValidationErrorCodes.DoneIsNull.class)
  private Boolean done;

  public AbstractTodo() {
    super();
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public OffsetDateTime getDueDate() {
    return dueDate;
  }

  public void setDueDate(final OffsetDateTime dueDate) {
    this.dueDate = dueDate;
  }

  public Boolean getDone() {
    return done;
  }

  public void setDone(final Boolean done) {
    this.done = done;
  }

  @Override
  protected Object[] values() {
    return new Object[]{title, description, dueDate, done};
  }
}
