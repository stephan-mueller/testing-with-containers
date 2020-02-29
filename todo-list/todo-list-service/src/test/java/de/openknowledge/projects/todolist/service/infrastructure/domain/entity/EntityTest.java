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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import javax.persistence.MappedSuperclass;

/**
 * Test class for the entity superclass {@link AbstractEntity}.
 */
public class EntityTest {

  @Test
  public void getId() {
    AbstractEntity<Long> entity = new TestEntityA();
    assertThat(entity.getId()).isNull();

    ((TestEntityA) entity).setId(1L);
    assertThat(entity.getId()).isEqualTo(1L);
  }

  @Test
  public void hasAnnotationMappedSuperclass() {
    assertThat(AbstractEntity.class.getAnnotation(MappedSuperclass.class)).isNotNull();
  }

  @Test
  public void testEquals() {
    AbstractEntity<Long> entityA = new TestEntityA();
    ((TestEntityA) entityA).setId(1L);
    assertThat(entityA.equals(entityA)).isTrue();
    assertThat(Objects.equals(entityA, null)).isFalse();

    AbstractEntity<Long> entityAChild = new TestEntityAChild();
    assertThat(entityA.equals(entityAChild)).isFalse();
    assertThat(entityAChild.equals(entityA)).isFalse();

    ((TestEntityAChild) entityAChild).setId(1L);
    assertThat(entityA.equals(entityAChild)).isFalse();
    assertThat(entityAChild.equals(entityA)).isFalse();

    AbstractEntity<Long> entityB = new TestEntityB();
    assertThat(entityA.equals(entityB)).isFalse();
    assertThat(entityB.equals(entityA)).isFalse();

    ((TestEntityB) entityB).setId(1L);
    assertThat(entityA.equals(entityB)).isFalse();
    assertThat(entityB.equals(entityA)).isFalse();

    AbstractEntity<Long> entityC = new TestEntityA();
    assertThat(entityA.equals(entityC)).isFalse();
    assertThat(entityC.equals(entityA)).isFalse();

    ((TestEntityA) entityC).setId(1L);
    assertThat(entityA.equals(entityC)).isTrue();
    assertThat(entityC.equals(entityA)).isTrue();
  }

  @Test
  public void testHashCode() {
    AbstractEntity<Long> entityA = new TestEntityA();
    int hashCodeBeforeIdWasSet = entityA.hashCode();
    ((TestEntityA) entityA).setId(1L);
    assertThat(entityA.hashCode()).isNotEqualTo(hashCodeBeforeIdWasSet);

    entityA = new TestEntityA();
    AbstractEntity<Long> entityB = new TestEntityB();
    ((TestEntityA) entityA).setId(1L);
    ((TestEntityB) entityB).setId(1L);
    assertThat(entityA.hashCode()).isEqualTo(entityB.hashCode());

    entityA = new TestEntityA();
    entityB = new TestEntityB();
    ((TestEntityA) entityA).setId(1L);
    ((TestEntityB) entityB).setId(2L);
    assertThat(entityA.hashCode()).isNotEqualTo(entityB.hashCode());
  }

  @Test
  public void testToString() {
    AbstractEntity<Long> entity = new TestEntityA();
    assertThat(entity.toString()).isEqualTo(entity.getClass().getSimpleName() + "#null");

    ((TestEntityA) entity).setId(1L);
    assertThat(entity.toString()).isEqualTo(entity.getClass().getSimpleName() + "#1");
  }

  private static class TestEntityA extends AbstractEntity<Long> {

    private Long id;

    @Override
    public Long getId() {
      return id;
    }

    void setId(final Long id) {
      this.id = id;
    }
  }

  private static class TestEntityB extends AbstractEntity<Long> {

    private Long id;

    @Override
    public Long getId() {
      return id;
    }

    void setId(final Long id) {
      this.id = id;
    }
  }

  private static class TestEntityAChild extends TestEntityA {

  }
}
