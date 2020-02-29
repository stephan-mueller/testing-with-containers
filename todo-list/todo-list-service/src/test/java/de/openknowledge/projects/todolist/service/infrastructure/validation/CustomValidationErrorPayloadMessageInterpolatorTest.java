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
package de.openknowledge.projects.todolist.service.infrastructure.validation;

import static org.assertj.core.api.Assertions.assertThat;

import de.openknowledge.projects.todolist.service.infrastructure.domain.entity.AbstractEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

/**
 * Test class for the Bean Validation message interpolator {@link CustomValidationErrorPayloadMessageInterpolator}
 */
public class CustomValidationErrorPayloadMessageInterpolatorTest {

  private Validator validator;

  @BeforeEach
  public void setUp() {
    Locale.setDefault(Locale.ENGLISH);

    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @Test
  public void customMessageShouldBeInterpolated() {
    TestEntityWithValidationErrorPayloadAndCustomMessage entity = new TestEntityWithValidationErrorPayloadAndCustomMessage();

    Set<ConstraintViolation<TestEntityWithValidationErrorPayloadAndCustomMessage>> constraintViolations = validator.validate(entity);
    assertThat(constraintViolations).hasSize(1);

    ConstraintViolation<TestEntityWithValidationErrorPayloadAndCustomMessage> constraintViolation = constraintViolations.iterator().next();
    assertThat(constraintViolation.getMessage()).isEqualTo("VALUE_IS_NULL");
    assertThat(constraintViolation.getMessageTemplate()).isEqualTo("VALUE_IS_NULL");
  }

  @Test
  public void customMessageTemplateShouldBeInterpolated() {
    TestEntityWithValidationErrorPayloadAndCustomMessageTemplate entity = new TestEntityWithValidationErrorPayloadAndCustomMessageTemplate();

    Set<ConstraintViolation<TestEntityWithValidationErrorPayloadAndCustomMessageTemplate>> constraintViolations = validator.validate(entity);
    assertThat(constraintViolations).hasSize(1);

    ConstraintViolation<TestEntityWithValidationErrorPayloadAndCustomMessageTemplate> constraintViolation = constraintViolations.iterator().next();
    assertThat(constraintViolation.getMessage()).isEqualTo("value must not be null");
    assertThat(constraintViolation.getMessageTemplate()).isEqualTo("{VALUE_IS_NULL}");
  }

  @Test
  public void errorCodeShouldBeInterpolated() {
    TestEntityWithValidationErrorPayload entity = new TestEntityWithValidationErrorPayload();

    Set<ConstraintViolation<TestEntityWithValidationErrorPayload>> constraintViolations = validator.validate(entity);
    assertThat(constraintViolations).hasSize(1);

    ConstraintViolation<TestEntityWithValidationErrorPayload> constraintViolation = constraintViolations.iterator().next();
    assertThat(constraintViolation.getMessage()).isEqualTo("value must not be null");
    assertThat(constraintViolation.getMessageTemplate()).isEqualTo("{javax.validation.constraints.NotNull.message}");
  }

  @Test
  public void defaultMessageShouldBeInterpolated() {
    TestEntityWithoutValidationErrorPayload entity = new TestEntityWithoutValidationErrorPayload();

    Set<ConstraintViolation<TestEntityWithoutValidationErrorPayload>> constraintViolations = validator.validate(entity);
    assertThat(constraintViolations).hasSize(1);

    ConstraintViolation<TestEntityWithoutValidationErrorPayload> constraintViolation = constraintViolations.iterator().next();
    assertThat(constraintViolation.getMessage()).isEqualTo("must not be null");
    assertThat(constraintViolation.getMessageTemplate()).isEqualTo("{javax.validation.constraints.NotNull.message}");
  }

  private static class TestEntityWithValidationErrorPayloadAndCustomMessage extends AbstractEntity<Long> {

    private Long id;

    @NotNull(message = "VALUE_IS_NULL", payload = TestValueIsNull.class)
    private String value;

    @Override
    public Long getId() {
      return id;
    }

    public String getValue() {
      return value;
    }
  }

  private static class TestEntityWithValidationErrorPayloadAndCustomMessageTemplate extends AbstractEntity<Long> {

    private Long id;

    @NotNull(message = "{VALUE_IS_NULL}", payload = TestValueIsNull.class)
    private String value;

    @Override
    public Long getId() {
      return id;
    }

    public String getValue() {
      return value;
    }
  }

  private static class TestEntityWithValidationErrorPayload extends AbstractEntity<Long> {

    private Long id;

    @NotNull(payload = TestValueIsNull.class)
    private String value;

    @Override
    public Long getId() {
      return id;
    }

    public String getValue() {
      return value;
    }
  }

  private static class TestEntityWithoutValidationErrorPayload extends AbstractEntity<Long> {

    private Long id;

    @NotNull
    private String value;

    @Override
    public Long getId() {
      return id;
    }

    public String getValue() {
      return value;
    }
  }
}