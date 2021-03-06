/*
 * Copyright 2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.mini.rx;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

/**
 * @author nicola
 * @since 07/09/2017
 */
public interface Scheduler extends Closeable {

    void schedule(Runnable runnable);

    void schedule(Runnable runnable, long delay, TimeUnit unit);

    void scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit);

    void scheduleWithFixedDelay(Runnable runnable, long initialDelay, long period, TimeUnit unit);

}
