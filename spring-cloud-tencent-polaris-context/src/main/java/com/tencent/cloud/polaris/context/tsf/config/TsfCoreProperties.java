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
	 * 仅本地测试时使用.
	 */
	@Value("${tsf_namespace_id:}")
	private String tsfNamespaceId;

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

	@Override
	public String toString() {
		return "TsfCoreProperties{" +
				"tsePolarisIp='" + tsePolarisIp + '\'' +
				", tsfConsulEnable=" + tsfConsulEnable +
				", tsePolarisEnable=" + tsePolarisEnable +
				", tsfApplicationId='" + tsfApplicationId + '\'' +
				", tsfGroupId='" + tsfGroupId + '\'' +
				", tsfNamespaceId='" + tsfNamespaceId + '\'' +
				'}';
	}
}
