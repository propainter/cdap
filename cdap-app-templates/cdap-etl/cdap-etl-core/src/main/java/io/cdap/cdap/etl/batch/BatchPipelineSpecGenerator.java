/*
 * Copyright © 2016-2019 Cask Data, Inc.
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

package io.cdap.cdap.etl.batch;

import io.cdap.cdap.api.DatasetConfigurer;
import io.cdap.cdap.api.plugin.PluginConfigurer;
import io.cdap.cdap.etl.api.Engine;
import io.cdap.cdap.etl.common.DefaultPipelineConfigurer;
import io.cdap.cdap.etl.common.DefaultStageConfigurer;
import io.cdap.cdap.etl.proto.v2.ETLBatchConfig;
import io.cdap.cdap.etl.proto.v2.ETLStage;
import io.cdap.cdap.etl.proto.v2.spec.StageSpec;
import io.cdap.cdap.etl.spec.PipelineSpecGenerator;
import io.cdap.cdap.etl.validation.InvalidPipelineException;

import java.util.Set;

/**
 * Generates a pipeline spec for batch apps.
 */
public class BatchPipelineSpecGenerator extends PipelineSpecGenerator<ETLBatchConfig, BatchPipelineSpec> {

  public <T extends PluginConfigurer & DatasetConfigurer> BatchPipelineSpecGenerator(T configurer,
                                                                                     Set<String> sourcePluginTypes,
                                                                                     Set<String> sinkPluginTypes,
                                                                                     Engine engine) {
    super(configurer, sourcePluginTypes, sinkPluginTypes, engine);
  }

  @Override
  public BatchPipelineSpec generateSpec(ETLBatchConfig config) throws InvalidPipelineException {
    BatchPipelineSpec.Builder specBuilder = BatchPipelineSpec.builder();

    for (ETLStage endingAction : config.getPostActions()) {
      String name = endingAction.getName();
      DefaultPipelineConfigurer pipelineConfigurer =
        new DefaultPipelineConfigurer(pluginConfigurer, datasetConfigurer, name, engine,
                                      new DefaultStageConfigurer(name));
      StageSpec spec = configureStage(endingAction.getName(), endingAction.getPlugin(), pipelineConfigurer).build();
      specBuilder.addAction(new ActionSpec(name, spec.getPlugin()));
    }

    configureStages(config, specBuilder);
    return specBuilder.build();
  }
}
