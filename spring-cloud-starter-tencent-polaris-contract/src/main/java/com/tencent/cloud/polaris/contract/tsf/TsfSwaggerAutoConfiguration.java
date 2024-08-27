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
package com.tencent.cloud.polaris.contract.tsf;

import com.tencent.cloud.polaris.context.tsf.ConditionalOnTsfEnabled;
import com.tencent.cloud.polaris.contract.config.PolarisContractProperties;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webflux.api.MultipleOpenApiWebFluxResource;
import org.springdoc.webmvc.api.MultipleOpenApiWebMvcResource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
@ConditionalOnTsfEnabled
@ConditionalOnClass(name = "org.springframework.web.servlet.config.annotation.EnableWebMvc")
@ConditionalOnProperty(value = "tsf.swagger.enabled", havingValue = "true", matchIfMissing = true)
public class TsfSwaggerAutoConfiguration {

	@Bean
	@ConditionalOnBean(OpenAPI.class)
	public TsfApiMetadataGrapher tsfApiMetadataGrapher(@Nullable MultipleOpenApiWebMvcResource multipleOpenApiWebMvcResource,
			@Nullable MultipleOpenApiWebFluxResource multipleOpenApiWebFluxResource, ApplicationContext context,
			PolarisContractProperties polarisContractProperties, ObjectMapperProvider springdocObjectMapperProvider) {
		return new TsfApiMetadataGrapher(multipleOpenApiWebMvcResource, multipleOpenApiWebFluxResource,
				polarisContractProperties.getGroup(), context, springdocObjectMapperProvider);
	}
}
