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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author nicola
 * @since 08/09/2017
 */
public class DefaultTasks implements Tasks {

    private RxContext ctx;

    public DefaultTasks(RxContext ctx) {
        this.ctx = ctx;
    }

    @SafeVarargs
    @Override
    public final <T> TaskCollector<T> join(Task<T>... tasks) {
        return new DefaultTaskCollector<>(ctx, Arrays.asList(tasks));
    }

    @Override
    public <T> T getResultBlocking(Task<T> task, long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<T> ref = new ArrayList<T>(1);
        task.accept(result -> {
            ref.add(result);
            latch.countDown();
        });
        if (!latch.await(timeout, unit)) {
            throw new TimeoutException("Timeout while waiting for task result");
        }
        return ref.get(0);
    }
}
