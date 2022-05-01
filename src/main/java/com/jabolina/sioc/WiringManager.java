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

import com.jabolina.sioc.util.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for instantiation and injection of classes within a single namespace.
 */
public class WiringManager {

  // We keep the namespace components in memory during this process.
  private final Map<String, Object> components = new HashMap<>();

  /**
   * Given the list ordered with the classes with the {@link Managed} annotation, we start the wiring process. This
   * process involves in instantiating the classes using the default no-argument constructor and then injecting the
   * dependencies.
   *
   * @param orderedComponents: A list with the components in the correct order.
   * @return The wired components.
   */
  public List<Object> wire(List<Class<?>> orderedComponents) {
    for (Class<?> component : orderedComponents) {
      initialize(component);
    }

    for (Class<?> component : orderedComponents) {
      inject(component);
    }

    return new ArrayList<>(components.values());
  }

  /**
   * This will initialize the component and assert that only one exists with the given name.
   *
   * @param component: A single component to initialize.
   */
  private void initialize(Class<?> component) {
    Object instance;
    try {
      instance = constructor(component).newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }

    String name = componentName(component);
    if (components.put(name, instance) != null) {
      throw new RuntimeException("Component for '" + name + "' already exists!");
    }
  }

  private Constructor<?> constructor(Class<?> component) {
    List<Constructor<?>> constructors = Arrays.asList(component.getDeclaredConstructors());
    return constructors.stream()
        .filter(c -> c.getParameterCount() == 0)
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No default constructor for " + component.getName()));
  }

  private void inject(Class<?> component) {
    Object owner = components.get(componentName(component));

    for (Field field : component.getDeclaredFields()) {
      if (Reflections.containsAnnotation(field, Inject.class)) {
        Class<?> dependencyClass = field.getType();
        Object dependency = components.get(componentName(dependencyClass));
        assert dependency != null : "Dependency " + dependencyClass + " not found!";

        field.trySetAccessible();

        try {
          field.set(owner, dependency);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  /**
   * Retrieve the component name. All components must be unique within a namespace, we first verify the name in the
   * {@link Managed} annotation, and fallback to the class name.
   *
   * @param component: The component to retrieve the name.
   * @return The component name.
   */
  private String componentName(Class<?> component) {
    Managed annotation = component.getAnnotation(Managed.class);
    assert annotation != null : "Component " + component.getName() + " does not have annotation!";

    String name = annotation.name();
    return name.length() == 0
        ? component.getName()
        : name;
  }
}
