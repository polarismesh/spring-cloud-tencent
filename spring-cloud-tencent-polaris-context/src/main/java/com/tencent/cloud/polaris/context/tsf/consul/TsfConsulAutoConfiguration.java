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

package com.tencent.cloud.polaris.context.tsf.consul;

import com.tencent.cloud.polaris.context.tsf.ConditionalOnTsfEnabled;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConditionalOnTsfEnabled
public class TsfConsulAutoConfiguration {

	static {
		// 默认关闭对 discovery client（consul）的健康探测，避免 consul 故障时，影响监控探测
		System.setProperty("spring.cloud.discovery.client.health-indicator.enabled", "false");
	}

	@Bean
	@ConditionalOnMissingBean
	public TsfConsulProperties tsfConsulProperties() {
		return new TsfConsulProperties();
	}
}
