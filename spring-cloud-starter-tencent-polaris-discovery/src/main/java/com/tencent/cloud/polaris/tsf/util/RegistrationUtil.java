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

package com.tencent.cloud.polaris.tsf.util;

import java.util.Map;

import com.ecwid.consul.v1.agent.model.NewService;
import com.tencent.cloud.common.util.AddressUtils;
import com.tencent.cloud.common.util.JacksonUtils;
import com.tencent.cloud.polaris.context.tsf.config.TsfCoreProperties;
import com.tencent.cloud.polaris.tsf.TsfDiscoveryProperties;
import com.tencent.cloud.polaris.tsf.TsfHeartbeatProperties;
import com.tencent.polaris.api.config.Configuration;
import com.tencent.polaris.factory.config.global.ServerConnectorConfigImpl;
import com.tencent.polaris.plugins.connector.common.constant.ConsulConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.client.discovery.ManagementServerPortUtils;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.cloud.commons.util.IdUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * TSF registration utils.
 *
 * @author Haotian Zhang
 */
public final class RegistrationUtil {
	/**
	 * - 分隔符.
	 */
	public static final char SEPARATOR = '-';
	/**
	 * Server connector ID.
	 */
	public static final String ID = "consul";
	private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationUtil.class);

	private RegistrationUtil() {
	}

	public static String getAppName(TsfDiscoveryProperties properties, Environment env) {
		String appName = properties.getServiceName();
		if (!StringUtils.isEmpty(appName)) {
			return appName;
		}
		return env.getProperty("spring.application.name", "application");
	}

	public static String getInstanceId(TsfCoreProperties properties, ApplicationContext context) {
		// tsf consul 不支持 dns，所以这里不需要 normalize，并且由于优雅下线，readiness probe 联动都是依赖 service id 的，normalize 后两边对不上，所以需要去掉 normalize
		if (!StringUtils.hasText(properties.getInstanceId())) {
			return IdUtils.getDefaultInstanceId(context.getEnvironment(), false);
		}
		else {
			return properties.getInstanceId();
		}
	}

	// 更新了判断逻辑，仅判断为空的场景
	public static String normalizeForDns(String s) {
		// if (s == null || !Character.isLetter(s.charAt(0))
		// || !Character.isLetterOrDigit(s.charAt(s.length()-1))) {
		// throw new IllegalArgumentException("Consul service ids must not be empty,
		// must start with a letter, end with a letter or digit, and have as interior
		// characters only letters, digits, and hyphen");
		// }

		// tsf not check consul service instance id start with letter and end with
		// letter or digit
		if (s == null) {
			throw new IllegalArgumentException("Consul service ids must not be empty");
		}

		StringBuilder normalized = new StringBuilder();
		Character prev = null;
		for (char curr : s.toCharArray()) {
			Character toAppend = null;
			if (Character.isLetterOrDigit(curr)) {
				toAppend = curr;
			}
			else if (prev == null || !(prev == SEPARATOR)) {
				toAppend = SEPARATOR;
			}
			if (toAppend != null) {
				normalized.append(toAppend);
				prev = toAppend;
			}
		}

		return normalized.toString();
	}

	public static void setCheck(AutoServiceRegistrationProperties autoServiceRegistrationProperties,
			TsfDiscoveryProperties properties, TsfCoreProperties tsfCoreProperties, ApplicationContext context,
			TsfHeartbeatProperties tsfHeartbeatProperties, Registration registration, Configuration configuration) {
		if (properties.isRegisterHealthCheck()) {
			Integer checkPort;
			if (shouldRegisterManagement(autoServiceRegistrationProperties, properties, context)) {
				checkPort = getManagementPort(properties, context);
			}
			else {
				checkPort = registration.getPort();
			}
			Assert.notNull(checkPort, "checkPort may not be null");

			for (ServerConnectorConfigImpl config : configuration.getGlobal().getServerConnectors()) {
				if (org.apache.commons.lang.StringUtils.equals(config.getId(), ID)) {
					Map<String, String> metadata = config.getMetadata();
					NewService.Check check = createCheck(checkPort, tsfHeartbeatProperties, properties, tsfCoreProperties);
					String checkJson = JacksonUtils.serialize2Json(check);
					LOGGER.debug("Check is : {}", checkJson);
					metadata.put(ConsulConstant.MetadataMapKey.CHECK_KEY, checkJson);
				}
			}

		}
	}

	public static NewService.Check createCheck(Integer port, TsfHeartbeatProperties ttlConfig,
			TsfDiscoveryProperties properties, TsfCoreProperties tsfCoreProperties) {
		NewService.Check check = new NewService.Check();
		if (ttlConfig.isEnabled()) {
			check.setTtl(ttlConfig.getTtl());
			return check;
		}

		Assert.notNull(port, "createCheck port must not be null");
		Assert.isTrue(port > 0, "createCheck port must be greater than 0");

		if (properties.getHealthCheckUrl() != null) {
			check.setHttp(properties.getHealthCheckUrl());
		}
		else {
			check.setHttp(String.format("%s://%s:%s%s", tsfCoreProperties.getScheme(),
					AddressUtils.getIpCompatible(properties.getHostname()), port,
					properties.getHealthCheckPath()));
		}
		check.setInterval(properties.getHealthCheckInterval());
		check.setTimeout(properties.getHealthCheckTimeout());
		if (StringUtils.hasText(properties.getHealthCheckCriticalTimeout())) {
			check.setDeregisterCriticalServiceAfter(properties.getHealthCheckCriticalTimeout());
		}
		check.setTlsSkipVerify(properties.getHealthCheckTlsSkipVerify());
		return check;
	}

	/**
	 * @return if the management service should be registered with the {@link ServiceRegistry}
	 */
	public static boolean shouldRegisterManagement(AutoServiceRegistrationProperties autoServiceRegistrationProperties, TsfDiscoveryProperties properties, ApplicationContext context) {
		return autoServiceRegistrationProperties.isRegisterManagement()
				&& getManagementPort(properties, context) != null
				&& ManagementServerPortUtils.isDifferent(context);
	}

	/**
	 * @return the port of the Management Service
	 */
	public static Integer getManagementPort(TsfDiscoveryProperties properties, ApplicationContext context) {
		// If an alternate external port is specified, use it instead
		if (properties.getManagementPort() != null) {
			return properties.getManagementPort();
		}
		return ManagementServerPortUtils.getPort(context);
	}
}
