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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * @author nicola
 * @since 08/09/2017
 */
public class DefaultTaskCollector<T> implements TaskCollector<T> {

    private RxContext ctx;

    private Collection<Task<T>> tasks;

    public DefaultTaskCollector(RxContext ctx, Collection<Task<T>> tasks) {
        this.ctx = ctx;
        this.tasks = tasks;
    }

    @Override
    public <R> Task<R> then(Function<List<T>, R> reduce) {
        List<T> result = new LinkedList<>();
        return callback -> {
            for (Task<T> task : tasks) {
                task.accept(res -> {
                    ctx.getSchedulerManager().serialized(DefaultTaskCollector.this).schedule(() -> {
                        result.add(res);
                        if (result.size() == tasks.size()) {
                            callback.accept(reduce.apply(result));
                        }
                    });
                });
            }
        };
    }
}
