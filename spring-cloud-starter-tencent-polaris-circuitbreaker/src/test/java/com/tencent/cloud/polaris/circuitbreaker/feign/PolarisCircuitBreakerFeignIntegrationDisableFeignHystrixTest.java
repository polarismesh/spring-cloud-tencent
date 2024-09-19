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

package com.tencent.cloud.polaris.circuitbreaker.feign;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.tencent.polaris.test.common.Consts.NAMESPACE_TEST;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * @author Shedfree Wu
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT,
		classes = PolarisCircuitBreakerFeignIntegrationTest.TestConfig.class,
		properties = {
				"feign.hystrix.enabled=false",
				"spring.cloud.gateway.enabled=false",
				"feign.circuitbreaker.enabled=true",
				"spring.cloud.polaris.namespace=" + NAMESPACE_TEST,
				"spring.cloud.polaris.service=test"
		})
public class PolarisCircuitBreakerFeignIntegrationDisableFeignHystrixTest {

	@Autowired
	private PolarisCircuitBreakerFeignIntegrationTest.EchoService echoService;

	@Test
	public void testFeignClient() {
		assertThatThrownBy(() -> {
			echoService.echo("test");
		}).isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Load balancer does not have available server for client");
	}
}
