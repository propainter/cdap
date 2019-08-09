/*
 * Copyright © 2018 Cask Data, Inc.
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

package io.cdap.cdap.runtime.spi.provisioner.dataproc;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.ComputeEngineCredentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.io.CharStreams;
import io.cdap.cdap.runtime.spi.ssh.SSHPublicKey;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * Configuration for Dataproc.
 */
public class DataprocConf {
  static final String PROJECT_ID_KEY = "projectId";
  static final String AUTO_DETECT = "auto-detect";
  static final String NETWORK = "network";
  static final String NETWORK_HOST_PROJECT_ID = "networkHostProjectId";
  static final String PREFER_EXTERNAL_IP = "preferExternalIP";
  static final String STACKDRIVER_LOGGING_ENABLED = "stackdriverLoggingEnabled";
  static final String STACKDRIVER_MONITORING_ENABLED = "stackdriverMonitoringEnabled";

  private static final Pattern CLUSTER_PROPERTIES_PATTERN = Pattern.compile("^[a-zA-Z0-9\\-]+:");

  private final String accountKey;
  private final String region;
  private final String zone;
  private final String projectId;
  private final String network;
  private final String networkHostProjectID;
  private final String subnet;

  private final int masterNumNodes;
  private final int masterCPUs;
  private final int masterMemoryMB;
  private final int masterDiskGB;

  private final int workerNumNodes;
  private final int workerCPUs;
  private final int workerMemoryMB;
  private final int workerDiskGB;

  private final long pollCreateDelay;
  private final long pollCreateJitter;
  private final long pollDeleteDelay;
  private final long pollInterval;

  private final boolean preferExternalIP;
  private final boolean stackdriverLoggingEnabled;
  private final boolean stackdriverMonitoringEnabled;
  private final SSHPublicKey publicKey;
  private final Map<String, String> dataprocProperties;

  DataprocConf(DataprocConf conf, String network, String subnet) {
    this(conf.accountKey, conf.region, conf.zone, conf.projectId, conf.networkHostProjectID, network, subnet,
         conf.masterNumNodes, conf.masterCPUs, conf.masterMemoryMB, conf.masterDiskGB,
         conf.workerNumNodes, conf.workerCPUs, conf.workerMemoryMB, conf.workerDiskGB,
         conf.pollCreateDelay, conf.pollCreateJitter, conf.pollDeleteDelay, conf.pollInterval,
         conf.preferExternalIP, conf.stackdriverLoggingEnabled, conf.stackdriverMonitoringEnabled,
         conf.publicKey, conf.dataprocProperties);
  }

  private DataprocConf(@Nullable String accountKey, String region, String zone, String projectId,
                       @Nullable String networkHostProjectId, @Nullable String network, @Nullable String subnet,
                       int masterNumNodes, int masterCPUs, int masterMemoryMB,
                       int masterDiskGB, int workerNumNodes, int workerCPUs, int workerMemoryMB, int workerDiskGB,
                       long pollCreateDelay, long pollCreateJitter, long pollDeleteDelay, long pollInterval,
                       boolean preferExternalIP, boolean stackdriverLoggingEnabled,
                       boolean stackdriverMonitoringEnabled, @Nullable SSHPublicKey publicKey,
                       Map<String, String> dataprocProperties) {
    this.accountKey = accountKey;
    this.region = region;
    this.zone = zone;
    this.projectId = projectId;
    this.networkHostProjectID = networkHostProjectId;
    this.network = network;
    this.subnet = subnet;
    this.masterNumNodes = masterNumNodes;
    this.masterCPUs = masterCPUs;
    this.masterMemoryMB = masterMemoryMB;
    this.masterDiskGB = masterDiskGB;
    this.workerNumNodes = workerNumNodes;
    this.workerCPUs = workerCPUs;
    this.workerMemoryMB = workerMemoryMB;
    this.workerDiskGB = workerDiskGB;
    this.pollCreateDelay = pollCreateDelay;
    this.pollCreateJitter = pollCreateJitter;
    this.pollDeleteDelay = pollDeleteDelay;
    this.pollInterval = pollInterval;
    this.preferExternalIP = preferExternalIP;
    this.stackdriverLoggingEnabled = stackdriverLoggingEnabled;
    this.stackdriverMonitoringEnabled = stackdriverMonitoringEnabled;
    this.publicKey = publicKey;
    this.dataprocProperties = dataprocProperties;
  }

  public String getRegion() {
    return region;
  }

  public String getZone() {
    return zone;
  }

  public String getProjectId() {
    return projectId;
  }

  @Nullable
  public String getNetwork() {
    return network;
  }

  @Nullable
  public String getNetworkHostProjectID() {
    return NETWORK_HOST_PROJECT_ID;
  }

  @Nullable
  public String getSubnet() {
    return subnet;
  }

  public int getMasterNumNodes() {
    return masterNumNodes;
  }

  public int getMasterDiskGB() {
    return masterDiskGB;
  }

  public int getWorkerNumNodes() {
    return workerNumNodes;
  }

  public int getWorkerDiskGB() {
    return workerDiskGB;
  }

  public String getMasterMachineType() {
    return getMachineType(masterCPUs, masterMemoryMB);
  }

  public String getWorkerMachineType() {
    return getMachineType(workerCPUs, workerMemoryMB);
  }

  public long getPollCreateDelay() {
    return pollCreateDelay;
  }

  public long getPollCreateJitter() {
    return pollCreateJitter;
  }

  public long getPollDeleteDelay() {
    return pollDeleteDelay;
  }

  public long getPollInterval() {
    return pollInterval;
  }

  public boolean isPreferExternalIP() {
    return preferExternalIP;
  }

  public boolean isStackdriverLoggingEnabled() {
    return stackdriverLoggingEnabled;
  }

  public boolean isStackdriverMonitoringEnabled() {
    return stackdriverMonitoringEnabled;
  }

  @Nullable
  public SSHPublicKey getPublicKey() {
    return publicKey;
  }

  public Map<String, String> getDataprocProperties() {
    return dataprocProperties;
  }

  /**
   * @return GoogleCredential for use with Compute
   * @throws IOException if there was an error reading the account key
   */
  public GoogleCredential getComputeCredential() throws IOException {
    if (accountKey == null) {
      return GoogleCredential.getApplicationDefault();
    }

    try (InputStream is = new ByteArrayInputStream(accountKey.getBytes(StandardCharsets.UTF_8))) {
      return GoogleCredential.fromStream(is)
        .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
    }
  }

  /**
   * @return GoogleCredentials for use with Dataproc
   * @throws IOException if there was an error reading the account key
   */
  public GoogleCredentials getDataprocCredentials() throws IOException {
    if (accountKey == null) {
      return getComputeEngineCredentials();
    }

    try (InputStream is = new ByteArrayInputStream(accountKey.getBytes(StandardCharsets.UTF_8))) {
      return GoogleCredentials.fromStream(is);
    }
  }

  private static GoogleCredentials getComputeEngineCredentials() throws IOException {
    try {
      GoogleCredentials credentials = ComputeEngineCredentials.create();
      credentials.refreshAccessToken();
      return credentials;
    } catch (IOException e) {
      throw new IOException("Unable to get credentials from the environment. "
                              + "Please explicitly set the account key.", e);
    }
  }

  private String getMachineType(int cpus, int memoryGB) {
    // TODO: there are special names for pre-defined cpu and memory
    // for example, 4cpu 3.6gb memory is 'n1-highcpu-4', 4cpu 15gb memory is 'n1-standard-4'
    return String.format("custom-%d-%d", cpus, memoryGB);
  }

  /**
   * Create the conf from a property map while also performing validation.
   *
   * @throws IllegalArgumentException if it is an invalid config
   */
  public static DataprocConf fromProperties(Map<String, String> properties) {
    return create(properties, null);
  }

  public static DataprocConf create(Map<String, String> properties, @Nullable SSHPublicKey publicKey) {
    String accountKey = getString(properties, "accountKey");
    if (accountKey == null || AUTO_DETECT.equals(accountKey)) {
      try {
        getComputeEngineCredentials();
      } catch (IOException e) {
        throw new IllegalArgumentException(e.getMessage(), e);
      }
    }
    String projectId = getString(properties, PROJECT_ID_KEY);
    if (projectId == null || AUTO_DETECT.equals(projectId)) {
      projectId = getSystemProjectId();
    }

    String zone = getString(properties, "zone");
    if (zone == null || AUTO_DETECT.equals(zone)) {
      zone = getSystemZone();
    }
    String networkHostProjectID = getString(properties, NETWORK_HOST_PROJECT_ID);
    String network = getString(properties, NETWORK);
    if (network == null || AUTO_DETECT.equals(network)) {
      network = null;
    }
    String subnet = getString(properties, "subnet");

    int masterNumNodes = getInt(properties, "masterNumNodes", 1);
    if (masterNumNodes != 1 && masterNumNodes != 3) {
      throw new IllegalArgumentException(
        String.format("Invalid config 'masterNumNodes' = %d. Master nodes must be either 1 or 3.", masterNumNodes));
    }
    int workerNumNodes = getInt(properties, "workerNumNodes", 2);
    if (workerNumNodes == 1) {
      throw new IllegalArgumentException(
        "Invalid config 'workerNumNodes' = 1. Worker nodes must either be zero for a single node cluster, " +
          "or at least 2 for a multi node cluster.");
    }
    // TODO: more extensive validation. Each cpu number has a different allowed memory range
    // for example, 1 cpu requires memory from 3.5gb to 6.5gb in .25gb increments
    // 3 cpu requires memory from 3.6gb to 26gb in .25gb increments
    int masterCPUs = getInt(properties, "masterCPUs", 4);
    int workerCPUs = getInt(properties, "workerCPUs", 4);
    int masterMemoryGB = getInt(properties, "masterMemoryMB", 15 * 1024);
    int workerMemoryGB = getInt(properties, "workerMemoryMB", 15 * 1024);

    int masterDiskGB = getInt(properties, "masterDiskGB", 500);
    int workerDiskGB = getInt(properties, "workerDiskGB", 500);

    long pollCreateDelay = getLong(properties, "pollCreateDelay", 60);
    long pollCreateJitter = getLong(properties, "pollCreateJitter", 20);
    long pollDeleteDelay = getLong(properties, "pollDeleteDelay", 30);
    long pollInterval = getLong(properties, "pollInterval", 2);

    boolean preferExternalIP = Boolean.parseBoolean(properties.get(PREFER_EXTERNAL_IP));
    // By default stackdriver is enabled. This is for backward compatibility
    boolean stackdriverLoggingEnabled = Boolean.parseBoolean(properties.getOrDefault(STACKDRIVER_LOGGING_ENABLED,
                                                                                     "true"));
    boolean stackdriverMonitoringEnabled = Boolean.parseBoolean(properties.getOrDefault(STACKDRIVER_MONITORING_ENABLED,
                                                                                        "true"));

    Map<String, String> dataprocProps = Collections.unmodifiableMap(
      properties.entrySet().stream()
        .filter(e -> CLUSTER_PROPERTIES_PATTERN.matcher(e.getKey()).find())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
    );

    // always use 'global' region until CDAP-14376 is fixed.
    return new DataprocConf(accountKey, "global", zone, projectId, networkHostProjectID, network, subnet,
                            masterNumNodes, masterCPUs, masterMemoryGB, masterDiskGB,
                            workerNumNodes, workerCPUs, workerMemoryGB, workerDiskGB,
                            pollCreateDelay, pollCreateJitter, pollDeleteDelay, pollInterval,
                            preferExternalIP, stackdriverLoggingEnabled,
                            stackdriverMonitoringEnabled, publicKey, dataprocProps);
  }

  // the UI never sends nulls, it only sends empty strings.
  @Nullable
  private static String getString(Map<String, String> properties, String key) {
    String val = properties.get(key);
    if (val != null && val.isEmpty()) {
      return null;
    }
    return val;
  }

  private static int getInt(Map<String, String> properties, String key, int defaultVal) {
    String valStr = properties.get(key);
    if (valStr == null || valStr.isEmpty()) {
      return defaultVal;
    }
    try {
      int val = Integer.parseInt(valStr);
      if (val < 0) {
        throw new IllegalArgumentException(
          String.format("Invalid config '%s' = '%s'. Must be a positive integer.", key, valStr));
      }
      return val;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
        String.format("Invalid config '%s' = '%s'. Must be a valid, positive integer.", key, valStr));
    }
  }

  private static long getLong(Map<String, String> properties, String key, long defaultVal) {
    String valStr = properties.get(key);
    if (valStr == null || valStr.isEmpty()) {
      return defaultVal;
    }
    try {
      long val = Long.parseLong(valStr);
      if (val < 0) {
        throw new IllegalArgumentException(
          String.format("Invalid config '%s' = '%s'. Must be a positive long.", key, valStr));
      }
      return val;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
        String.format("Invalid config '%s' = '%s'. Must be a valid, positive long.", key, valStr));
    }
  }

  /**
   * Get network from the metadata server.
   */
  static String getSystemNetwork() {
    try {
      String network = getMetadata("instance/network-interfaces/0/network");
      // will be something like projects/<project-number>/networks/default
      return network.substring(network.lastIndexOf('/') + 1);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to get the network from the environment. "
                                           + "Please explicitly set the network.", e);
    }
  }

  /**
   * Get zone from the metadata server.
   */
  private static String getSystemZone() {
    try {
      String zone = getMetadata("instance/zone");
      // will be something like projects/<project-number>/zones/us-east1-b
      return zone.substring(zone.lastIndexOf('/') + 1);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to get the zone from the environment. "
                                           + "Please explicitly set the zone.", e);
    }
  }

  /**
   * Get project id from the metadata server.
   */
  static String getSystemProjectId() {
    try {
      return getMetadata("project/project-id");
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to get project id from the environment. "
                                           + "Please explicitly set the project id and account key.", e);
    }
  }

  /**
   * Makes a request to the metadata server that lives on the VM, as described at
   * https://cloud.google.com/compute/docs/storing-retrieving-metadata.
   */
  private static String getMetadata(String resource) throws IOException {
    URL url = new URL("http://metadata.google.internal/computeMetadata/v1/" + resource);
    HttpURLConnection connection = null;
    try {
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("Metadata-Flavor", "Google");
      connection.connect();
      try (Reader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
        return CharStreams.toString(reader);
      }
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }
}
