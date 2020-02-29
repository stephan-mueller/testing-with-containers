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
package de.openknowledge.projects.todolist.service.infrastructure.domain.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

/**
 * Test class for the DTO {@link ApplicationErrorsDTO}.
 */
public class ApplicationErrorsDTOTest {

  @Test
  public void instantiationShouldFailForErrors() {
    assertThatNullPointerException()
        .isThrownBy(() -> new ApplicationErrorsDTO(null))
        .withMessage("errors must not be null")
        .withNoCause();
  }

  @Test
  public void instantiationShouldSucceed() {
    Set<ApplicationErrorDTO> errors = Collections.singleton(new ApplicationErrorDTO(() -> "UNKNOWN", "An unknown error occurred"));
    ApplicationErrorsDTO applicationError = new ApplicationErrorsDTO(errors);
    assertThat(applicationError.getUuid()).isNotNull();
    assertThat(applicationError.getTimestamp()).isNotNull();
    assertThat(applicationError.getErrors()).hasSize(1);
  }
}
