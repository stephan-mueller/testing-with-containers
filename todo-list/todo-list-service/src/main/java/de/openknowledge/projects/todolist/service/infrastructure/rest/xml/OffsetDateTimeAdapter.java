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
package de.openknowledge.projects.todolist.service.infrastructure.rest.xml;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class OffsetDateTimeAdapter extends XmlAdapter<String, OffsetDateTime> {

  private DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

  @Override
  public OffsetDateTime unmarshal(final String value) {
    return value != null ? formatter.parse(value, OffsetDateTime::from) : null;
  }

  @Override
  public String marshal(final OffsetDateTime value) {
    return value != null ? formatter.format(value) : null;
  }
}