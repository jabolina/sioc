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

import com.jabolina.sioc.graph.TopologicalSorting;
import com.jabolina.sioc.util.PackageLoader;
import com.jabolina.sioc.util.Reflections;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jabolina.sioc.util.Reflections.containsAnnotation;

/**
 * Entrypoint for managing the lifecycle of components within a single namespace.
 *
 * The process for managing is only a few steps at this moment:
 *
 * 1. Load and filter all classes for a specified package;
 * 2. Generate a dependency graph;
 * 3. Do a topological sort in the graph;
 * 4. Instantiate the classes accordingly with the topological order and inject the dependencies;
 *
 * During initialization process the class will be locked, so other operations can not be applied. The initialization
 * only instantiates and inject the classes, we moved the start and stop operations in distinct methods.
 *
 * The start operation begins after calling the {@link #start()} method. This will iterate in all the components within
 * the namespace and call the method with the {@link Start} annotation. The same applies to the stop process, start with
 * the {@link #stop()} method.
 *
 * At the time of writing, we are handling only synchronous methods.
 */
@ThreadSafe
public class LifecycleManagement {

  private final String packageName;

  @GuardedBy("this")
  private final List<Object> components = new ArrayList<>();
  private final WiringManager wiring = new WiringManager();

  private volatile boolean initialized = false;

  public LifecycleManagement(String packageName) {
    this.packageName = packageName;
  }

  /**
   * Load the classes with {@link Managed} annotation and inject the dependencies with the {@link Inject} annotation.
   */
  public synchronized void initialize() {
    if (initialized) {
      return;
    }

    initialized = true;
    Map<Class<?>, Collection<Class<?>>> graph = dependencyGraph(lifecycleClasses());
    List<Class<?>> topologicalSorted = TopologicalSorting.sort(graph);
    components.addAll(wiring.wire(topologicalSorted));
  }

  /**
   * Execute the method with the {@link Start} annotation in all classes that are managed, iff the components are
   * already initialized.
   */
  public synchronized void start() {
    if (initialized) {
      componentMethod(Start.class);
    }
  }

  /**
   * Execute the method with the {@link Stop} annotation in all classes that are managed, iff the components are
   * already initialized.
   */
  public synchronized void stop() {
    if (initialized) {
      componentMethod(Stop.class);
    }
  }

  private synchronized void componentMethod(Class<? extends Annotation> annotation) {
    for (Object component : components) {
      Reflections.findMethodWith(component, annotation)
          .ifPresent(m -> {
            try {
              m.invoke(component);
            } catch (IllegalAccessException | InvocationTargetException e) {
              throw new RuntimeException(e);
            }
          });
    }
  }

  private Map<Class<?>, Collection<Class<?>>> dependencyGraph(Collection<Class<?>> classes) {
    Map<Class<?>, Collection<Class<?>>> dependency = new HashMap<>();
    for (Class<?> clazz : classes) {
      dependency.putIfAbsent(clazz, dependencies(clazz));
    }

    return dependency;
  }

  private List<Class<?>> dependencies(Class<?> clazz) {
    List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
    return fields.stream()
        .filter(this::isDependency)
        .map(Field::getType)
        .collect(Collectors.toList());
  }

  private Set<Class<?>> lifecycleClasses() {
    Collection<Class<?>> loaded = PackageLoader.load(packageName);
    return loaded.stream()
        .filter(this::isManagedClass)
        .collect(Collectors.toSet());
  }

  private boolean isManagedClass(Class<?> clazz) {
    return containsAnnotation(clazz, Managed.class);
  }

  private boolean isDependency(Field field) {
    return containsAnnotation(field, Inject.class);
  }
}
