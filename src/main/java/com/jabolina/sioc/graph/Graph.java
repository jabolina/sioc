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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Graph<T> implements Iterable<Graph.Edge<T>> {

  static class Edge<T> {
    private final T source;
    private final Collection<T> adjacency;

    private Edge(T source, Collection<T> adjacency) {
      this.source = source;
      this.adjacency = adjacency;
    }

    public T source() {
      return source;
    }

    public Collection<T> adjacency() {
      return adjacency;
    }
  }

  private final Set<Edge<T>> edges = new HashSet<>();

  public static <V> Graph<V> from(Map<V, Collection<V>> representation) {
    final Graph<V> graph = new Graph<>();

    for (Map.Entry<V, Collection<V>> entry: representation.entrySet()) {
      Edge<V> edge = new Edge<V>(entry.getKey(), new ArrayList<>(entry.getValue()));
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
}
