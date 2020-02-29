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

import java.io.Serializable;
import java.util.Arrays;

/**
 * A base class for value objects like <tt>City</tt>, <tt>PhoneNumber</tt> and so on. This class provides implementations for {@link
 * #equals(Object)} and {@link #hashCode()} using the return value of {@link #values()}. Subclasses have to implement this method and return
 * the actual values of this value object.
 * <p>
 * Concrete subclasses of this class may look like this:
 *
 * <pre>
 * public class City extends AbstractValueObject {
 *
 *     private ZipCode zip;
 *     private CityName name;
 *
 *     protected City() {
 *         // required for proxying
 *     }
 *
 *     public City(ZipCode zipCode, CityName cityName) {
 *         zip = notNull(zipCode, "zip code may not be null");
 *         name = notNull(cityName, "city name may not be null");
 *     }
 *
 *     public ZipCode getZipCode() {
 *         return zip;
 *     }
 *
 *     public CityName getName() {
 *         return name;
 *     }
 *
 *     protected Object[] values() {
 *         return new Object[] { zip, name };
 *     }
 * }
 * </pre>
 */
public abstract class AbstractValueObject implements Serializable {

  private transient Object[] values;

  public int hashCode() {
    return Arrays.hashCode(getValues());
  }

  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || !(object.getClass().isAssignableFrom(getClass())
                            || getClass().isAssignableFrom(object.getClass()))) {
      return false;
    }
    AbstractValueObject valueObject = (AbstractValueObject) object;
    return Arrays.equals(getValues(), valueObject.getValues());
  }

  /**
   * Returns all values of this value object Subclasses have to implement this method and return the actual values that make up this value
   * object.
   */
  protected abstract Object[] values();

  private Object[] getValues() {
    if (values == null) {
      values = values();
    }
    return values;
  }
}
