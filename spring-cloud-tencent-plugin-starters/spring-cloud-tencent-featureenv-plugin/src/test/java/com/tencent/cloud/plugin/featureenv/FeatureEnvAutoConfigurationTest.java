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

package com.tencent.cloud.plugin.featureenv;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link FeatureEnvAutoConfiguration}.
 *
 * @author Hoatian Zhang
 */
@ExtendWith(MockitoExtension.class)
public class FeatureEnvAutoConfigurationTest {

	private final ApplicationContextRunner enabledApplicationContextRunner = new ApplicationContextRunner();
	private final ApplicationContextRunner disabledApplicationContextRunner = new ApplicationContextRunner();

	@Test
	public void testEnabled() {
		this.enabledApplicationContextRunner.withConfiguration(AutoConfigurations.of(FeatureEnvAutoConfiguration.class))
				.run(context -> {
					assertThat(context).hasSingleBean(FeatureEnvProperties.class);
					assertThat(context).hasSingleBean(FeatureEnvRouterRequestInterceptor.class);
				});
	}

	@Test
	public void testDisabled() {
		this.disabledApplicationContextRunner.withConfiguration(AutoConfigurations.of(FeatureEnvAutoConfiguration.class))
				.withPropertyValues("spring.cloud.tencent.plugin.router.feature-env.enabled=false")
				.run(context -> {
					assertThat(context).doesNotHaveBean(FeatureEnvProperties.class);
					assertThat(context).doesNotHaveBean(FeatureEnvRouterRequestInterceptor.class);
				});
	}
}
