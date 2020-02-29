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

import static javax.persistence.AccessType.FIELD;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * A base class for simple value objects like <tt>ZipCode</tt>, <tt>CityName</tt> and so on.
 *
 * Concrete subclasses of this class may look like this:
 *
 * <pre>
 * public class EmailAddress extends AbstractSimpleValueObject&lt;String> {
 *
 *   private static final String PATTERN = ...
 *
 *   protected EmailAddress() {
 *     // required for proxying
 *   }
 *
 *   public EmailAddress(final String emailAddress) {
 *     super(emailAddress);
 *   }
 *
 *   protected String validateAndNormalize(final String value) {
 *     matchesPattern(value, PATTERN, "%1 is not a valid email address", value);
 *   }
 * }
 * </pre>
 */
@MappedSuperclass
@Access(FIELD)
public abstract class AbstractSimpleValueObject<V extends Comparable<? super V>>
    implements Serializable, Comparable<AbstractSimpleValueObject<V>> {

  public static final String DEFAULT_COLUMN_NAME = "value";

  @Column(name = DEFAULT_COLUMN_NAME)
  private V value;

  protected AbstractSimpleValueObject() {
    // required for proxying
  }

  public AbstractSimpleValueObject(final V initialValue) {
    value = validateAndNormalize(initialValue);
  }

  /**
   * Validates and normalizes the constructor parameter of this simple value object. This implementation checks the value to be non-null.
   * Subclasses may override this method to alter validation and normalization.
   *
   * @param v The constructor value.
   * @return The validated and normalized value.
   */
  protected V validateAndNormalize(final V v) {
    return notNull(v, "value must not be null");
  }

  /**
   * Returns the simple value of this value object. May be overridden by
   *
   * @return the simple value
   */
  public V getValue() {
    return value;
  }

  public String toString() {
    return getValue().toString();
  }

  public int hashCode() {
    return getValue().hashCode();
  }

  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || !(object.getClass().isAssignableFrom(getClass())
                            || getClass().isAssignableFrom(object.getClass()))) {
      return false;
    }
    AbstractSimpleValueObject<V> valueObject = (AbstractSimpleValueObject<V>) object;
    return valueObject.getValue().equals(getValue());
  }

  @Override
  public int compareTo(final AbstractSimpleValueObject<V> object) {
    return getValue().compareTo(object.getValue());
  }
}
