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
package com.jabolina.sioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A class annotation, identifying that the given type must be managed during runtime. Components must be unique within
 * a namespace, so is not possible to annotate classes of the same type without name. As the time of writing we handle
 * only a single namespace and all managed components are unique.
 *
 * All classes that have this annotation __must__ provide the default constructor without arguments.
 *
 * Only classes with this annotation can have its dependencies injected during start. The same applies to using
 * the {@link Start} and {@link Stop} annotations in methods.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Managed {

  /**
   * Defines the managed component name. If this is empty, the class name is used instead.
   *
   * @return the custom component name.
   */
  String name() default "";
}
