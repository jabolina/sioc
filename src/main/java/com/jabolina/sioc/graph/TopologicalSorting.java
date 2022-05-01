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

/**
 * Implements an algorithm for do a topological sort over a directed acyclic graph. This is essential to the
 * injection, where we define the instantiation order.
 *
 * Here we implement the <a href="https://en.wikipedia.org/wiki/Topological_sorting#Kahn's_algorithm">Kahn's
 * algorithm</a>. We follow this approach because the algorithm is small and straight-forward to implement. We have
 * some requirements to succeed with the sorting:
 *
 * 1. No cycles in the Graph (is a DAG);
 * 2. We must have a starting point, that is, at least one class that does not have any dependency.
 *
 * All of this could be improved in the future, with some more sophisticated algorithm.
 */
public final class TopologicalSorting {

  private TopologicalSorting() { }

  /**
   * Try to do a topological sort in the given graph. If the requirements are met, then a sorted list is returned,
   * otherwise an exception is thrown.
   *
   * @param graph: The graph to be sorted.
   * @return A sorted list to how traverse the graph.
   */
  public static List<Class<?>> sort(Map<Class<?>, Collection<Class<?>>> graph) {
    return sort(Graph.from(graph));
  }

  /**
   * <a href="https://en.wikipedia.org/wiki/Topological_sorting#Kahn's_algorithm">Kahn's algorithm</a> implementation.
   *
   * @param graph: The graph to sort.
   * @return A list with the topological sort of the graph.
   */
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
        edge.remove(source.source());

        if (edge.adjacency().isEmpty()) {
          edges.add(edge);
        }
      }
    }

    return sorted;
  }
}
