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

import static org.apache.commons.lang3.Validate.notNull;

import de.openknowledge.projects.todolist.service.infrastructure.domain.value.AbstractValueObject;
import de.openknowledge.projects.todolist.service.infrastructure.rest.xml.OffsetDateTimeAdapter;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A DTO that represents a list of errors.
 */
@Schema
@XmlRootElement
public class ApplicationErrorsDTO extends AbstractValueObject implements ErrorDetails {

  @Schema(example = "55e6e986-767b-433a-9544-ddc8e25d67ab")
  @XmlElement
  private String uuid;

  @Schema(example = "2019-07-01T12:34:56.789+02:00")
  @XmlElement
  @XmlJavaTypeAdapter(OffsetDateTimeAdapter.class)
  private OffsetDateTime timestamp;

  @XmlElement
  private List<ErrorDTO> errors;

  public ApplicationErrorsDTO() {
    super();
  }

  public ApplicationErrorsDTO(final Collection<? extends ErrorDTO> errors) {
    this();
    this.uuid = UUID.randomUUID().toString();
    this.timestamp = OffsetDateTime.now();
    this.errors = new LinkedList<>();
    this.errors.addAll(notNull(errors, "errors must not be null"));
  }

  @Override
  public String getUuid() {
    return uuid;
  }

  @Override
  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public List<ErrorDTO> getErrors() {
    return errors;
  }

  @Override
  protected Object[] values() {
    return new Object[]{uuid, timestamp, errors};
  }

  @Override
  public String toString() {
    return "ApplicationErrorsDTO{" +
           "uuid='" + uuid + '\'' +
           ", timestamp=" + timestamp +
           ", errors=" + errors.toString() +
           "} " + super.toString();
  }
}
