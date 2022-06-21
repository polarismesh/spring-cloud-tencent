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
 *
 */

package com.tencent.cloud.polaris.circuitbreaker.example;

import com.tencent.cloud.polaris.circuitbreaker.PolarisResponseErrorHandler;
import com.tencent.cloud.polaris.circuitbreaker.PolarisRestTemplateResponseErrorHandler;
import com.tencent.polaris.api.core.ConsumerAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Circuit breaker example caller application.
 *
 * @author Haotian Zhang
 */
@SpringBootApplication
@EnableFeignClients
public class ServiceA {

	public static void main(String[] args) {
		SpringApplication.run(ServiceA.class);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(ConsumerAPI consumerAPI, @Autowired(required = false) PolarisResponseErrorHandler polarisResponseErrorHandler) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new PolarisRestTemplateResponseErrorHandler(consumerAPI, polarisResponseErrorHandler));
		return restTemplate;
	}

}
