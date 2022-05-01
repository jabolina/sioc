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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public final class LifecycleLoader {
  private static final String CLASS_EXTENSION = ".class";
  private static final LifecycleLoader INSTANCE = new LifecycleLoader();

  private LifecycleLoader() { }

  public static LifecycleLoader instance() {
    return INSTANCE;
  }

  public Collection<Class<?>> load(String packageName) {
    InputStream input = ClassLoader.getSystemClassLoader()
        .getResourceAsStream(packageName.replace(".", "/"));

    assert input != null : "Failed to load lifecycle classes";

    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    return reader.lines()
        .filter(line -> line.endsWith(CLASS_EXTENSION))
        .map(c -> load(c, packageName))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  private Class<?> load(String className, String packageName) {
    try {
      return Class.forName(String.format("%s.%s", packageName, className.replace(CLASS_EXTENSION, "")));
    } catch (ClassNotFoundException ignore) { }

    return null;
  }
}
