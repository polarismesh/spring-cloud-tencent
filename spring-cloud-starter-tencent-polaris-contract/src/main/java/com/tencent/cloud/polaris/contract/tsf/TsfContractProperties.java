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

package com.tencent.cloud.polaris.contract.tsf;

import com.tencent.cloud.polaris.contract.config.ExtendedContractProperties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for TSF contract.
 *
 * @author Haotian Zhang
 */
@ConfigurationProperties("tsf.swagger")
public class TsfContractProperties implements ExtendedContractProperties {

	@Value("${tsf.swagger.basePackage:}")
	private String basePackage;

	@Value("${tsf.swagger.excludePath:}")
	private String excludePath;

	@Value("${tsf.swagger.enabled:true}")
	private boolean enabled;

	@Value("${tsf.swagger.group:polaris}")
	private String groupName;

	@Value("${tsf.swagger.basePath:/**}")
	private String basePath;

	@Value("${tsf.swagger.doc.auto-startup:true}")
	private boolean exposure;

	/**
	 * applicationId 应用Id.
	 */
	@Value("${tsf_application_id:}")
	private String name;

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String getBasePackage() {
		return basePackage;
	}

	@Override
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	@Override
	public String getExcludePath() {
		return excludePath;
	}

	@Override
	public void setExcludePath(String excludePath) {
		this.excludePath = excludePath;
	}

	@Override
	public String getGroup() {
		return groupName;
	}

	@Override
	public void setGroup(String group) {
		this.groupName = group;
	}

	@Override
	public String getBasePath() {
		return basePath;
	}

	@Override
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	@Override
	public boolean isExposure() {
		return exposure;
	}

	@Override
	public void setExposure(boolean exposure) {
		this.exposure = exposure;
	}

	@Override
	public boolean isReportEnabled() {
		return enabled;
	}

	@Override
	public void setReportEnabled(boolean reportEnabled) {

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
