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
package com.jabolina.sioc.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Helper to load all classes from a specified package.
 */
public final class PackageLoader {
  private static final String CLASS_EXTENSION = ".class";

  private PackageLoader() { }

  /**
   * Load all classes from the specified package.
   *
   * @param packageName: Package to load classes.
   * @return A list containing the classes.
   */
  public static List<Class<?>> load(String packageName) {
    InputStream input = ClassLoader.getSystemClassLoader()
        .getResourceAsStream(packageName.replace(".", "/"));

    // Means that we are unable to create an input stream pointing to the package.
    if (input == null) {
      return Collections.emptyList();
    }

    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    return reader.lines()
        .filter(line -> line.endsWith(CLASS_EXTENSION))
        .map(c -> load(c, packageName))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private static Class<?> load(String className, String packageName) {
    try {
      return Class.forName(name(className, packageName));
    } catch (ClassNotFoundException ignore) { }

    return null;
  }

  private static String name(String className, String packageName) {
    return String.format("%s.%s", packageName, className.replace(CLASS_EXTENSION, ""));
  }
}
