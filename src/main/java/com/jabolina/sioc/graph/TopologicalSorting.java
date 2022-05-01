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
package com.jabolina.sioc.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public final class TopologicalSorting {

  private TopologicalSorting() { }

  public static List<Class<?>> sort(Map<Class<?>, Collection<Class<?>>> graph) {
    return sort(Graph.from(graph));
  }

  private static List<Class<?>> sort(Graph<Class<?>> graph) {
    List<Class<?>> sorted = new ArrayList<>(graph.size());
    Stack<Graph.Edge<Class<?>>> edges = new Stack<>();

    for (Graph.Edge<Class<?>> edge : graph) {
      if (edge.adjacency().isEmpty()) {
        edges.add(edge);
      }
    }

    assert !edges.isEmpty() : "Lifecycle without starting point!";

    while (!edges.isEmpty()) {
      Graph.Edge<Class<?>> source = edges.pop();
      graph.remove(source);
      sorted.add(source.source());

      for (Graph.Edge<Class<?>> edge : graph) {
        edge.adjacency().remove(source.source());

        if (edge.adjacency().isEmpty() && !sorted.contains(edge.source())) {
          edges.add(edge);
        }
      }
    }

    return sorted;
  }
}
