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
package de.openknowledge.projects.todolist.service.infrastructure.domain.builder;

import static org.apache.commons.lang3.Validate.notNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import de.openknowledge.projects.todolist.service.infrastructure.domain.entity.AbstractEntity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import javax.validation.constraints.NotNull;

/**
 * Test class for the builder superclass {@link AbstractBuilder}.
 */
public class BuilderTest {

  @BeforeAll
  public static void setUpBeforeClass() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @Test
  public void build() {
    TestEntity entity = TestEntity.newBuilder().withValue(1).build();
    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isNull();
    assertThat(entity.getValue()).isEqualTo(1);
  }

  @Test
  public void buildShouldFailForConstraintViolation() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> TestEntity.newBuilder().build())
        .withMessage("BeanValidation failed, reasons: [TestEntity.value must not be null]")
        .withNoCause();
  }

  private static class TestEntity extends AbstractEntity<Long> {

    private Long id;

    @NotNull
    private Integer value;

    private static TestBuilder newBuilder() {
      return new TestBuilder(new TestEntity());
    }

    @Override
    public Long getId() {
      return id;
    }

    public Integer getValue() {
      return value;
    }
  }

  private static class TestBuilder extends AbstractBuilder<TestEntity> {

    private TestBuilder(final TestEntity testEntity) {
      super(testEntity);
    }

    @Override
    protected TestEntity newInstance() {
      return new TestEntity();
    }

    @Override
    protected TestBuilder thisBuilder() {
      return this;
    }

    private TestBuilder withValue(final Integer value) {
      this.instance.value = notNull(value, "value must not be null");
      return this;
    }
  }
}
