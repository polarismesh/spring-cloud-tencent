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

package com.tencent.cloud.polaris.context.tsf.env;

import java.util.HashMap;
import java.util.Map;

import com.tencent.polaris.api.utils.StringUtils;
import org.apache.commons.logging.Log;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Read TSF env.
 *
 * @author Haotian Zhang
 */
public final class TsfCoreEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

	/**
	 * run before {@link ConfigDataEnvironmentPostProcessor}.
	 */
	public static final int ORDER = ConfigDataEnvironmentPostProcessor.ORDER - 1;

	private final Log LOGGER;

	private TsfCoreEnvironmentPostProcessor(DeferredLogFactory logFactory) {
		this.LOGGER = logFactory.getLog(getClass());
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		String tsfAppId = environment.getProperty("tsf_app_id");
		if (StringUtils.isNotBlank(tsfAppId)) {
			Map<String, Object> defaultProperties = new HashMap<>();

			// tsf_application_id
			String tsfApplicationId = environment.getProperty("tsf_application_id");
			if (StringUtils.isBlank(tsfApplicationId)) {
				LOGGER.error("tsf_application_id is empty");
			}

			// tsf_group_id
			String tsfGroupId = environment.getProperty("tsf_group_id");
			if (StringUtils.isBlank(tsfGroupId)) {
				LOGGER.error("tsf_group_id is empty");
			}

			// tsf_namespace_id
			String tsfNamespaceId = environment.getProperty("tsf_namespace_id");
			if (StringUtils.isBlank(tsfNamespaceId)) {
				LOGGER.error("tsf_namespace_id is empty");
			}
			else {
				defaultProperties.put("spring.cloud.polaris.namespace", tsfNamespaceId);
			}

			// tsf_consul_ip
			String tsfConsulIp = environment.getProperty("tsf_consul_ip");
			if (StringUtils.isBlank(tsfConsulIp)) {
				LOGGER.error("tsf_consul_ip is empty");
			}

			// tsf_consul_port
			String tsfConsulPort = environment.getProperty("tsf_consul_port");
			if (StringUtils.isBlank(tsfConsulPort)) {
				LOGGER.error("tsf_consul_port is empty");
			}

			// tsf_token
			String tsfConsulToken = environment.getProperty("tsf_token");
			if (StringUtils.isBlank(tsfConsulToken)) {
				LOGGER.error("tsf_token is empty");
			}

			// tse_polaris_enable
			String tsePolarisEnable = environment.getProperty("tse_polaris_enable", "false");
			if (StringUtils.equals(tsePolarisEnable, "true")) {
				defaultProperties.put("spring.cloud.polaris.config.enabled", "true");
			}
			else {
				defaultProperties.put("spring.cloud.polaris.config.enabled", "true");
				defaultProperties.put("spring.cloud.polaris.config.internal-enabled", "false");
				defaultProperties.put("spring.cloud.polaris.config.data-source", "consul");
				defaultProperties.put("spring.cloud.polaris.config.address", "http://" + tsfConsulIp + ":" + tsfConsulPort);
				defaultProperties.put("spring.cloud.polaris.config.port", tsfConsulPort);
				defaultProperties.put("spring.cloud.polaris.config.token", tsfConsulToken);
				defaultProperties.put("spring.cloud.polaris.config.groups[0].namespace", "config");
				defaultProperties.put("spring.cloud.polaris.config.groups[0].name", "application");
				defaultProperties.put("spring.cloud.polaris.config.groups[0].files[0]", tsfApplicationId + "/" + tsfGroupId + "/");
				defaultProperties.put("spring.cloud.polaris.config.groups[0].files[1]", tsfNamespaceId + "/");
			}

			// tse_polaris_ip
			defaultProperties.put("spring.cloud.polaris.address", "grpc://" + environment.getProperty("tse_polaris_ip", "") + ":8091");

			// tsf_sctt_extensions_port
			defaultProperties.put("spring.cloud.polaris.stat.port", environment.getProperty("tsf_sctt_extensions_port", "11134"));

			// rule based router fail over type
			defaultProperties.put("spring.cloud.polaris.router.rule-router.fail-over", "none");

			// namespace affinity router
			defaultProperties.put("spring.cloud.polaris.router.namespace-router.enabled", "true");

			MapPropertySource propertySource = new MapPropertySource("tsf-polaris-properties", defaultProperties);
			environment.getPropertySources().addFirst(propertySource);
		}
	}
}
