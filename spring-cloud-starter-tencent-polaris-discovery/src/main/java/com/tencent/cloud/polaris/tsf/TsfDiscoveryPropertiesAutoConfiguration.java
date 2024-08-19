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

import com.tencent.cloud.common.tsf.ConditionalOnTsfEnabled;
import com.tencent.cloud.common.util.inet.PolarisInetUtils;
import com.tencent.cloud.plugin.lossless.config.LosslessProperties;
import com.tencent.cloud.polaris.PolarisDiscoveryProperties;
import com.tencent.cloud.polaris.context.config.PolarisContextProperties;
import com.tencent.cloud.polaris.context.tsf.config.TsfCoreProperties;
import com.tencent.cloud.polaris.context.tsf.consul.TsfConsulProperties;
import com.tencent.cloud.polaris.tsf.lossless.TsfLosslessConfigModifier;
import com.tencent.cloud.polaris.tsf.lossless.TsfLosslessProperties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto configuration for TSF discovery.
 *
 * @author Haotian Zhang
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnTsfEnabled
public class TsfDiscoveryPropertiesAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public TsfDiscoveryProperties tsfDiscoveryProperties(PolarisInetUtils polarisInetUtils) {
		return new TsfDiscoveryProperties(polarisInetUtils);
	}

	@Bean
	@ConditionalOnMissingBean
	public TsfHeartbeatProperties tsfHeartbeatProperties() {
		return new TsfHeartbeatProperties();
	}

	@Bean
	@ConditionalOnMissingBean
	public TsfLosslessProperties tsfLosslessProperties() {
		return new TsfLosslessProperties();
	}

	@Bean
	@ConditionalOnMissingBean
	public TsfDiscoveryConfigModifier tsfDiscoveryConfigModifier(TsfCoreProperties tsfCoreProperties,
			TsfConsulProperties tsfConsulProperties, TsfDiscoveryProperties tsfDiscoveryProperties,
			TsfHeartbeatProperties tsfHeartbeatProperties, PolarisDiscoveryProperties polarisDiscoveryProperties,
			PolarisContextProperties polarisContextProperties, ApplicationContext context) {
		return new TsfDiscoveryConfigModifier(tsfCoreProperties, tsfConsulProperties, tsfDiscoveryProperties,
				tsfHeartbeatProperties, polarisDiscoveryProperties, polarisContextProperties, context);
	}

	@Bean
	@ConditionalOnMissingBean
	public TsfZeroProtectionConfigModifier tsfZeroProtectionConfigModifier() {
		return new TsfZeroProtectionConfigModifier();
	}

	@Bean
	@ConditionalOnMissingBean
	public TsfLosslessConfigModifier tsfLosslessConfigModifier(LosslessProperties losslessProperties, TsfLosslessProperties tsfLosslessProperties) {
		return new TsfLosslessConfigModifier(losslessProperties, tsfLosslessProperties);
	}
}
