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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Read TSF env.
 *
 * @author Haotian Zhang
 */
public class TsfCoreEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

	/**
	 * run before {@link ConfigDataEnvironmentPostProcessor}.
	 */
	public static final int ORDER = ConfigDataEnvironmentPostProcessor.ORDER - 1;

	@Override
	public int getOrder() {
		return ORDER;
	}

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		String tsfAppId = environment.getProperty("tsf_app_id");
		if (StringUtils.isNotBlank(tsfAppId)) {
			Map<String, Object> defaultProperties = new HashMap<>();

			// TODO 接入consul配置后需要改动这个选项的判断
			// tse_polaris_enable
			defaultProperties.put("spring.cloud.polaris.config.enabled", environment.getProperty("tse_polaris_enable", "false"));

			// tse_polaris_ip
			defaultProperties.put("spring.cloud.polaris.address", "grpc://" + environment.getProperty("tse_polaris_ip", "") + ":8091");

			// tse_polaris_ip
			defaultProperties.put("spring.cloud.polaris.stat.port", environment.getProperty("tsf_sctt_extensions_port", "11134"));

			MapPropertySource propertySource = new MapPropertySource("tsf-polaris-properties", defaultProperties);
			environment.getPropertySources().addFirst(propertySource);
		}
	}
}
