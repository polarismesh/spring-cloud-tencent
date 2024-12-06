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

package com.tencent.cloud.polaris.context.logging;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to get <code>spring.cloud.polaris.logging.path</code> from environment.
 *
 * @author wenxuan70
 */
public class PolarisLoggingPathPropertyTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withPropertyValues("spring.cloud.polaris.logging.path=/tmp/polaris/logs");

	@Test
	public void testGetPropertyFromEnvironment() {
		contextRunner.run(context -> {
			String loggingPath = context.getEnvironment().getProperty("spring.cloud.polaris.logging.path");
			assertThat(loggingPath).isEqualTo("/tmp/polaris/logs");
		});
	}
}
