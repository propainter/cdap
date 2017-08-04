/*
 * Copyright © 2017 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package co.cask.cdap.internal.app.program;

import co.cask.cdap.api.messaging.TopicNotFoundException;
import co.cask.cdap.app.runtime.ProgramOptions;
import co.cask.cdap.app.runtime.ProgramStateWriter;
import co.cask.cdap.common.ServiceUnavailableException;
import co.cask.cdap.common.app.RunIds;
import co.cask.cdap.common.conf.CConfiguration;
import co.cask.cdap.common.conf.Constants;
import co.cask.cdap.common.service.RetryStrategies;
import co.cask.cdap.common.service.RetryStrategy;
import co.cask.cdap.internal.app.runtime.ProgramOptionConstants;
import co.cask.cdap.messaging.MessagingService;
import co.cask.cdap.messaging.client.StoreRequestBuilder;
import co.cask.cdap.proto.BasicThrowable;
import co.cask.cdap.proto.Notification;
import co.cask.cdap.proto.ProgramRunStatus;
import co.cask.cdap.proto.id.NamespaceId;
import co.cask.cdap.proto.id.ProgramRunId;
import co.cask.cdap.proto.id.TopicId;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * An implementation of ProgramStateWriter that publishes program status events to TMS
 */
public final class MessagingProgramStateWriter implements ProgramStateWriter {
  private static final Logger LOG = LoggerFactory.getLogger(MessagingProgramStateWriter.class);
  private static final Gson GSON = new Gson();
  private final MessagingService messagingService;
  private final TopicId topicId;
  private final RetryStrategy retryStrategy;

  @Inject
  public MessagingProgramStateWriter(CConfiguration cConf, MessagingService messagingService) {
    this.topicId = NamespaceId.SYSTEM.topic(cConf.get(Constants.AppFabric.PROGRAM_STATUS_EVENT_TOPIC));
    this.retryStrategy = RetryStrategies.fromConfiguration(cConf, "program.message.");
    this.messagingService = messagingService;
  }

  @Override
  public void start(ProgramRunId programRunId, ProgramOptions programOptions, @Nullable String twillRunId) {
    long startTime = RunIds.getTime(RunIds.fromString(programRunId.getRun()), TimeUnit.MILLISECONDS);
    if (startTime == -1) {
      // If RunId is not time-based, use current time as start time
      startTime = System.currentTimeMillis();
    }
    ImmutableMap.Builder properties = ImmutableMap.<String, String>builder()
      .put(ProgramOptionConstants.START_TIME, String.valueOf(startTime))
      .put(ProgramOptionConstants.PROGRAM_STATUS, ProgramRunStatus.STARTING.name())
      .put(ProgramOptionConstants.USER_OVERRIDES, GSON.toJson(programOptions.getUserArguments().asMap()))
      .put(ProgramOptionConstants.SYSTEM_OVERRIDES, GSON.toJson(programOptions.getArguments().asMap()));
    if (twillRunId != null) {
      properties.put(ProgramOptionConstants.TWILL_RUN_ID, twillRunId);
    }
    publish(programRunId, properties);
  }

  @Override
  public void running(ProgramRunId programRunId, @Nullable String twillRunId) {
    ImmutableMap.Builder properties = ImmutableMap.<String, String>builder()
      .put(ProgramOptionConstants.LOGICAL_START_TIME, String.valueOf(System.currentTimeMillis()))
      .put(ProgramOptionConstants.PROGRAM_STATUS, ProgramRunStatus.RUNNING.name());
    if (twillRunId != null) {
      properties.put(ProgramOptionConstants.TWILL_RUN_ID, twillRunId);
    }
    publish(programRunId, properties);
  }

  @Override
  public void completed(ProgramRunId programRunId) {
    stop(programRunId, ProgramRunStatus.COMPLETED, null);
  }

  @Override
  public void killed(ProgramRunId programRunId) {
    stop(programRunId, ProgramRunStatus.KILLED, null);
  }

  @Override
  public void error(ProgramRunId programRunId, Throwable failureCause) {
    stop(programRunId, ProgramRunStatus.FAILED, failureCause);
  }

  @Override
  public void suspend(ProgramRunId programRunId) {
    publish(
      programRunId,
      ImmutableMap.<String, String>builder()
        .put(ProgramOptionConstants.PROGRAM_STATUS, ProgramRunStatus.SUSPENDED.name())
    );
  }

  @Override
  public void resume(ProgramRunId programRunId) {
    publish(
      programRunId,
      ImmutableMap.<String, String>builder()
        .put(ProgramOptionConstants.PROGRAM_STATUS, ProgramRunStatus.RESUMING.name())
    );
  }

  private void stop(ProgramRunId programRunId, ProgramRunStatus runStatus, @Nullable Throwable cause) {
    ImmutableMap.Builder properties = ImmutableMap.<String, String>builder()
      .put(ProgramOptionConstants.END_TIME, String.valueOf(System.currentTimeMillis()))
      .put(ProgramOptionConstants.PROGRAM_STATUS, runStatus.name());
    if (cause != null) {
      properties.put(ProgramOptionConstants.PROGRAM_ERROR, GSON.toJson(new BasicThrowable(cause)));
    }
    publish(programRunId, properties);
  }

  private void publish(ProgramRunId programRunId, ImmutableMap.Builder<String, String> properties) {
    // ProgramRunId is always required in a notification
    properties.put(ProgramOptionConstants.PROGRAM_RUN_ID, GSON.toJson(programRunId));
    Notification programStatusNotification = new Notification(Notification.Type.PROGRAM_STATUS, properties.build());

    int failureCount = 0;
    long startTime = -1L;
    boolean done = false;
    // TODO CDAP-12255 this code was basically copied from MessagingMetricsCollectionService.TopicPayload#publish.
    // This should be refactored into a common class for publishing to TMS with a retry strategy
    while (!done) {
      try {
        messagingService.publish(StoreRequestBuilder.of(topicId)
                                   .addPayloads(GSON.toJson(programStatusNotification))
                                   .build());
        done = true;
      } catch (IOException e) {
        // These exceptions are not retry-able
        LOG.error("Failed to publish messages to TMS: ", e);
        break;
      } catch (TopicNotFoundException | ServiceUnavailableException e) {
        // These exceptions are retry-able due to TMS not completely started
        if (startTime < 0) {
          startTime = System.currentTimeMillis();
        }
        long retryMillis = retryStrategy.nextRetry(++failureCount, startTime);
        if (retryMillis < 0) {
          LOG.error("Failed to publish messages to TMS and exceeded retry limit.", e);
          break;
        }
        LOG.debug("Failed to publish messages to TMS due to {}. Will be retried in {} ms.",
                  e.getMessage(), retryMillis);
        try {
          TimeUnit.MILLISECONDS.sleep(retryMillis);
        } catch (InterruptedException e1) {
          // Something explicitly stopping this thread. Simply just break and reset the interrupt flag.
          Thread.currentThread().interrupt();
          done = true;
        }
      }
    }
  }
}