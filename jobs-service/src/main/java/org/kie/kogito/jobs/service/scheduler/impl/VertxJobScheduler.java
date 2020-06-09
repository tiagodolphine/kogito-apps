/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.service.scheduler.impl;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.axle.core.Vertx;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.service.executor.JobExecutor;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.refactoring.job.HttpJob;
import org.kie.kogito.jobs.service.refactoring.job.HttpJobContext;
import org.kie.kogito.jobs.service.refactoring.job.JobDetails;
import org.kie.kogito.jobs.service.refactoring.job.ManageableJobHandle;
import org.kie.kogito.jobs.service.refactoring.vertx.VertxTimerServiceScheduler;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.BaseTimerJobScheduler;
import org.kie.kogito.jobs.service.stream.AvailableStreams;
import org.kie.kogito.jobs.service.utils.ErrorHandling;
import org.kie.kogito.timer.JobHandle;
import org.kie.kogito.timer.Trigger;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job Scheduler based on Vert.x engine.
 */
@ApplicationScoped
public class VertxJobScheduler extends BaseTimerJobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxJobScheduler.class);

    @Inject
    Vertx vertx;

    @Inject
    VertxTimerServiceScheduler delegate;

    protected VertxJobScheduler() {
    }

    public VertxJobScheduler(Vertx vertx, JobExecutor jobExecutor, ReactiveJobRepository jobRepository,
                             long backoffRetryMillis,
                             long maxIntervalLimitToRetryMillis) {
        super(jobExecutor, jobRepository, backoffRetryMillis, maxIntervalLimitToRetryMillis);
        this.vertx = vertx;
    }

    @Override
    public PublisherBuilder<ManageableJobHandle> doSchedule(JobDetails job, Optional<Trigger> trigger) {
        LOGGER.debug("Job Scheduling {}", job);
        return ReactiveStreams
                .of(job)
                .map(j -> delegate.scheduleJob(new HttpJob(), new HttpJobContext(job),
                                               trigger.orElse(job.getTrigger())));
    }

    @Override
    public Publisher<ManageableJobHandle> doCancel(JobDetails scheduledJob) {
        return ReactiveStreams
                .of(scheduledJob)
                .map(j -> {
                    ManageableJobHandle handle = new ManageableJobHandle(j.getScheduledId());
                    handle.setCancel(delegate.removeJob(handle));
                    return handle;
                })
                .buildRs();
    }

    //Stream Processors

    @Incoming(AvailableStreams.JOB_ERROR_EVENTS)
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public CompletionStage jobErrorProcessor(JobExecutionResponse response) {
        LOGGER.warn("Error received {}", response);
        return ErrorHandling.skipErrorPublisherBuilder(this::handleJobExecutionError, response)
                .findFirst()
                .run()
                .thenApply(Optional::isPresent)
                .exceptionally(e -> {
                    LOGGER.error("Error handling error {}", response, e);
                    return false;
                });
    }

    @Incoming(AvailableStreams.JOB_SUCCESS_EVENTS)
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public CompletionStage jobSuccessProcessor(JobExecutionResponse response) {
        LOGGER.debug("Success received to be processed {}", response);
        return ErrorHandling.skipErrorPublisherBuilder(this::handleJobExecutionSuccess, response)
                .findFirst()
                .run();
    }
}