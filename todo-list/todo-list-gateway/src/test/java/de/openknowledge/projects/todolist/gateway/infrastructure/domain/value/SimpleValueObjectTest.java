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
package de.openknowledge.projects.todolist.gateway.infrastructure.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.MappedSuperclass;

/**
 * Test class for the value object superclass {@link AbstractSimpleValueObject}.
 */
public class SimpleValueObjectTest {

  @Test
  public void compareTo() {
    TestStringValueObjectA valueObjectA = new TestStringValueObjectA("a");
    assertThat(valueObjectA.compareTo(valueObjectA)).isEqualTo(0);

    TestStringValueObjectB valueObjectB = new TestStringValueObjectB("a");
    assertThat(valueObjectA.compareTo(valueObjectB)).isEqualTo(0);

    TestStringValueObjectA valueObjectC = new TestStringValueObjectA("b");
    assertThat(valueObjectA.compareTo(valueObjectC)).isEqualTo(-1);
    assertThat(valueObjectC.compareTo(valueObjectA)).isEqualTo(1);
  }

  @Test
  public void compareToShouldFailForMissingValue() {
    assertThatNullPointerException()
        .isThrownBy(() -> new TestStringValueObjectA("a").compareTo(null))
        .withNoCause();
  }

  @Test
  public void hasAnnotationAccess() {
    assertThat(AbstractSimpleValueObject.class.getAnnotation(Access.class)).isNotNull();
  }

  @Test
  public void hasAnnotationMappedSuperclass() {
    assertThat(AbstractSimpleValueObject.class.getAnnotation(MappedSuperclass.class)).isNotNull();
  }

  @Test
  public void instantiationShouldFailForMissingInput() {
    assertThatNullPointerException()
        .isThrownBy(() -> new TestStringValueObjectA(null))
        .withMessage("value must not be null")
        .withNoCause();
  }

  @Test
  public void instantiationSucceeded() {
    new TestStringValueObjectA("Abc");
  }

  @Test
  public void testEquals() {
    AbstractSimpleValueObject<String> valueObjectA = new TestStringValueObjectA("a");

    assertThat(valueObjectA.equals(valueObjectA)).isTrue();
    assertThat(Objects.equals(valueObjectA, null)).isFalse();

    AbstractSimpleValueObject<String> valueObjectAChild = new TestStringValueObjectAChild("a");
    AbstractSimpleValueObject<String> valueObjectB = new TestStringValueObjectB("a");
    AbstractSimpleValueObject<String> valueObjectC = new TestStringValueObjectA("a");
    assertThat(valueObjectA.equals(valueObjectAChild)).isTrue();
    assertThat(valueObjectA.equals(valueObjectB)).isFalse();
    assertThat(valueObjectA.equals(valueObjectC)).isTrue();
  }

  @Test
  public void testHashCode() {
    TestStringValueObjectA valueObjectA = new TestStringValueObjectA("a");
    assertThat(valueObjectA.hashCode()).isEqualTo(valueObjectA.getValue().hashCode());
  }

  @Test
  public void testToString() {
    TestStringValueObjectA valueObjectA = new TestStringValueObjectA("a");
    assertThat(valueObjectA.toString()).isEqualTo("a");
  }

  private static class TestStringValueObjectA extends AbstractSimpleValueObject<String> {

    protected TestStringValueObjectA(final String value) {
      super(value);
    }
  }

  private static final class TestStringValueObjectAChild extends TestStringValueObjectA {

    private TestStringValueObjectAChild(final String value) {
      super(value);
    }
  }

  private static final class TestStringValueObjectB extends AbstractSimpleValueObject<String> {

    private TestStringValueObjectB(final String value) {
      super(value);
    }
  }
}
