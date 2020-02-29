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
package de.openknowledge.projects.todolist.gateway.infrastructure.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ParameterNameProvider;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * Custom provider that looks up parameter names for JAX-RS parameters.
 */
public class CustomParameterNameProvider implements ParameterNameProvider {

  private static String getAnnotationValue(final Annotation annotation) {
    if (annotation instanceof HeaderParam) {
      return ((HeaderParam) annotation).value();
    } else if (annotation instanceof PathParam) {
      return ((PathParam) annotation).value();
    } else if (annotation instanceof QueryParam) {
      return ((QueryParam) annotation).value();
    } else {
      return null;
    }
  }

  @Override
  public List<String> getParameterNames(final Constructor<?> constructor) {
    return lookupParameterNames(constructor.getParameterAnnotations());
  }

  @Override
  public List<String> getParameterNames(final Method method) {
    return lookupParameterNames(method.getParameterAnnotations());
  }

  private List<String> lookupParameterNames(final Annotation[][] parameterAnnotations) {
    List<String> parameterNames = new ArrayList<>();
    if (parameterAnnotations != null) {
      for (Annotation[] annotations : parameterAnnotations) {
        String annotationValue = "requestBody";
        for (Annotation annotation : annotations) {
          annotationValue = getAnnotationValue(annotation);
          if (annotationValue != null) {
            break;
          }
        }
        parameterNames.add(annotationValue);
      }
    }
    return parameterNames;
  }
}