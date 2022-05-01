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

import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represent a simple graph. We use this as an alternative representation of the graph. This structure is thread-safe,
 * delegating all operations to a synchronized collection.
 *
 * @param <T>: type of the values in the graph.
 */
@ThreadSafe
public class Graph<T> implements Iterable<Graph.Edge<T>> {

  // We must use a thread-safe collection to guarantee thread-safety.
  private final Set<Edge<T>> edges = Collections.synchronizedSet(new HashSet<>());

  /**
   * Creates a new instance of {@link Graph<V>} representing the same graph provided as argument.
   *
   * @param representation: A graph represented with a {@link Map}.
   * @param <V>: Type of the Graph.
   * @return A new instance of {@link Graph<V>}.
   */
  public static <V> Graph<V> from(Map<V, Collection<V>> representation) {
    final Graph<V> graph = new Graph<>();
    for (Map.Entry<V, Collection<V>> entry: representation.entrySet()) {
      Edge<V> edge = new Edge<>(entry.getKey(), new ArrayList<>(entry.getValue()));
      graph.edges.add(edge);
    }

    return graph;
  }

  public int size() {
    return edges.size();
  }

  public void remove(Edge<T> edge) {
    edges.remove(edge);
  }

  @Override
  public Iterator<Edge<T>> iterator() {
    return edges.iterator();
  }

  /**
   * Represents a single edge in the graph. This contains the source and the adjacency list the edge connects to.
   *
   * @param <T>: Type of the Edge.
   */
  static class Edge<T> {
    private final T source;
    private final Collection<T> adjacency;

    private Edge(T source, List<T> adjacency) {
      this.source = source;
      this.adjacency = Collections.synchronizedList(adjacency);
    }

    public T source() {
      return source;
    }

    public Collection<T> adjacency() {
      return Collections.unmodifiableCollection(adjacency);
    }

    public boolean remove(T t) {
      return adjacency.remove(t);
    }
  }
}
