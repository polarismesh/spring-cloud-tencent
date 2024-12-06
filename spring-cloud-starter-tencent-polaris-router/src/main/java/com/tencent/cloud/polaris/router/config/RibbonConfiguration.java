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

package com.tencent.cloud.polaris.router.config;

import com.tencent.cloud.polaris.router.beanprocessor.PolarisLoadBalancerCompositeRuleBeanPostProcessor;

import org.springframework.context.annotation.Bean;

/**
 * Configuration for ribbon components. IRule is not singleton bean, Each service corresponds to an IRule.
 *
 * @author lepdou 2022-05-17
 */
public class RibbonConfiguration {

	@Bean
	public PolarisLoadBalancerCompositeRuleBeanPostProcessor polarisLoadBalancerCompositeRuleBeanPostProcessor() {
		return new PolarisLoadBalancerCompositeRuleBeanPostProcessor();
	}
}
