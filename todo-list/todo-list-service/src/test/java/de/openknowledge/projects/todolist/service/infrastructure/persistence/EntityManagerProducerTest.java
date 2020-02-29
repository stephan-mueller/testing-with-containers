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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Test class for the producer {@link EntityManagerProducer}.
 */
@ExtendWith(MockitoExtension.class)
public class EntityManagerProducerTest {

  @InjectMocks
  private EntityManagerProducer producer;

  @Mock
  private EntityManager entityManager;

  @Mock
  private EntityManagerFactory entityManagerFactory;

  @Test
  public void close() {
    producer.close(entityManager);
    Mockito.verify(entityManager).close();
  }

  @Test
  public void createEntityManager() {
    Mockito.when(producer.createEntityManager()).thenReturn(entityManager);
    assertThat(producer.createEntityManager()).isEqualTo(entityManager);
  }
}
