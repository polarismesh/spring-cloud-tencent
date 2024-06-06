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

package com.tencent.cloud.polaris.tsf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import com.tencent.cloud.common.constant.OrderConstant;
import com.tencent.cloud.common.util.JacksonUtils;
import com.tencent.cloud.polaris.PolarisDiscoveryProperties;
import com.tencent.cloud.polaris.context.PolarisConfigModifier;
import com.tencent.cloud.polaris.context.config.PolarisContextProperties;
import com.tencent.cloud.polaris.context.tsf.config.TsfCoreProperties;
import com.tencent.cloud.polaris.context.tsf.consul.TsfConsulProperties;
import com.tencent.cloud.polaris.tsf.util.RegistrationUtil;
import com.tencent.polaris.api.config.plugin.DefaultPlugins;
import com.tencent.polaris.factory.config.ConfigurationImpl;
import com.tencent.polaris.factory.config.consumer.DiscoveryConfigImpl;
import com.tencent.polaris.factory.config.global.ServerConnectorConfigImpl;
import com.tencent.polaris.factory.config.provider.RegisterConfigImpl;
import com.tencent.polaris.plugins.connector.common.constant.ConsulConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

/**
 * Modifier for TSF discovery.
 *
 * @author Haotian Zhang
 */
public class TsfDiscoveryConfigModifier implements PolarisConfigModifier {

	private static final Logger LOGGER = LoggerFactory.getLogger(TsfDiscoveryConfigModifier.class);

	private final TsfCoreProperties tsfCoreProperties;
	private final TsfConsulProperties tsfConsulProperties;
	private final TsfDiscoveryProperties tsfDiscoveryProperties;

	private final TsfHeartbeatProperties tsfHeartbeatProperties;

	private final PolarisDiscoveryProperties polarisDiscoveryProperties;

	private final PolarisContextProperties polarisContextProperties;
	private final ApplicationContext context;

	public TsfDiscoveryConfigModifier(TsfCoreProperties tsfCoreProperties, TsfConsulProperties tsfConsulProperties,
			TsfDiscoveryProperties tsfDiscoveryProperties, TsfHeartbeatProperties tsfHeartbeatProperties,
			PolarisDiscoveryProperties polarisDiscoveryProperties, PolarisContextProperties polarisContextProperties, ApplicationContext context) {
		this.tsfCoreProperties = tsfCoreProperties;
		this.tsfConsulProperties = tsfConsulProperties;
		this.tsfDiscoveryProperties = tsfDiscoveryProperties;
		this.tsfHeartbeatProperties = tsfHeartbeatProperties;
		this.polarisDiscoveryProperties = polarisDiscoveryProperties;
		this.polarisContextProperties = polarisContextProperties;
		this.context = context;
	}

	@Override
	public void modify(ConfigurationImpl configuration) {
		// namespace id
		polarisDiscoveryProperties.setHeartbeatInterval(Long.valueOf(tsfHeartbeatProperties.computeHearbeatInterval()
				.toStandardDuration().getMillis()).intValue());
		polarisContextProperties.setNamespace(tsfDiscoveryProperties.getTsfNamespaceId());
		polarisDiscoveryProperties.setNamespace(tsfDiscoveryProperties.getTsfNamespaceId());
		System.setProperty("spring.cloud.polaris.namespace", tsfDiscoveryProperties.getTsfNamespaceId());

		// application id
		polarisDiscoveryProperties.setVersion(tsfDiscoveryProperties.getTsfProgVersion());

		// instance id
		polarisDiscoveryProperties.setInstanceId(tsfDiscoveryProperties.getInstanceId());

		boolean consulEnable = tsfCoreProperties.isTsfConsulEnable();
		boolean polarisEnable = tsfCoreProperties.isTsePolarisEnable();

		// 删除可能存在的consul connector配置
		if (!CollectionUtils.isEmpty(configuration.getGlobal().getServerConnectors())) {
			for (ServerConnectorConfigImpl config : configuration.getGlobal().getServerConnectors()) {
				if (StringUtils.equals(config.getId(), RegistrationUtil.ID)) {
					configuration.getGlobal().getServerConnectors().remove(config);
				}
			}
		}
		else {
			configuration.getGlobal().setServerConnectors(new ArrayList<>());
		}
		// 删除可能存在的consul发现配置
		for (DiscoveryConfigImpl dc : configuration.getConsumer().getDiscoveries()) {
			if (StringUtils.equals(dc.getServerConnectorId(), RegistrationUtil.ID)) {
				configuration.getConsumer().getDiscoveries().remove(dc);
			}
		}
		// 删除可能存在的consul注册配置
		for (RegisterConfigImpl rc : configuration.getProvider().getRegisters()) {
			if (StringUtils.equals(rc.getServerConnectorId(), RegistrationUtil.ID)) {
				configuration.getProvider().getRegisters().remove(rc);
			}
		}

		// 如果ServerConnectors为空，则把ServerConnector（如有）复制过去
		if (CollectionUtils.isEmpty(configuration.getGlobal().getServerConnectors())
				&& null != configuration.getGlobal().getServerConnector()) {
			configuration.getGlobal().getServerConnectors().add(configuration.getGlobal().getServerConnector());
		}
		if (consulEnable) {
			// enable consul
			ServerConnectorConfigImpl serverConnectorConfig = new ServerConnectorConfigImpl();
			serverConnectorConfig.setId(RegistrationUtil.ID);
			serverConnectorConfig.setAddresses(
					Collections.singletonList(tsfConsulProperties.getHost() + ":" + tsfConsulProperties.getPort()));
			LOGGER.info("Will register to consul server: [" + tsfConsulProperties.getHost() + ":" + tsfConsulProperties.getPort() + "]");
			serverConnectorConfig.setProtocol(DefaultPlugins.SERVER_CONNECTOR_CONSUL);

			Map<String, String> metadata = serverConnectorConfig.getMetadata();
			String appName = RegistrationUtil.getAppName(tsfDiscoveryProperties, context.getEnvironment());
			metadata.put(ConsulConstant.MetadataMapKey.SERVICE_NAME_KEY, RegistrationUtil.normalizeForDns(appName));
			metadata.put(ConsulConstant.MetadataMapKey.INSTANCE_ID_KEY, RegistrationUtil.getInstanceId(tsfDiscoveryProperties, context));
			if (StringUtils.isNotBlank(tsfDiscoveryProperties.getAclToken())) {
				serverConnectorConfig.setToken(tsfDiscoveryProperties.getAclToken());
			}
			metadata.put(ConsulConstant.MetadataMapKey.TAGS_KEY, JacksonUtils.serialize2Json(RegistrationUtil.createTags(tsfDiscoveryProperties)));
			if (StringUtils.isNotBlank(tsfDiscoveryProperties.getDefaultQueryTag())) {
				metadata.put(ConsulConstant.MetadataMapKey.QUERY_TAG_KEY, tsfDiscoveryProperties.getDefaultQueryTag());
			}
			metadata.put(ConsulConstant.MetadataMapKey.QUERY_PASSING_KEY, String.valueOf(tsfDiscoveryProperties.isQueryPassing()));
			if (tsfDiscoveryProperties.isPreferIpAddress()
					&& StringUtils.isNotBlank(tsfDiscoveryProperties.getIpAddress())) {
				metadata.put(ConsulConstant.MetadataMapKey.PREFER_IP_ADDRESS_KEY,
						String.valueOf(tsfDiscoveryProperties.isPreferIpAddress()));
				metadata.put(ConsulConstant.MetadataMapKey.IP_ADDRESS_KEY, tsfDiscoveryProperties.getIpAddress());
			}
			if (!tsfDiscoveryProperties.isPreferAgentAddress()) {
				metadata.put(ConsulConstant.MetadataMapKey.PREFER_IP_ADDRESS_KEY,
						String.valueOf(tsfDiscoveryProperties.isPreferIpAddress()));
				metadata.put(ConsulConstant.MetadataMapKey.IP_ADDRESS_KEY, tsfDiscoveryProperties.getHostname());
			}
			configuration.getGlobal().getServerConnectors().add(serverConnectorConfig);

			// 添加发现配置
			DiscoveryConfigImpl discoveryConfig = new DiscoveryConfigImpl();
			discoveryConfig.setServerConnectorId(RegistrationUtil.ID);
			discoveryConfig.setEnable(tsfDiscoveryProperties.isEnabled());
			configuration.getConsumer().getDiscoveries().add(discoveryConfig);

			// 添加注册配置
			RegisterConfigImpl registerConfig = new RegisterConfigImpl();
			registerConfig.setServerConnectorId(RegistrationUtil.ID);
			registerConfig.setEnable(tsfDiscoveryProperties.isRegister());
			configuration.getProvider().getRegisters().add(registerConfig);
		}

		if (polarisDiscoveryProperties != null) {
			if (!polarisEnable) {
				configuration.getGlobal().getAPI().setReportEnable(false);
				for (DiscoveryConfigImpl dc : configuration.getConsumer().getDiscoveries()) {
					if (StringUtils.equals(dc.getServerConnectorId(), "polaris")) {
						dc.setEnable(false);
					}
				}
				for (RegisterConfigImpl rc : configuration.getProvider().getRegisters()) {
					if (StringUtils.equals(rc.getServerConnectorId(), "polaris")) {
						rc.setEnable(false);
						rc.setReportServiceContractEnable(false);
					}
				}
			}
		}
	}

	@Override
	public int getOrder() {
		return OrderConstant.Modifier.CONSUL_DISCOVERY_CONFIG_ORDER + 1;
	}
}
