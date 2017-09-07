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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author nicola
 * @since 07/09/2017
 */
public class ActionTest {

    class Dest implements Action<Integer> {

        private Scheduler scheduler;

        private int counter;

        public Dest(Scheduler scheduler) {
            this.scheduler = scheduler;
        }

        @Override
        public void execute(Consumer<Integer> callback) {
            scheduler.serialized(this).schedule(() -> {
                callback.accept(counter++);
            });
        }
    }

    @Test
    public void test() throws InterruptedException {

        Scheduler scheduler = new DefaultSchedulerManager().computation();
        Scheduler serialScheduler = scheduler.serialized(this);
        Dest dest = new Dest(scheduler);

        List<Integer> lst = new LinkedList<>();


        int limit = 500;
        CountDownLatch latch = new CountDownLatch(limit);
        for (int i=0; i<limit; i++) {
            scheduler.schedule(() -> {
                dest.execute(x -> serialScheduler.schedule(() -> {
                    lst.add(x);
                    latch.countDown();
                }));
            });
        }

        latch.await(1, TimeUnit.SECONDS);
        assertEquals(limit * (limit-1) / 2, lst.parallelStream().reduce((i, j)-> i + j).get().intValue());

    }

}
