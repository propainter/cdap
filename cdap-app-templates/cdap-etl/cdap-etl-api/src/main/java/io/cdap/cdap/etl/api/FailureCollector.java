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

package io.cdap.cdap.etl.api;

import io.cdap.cdap.api.annotation.Beta;
import io.cdap.cdap.etl.api.validation.ValidationException;
import io.cdap.cdap.etl.api.validation.ValidationFailure;

import javax.annotation.Nullable;

/**
 * Failure collector is responsible to collect {@link ValidationFailure}s.
 */
@Beta
public interface FailureCollector {

  /**
   * Add a validation failure to this failure collector.
   *
   * @param message failure message
   * @param correctiveAction corrective action
   * @return a validation failure
   */
  ValidationFailure addFailure(String message, @Nullable String correctiveAction);

  /**
   * Throws validation exception if there are any failures that are added to the failure collector through
   * {@link #addFailure(String, String)}.
   *
   * @throws ValidationException if there are any validation failures
   */
  void throwIfFailure() throws ValidationException;
}
