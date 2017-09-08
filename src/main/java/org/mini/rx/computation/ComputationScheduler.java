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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.mini.rx.Scheduler;

/**
 * @author nicola
 * @since 07/09/2017
 */
public class ComputationScheduler implements Scheduler {

    private List<ScheduledExecutorService> executorServices;

    // TODO better looking for affinity
    private Random rnd = new Random();

    public ComputationScheduler() {
        int cores = Runtime.getRuntime().availableProcessors();
        int numExecutors = 2 * cores;
        this.executorServices = new ArrayList<>(numExecutors);
        for (int i = 0; i < numExecutors; i++) {
            executorServices.add(Executors.newSingleThreadScheduledExecutor());
        }
    }

    @Override
    public void schedule(Runnable runnable) {
        getExecutorFor(runnable).execute(runnable);
    }

    @Override
    public void schedule(Runnable runnable, long delay, TimeUnit unit) {
        getExecutorFor(runnable).schedule(runnable, delay, unit);
    }

    @Override
    public void scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        getExecutorFor(runnable).scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    @Override
    public void scheduleWithFixedDelay(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        getExecutorFor(runnable).scheduleWithFixedDelay(runnable, initialDelay, period, unit);
    }

    private ScheduledExecutorService getExecutorFor(Object target) {
        int offset;
        if (target instanceof SerialRunnable) {
            offset = ((SerialRunnable) target).channelId() % executorServices.size();
        } else {
            offset = rnd.nextInt(executorServices.size());
        }
        return this.executorServices.get(offset);
    }

}
