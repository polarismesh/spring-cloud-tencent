/*
 * Tencent is pleased to support the open source community by making Spring Cloud Tencent available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.tencent.cloud.polaris.tsf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tencent.cloud.common.util.inet.PolarisInetUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;

/**
 * Defines configuration for service discovery and registration.
 *
 * @author Spencer Gibb
 * @author Donnabell Dmello
 * @author Venil Noronha
 * @author Richard Kettelerij
 */
@ConfigurationProperties("tsf.discovery")
public class TsfDiscoveryProperties {

	protected static final String MANAGEMENT = "management";

	private InetUtils.HostInfo hostInfo;

	@Value("${tsf_token:${consul.token:${CONSUL_TOKEN:${spring.cloud.consul.token:${SPRING_CLOUD_CONSUL_TOKEN:}}}}}")
	private String aclToken;

	/**
	 * Tags to use when registering service.
	 */
	private List<String> tags = new ArrayList<>();

	/**
	 * If service discovery enabled.
	 */
	private boolean enabled = true;

	/**
	 * Tags to use when registering management service.
	 */
	private List<String> managementTags = new ArrayList<>();

	/**
	 * Alternate server path to invoke for health checking .
	 */
	private String healthCheckPath = "/actuator/health";

	/**
	 * Custom health check url to override default.
	 */
	private String healthCheckUrl;

	/**
	 * How often to perform the health check (e.g. 10s), defaults to 10s.
	 */
	private String healthCheckInterval = "10s";

	/**
	 * Timeout for health check (e.g. 10s).
	 */
	private String healthCheckTimeout;

	/**
	 * Timeout to deregister services critical for longer than timeout (e.g. 30m).
	 * Requires consul version 7.x or higher.
	 */
	private String healthCheckCriticalTimeout;

	/**
	 * IP address to use when accessing service (must also set preferIpAddress to use).
	 */
	private String ipAddress;

	/**
	 * Hostname to use when accessing server.
	 */
	private String hostname;

	/**
	 * Port to register the service under (defaults to listening port).
	 */
	private Integer port;

	/**
	 * Port to register the management service under (defaults to management port).
	 */
	private Integer managementPort;

	private Lifecycle lifecycle = new Lifecycle();

	/**
	 * Use ip address rather than hostname during registration.
	 * 默认使用IP地址
	 */
	private boolean preferIpAddress = true;

	/**
	 * Source of how we will determine the address to use.
	 */
	private boolean preferAgentAddress = false;

	/**
	 * The delay between calls to watch consul catalog in millis, default is 1000.
	 */
	private int catalogServicesWatchDelay = 1000;

	/**
	 * The number of seconds to block while watching consul catalog, default is 2.
	 */
	private int catalogServicesWatchTimeout = 55;

	/**
	 * Service name.
	 */
	private String serviceName;

	/**
	 * Unique service instance id.
	 */
	@Value("${tsf_instance_id:${spring.cloud.consul.discovery.instanceId:${SPRING_CLOUD_CONSUL_DISCOVERY_INSTANCEID:}}}")
	private String instanceId;

	/**
	 * Service instance zone.
	 */
	private String instanceZone;

	/**
	 * Service instance group.
	 */
	private String instanceGroup;

	/**
	 * Service instance zone comes from metadata.
	 * This allows changing the metadata tag name.
	 */
	private String defaultZoneMetadataName = "zone";

	/**
	 * Whether to register an http or https service.
	 */
	private String scheme = "http";

	/**
	 * Suffix to use when registering management service.
	 */
	private String managementSuffix = MANAGEMENT;

	/**
	 * Map of serviceId's -> tag to query for in server list.
	 * This allows filtering services by a single tag.
	 */
	private Map<String, String> serverListQueryTags = new HashMap<>();

	/**
	 * Map of serviceId's -> datacenter to query for in server list.
	 * This allows looking up services in another datacenters.
	 */
	private Map<String, String> datacenters = new HashMap<>();

	/**
	 * Tag to query for in service list if one is not listed in serverListQueryTags.
	 */
	private String defaultQueryTag;

	/**
	 * Add the 'passing` parameter to /v1/health/service/serviceName.
	 * This pushes health check passing to the server.
	 */
	private boolean queryPassing = true;

	/**
	 * Register as a service in consul.
	 */
	private boolean register = true;

	/**
	 * Disable automatic de-registration of service in consul.
	 */
	private boolean deregister = true;

	/**
	 * Register health check in consul. Useful during development of a service.
	 */
	private boolean registerHealthCheck = true;

	/**
	 * Throw exceptions during service registration if true, otherwise, log
	 * warnings (defaults to true).
	 */
	private boolean failFast = true;

	/**
	 * Skips certificate verification during service checks if true, otherwise
	 * runs certificate verification.
	 */
	private Boolean healthCheckTlsSkipVerify;

	/**
	 * tsf service consul registration tags.
	 *
	 * applicationId 应用Id
	 */
	@Value("${tsf_application_id:}")
	private String tsfApplicationId;

	/**
	 * tsf service consul registration tags.
	 *
	 * groupId 部署组Id
	 */
	@Value("${tsf_group_id:}")
	private String tsfGroupId;

	/**
	 * 仅本地测试时使用.
	 */
	@Value("${tsf_namespace_id:}")
	private String tsfNamespaceId;

	/**
	 * tsf service consul registration tags.
	 *
	 * progVersion 包版本
	 */
	@Value("${tsf_prog_version:}")
	private String tsfProgVersion;

	/**
	 * tsf service consul registration tags.
	 *
	 * 地域信息
	 */
	@Value("${tsf_region:}")
	private String tsfRegion;

	/**
	 * tsf service consul registration tags.
	 *
	 * 可用区信息
	 */
	@Value("${tsf_zone:}")
	private String tsfZone;

	/**
	 * 有状态服务回调的线程池.
	 */
	private int callbackPoolSize = 10;

	private long callbackInitialDelay = 10 * 1000L;

	private long callbackErrorDelay = 30 * 1000L;


	/**
	 * 是否开启零实例保护，默认开启。开启时如果 consul 返回在线实例为0，用上次的缓存（正常来说线上环境不应该有provider全下线的情况）.
	 * 定制化双发现时可能需要关闭，此时如果provider下线，则返回空列表。如果 consul 连接不上则用缓存.
	 */
	private boolean zeroInstanceProtect = true;

	private int testConnectivityTimeout = 5000;

	private Map<String, String> serviceMeta;

	@SuppressWarnings("unused")
	private TsfDiscoveryProperties() {
		this.managementTags.add(MANAGEMENT);
	}

	public TsfDiscoveryProperties(PolarisInetUtils polarisInetUtils) {
		this();
		this.hostInfo = polarisInetUtils.findFirstNonLoopbackHostInfo();
		this.ipAddress = this.hostInfo.getIpAddress();
		this.hostname = this.hostInfo.getHostname();
	}

	/**
	 * @param serviceId The service who's filtering tag is being looked up
	 * @return The tag the given service id should be filtered by, or null.
	 */
	public String getQueryTagForService(String serviceId) {
		String tag = serverListQueryTags.get(serviceId);
		return tag != null ? tag : defaultQueryTag;
	}

	public String getHostname() {
		return this.preferIpAddress ? this.ipAddress : this.hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
		this.hostInfo.override = true;
	}

	private InetUtils.HostInfo getHostInfo() {
		return hostInfo;
	}

	private void setHostInfo(InetUtils.HostInfo hostInfo) {
		this.hostInfo = hostInfo;
	}

	public String getAclToken() {
		return aclToken;
	}

	public void setAclToken(String aclToken) {
		this.aclToken = aclToken;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<String> getManagementTags() {
		return managementTags;
	}

	public void setManagementTags(List<String> managementTags) {
		this.managementTags = managementTags;
	}

	public String getHealthCheckPath() {
		return healthCheckPath;
	}

	public void setHealthCheckPath(String healthCheckPath) {
		this.healthCheckPath = healthCheckPath;
	}

	public String getHealthCheckUrl() {
		return healthCheckUrl;
	}

	public void setHealthCheckUrl(String healthCheckUrl) {
		this.healthCheckUrl = healthCheckUrl;
	}

	public String getHealthCheckInterval() {
		return healthCheckInterval;
	}

	public void setHealthCheckInterval(String healthCheckInterval) {
		this.healthCheckInterval = healthCheckInterval;
	}

	public String getHealthCheckTimeout() {
		return healthCheckTimeout;
	}

	public void setHealthCheckTimeout(String healthCheckTimeout) {
		this.healthCheckTimeout = healthCheckTimeout;
	}

	public String getHealthCheckCriticalTimeout() {
		return healthCheckCriticalTimeout;
	}

	public void setHealthCheckCriticalTimeout(String healthCheckCriticalTimeout) {
		this.healthCheckCriticalTimeout = healthCheckCriticalTimeout;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
		this.hostInfo.override = true;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getManagementPort() {
		return managementPort;
	}

	public void setManagementPort(Integer managementPort) {
		this.managementPort = managementPort;
	}

	public Lifecycle getLifecycle() {
		return lifecycle;
	}

	public void setLifecycle(Lifecycle lifecycle) {
		this.lifecycle = lifecycle;
	}

	public boolean isPreferIpAddress() {
		return preferIpAddress;
	}

	public void setPreferIpAddress(boolean preferIpAddress) {
		this.preferIpAddress = preferIpAddress;
	}

	public boolean isPreferAgentAddress() {
		return preferAgentAddress;
	}

	public void setPreferAgentAddress(boolean preferAgentAddress) {
		this.preferAgentAddress = preferAgentAddress;
	}

	public int getCatalogServicesWatchDelay() {
		return catalogServicesWatchDelay;
	}

	public void setCatalogServicesWatchDelay(int catalogServicesWatchDelay) {
		this.catalogServicesWatchDelay = catalogServicesWatchDelay;
	}

	public int getCatalogServicesWatchTimeout() {
		return catalogServicesWatchTimeout;
	}

	public void setCatalogServicesWatchTimeout(int catalogServicesWatchTimeout) {
		this.catalogServicesWatchTimeout = catalogServicesWatchTimeout;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceZone() {
		return instanceZone;
	}

	public void setInstanceZone(String instanceZone) {
		this.instanceZone = instanceZone;
	}

	public String getInstanceGroup() {
		return instanceGroup;
	}

	public void setInstanceGroup(String instanceGroup) {
		this.instanceGroup = instanceGroup;
	}

	public String getDefaultZoneMetadataName() {
		return defaultZoneMetadataName;
	}

	public void setDefaultZoneMetadataName(String defaultZoneMetadataName) {
		this.defaultZoneMetadataName = defaultZoneMetadataName;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getManagementSuffix() {
		return managementSuffix;
	}

	public void setManagementSuffix(String managementSuffix) {
		this.managementSuffix = managementSuffix;
	}

	public Map<String, String> getServerListQueryTags() {
		return serverListQueryTags;
	}

	public void setServerListQueryTags(Map<String, String> serverListQueryTags) {
		this.serverListQueryTags = serverListQueryTags;
	}

	public Map<String, String> getDatacenters() {
		return datacenters;
	}

	public void setDatacenters(Map<String, String> datacenters) {
		this.datacenters = datacenters;
	}

	public String getDefaultQueryTag() {
		return defaultQueryTag;
	}

	public void setDefaultQueryTag(String defaultQueryTag) {
		this.defaultQueryTag = defaultQueryTag;
	}

	public boolean isQueryPassing() {
		return queryPassing;
	}

	public void setQueryPassing(boolean queryPassing) {
		this.queryPassing = queryPassing;
	}

	public boolean isRegister() {
		return register;
	}

	public void setRegister(boolean register) {
		this.register = register;
	}

	public boolean isDeregister() {
		return deregister;
	}

	public void setDeregister(boolean deregister) {
		this.deregister = deregister;
	}

	public boolean isRegisterHealthCheck() {
		return registerHealthCheck;
	}

	public void setRegisterHealthCheck(boolean registerHealthCheck) {
		this.registerHealthCheck = registerHealthCheck;
	}

	public boolean isFailFast() {
		return failFast;
	}

	public void setFailFast(boolean failFast) {
		this.failFast = failFast;
	}

	public Boolean getHealthCheckTlsSkipVerify() {
		return healthCheckTlsSkipVerify;
	}

	public void setHealthCheckTlsSkipVerify(Boolean healthCheckTlsSkipVerify) {
		this.healthCheckTlsSkipVerify = healthCheckTlsSkipVerify;
	}

	public String getTsfApplicationId() {
		return tsfApplicationId;
	}

	public void setTsfApplicationId(final String tsfApplicationId) {
		this.tsfApplicationId = tsfApplicationId;
	}

	public String getTsfGroupId() {
		return tsfGroupId;
	}

	public void setTsfGroupId(final String tsfGroupId) {
		this.tsfGroupId = tsfGroupId;
	}

	public String getTsfNamespaceId() {
		return tsfNamespaceId;
	}

	public void setTsfNamespaceId(String tsfNamespaceId) {
		this.tsfNamespaceId = tsfNamespaceId;
	}

	public String getTsfProgVersion() {
		return tsfProgVersion;
	}

	public void setTsfProgVersion(final String tsfProgVersion) {
		this.tsfProgVersion = tsfProgVersion;
	}

	public String getTsfRegion() {
		return tsfRegion;
	}

	public void setTsfRegion(final String tsfRegion) {
		this.tsfRegion = tsfRegion;
	}

	public String getTsfZone() {
		return tsfZone;
	}

	public void setTsfZone(final String tsfZone) {
		this.tsfZone = tsfZone;
	}

	public Map<String, String> getServiceMeta() {
		return serviceMeta;
	}

	public void setServiceMeta(final Map<String, String> serviceMeta) {
		this.serviceMeta = serviceMeta;
	}

	public int getCallbackPoolSize() {
		return callbackPoolSize;
	}

	public void setCallbackPoolSize(int callbackPoolSize) {
		this.callbackPoolSize = callbackPoolSize;
	}

	public long getCallbackInitialDelay() {
		return callbackInitialDelay;
	}

	public void setCallbackInitialDelay(long callbackInitialDelay) {
		this.callbackInitialDelay = callbackInitialDelay;
	}

	public long getCallbackErrorDelay() {
		return callbackErrorDelay;
	}

	public void setCallbackErrorDelay(long callbackErrorDelay) {
		this.callbackErrorDelay = callbackErrorDelay;
	}

	public boolean isZeroInstanceProtect() {
		return zeroInstanceProtect;
	}

	public void setZeroInstanceProtect(boolean zeroInstanceProtect) {
		this.zeroInstanceProtect = zeroInstanceProtect;
	}

	public int getTestConnectivityTimeout() {
		return testConnectivityTimeout;
	}

	public void setTestConnectivityTimeout(int testConnectivityTimeout) {
		this.testConnectivityTimeout = testConnectivityTimeout;
	}

	@Override
	public String toString() {
		return "ConsulDiscoveryProperties{" +
				"hostInfo=" + hostInfo +
				", aclToken='" + aclToken + '\'' +
				", tags=" + tags +
				", enabled=" + enabled +
				", managementTags=" + managementTags +
				", healthCheckPath='" + healthCheckPath + '\'' +
				", healthCheckUrl='" + healthCheckUrl + '\'' +
				", healthCheckInterval='" + healthCheckInterval + '\'' +
				", healthCheckTimeout='" + healthCheckTimeout + '\'' +
				", healthCheckCriticalTimeout='" + healthCheckCriticalTimeout + '\'' +
				", ipAddress='" + ipAddress + '\'' +
				", hostname='" + hostname + '\'' +
				", port=" + port +
				", managementPort=" + managementPort +
				", lifecycle=" + lifecycle +
				", preferIpAddress=" + preferIpAddress +
				", preferAgentAddress=" + preferAgentAddress +
				", catalogServicesWatchDelay=" + catalogServicesWatchDelay +
				", catalogServicesWatchTimeout=" + catalogServicesWatchTimeout +
				", serviceName='" + serviceName + '\'' +
				", instanceId='" + instanceId + '\'' +
				", instanceZone='" + instanceZone + '\'' +
				", instanceGroup='" + instanceGroup + '\'' +
				", defaultZoneMetadataName='" + defaultZoneMetadataName + '\'' +
				", scheme='" + scheme + '\'' +
				", managementSuffix='" + managementSuffix + '\'' +
				", serverListQueryTags=" + serverListQueryTags +
				", datacenters=" + datacenters +
				", defaultQueryTag='" + defaultQueryTag + '\'' +
				", queryPassing=" + queryPassing +
				", register=" + register +
				", deregister=" + deregister +
				", registerHealthCheck=" + registerHealthCheck +
				", failFast=" + failFast +
				", healthCheckTlsSkipVerify=" + healthCheckTlsSkipVerify +
				'}';
	}

	public static class Lifecycle {
		private boolean enabled = true;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		@Override
		public String toString() {
			return "Lifecycle{" +
					"enabled=" + enabled +
					'}';
		}
	}
}
