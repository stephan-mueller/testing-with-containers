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
package de.openknowledge.projects.todolist.service.infrastructure.domain.entity;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

/**
 * Abstract entity. Provides means for entity classes.
 */
@MappedSuperclass
public abstract class AbstractEntity<I extends Long> implements Serializable {

  /**
   * @return The primary key, or ID of this entity
   */
  public abstract I getId();

  @Override
  public int hashCode() {
    I id = getId();
    return id == null ? super.hashCode() : id.hashCode();
  }

  @Override
  public final boolean equals(final Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || !(object.getClass().isAssignableFrom(getClass())
                            && getClass().isAssignableFrom(object.getClass()))) {
      return false;
    }

    AbstractEntity<I> entity = (AbstractEntity<I>) object;
    return getId() != null && getId().equals(entity.getId());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "#" + getId();
  }
}
