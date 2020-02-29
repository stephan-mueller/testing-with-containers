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

import static org.apache.commons.lang3.Validate.isTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * A base class for builders, that handles creation an building of objects and supports deep subclassing hierarchies. Subclasses may access
 * the protected attribute {@link #instance} to construct the object.
 *
 * <p>
 * Example for a builder with deep subclassing hierarchy:
 * </p>
 *
 * <pre>
 *
 * public abstract class MyAbstractBuilder<M extends MyObject, B extends MyAbstractBuilder>
 *         extends AbstractBuilder<M, B> {
 *
 *     public B withMyAttribute(MyValue myValue) {
 *         instance.myValue = myValue;
 *         return thisBuilder();
 *     }
 *
 *     protected void validate() {
 *         notNull(instance.myValue, "my value may not be null");
 *     }
 * }
 *
 * public class MyBuilder extends MyAbstractBuilder<MyObject, MyBuilder> {
 *
 *     protected MyObject newInstance() {
 *         return new MyObject();
 *     }
 *
 *     protected MyBuilder thisBuilder() {
 *         return this;
 *     }
 * }
 *
 * public class MyBuilderSubclass extends MyAbstractBuilder<MyObjectSubclass, MyBuilderSubclass> {
 *
 *     public MyBuilderSubclass withSubclassAttribute(SubclassAttribute attribute) {
 *         instance.attribute = attribute;
 *         return this;
 *     }
 *
 *     protected MyObjectSubclass newInstance() {
 *         return new MyObjectSubclass();
 *     }
 *
 *     protected MyBuilderSubclass thisBuilder() {
 *         return this;
 *     }
 *
 *     protected void validate() {
 *         super.validate();
 *         notNull(instance.attribute, "attribute may not be null");
 *     }
 * }
 * </pre>
 *
 * @param <E> the type of the object to be constructed
 */
public abstract class AbstractBuilder<E> implements Builder<E> {

  /**
   * The instance to be constructed
   */
  protected E instance;

  /**
   * Constructs the builder using {@link #newInstance()} to create an empty instance of the type to be constructed
   */
  protected AbstractBuilder() {
    instance = newInstance();
  }

  /**
   * Constructs the builder using the parameter to initialize the instance of the type to be constructed
   */
  protected AbstractBuilder(final E v) {
    instance = v;
  }

  public E build() {
    validate();
    E result = instance;
    instance = newInstance();
    return result;
  }

  protected AbstractBuilder<E> thisBuilder() {
    return this;
  }

  protected E newInstance() {
    return ObjectBuilder.<E>fromGenericType(getClass(), AbstractBuilder.class).build();
  }


  /**
   * Validate instance with bean validation before it is released to the public.
   *
   * @throws IllegalArgumentException Thrown to indicate that validation fails.
   */
  protected void validate() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<E>> constraintViolations = validator.validate(instance);

    String violations = ConstraintViolationFormat.format(constraintViolations);
    isTrue(constraintViolations.isEmpty(), "BeanValidation failed, reasons: [%s]", violations);
  }
}
