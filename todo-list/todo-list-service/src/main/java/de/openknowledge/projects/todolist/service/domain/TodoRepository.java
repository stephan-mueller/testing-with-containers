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

import de.openknowledge.projects.todolist.service.infrastructure.domain.repository.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 * A repository that provides access to {@link Todo} entities.
 */
@Repository
public class TodoRepository implements Serializable {

  private static final Logger LOG = LoggerFactory.getLogger(TodoRepository.class);

  @PersistenceContext
  private EntityManager entityManager;

  public TodoRepository() {
    super();
  }

  TodoRepository(final EntityManager entityManager) {
    this();
    this.entityManager = notNull(entityManager, "entityManager must not be null");
  }

  @Transactional
  public Todo create(final Todo todo) {
    LOG.debug("Create todo");
    entityManager.persist(todo);
    return todo;
  }

  @Transactional
  public void delete(final Todo todo) {
    LOG.debug("Delete todo with id {}", todo);
    Todo reference = entityManager.getReference(Todo.class, todo.getId());
    entityManager.remove(reference);
  }

  public Optional<Todo> find(final Long id)  {
    LOG.debug("Locating todo with id {}", id);
    return Optional.ofNullable(entityManager.find(Todo.class, id));
  }

  public List<Todo> findAll() {
    LOG.debug("Searching for todos");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Todo> cq = cb.createQuery(Todo.class);

    Root<Todo> root = cq.from(Todo.class);

    cq.select(root);

    TypedQuery<Todo> query = entityManager.createQuery(cq);
    List<Todo> results = query.getResultList();

    LOG.debug("Located {} todos", results.size());

    return results;
  }

  @Transactional
  public Todo update(final Todo todo) {
    LOG.debug("Update todo with id {}", todo.getId());
    return entityManager.merge(todo);
  }
}
