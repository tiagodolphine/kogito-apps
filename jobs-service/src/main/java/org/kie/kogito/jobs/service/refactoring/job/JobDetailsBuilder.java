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

import java.time.ZonedDateTime;

import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.timer.Trigger;

public class JobDetailsBuilder<T> {

    private String id;
    private String correlationId;
    private JobStatus status;
    private ZonedDateTime lastUpdate;
    private Integer retries;
    private Integer executionCounter;
    private String scheduledId;
    private T payload;
    private Recipient recipient;
    private Trigger trigger;
    private JobDetails.Type type;

    public JobDetailsBuilder id(String id) {
        this.id = id;
        return this;
    }

    public JobDetailsBuilder correlationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public JobDetailsBuilder status(JobStatus status) {
        this.status = status;
        return this;
    }

    public JobDetailsBuilder lastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    public JobDetailsBuilder retries(Integer retries) {
        this.retries = retries;
        return this;
    }

    public JobDetailsBuilder executionCounter(Integer executionCounter) {
        this.executionCounter = executionCounter;
        return this;
    }

    public JobDetailsBuilder scheduledId(String scheduledId) {
        this.scheduledId = scheduledId;
        return this;
    }

    public JobDetailsBuilder payload(T payload) {
        this.payload = payload;
        return this;
    }

    public JobDetailsBuilder recipient(Recipient recipient) {
        this.recipient = recipient;
        return this;
    }

    public JobDetailsBuilder trigger(Trigger trigger) {
        this.trigger = trigger;
        return this;
    }

    public JobDetailsBuilder type(JobDetails.Type type) {
        this.type = type;
        return this;
    }

    public JobDetails build() {
        return new JobDetails(id, correlationId, status, lastUpdate, retries, executionCounter, scheduledId, payload,
                              recipient, trigger, type);
    }

    public JobDetailsBuilder of(JobDetails jobDetails) {
        return id(jobDetails.getId())
                .correlationId(jobDetails.getCorrelationId())
                .status(jobDetails.getStatus())
                .lastUpdate(jobDetails.getLastUpdate())
                .retries(jobDetails.getRetries())
                .executionCounter(jobDetails.getExecutionCounter())
                .scheduledId(jobDetails.getScheduledId())
                .payload(jobDetails.getPayload())
                .recipient(jobDetails.getRecipient())
                .trigger(jobDetails.getTrigger())
                .type(jobDetails.getType());
    }

    public JobDetailsBuilder<T> incrementRetries() {
        this.retries++;
        return this;
    }

    public JobDetailsBuilder<T> incrementExecutionCounter() {
        this.executionCounter++;
        return this;
    }
}