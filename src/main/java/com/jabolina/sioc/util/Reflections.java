/*
 * Copyright 2022-present jabolina
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jabolina.sioc.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class Reflections {

  private Reflections() { }

  public static boolean containsAnnotation(AnnotatedElement element, Class<? extends Annotation> annotation) {
    List<Annotation> annotations = Arrays.asList(element.getAnnotations());
    return annotations.stream()
        .anyMatch(a -> a.annotationType().equals(annotation));
  }

  public static Optional<Method> findMethodWith(Object object, Class<? extends Annotation> annotation) {
    List<Method> methods = Arrays.asList(object.getClass().getDeclaredMethods());
    return methods.stream()
        .filter(m -> m.isAnnotationPresent(annotation))
        .findFirst();
  }
}
