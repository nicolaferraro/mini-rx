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

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.mini.rx.computation.SerializedScheduler;
import org.mini.rx.computation.ComputationScheduler;

/**
 * @author nicola
 * @since 07/09/2017
 */
class DefaultSchedulerManager implements SchedulerManager {

    private ComputationScheduler computationScheduler = new ComputationScheduler();

    private ConcurrentHashMap<Object, Scheduler> serializedSchedulers = new ConcurrentHashMap<>();

    @Override
    public Scheduler computation() {
        return computationScheduler;
    }

    @Override
    public void close() throws IOException {
        computationScheduler.close();
        for (Scheduler scheduler : serializedSchedulers.values()) {
            scheduler.close();
        }
    }

    public Scheduler serialized(Object sync) {
        Objects.requireNonNull(sync, "Cannot serialize on a null object");
        serializedSchedulers.computeIfAbsent(sync, s -> new SerializedScheduler(computationScheduler, s));
        return serializedSchedulers.get(sync);
    }



}
