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

  /**
   * Verifies if the given element contains the given annotation.
   *
   * @param element: An element that can be annotated.
   * @param annotation: The annotation to verify.
   * @return true if the annotation is present, false otherwise.
   */
  public static boolean containsAnnotation(AnnotatedElement element, Class<? extends Annotation> annotation) {
    return element.isAnnotationPresent(annotation);
  }

  /**
   * Find a method in the object that contains the given annotation. We return only the first method found.
   *
   * @param object: The object to be verified.
   * @param annotation: The method annotation.
   * @return An {@link Optional<Method>} with the __first__ method found, an empty {@link Optional<Method>} otherwise.
   */
  public static Optional<Method> findMethodWith(Object object, Class<? extends Annotation> annotation) {
    List<Method> methods = Arrays.asList(object.getClass().getDeclaredMethods());
    return methods.stream()
        .filter(m -> m.isAnnotationPresent(annotation))
        .findFirst();
  }
}
