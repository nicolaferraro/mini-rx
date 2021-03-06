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

/**
 * @author nicola
 * @since 08/09/2017
 */
public class DefaultRxContext implements RxContext {

    /**
     * Per-context scheduler manager
     */
    private SchedulerManager schedulerManager = new DefaultSchedulerManager();

    @Override
    public SchedulerManager getSchedulerManager() {
        return schedulerManager;
    }

    @Override
    public AsyncTask newAsyncTask() {
        return new DefaultAsyncTask(this, null);
    }

    @Override
    public AsyncTask newAsyncTask(String name) {
        return new DefaultAsyncTask(this, name);
    }

    @Override
    public void close() throws IOException {
        this.schedulerManager.close();
    }
}
