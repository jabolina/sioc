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

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LifecycleManagementTest {

  private static CountDownLatch start = new CountDownLatch(3);
  private static CountDownLatch stop = new CountDownLatch(3);

  @Test
  public void testLifecycleManagement() throws InterruptedException {
    final LifecycleManagement management = new LifecycleManagement(this.getClass().getPackageName());
    management.initialize();
    management.start();

    if (!start.await(10, TimeUnit.SECONDS)) {
      throw new RuntimeException("Test timed out");
    }

    management.stop();
    if (!stop.await(10, TimeUnit.SECONDS)) {
      throw new RuntimeException("Test timed out");
    }
  }

  @Component
  static class A {

    @Depends
    private B b;

    @Start
    public void begin() {
      LifecycleManagementTest.start();
      b.additionalStart();
    }

    @Stop
    public void stop() {
      LifecycleManagementTest.stop();
      b.additionalStop();
    }
  }

  @Component(name = "CustomName")
  static class B {

    @Start
    public void begin() {
      LifecycleManagementTest.start();
    }

    @Stop
    public void stop() {
      LifecycleManagementTest.stop();
    }

    public void additionalStart() {
      begin();
    }

    public void additionalStop() {
      stop();
    }
  }

  protected static void start() {
    start.countDown();
  }

  protected static void stop() {
    stop.countDown();
  }
}
