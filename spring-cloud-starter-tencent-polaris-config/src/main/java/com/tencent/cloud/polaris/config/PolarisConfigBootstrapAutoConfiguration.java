/*
 * Tencent is pleased to support the open source community by making spring-cloud-tencent available.
 *
 * Copyright (C) 2021 THL A29 Limited, a Tencent company. All rights reserved.
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

package com.tencent.cloud.polaris.config;

import com.tencent.cloud.polaris.config.adapter.AffectedConfigurationPropertiesRebinder;
import com.tencent.cloud.polaris.config.adapter.PolarisConfigFileLocator;
import com.tencent.cloud.polaris.config.condition.ConditionalOnReflectRefreshType;
import com.tencent.cloud.polaris.config.config.PolarisConfigProperties;
import com.tencent.cloud.polaris.config.config.PolarisCryptoConfigProperties;
import com.tencent.cloud.polaris.context.PolarisSDKContextManager;
import com.tencent.cloud.polaris.context.config.PolarisContextAutoConfiguration;
import com.tencent.cloud.polaris.context.config.PolarisContextProperties;
import com.tencent.polaris.configuration.api.core.ConfigFileService;
import com.tencent.polaris.configuration.factory.ConfigFileServiceFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.cloud.context.properties.ConfigurationPropertiesBeans;
import org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

/**
 * polaris config module auto configuration at bootstrap phase.
 *
 * @author lepdou 2022-03-10
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnPolarisConfigEnabled
@Import(PolarisContextAutoConfiguration.class)
public class PolarisConfigBootstrapAutoConfiguration {

	@Bean
	public PolarisConfigProperties polarisProperties() {
		return new PolarisConfigProperties();
	}

	@Bean
	public PolarisCryptoConfigProperties polarisCryptoConfigProperties() {
		return new PolarisCryptoConfigProperties();
	}

	@Bean
	@ConditionalOnConnectRemoteServerEnabled
	public ConfigFileService configFileService(PolarisSDKContextManager polarisSDKContextManager) {
		return ConfigFileServiceFactory.createConfigFileService(polarisSDKContextManager.getConfigSDKContext());
	}

	@Bean
	@ConditionalOnConnectRemoteServerEnabled
	public PolarisConfigFileLocator polarisConfigFileLocator(
			PolarisConfigProperties polarisConfigProperties,
			PolarisContextProperties polarisContextProperties,
			ConfigFileService configFileService,
			Environment environment) {
		return new PolarisConfigFileLocator(polarisConfigProperties,
				polarisContextProperties, configFileService, environment);
	}

	@Bean
	@ConditionalOnConnectRemoteServerEnabled
	public ConfigurationModifier configurationModifier(PolarisConfigProperties polarisConfigProperties,
			PolarisCryptoConfigProperties polarisCryptoConfigProperties,
			PolarisContextProperties polarisContextProperties) {
		return new ConfigurationModifier(polarisConfigProperties, polarisCryptoConfigProperties, polarisContextProperties);
	}

	@Bean
	@Primary
	@ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
	@ConditionalOnReflectRefreshType
	public ConfigurationPropertiesRebinder affectedConfigurationPropertiesRebinder(
			ConfigurationPropertiesBeans beans) {
		return new AffectedConfigurationPropertiesRebinder(beans);
	}
}
