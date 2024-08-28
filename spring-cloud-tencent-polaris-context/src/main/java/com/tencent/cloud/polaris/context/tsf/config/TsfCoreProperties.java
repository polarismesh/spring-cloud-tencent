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

package com.tencent.cloud.polaris.context.tsf.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Core properties.
 *
 * @author Haotian Zhang
 */
@ConfigurationProperties("tsf")
public class TsfCoreProperties {

	@Value("${tse_polaris_ip:}")
	private String tsePolarisIp = "";

	@Value("${tsf_consul_enable:false}")
	private boolean tsfConsulEnable = false;

	@Value("${tse_polaris_enable:false}")
	private boolean tsePolarisEnable = false;

	/**
	 * Unique service instance id.
	 */
	@Value("${tsf_instance_id:${spring.cloud.consul.discovery.instanceId:${SPRING_CLOUD_CONSUL_DISCOVERY_INSTANCEID:}}}")
	private String instanceId;

	/**
	 * tsf service consul registration tags.
	 * <p>
	 * applicationId 应用Id
	 */
	@Value("${tsf_application_id:}")
	private String tsfApplicationId;

	/**
	 * tsf service consul registration tags.
	 * <p>
	 * groupId 部署组Id
	 */
	@Value("${tsf_group_id:}")
	private String tsfGroupId;

	/**
	 * tsf service consul registration tags.
	 *
	 * progVersion 包版本
	 */
	@Value("${tsf_prog_version:}")
	private String tsfProgVersion;

	/**
	 * 仅本地测试时使用.
	 */
	@Value("${tsf_namespace_id:}")
	private String tsfNamespaceId;

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
	 * Tags to use when registering service.
	 */
	@Value("${tsf.discovery.tags:}")
	private List<String> tags = new ArrayList<>();

	/**
	 * Service instance zone.
	 */
	@Value("${tsf.discovery.instanceZone:}")
	private String instanceZone;

	/**
	 * Service instance group.
	 */
	@Value("${tsf.discovery.instanceGroup:}")
	private String instanceGroup;

	/**
	 * Service instance zone comes from metadata.
	 * This allows changing the metadata tag name.
	 */
	@Value("${tsf.discovery.defaultZoneMetadataName:zone}")
	private String defaultZoneMetadataName = "zone";

	/**
	 * Whether to register an http or https service.
	 */
	@Value("${tsf.discovery.scheme:http}")
	private String scheme = "http";

	public String getTsePolarisIp() {
		return tsePolarisIp;
	}

	public void setTsePolarisIp(String tsePolarisIp) {
		this.tsePolarisIp = tsePolarisIp;
	}

	public boolean isTsfConsulEnable() {
		return tsfConsulEnable;
	}

	public void setTsfConsulEnable(boolean tsfConsulEnable) {
		this.tsfConsulEnable = tsfConsulEnable;
	}

	public boolean isTsePolarisEnable() {
		return tsePolarisEnable;
	}

	public void setTsePolarisEnable(boolean tsePolarisEnable) {
		this.tsePolarisEnable = tsePolarisEnable;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
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

	public String getTsfProgVersion() {
		return tsfProgVersion;
	}

	public void setTsfProgVersion(final String tsfProgVersion) {
		this.tsfProgVersion = tsfProgVersion;
	}

	public String getTsfNamespaceId() {
		return tsfNamespaceId;
	}

	public void setTsfNamespaceId(String tsfNamespaceId) {
		this.tsfNamespaceId = tsfNamespaceId;
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

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
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

	@Override
	public String toString() {
		return "TsfCoreProperties{" +
				"tsePolarisIp='" + tsePolarisIp + '\'' +
				", tsfConsulEnable=" + tsfConsulEnable +
				", tsePolarisEnable=" + tsePolarisEnable +
				", instanceId='" + instanceId + '\'' +
				", tsfApplicationId='" + tsfApplicationId + '\'' +
				", tsfGroupId='" + tsfGroupId + '\'' +
				", tsfProgVersion='" + tsfProgVersion + '\'' +
				", tsfNamespaceId='" + tsfNamespaceId + '\'' +
				", tsfRegion='" + tsfRegion + '\'' +
				", tsfZone='" + tsfZone + '\'' +
				", tags=" + tags +
				", instanceZone='" + instanceZone + '\'' +
				", instanceGroup='" + instanceGroup + '\'' +
				", defaultZoneMetadataName='" + defaultZoneMetadataName + '\'' +
				", scheme='" + scheme + '\'' +
				'}';
	}
}
