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

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A DTO that represents a modified {@link Todo}.
 */
@Schema
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ModifiedTodo extends AbstractTodo {

  public ModifiedTodo() {
    super();
  }

  @Override
  public Boolean getDone() {
    return super.getDone();
  }

  @Override
  public String toString() {
    return "ModifiedTodo{" +
           "title='" + getTitle() + '\'' +
           ", description='" + getDescription() + '\'' +
           ", dueDate='" + getDueDate() + '\'' +
           ", done=" + getDone() +
           '}';
  }
}
