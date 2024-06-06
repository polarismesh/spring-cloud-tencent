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

package com.tencent.cloud.polaris.tsf.registry;

import com.tencent.cloud.polaris.registry.PolarisRegistration;
import com.tencent.cloud.polaris.registry.PolarisRegistrationCustomizer;
import com.tencent.cloud.polaris.tsf.TsfDiscoveryProperties;
import com.tencent.cloud.polaris.tsf.TsfHeartbeatProperties;
import com.tencent.cloud.polaris.tsf.util.RegistrationUtil;
import com.tencent.polaris.client.api.SDKContext;

import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.context.ApplicationContext;

/**
 * 服务注册时端口相关逻辑.
 *
 * @author Haotian Zhang
 */
public class TsfPortPolarisRegistrationCustomizer implements PolarisRegistrationCustomizer {

	private final AutoServiceRegistrationProperties autoServiceRegistrationProperties;
	private final ApplicationContext context;
	private final TsfDiscoveryProperties tsfDiscoveryProperties;
	private final TsfHeartbeatProperties tsfHeartbeatProperties;
	private final SDKContext sdkContext;

	public TsfPortPolarisRegistrationCustomizer(AutoServiceRegistrationProperties autoServiceRegistrationProperties,
			ApplicationContext context, TsfDiscoveryProperties tsfDiscoveryProperties,
			TsfHeartbeatProperties tsfHeartbeatProperties, SDKContext sdkContext) {
		this.autoServiceRegistrationProperties = autoServiceRegistrationProperties;
		this.context = context;
		this.tsfDiscoveryProperties = tsfDiscoveryProperties;
		this.tsfHeartbeatProperties = tsfHeartbeatProperties;
		this.sdkContext = sdkContext;
	}

	@Override
	public void customize(PolarisRegistration registration) {
		if (tsfDiscoveryProperties.getPort() != null) {
			registration.setPort(tsfDiscoveryProperties.getPort());
		}
		// we know the port and can set the check
		RegistrationUtil.setCheck(autoServiceRegistrationProperties, tsfDiscoveryProperties, context,
				tsfHeartbeatProperties, registration, sdkContext.getConfig());
	}
}
