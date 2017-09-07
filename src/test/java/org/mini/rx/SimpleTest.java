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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author nicola
 * @since 07/09/2017
 */
public class SimpleTest {

    @Test
    public void testParallel() throws Exception {

        SchedulerManager schedulers = new DefaultSchedulerManager();
        Queue<Integer> nums = new ConcurrentLinkedQueue<>();

        int limit = 200;
        CountDownLatch latch = new CountDownLatch(limit);
        for (int i=0; i < limit; i++) {
            final int n = i;
            schedulers.computation().schedule(() -> {
                nums.add(n);
                latch.countDown();
            });
        }

        assertTrue(latch.await(1, TimeUnit.SECONDS));

        assertEquals(limit, new HashSet<>(nums).size());
    }

    @Test
    public void testAsyncCall() throws Exception {

        SchedulerManager schedulers = new DefaultSchedulerManager();
        Function<Integer, Integer> square = x -> x*x;

        Queue<Integer> nums = new LinkedList<>();
        CountDownLatch latch = new CountDownLatch(1);

        int limit = 200;
        for (int i=0; i < limit; i++) {
            final int n = i;
            schedulers.computation().schedule(() -> {
                int y = square.apply(n);

                schedulers.computation().serialized(SimpleTest.this).schedule(() -> {
                    nums.add(y);
                    for (int x : nums) {
                        x += 1; // make something in parallel
                        if (x == limit +10) {
                            System.out.println("Never");
                        }
                    }

                    if (nums.size() == limit) {
                        latch.countDown();
                    }
                });
            });
        }

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertEquals(limit, new HashSet<>(nums).size());
    }

}
