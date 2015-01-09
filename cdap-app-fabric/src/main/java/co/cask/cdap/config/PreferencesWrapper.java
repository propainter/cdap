/*
 * Copyright © 2015 Cask Data, Inc.
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

package co.cask.cdap.config;

import co.cask.cdap.api.common.Bytes;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import java.util.Map;

/**
 * Wrapper around {@link ConfigStore} specifically for Preferences API.
 */
public class PreferencesWrapper {
  private static final String PREFERENCES = "preferences";
  private static final String INSTANCE_NAME = "global";
  private static final String NAMESPACE = "default";

  private final ConfigStore configStore;

  @Inject
  public PreferencesWrapper(ConfigStore configStore) {
    this.configStore = configStore;
  }

  private Map<String, String> getPropertiesHelper(String id) {
    Map<String, String> value = Maps.newHashMap();
    try {
      Config config = configStore.get(NAMESPACE, PREFERENCES, id);
      value.putAll(config.getProperties());
    } catch (ConfigNotFoundException e) {
      //no-op - return empty map
    }
    return value;
  }

  private void setPropertiesHelper(String id, Map<String, String> propertyMap) {
    Config config = new Config(id, propertyMap);
    configStore.createOrUpdate(NAMESPACE, PREFERENCES, config);
  }

  private void deletePropertiesHelper(String id) {
    try {
      configStore.delete(NAMESPACE, PREFERENCES, id);
    } catch (ConfigNotFoundException e) {
      //no-op
    }
  }

  public Map<String, String> getProperties() {
    return getPropertiesHelper(generateId());
  }

  public Map<String, String> getProperties(String namespace) {
    return getPropertiesHelper(generateId(namespace));
  }

  public Map<String, String> getProperties(String namespace, String appId) {
    return getPropertiesHelper(generateId(namespace, appId));
  }

  public Map<String, String> getProperties(String namespace, String appId, String programType, String programId) {
    return getPropertiesHelper(generateId(namespace, appId, programType, programId));
  }

  public Map<String, String> getResolvedProperties() {
    return getProperties();
  }

  public Map<String, String> getResolvedProperties(String namespace) {
    Map<String, String> propMap = getResolvedProperties();
    propMap.putAll(getProperties(namespace));
    return propMap;
  }

  public Map<String, String> getResolvedProperties(String namespace, String appId) {
    Map<String, String> propMap = getResolvedProperties(namespace);
    propMap.putAll(getProperties(namespace, appId));
    return propMap;
  }

  public Map<String, String> getResolvedProperties(String namespace, String appId, String programType,
                                                   String programId) {
    Map<String, String> propMap = getResolvedProperties(namespace, appId);
    propMap.putAll(getProperties(namespace, appId, programType, programId));
    return propMap;
  }

  public void setProperties(Map<String, String> propMap) {
    setPropertiesHelper(generateId(), propMap);
  }

  public void setProperties(String namespace, Map<String, String> propMap) {
    setPropertiesHelper(generateId(namespace), propMap);
  }

  public void setProperties(String namespace, String appId, Map<String, String> propMap) {
    setPropertiesHelper(generateId(namespace, appId), propMap);
  }

  public void setProperties(String namespace, String appId, String programType, String programId,
                            Map<String, String> propMap) {
    setPropertiesHelper(generateId(namespace, appId, programType, programId), propMap);
  }

  public void deleteProperties() {
    deletePropertiesHelper(generateId());
  }

  public void deleteProperties(String namespace) {
    deletePropertiesHelper(generateId(namespace));
  }

  public void deleteProperties(String namespace, String appId) {
    deletePropertiesHelper(generateId(namespace, appId));
  }

  public void deleteProperties(String namespace, String appId, String programType, String programId) {
    deletePropertiesHelper(generateId(namespace, appId, programType, programId));
  }

  private String generateId() {
    return getMultipartKey(INSTANCE_NAME);
  }

  private String generateId(String namespace) {
    return getMultipartKey(INSTANCE_NAME, namespace);
  }

  private String generateId(String namespace, String appId) {
    return getMultipartKey(INSTANCE_NAME, namespace, appId);
  }

  private String generateId(String namespace, String appId, String programType, String programId) {
    return getMultipartKey(INSTANCE_NAME, namespace, appId, programType, programId);
  }

  private String getMultipartKey(String... parts) {
    int sizeOfParts = 0;
    for (String part : parts) {
      sizeOfParts += part.length();
    }

    byte[] result = new byte[sizeOfParts + (parts.length * Bytes.SIZEOF_INT)];

    int offset = 0;
    for (String part : parts) {
      Bytes.putInt(result, offset, part.length());
      offset += Bytes.SIZEOF_INT;
      Bytes.putBytes(result, offset, part.getBytes(), 0, part.length());
      offset += part.length();
    }
    return Bytes.toString(result);
  }
}
