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
package org.mini.rx.userapp;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mini.rx.DefaultRxContext;
import org.mini.rx.RxContext;
import org.mini.rx.Task;

import static org.junit.Assert.assertEquals;

/**
 * @author nicola
 * @since 08/09/2017
 */
public class TasksTest {

    @Test
    public void testTasks() throws Exception {

        RxContext ctx = new DefaultRxContext();

        Task<Integer> compute1 = ret -> {
            int sum = 0;
            for (int i=1; i<=40; i++) {
                sum += i;
            }
            ret.accept(sum);
        };

        Task<Integer> compute2 = ret -> {
            int sum = 0;
            for (int i=1; i<=50; i++) {
                sum += i;
            }
            ret.accept(sum);
        };

        Task<Integer> sum = ctx.tasks().join(compute1, compute2).then(lst -> lst.stream().reduce((a, b) -> a+b).get());

        int result = ctx.tasks().getResultBlocking(sum, 1, TimeUnit.SECONDS);

        assertEquals(40/2*41 + 50/2*51, result);

    }

}
