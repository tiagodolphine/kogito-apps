/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.jobs.service.refactoring.job;

import java.util.concurrent.CompletableFuture;

import io.quarkus.arc.Arc;
import org.kie.kogito.jobs.service.executor.HttpJobExecutor;
import org.kie.kogito.timer.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The job that sends an HTTP Request based on the {@link HttpJobContext}.
 */
public class HttpJob implements Job<HttpJobContext> {

    private static Logger LOGGER = LoggerFactory.getLogger(HttpJob.class);

    private HttpJobExecutor executor;

    public HttpJob(HttpJobExecutor executor) {
        this.executor = executor;
    }

    public HttpJob() {
        this.executor = Arc.container().instance(HttpJobExecutor.class).get();
    }

    @Override
    public void execute(HttpJobContext ctx) {
        LOGGER.info("Executing for context {}", ctx.getJobDetails());
        System.out.println("oi");
        //load job
        //use adapter
        executor.execute(CompletableFuture.completedFuture(ctx.getJobDetails()));
    }
}
