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
package de.openknowledge.projects.todolist.gateway.infrastructure.domain.error;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * A DTO that represents an error with unique id.
 */
@Schema
public class ApplicationErrorDTO extends ErrorDTO implements ErrorDetails {

  @Schema(example = "55e6e986-767b-433a-9544-ddc8e25d67ab")
  private String uuid;

  @Schema(example = "2019-07-01T12:34:56.789+02:00")
  private OffsetDateTime timestamp;

  public ApplicationErrorDTO(final ErrorCode error, final String message) {
    super(error, message);
    this.uuid = UUID.randomUUID().toString();
    this.timestamp = OffsetDateTime.now();
  }

  @Override
  public String getUuid() {
    return uuid;
  }

  @Override
  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  @Override
  protected Object[] values() {
    return new Object[]{getCode(), getMessage(), uuid, timestamp};
  }

  @Override
  public String toString() {
    return "ApplicationErrorDTO{"
           + "code='" + getCode() + '\''
           + ", message='" + getCode() + '\''
           + ", uuid='" + uuid + '\''
           + ", timestamp=" + timestamp
           + "} " + super.toString();
  }
}
