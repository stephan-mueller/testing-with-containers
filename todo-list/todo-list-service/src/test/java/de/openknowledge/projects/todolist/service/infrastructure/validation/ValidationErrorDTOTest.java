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
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import de.openknowledge.projects.todolist.service.infrastructure.domain.entity.AbstractEntity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

/*
 * Test class for the DTO {@link ValidationErrorDTO}.
 */
public class ValidationErrorDTOTest {

  private Validator validator;

  @BeforeEach
  public void setUp() {
    Locale.setDefault(Locale.ENGLISH);

    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @Test
  public void instantiationShouldFailForMissingConstraintViolation() {
    assertThatNullPointerException()
        .isThrownBy(() -> new ValidationErrorDTO(null))
        .withMessage("constraintViolation must not be null")
        .withNoCause();
  }

  @Test
  public void instantiationShouldSucceedWithPayload() {
    TestEntityWithValidationErrorPayload entity = new TestEntityWithValidationErrorPayload();

    Set<ConstraintViolation<TestEntityWithValidationErrorPayload>> constraintViolations = validator.validate(entity);
    assertThat(constraintViolations).hasSize(1);

    ValidationErrorDTO validationError = new ValidationErrorDTO(constraintViolations.iterator().next());
    Assertions.assertThat(validationError.getCode()).isEqualTo("VALUE_IS_NULL");
    Assertions.assertThat(validationError.getMessage()).isEqualTo("value must not be null");
  }

  @Test
  public void instantiationShouldSucceedWithoutPayload() {
    TestEntityWithCustomMessageTemplate entity = new TestEntityWithCustomMessageTemplate();

    Set<ConstraintViolation<TestEntityWithCustomMessageTemplate>> constraintViolations = validator.validate(entity);
    assertThat(constraintViolations).hasSize(1);

    ValidationErrorDTO validationError = new ValidationErrorDTO(constraintViolations.iterator().next());
    Assertions.assertThat(validationError.getCode()).isEqualTo("UNKNOWN");
    Assertions.assertThat(validationError.getMessage()).isEqualTo("value must not be null");
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

  private static class TestEntityWithCustomMessageTemplate extends AbstractEntity<Long> {

    private Long id;

    @NotNull(message = "{VALUE_IS_NULL}")
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
