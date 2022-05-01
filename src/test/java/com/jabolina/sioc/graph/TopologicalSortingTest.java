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

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TopologicalSortingTest {

  @Test
  public void testTopologicalSorting() {
    Map<Class<?>, Collection<Class<?>>> graph = new HashMap<>();

    graph.put(A.class, List.of(B.class, C.class));
    graph.put(B.class, List.of(D.class));
    graph.put(C.class, List.of(B.class, D.class));
    graph.put(D.class, Collections.emptyList());

    List<Class<?>> expected = Arrays.asList(D.class, B.class, C.class, A.class);

    assertEquals(expected, TopologicalSorting.sort(graph));
  }

  private static class A { }

  private static class B { }

  private static class C { }

  private static class D { }
}
