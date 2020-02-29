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
package de.openknowledge.projects.todolist.service.infrastructure.persistence;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * CDI producer that provides the {@link EntityManager}.
 */
@Dependent
public class EntityManagerProducer {

  @PersistenceUnit(unitName = "default")
  private EntityManagerFactory entityManagerFactory;

  @Produces
  @RequestScoped
  public EntityManager createEntityManager() {
    return entityManagerFactory.createEntityManager();
  }

  public void close(@Disposes final EntityManager entityManager) {
    entityManager.close();
  }
}
