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
package org.mini.rx.computation;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.mini.rx.Scheduler;

/**
 * @author nicola
 * @since 07/09/2017
 */
public class SerializedScheduler implements Scheduler {

    private Scheduler delegate;

    private Object sync;

    public SerializedScheduler(Scheduler delegate, Object sync) {
        this.delegate = delegate;
        this.sync = sync;
    }

    @Override
    public void schedule(Runnable runnable) {
        delegate.schedule(toSerial(runnable));
    }

    @Override
    public void schedule(Runnable runnable, long delay, TimeUnit unit) {
        delegate.schedule(toSerial(runnable), delay, unit);
    }

    @Override
    public void scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        delegate.scheduleAtFixedRate(toSerial(runnable), initialDelay, period, unit);
    }

    @Override
    public void scheduleWithFixedDelay(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        delegate.scheduleWithFixedDelay(toSerial(runnable), initialDelay, period, unit);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    private SerialRunnable toSerial(Runnable runnable) {
        return SerialRunnable.serialize(this.sync, runnable);
    }

}
