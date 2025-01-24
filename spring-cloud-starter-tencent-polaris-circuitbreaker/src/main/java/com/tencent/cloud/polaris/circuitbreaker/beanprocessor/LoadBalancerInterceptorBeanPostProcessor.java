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

package com.tencent.cloud.polaris.circuitbreaker.beanprocessor;

import com.tencent.cloud.polaris.circuitbreaker.instrument.resttemplate.PolarisLoadBalancerInterceptor;
import com.tencent.cloud.rpc.enhancement.plugin.EnhancedPluginRunner;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestFactory;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

/**
 * LoadBalancerInterceptorBeanPostProcessor is used to wrap the default LoadBalancerInterceptor implementation and returns a custom PolarisLoadBalancerInterceptor.
 *
 * @author Shedfree Wu
 */
public class LoadBalancerInterceptorBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware, Ordered {
	/**
	 * The order of the bean post processor. if user want to wrap it(CustomLoadBalancerInterceptor -> PolarisLoadBalancerInterceptor), CustomLoadBalancerInterceptorBeanPostProcessor's order should be bigger than ${@link POLARIS_LOAD_BALANCER_INTERCEPTOR_POST_PROCESSOR_ORDER}.
	 */
	public static final int POLARIS_LOAD_BALANCER_INTERCEPTOR_POST_PROCESSOR_ORDER = 0;

	private BeanFactory factory;

	@Override
	public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
		this.factory = beanFactory;
	}

	@Override
	public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
		if (bean instanceof LoadBalancerInterceptor) {
			// Support rest template router.
			// Replaces the default LoadBalancerInterceptor implementation and returns a custom PolarisLoadBalancerInterceptor
			LoadBalancerRequestFactory requestFactory = this.factory.getBean(LoadBalancerRequestFactory.class);
			LoadBalancerClient loadBalancerClient = this.factory.getBean(LoadBalancerClient.class);
			EnhancedPluginRunner pluginRunner = this.factory.getBean(EnhancedPluginRunner.class);
			return new PolarisLoadBalancerInterceptor(loadBalancerClient, requestFactory, pluginRunner);
		}
		return bean;
	}

	@Override
	public int getOrder() {
		return POLARIS_LOAD_BALANCER_INTERCEPTOR_POST_PROCESSOR_ORDER;
	}
}
