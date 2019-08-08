/*
 * Copyright © 2019 Cask Data, Inc.
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

package io.cdap.cdap.etl.api.validation;

import io.cdap.cdap.api.annotation.Beta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

/**
 * Represents a failure condition occurred during validation.
 */
@Beta
public class ValidationFailure {
  private final String message;
  private final String correctiveAction;
  private final List<Cause> causes;


  /**
   * Creates a validation failure with provided message.
   *
   * @param message validation failure message
   */
  public ValidationFailure(String message) {
    this(message, null);
  }

  /**
   * Creates a validation failure with provided message and corrective action.
   *
   * @param message validation failure message
   * @param correctiveAction corrective action
   */
  public ValidationFailure(String message, @Nullable String correctiveAction) {
    this.message = message;
    this.correctiveAction = correctiveAction;
    this.causes = new ArrayList<>();
  }

  /**
   * Adds provided cause to this validation failure.
   *
   * @param cause cause of validation failure
   * @return validation failure with provided cause
   */
  public ValidationFailure withCause(Cause cause) {
    causes.add(cause);
    return this;
  }

  /**
   * Adds cause attributes that represents plugin not found failure cause.
   *
   * @param pluginId plugin id
   * @param pluginName plugin name
   * @param pluginType plugin type
   * @return validation failure with plugin not found cause
   */
  public ValidationFailure withPluginNotFoundCause(String pluginId, String pluginName, String pluginType) {
    causes.add(new Cause().with(CauseAttributes.PLUGIN_ID, pluginId).with(CauseAttributes.PLUGIN_NAME, pluginName)
                 .with(CauseAttributes.PLUGIN_TYPE, pluginType));
    return this;
  }

  /**
   * Adds cause attributes that represents stage configure property failure cause.
   *
   * @param stageConfigProperty stage config property
   * @return validation failure with invalid stage config property cause
   */
  public ValidationFailure withStageConfigCause(String stageConfigProperty) {
    causes.add(new Cause().with(CauseAttributes.STAGE_CONFIG, stageConfigProperty));
    return this;
  }

  /**
   * Adds cause attributes that represents invalid input schema field failure cause.
   *
   * @param fieldName name of the input schema field
   * @param inputStage stage name
   * @return validation failure with invalid input schema field cause
   */
  public ValidationFailure withInvalidInputSchemaCause(String fieldName, @Nullable String inputStage) {
    causes.add(new Cause().with(CauseAttributes.INPUT_STAGE, inputStage)
                 .with(CauseAttributes.INPUT_SCHEMA_FIELD, fieldName));
    return this;
  }

  /**
   * Adds cause attributes that represents invalid output schema field failure cause.
   *
   * @param fieldName name of the output schema field
   * @param outputPort stage name
   * @return validation failure with invalid output schema field cause
   */
  public ValidationFailure withInvalidOutputSchemaCause(String fieldName, @Nullable String outputPort) {
    causes.add(new Cause().with(CauseAttributes.OUTPUT_PORT, outputPort)
                 .with(CauseAttributes.OUTPUT_SCHEMA_FIELD, fieldName));
    return this;
  }

  /**
   * Returns failure message.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Returns corrective action for this failure.
   */
  @Nullable
  public String getCorrectiveAction() {
    return correctiveAction;
  }

  /**
   * Returns causes that caused this failure.
   */
  public List<Cause> getCauses() {
    return causes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidationFailure failure = (ValidationFailure) o;
    return message.equals(failure.message) &&
      Objects.equals(correctiveAction, failure.correctiveAction) && causes.equals(failure.causes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, correctiveAction, causes);
  }

  /**
   * Represents a cause of a failure.
   */
  @Beta
  public static class Cause {
    private final Map<String, String> attributes;

    /**
     * Creates a failure cause.
     */
    public Cause() {
      this.attributes = new HashMap<>();
    }

    /**
     * Adds attributes to this cause.
     *
     * @param attribute cause attribute name
     * @param value cause attribute value
     * @return this cause
     */
    public Cause with(String attribute, String value) {
      attributes.put(attribute, value);
      return this;
    }

    /**
     * Returns value of the provided cause attribute.
     *
     * @param attribute attribute name
     */
    public String getAttribute(String attribute) {
      return attributes.get(attribute);
    }

    /**
     * Returns all the attributes of the cause.
     */
    public Map<String, String> getAttributes() {
      return attributes;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Cause cause = (Cause) o;
      return attributes.equals(cause.attributes);
    }

    @Override
    public int hashCode() {
      return Objects.hash(attributes);
    }
  }
}
