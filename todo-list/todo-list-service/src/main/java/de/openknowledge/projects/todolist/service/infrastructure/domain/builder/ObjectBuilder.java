/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package de.openknowledge.projects.todolist.service.infrastructure.domain.builder;

import static org.apache.commons.lang3.Validate.notNull;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

/**
 * A class to build objects from generic type parameters.
 */
public final class ObjectBuilder<T> implements Builder<T> {

  private static final Logger LOG = LoggerFactory.getLogger(ObjectBuilder.class);

  private final Class<T> type;

  private final List<Object> parameters = new ArrayList<>();

  private Validator validator;

  private ObjectBuilder(final Class<T> objectType) {
    type = notNull(objectType);
  }

  private ObjectBuilder(final Class<T> objectType, final Object paramter) {
    this(objectType);
    parameters.add(paramter);
  }

  public static <V> ObjectBuilder<V> fromGenericType(final Class<?> subclass, final Class<?> superclass) {
    return fromGenericType(subclass, superclass, 0);
  }

  public static <V> ObjectBuilder<V> fromGenericType(final Class<?> subclass, final Class<?> superclass, final int
      parameterIndex) {
    Class<?> directSubclass = subclass;
    while (directSubclass.getSuperclass() != superclass) {
      directSubclass = directSubclass.getSuperclass();
    }
    Type genericSuperclass = directSubclass.getGenericSuperclass();
    if (!(genericSuperclass instanceof ParameterizedType)) {
      throw new IllegalStateException("Generic type argument missing for superclass " + superclass
          .getSimpleName());
    }
    ParameterizedType parameterizedSuperclass = (ParameterizedType) genericSuperclass;
    Type valueType = parameterizedSuperclass.getActualTypeArguments()[parameterIndex];
    if (valueType instanceof TypeVariable) {
      TypeVariable<?> variable = (TypeVariable<?>) valueType;
      TypeVariable<?>[] typeParameters = directSubclass.getTypeParameters();
      for (int i = 0; i < typeParameters.length; i++) {
        if (typeParameters[i].getName().equals(variable.getName())) {
          return fromGenericType(subclass, directSubclass, i);
        }
      }
      throw new IllegalStateException(variable + " cannot be resolved");
    }
    return new ObjectBuilder((Class<V>) valueType);
  }

  public static <V> ObjectBuilder<V> forType(final Class<V> type) {
    return new ObjectBuilder<>(type);
  }

  public Class<T> getType() {
    return type;
  }

  /**
   * Returning new instance to support reusability of the builder
   */
  public ObjectBuilder<T> withParameter(final Object parameter) {
    return new ObjectBuilder<>(type, parameter);
  }

  public ObjectBuilder<T> andParameter(final Object parameter) {
    parameters.add(parameter);
    return this;
  }

  public ObjectBuilder<T> validatedBy(final Validator beanValidator) {
    validator = beanValidator;
    return this;
  }

  public T build() {
    try {
      Object[] arguments = parameters.toArray();
      Constructor<T> constructor = resolveConstructor(arguments);
      if (constructor == null) {
        constructor = resolveConstructorWithParameterConversion(arguments);
        if (constructor == null) {
          throw new IllegalStateException("No suitable constructor found for parameters " + Arrays.toString(arguments));
        }
        arguments = convertArguments(constructor, arguments);
      }
      constructor.setAccessible(true);
      validate(constructor, arguments);
      return constructor.newInstance(arguments);
    } catch (InvocationTargetException e) {
      LOG.error(e.getMessage(), e);
      if (e.getTargetException() instanceof RuntimeException) {
        throw (RuntimeException) e.getTargetException();
      } else {
        throw new IllegalStateException(e.getTargetException());
      }
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  private Constructor<T> resolveConstructor(final Object[] arguments) {
    return resolveConstructor(arguments, false);
  }

  private Constructor<T> resolveConstructor(final Object[] arguments, final boolean convertParameters) {
    Class<?>[] parameterTypes = new Class[arguments.length];
    for (int i = 0; i < parameterTypes.length; i++) {
      Object parameter = arguments[i];
      parameterTypes[i] = parameter != null ? parameter.getClass() : null;
    }
    return resolveConstructor(parameterTypes, convertParameters);
  }

  private Constructor<T> resolveConstructor(final Class<?>[] parameterTypes, final boolean convertParameters) {
    Constructor<T> resolvedConstructor = null;
    Constructor<T> ambiguousConstructor = null;
    for (Constructor<T> constructor : (Constructor<T>[]) type.getDeclaredConstructors()) {
      if (matches(constructor, parameterTypes, convertParameters)) {
        if (resolvedConstructor == null || isMoreSpecific(constructor, resolvedConstructor)) {
          resolvedConstructor = constructor;
          ambiguousConstructor = null;
        } else if (!isMoreSpecific(resolvedConstructor, constructor)) {
          ambiguousConstructor = constructor;
        }
      }
    }
    if (ambiguousConstructor != null) {
      String msg = String.format("More that one constructor found for parameter types %s. Found %s and %s",
                                 Arrays.asList(parameterTypes), ambiguousConstructor, resolvedConstructor);
      throw new IllegalStateException(msg);
    }
    return resolvedConstructor;
  }

  private Constructor<T> resolveConstructorWithParameterConversion(final Object[] arguments) {
    return resolveConstructor(arguments, true);
  }

  private boolean matches(final Constructor<T> constructor, final Class<?>[] parameterTypes, boolean
      convertParameterTypes) {
    Class<?>[] constructorParameterTypes = constructor.getParameterTypes();
    if (constructorParameterTypes.length != parameterTypes.length) {
      return false;
    }
    for (int i = 0; i < parameterTypes.length; i++) {
      if (constructorParameterTypes[i].isPrimitive()) {
        constructorParameterTypes[i] = ClassUtils.primitiveToWrapper(constructorParameterTypes[i]);
      }
      if (parameterTypes[i] != null && !constructorParameterTypes[i].isAssignableFrom(parameterTypes[i])) {
        if (!convertParameterTypes || !isConvertible(parameterTypes[i], constructorParameterTypes[i])) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean isMoreSpecific(final Constructor<T> constructor, final Constructor<T> resolvedConstructor) {
    Class<?>[] parameterTypes = constructor.getParameterTypes();
    Class<?>[] resolvedParameterTypes = resolvedConstructor.getParameterTypes();
    for (int i = 0; i < parameterTypes.length; i++) {
      if (!resolvedParameterTypes[i].isAssignableFrom(parameterTypes[i])) {
        return false;
      }
    }
    return true;
  }

  private boolean isConvertible(final Class<?> sourceType, final Class<?> targetType) {
    try {
      targetType.getDeclaredConstructor(sourceType);
      return true;
    } catch (NoSuchMethodException e) {
      LOG.error(e.getMessage(), e);
      return false;
    }
  }

  private Object[] convertArguments(final Constructor<T> constructor, final Object... arguments) throws ReflectiveOperationException {
    Class<?>[] parameterTypes = constructor.getParameterTypes();
    for (int i = 0; i < arguments.length; i++) {
      if (parameterTypes[i].isPrimitive()) {
        parameterTypes[i] = ClassUtils.primitiveToWrapper(parameterTypes[i]);
      }
      if (!parameterTypes[i].isInstance(arguments[i])) {
        Constructor<?> convertingConstructor = parameterTypes[i].getDeclaredConstructor(arguments[i].getClass());
        convertingConstructor.setAccessible(true);
        arguments[i] = convertingConstructor.newInstance(arguments[i]);
      }
    }
    return arguments;
  }

  private void validate(final Constructor<T> constructor, final Object... arguments) {
    if (validator != null) {
      Set<ConstraintViolation<T>> violations = validator.forExecutables().validateConstructorParameters(constructor, arguments);
      if (!violations.isEmpty()) {
        throw new ConstraintViolationException(violations);
      }
    }
  }
}
