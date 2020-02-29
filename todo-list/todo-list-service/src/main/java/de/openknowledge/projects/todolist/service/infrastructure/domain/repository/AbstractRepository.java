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
package de.openknowledge.projects.todolist.service.infrastructure.domain.repository;

import static org.apache.commons.lang3.Validate.notNull;

import de.openknowledge.projects.todolist.service.infrastructure.domain.entity.AbstractEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import javax.persistence.EntityManager;

/**
 * Abstract repository. Provides means for repositories.
 *
 * @param <E> entity.
 */
public abstract class AbstractRepository<E extends AbstractEntity> implements Serializable {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractRepository.class);

  protected EntityManager entityManager;

  private Class<E> clazz;

  protected AbstractRepository() {
    super();
  }

  protected AbstractRepository(final EntityManager entityManager) {
    this();
    ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
    Type type = genericSuperclass.getActualTypeArguments()[0];
    this.clazz = (Class<E>) type;
    this.entityManager = notNull(entityManager, "entityManager must not be null");
  }

  /**
   * CreateTodoValidationGroup the given entity.
   *
   * @param entity the entity to be created.
   * @return the persisted entity.
   */
  public E create(final E entity) {
    LOG.debug("CreateTodoValidationGroup entity {} ", clazz.getSimpleName());
    entityManager.persist(entity);
    return entity;
  }

  /**
   * Delete the given entity.
   *
   * @param entity the entity to be deleted.
   */
  public void delete(final E entity) {
    LOG.debug("Delete entity {} with id {}", clazz.getSimpleName(), entity.getId());
    E reference = entityManager.getReference(clazz, entity.getId());
    entityManager.remove(reference);
  }

  /**
   * Find entity by given identifier.
   *
   * @param identifier the given identifier.
   * @return the matching entity.
   */
  public Optional<E> find(final Long identifier) {
    LOG.debug("Locating entity {} with identifier {}", clazz.getSimpleName(), identifier);
    return Optional.ofNullable(entityManager.find(clazz, identifier));
  }

  /**
   * Update the given entity.
   *
   * @param entity the entity to be updated.
   * @return the updated entity.
   */
  public E update(final E entity) {
    LOG.debug("Update entity {} with id {}", clazz.getSimpleName(), entity.getId());
    return entityManager.merge(entity);
  }
}
