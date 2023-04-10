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

package com.tencent.cloud.rpc.enhancement.config;

import java.util.Collections;
import java.util.List;

import com.tencent.cloud.polaris.context.ConditionalOnPolarisEnabled;
import com.tencent.cloud.polaris.context.config.PolarisContextAutoConfiguration;
import com.tencent.cloud.rpc.enhancement.feign.DefaultEnhancedFeignPluginRunner;
import com.tencent.cloud.rpc.enhancement.feign.EnhancedFeignBeanPostProcessor;
import com.tencent.cloud.rpc.enhancement.feign.EnhancedFeignPluginRunner;
import com.tencent.cloud.rpc.enhancement.feign.plugin.EnhancedFeignPlugin;
import com.tencent.cloud.rpc.enhancement.feign.plugin.reporter.ExceptionPolarisReporter;
import com.tencent.cloud.rpc.enhancement.feign.plugin.reporter.SuccessPolarisReporter;
import com.tencent.cloud.rpc.enhancement.resttemplate.BlockingLoadBalancerClientAspect;
import com.tencent.cloud.rpc.enhancement.scg.PolarisGatewayReporter;
import com.tencent.cloud.rpc.enhancement.resttemplate.PolarisRestTemplateReporter;
import com.tencent.cloud.rpc.enhancement.scg.EnhancedPolarisHttpHeadersFilter;
import com.tencent.cloud.rpc.enhancement.webclient.EnhancedWebClientReporter;
import com.tencent.polaris.api.core.ConsumerAPI;
import com.tencent.polaris.circuitbreak.api.CircuitBreakAPI;
import com.tencent.polaris.circuitbreak.factory.CircuitBreakAPIFactory;
import com.tencent.polaris.client.api.SDKContext;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Auto Configuration for Polaris {@link feign.Feign} OR {@link RestTemplate} which can automatically bring in the call
 * results for reporting.
 *
 * @author <a href="mailto:iskp.me@gmail.com">Palmer.Xu</a> 2022-06-29
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnPolarisEnabled
@ConditionalOnProperty(value = "spring.cloud.tencent.rpc-enhancement.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(RpcEnhancementReporterProperties.class)
@AutoConfigureAfter(PolarisContextAutoConfiguration.class)
public class RpcEnhancementAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(CircuitBreakAPI.class)
	public CircuitBreakAPI circuitBreakAPI(SDKContext polarisContext) {
		return CircuitBreakAPIFactory.createCircuitBreakAPIByContext(polarisContext);
	}

	/**
	 * Configuration for Polaris {@link feign.Feign} which can automatically bring in the call
	 * results for reporting.
	 *
	 * @author Haotian Zhang
	 */
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(name = "org.springframework.cloud.openfeign.FeignAutoConfiguration")
	@AutoConfigureBefore(name = "org.springframework.cloud.openfeign.FeignAutoConfiguration")
	@Role(RootBeanDefinition.ROLE_INFRASTRUCTURE)
	protected static class PolarisFeignClientAutoConfiguration {

		@Bean
		public EnhancedFeignPluginRunner enhancedFeignPluginRunner(
				@Autowired(required = false) List<EnhancedFeignPlugin> enhancedFeignPlugins) {
			return new DefaultEnhancedFeignPluginRunner(enhancedFeignPlugins);
		}

		@Bean
		public EnhancedFeignBeanPostProcessor polarisFeignBeanPostProcessor(@Lazy EnhancedFeignPluginRunner pluginRunner) {
			return new EnhancedFeignBeanPostProcessor(pluginRunner);
		}

		@Configuration
		static class PolarisReporterConfig {

			@Bean
			public SuccessPolarisReporter successPolarisReporter(RpcEnhancementReporterProperties properties,
					SDKContext context,
					ConsumerAPI consumerAPI,
					CircuitBreakAPI circuitBreakAPI) {
				return new SuccessPolarisReporter(properties, context, consumerAPI, circuitBreakAPI);
			}

			@Bean
			public ExceptionPolarisReporter exceptionPolarisReporter(RpcEnhancementReporterProperties properties,
					SDKContext context,
					ConsumerAPI consumerAPI,
					CircuitBreakAPI circuitBreakAPI) {
				return new ExceptionPolarisReporter(properties, context, consumerAPI, circuitBreakAPI);
			}
		}
	}

	/**
	 * Configuration for Polaris {@link RestTemplate} which can automatically bring in the call
	 * results for reporting.
	 *
	 * @author wh 2022/6/21
	 */
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(name = "org.springframework.web.client.RestTemplate")
	protected static class PolarisRestTemplateAutoConfiguration {

		@LoadBalanced
		@Autowired(required = false)
		private List<RestTemplate> restTemplates = Collections.emptyList();

		@Bean
		public PolarisRestTemplateReporter polarisRestTemplateReporter(RpcEnhancementReporterProperties properties,
				SDKContext context,
				ConsumerAPI consumerAPI,
				CircuitBreakAPI circuitBreakAPI) {
			return new PolarisRestTemplateReporter(properties, context, consumerAPI, circuitBreakAPI);
		}

		@Bean
		public SmartInitializingSingleton setPolarisReporterForRestTemplate(PolarisRestTemplateReporter reporter) {
			return () -> {
				for (RestTemplate restTemplate : restTemplates) {
					restTemplate.getInterceptors().add(reporter);
				}
			};
		}

		@Bean
		@ConditionalOnMissingBean
		@ConditionalOnClass(name = {"org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient"})
		public BlockingLoadBalancerClientAspect blockingLoadBalancerClientAspect() {
			return new BlockingLoadBalancerClientAspect();
		}
	}

	/**
	 * Configuration for Polaris {@link org.springframework.web.reactive.function.client.WebClient} which can automatically bring in the call
	 * results for reporting.
	 */
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(name = "org.springframework.web.reactive.function.client.WebClient")
	protected static class PolarisWebClientAutoConfiguration {

		@Autowired(required = false)
		private List<WebClient.Builder> webClientBuilder = Collections.emptyList();

		@Bean
		public EnhancedWebClientReporter exchangeFilterFunction(RpcEnhancementReporterProperties properties,
				SDKContext context,
				ConsumerAPI consumerAPI,
				CircuitBreakAPI circuitBreakAPI) {
			return new EnhancedWebClientReporter(properties, context, consumerAPI, circuitBreakAPI);
		}

		@Bean
		public SmartInitializingSingleton addEncodeTransferMetadataFilterForWebClient(EnhancedWebClientReporter reporter) {
			return () -> webClientBuilder.forEach(webClient -> {
				webClient.filter(reporter);
			});
		}
	}

	/**
	 * Configuration for Polaris {@link org.springframework.web.reactive.function.client.WebClient} which can automatically bring in the call
	 * results for reporting.
	 */
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(name = "org.springframework.cloud.gateway.config.GatewayAutoConfiguration")
	@Role(RootBeanDefinition.ROLE_INFRASTRUCTURE)
	protected static class PolarisGatewayAutoConfiguration {

		@Bean
		@ConditionalOnClass(name = {"org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter"})
		public HttpHeadersFilter enhancedPolarisHttpHeadersFilter() {
			return new EnhancedPolarisHttpHeadersFilter();
		}

		@Bean
		@ConditionalOnClass(name = "org.springframework.cloud.gateway.filter.GlobalFilter")
		public PolarisGatewayReporter polarisGatewayReporter(RpcEnhancementReporterProperties properties,
				SDKContext context,
				ConsumerAPI consumerAPI,
				CircuitBreakAPI circuitBreakAPI) {
			return new PolarisGatewayReporter(properties, context, consumerAPI, circuitBreakAPI);
		}

	}
}
