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
import com.jabolina.sioc.util.Reflections;
import net.jcip.annotations.GuardedBy;

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

public class LifecycleManagement {

  private final String space;

  @GuardedBy("this")
  private final List<Object> components = new ArrayList<>();
  private final WiringManager wiring = new WiringManager();

  public LifecycleManagement(String packageName) {
    this.space = packageName;
  }

  public synchronized void initialize() {
    Map<Class<?>, Collection<Class<?>>> graph = dependencyGraph(lifecycleClasses());
    List<Class<?>> topologicalSorted = TopologicalSorting.sort(graph);
    components.addAll(wiring.wire(topologicalSorted));
  }

  public synchronized void start() {
    componentMethod(Start.class);
  }

  public synchronized void stop() {
    componentMethod(Stop.class);
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
    Collection<Class<?>> loaded = LifecycleLoader.instance().load(space);
    return loaded.stream()
        .filter(this::isLifecycleClass)
        .collect(Collectors.toSet());
  }

  private boolean isLifecycleClass(Class<?> clazz) {
    return containsAnnotation(clazz, Component.class);
  }

  private boolean isDependency(Field field) {
    return containsAnnotation(field, Depends.class);
  }
}
