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

package com.tencent.cloud.polaris.config.tsf.adaptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;
import com.tencent.cloud.polaris.config.adapter.PolarisConfigCustomExtensionLayer;
import com.tencent.cloud.polaris.config.adapter.PolarisConfigPropertyAutoRefresher;
import com.tencent.cloud.polaris.config.adapter.PolarisPropertySource;
import com.tencent.cloud.polaris.config.adapter.PolarisPropertySourceManager;
import com.tencent.cloud.polaris.config.enums.ConfigFileFormat;
import com.tencent.cloud.polaris.config.tsf.cache.PolarisPropertyCache;
import com.tencent.cloud.polaris.config.tsf.encrypt.EncryptConfig;
import com.tencent.polaris.configuration.api.core.ConfigFileGroup;
import com.tencent.polaris.configuration.api.core.ConfigFileGroupChangeListener;
import com.tencent.polaris.configuration.api.core.ConfigFileGroupChangedEvent;
import com.tencent.polaris.configuration.api.core.ConfigFileMetadata;
import com.tencent.polaris.configuration.api.core.ConfigFileService;
import com.tencent.polaris.configuration.api.core.ConfigKVFile;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

/**
 * @author juanyinyang
 */
public class PolarisAdaptorTsfConfigExtensionLayer implements PolarisConfigCustomExtensionLayer {

	private static final Logger LOGGER = LoggerFactory.getLogger(PolarisAdaptorTsfConfigExtensionLayer.class);
	/**
	 * 应用配置.
	 */
	private static final String APP_CONFIG = "appconfig";
	/**
	 * 全局配置.
	 */
	private static final String PUB_CONFIG = "pubconfig";
	/**
	 * TSF应用ID.
	 */
	private static final String TSF_APPLICATION_ID = "tsf_application_id";
	/**
	 * TSF部署组ID.
	 */
	private static final String TSF_GROUP_ID = "tsf_group_id";
	/**
	 * TSF命名空间ID.
	 */
	private static final String TSF_NAMESPACE_ID = "tsf_namespace_id";
	/**
	 * TSF命名空间ID.
	 */
	private static final String POLARIS_ADAPTOR_TSF_CONFIG_FORMAT = "spring.cloud.polaris.config.format";

	// 最近一次的全量PolarisPropertySource集合（PolarisPropertySource按 namespace + fileGroup + fileName 确保唯一）
	private static final Set<String> registedPolarisPropertySets = Sets.newConcurrentHashSet();
	// 命名空间分组（namespace + fileGroup）的去重Set集合，如果这个分组已添加了ConfigFileGroupListener
	private static final Set<String> registedConfigFileGroupListenerSets = Sets.newConcurrentHashSet();

	private PolarisConfigPropertyAutoRefresher polarisConfigPropertyAutoRefresher;

	@Override
	public boolean isEnabled() {
		// tse_polaris_enable
		String tsePolarisEnable = System.getenv("tse_polaris_enable");
		if (StringUtils.isBlank(tsePolarisEnable)) {
			tsePolarisEnable = System.getProperty("tse_polaris_enable", "false");
		}
		return StringUtils.equals(tsePolarisEnable, "true");
	}

	/**
	 * @see PolarisConfigCustomExtensionLayer#initConfigFiles(CompositePropertySource,
	 *        PolarisPropertySourceManager,
	 *        ConfigFileService)
	 */
	@Override
	public void initConfigFiles(Environment environment, CompositePropertySource compositePropertySource,
			ConfigFileService configFileService) {
		String tsfApplicationId = environment.getProperty(TSF_APPLICATION_ID);
		String tsfGroupId = environment.getProperty(TSF_GROUP_ID);
		String tsfNamespaceId = environment.getProperty(TSF_NAMESPACE_ID);
		String polarisAdaptorTsfConfigFormat = environment.getProperty(POLARIS_ADAPTOR_TSF_CONFIG_FORMAT);
		LOGGER.info(
				"[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer initConfigFiles start, tsfNamespaceId:{}, tsfApplicationId:{}, tsfGroupId:{}",
				tsfNamespaceId, tsfApplicationId, tsfGroupId);
		loadAllPolarisConfigFile(compositePropertySource, configFileService,
				tsfNamespaceId, tsfApplicationId, tsfGroupId, polarisAdaptorTsfConfigFormat);
		LOGGER.info("[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer initConfigFiles end");
	}

	private void loadAllPolarisConfigFile(CompositePropertySource compositePropertySource,
			ConfigFileService configFileService, String tsfNamespaceId, String tsfApplicationId, String tsfGroupId,
			String polarisAdaptorTsfConfigFormat) {
		boolean isInitTsfEnv = StringUtils.isNotBlank(tsfNamespaceId) && StringUtils.isNotBlank(tsfApplicationId)
				&& StringUtils.isNotBlank(tsfGroupId);
		if (isInitTsfEnv) {
			String appConfigGroup = APP_CONFIG + "." + tsfApplicationId + "." + tsfGroupId;
			loadPolarisConfigFile(tsfNamespaceId, tsfApplicationId, tsfGroupId, polarisAdaptorTsfConfigFormat,
					compositePropertySource, configFileService, appConfigGroup);
		}

		String pubConfigGroup = PUB_CONFIG;
		loadPolarisConfigFile(tsfNamespaceId, tsfApplicationId, tsfGroupId, polarisAdaptorTsfConfigFormat,
				compositePropertySource, configFileService, pubConfigGroup);
	}

	private PolarisPropertySource loadPolarisPropertySource(String namespace, String group, String fileName,
			String polarisAdaptorTsfConfigFormat, ConfigFileService configFileService) {
		ConfigKVFile configKVFile;
		if (StringUtils.isNotBlank(polarisAdaptorTsfConfigFormat)) {
			switch (polarisAdaptorTsfConfigFormat) {
			case "properties":
				configKVFile = configFileService.getConfigPropertiesFile(namespace, group, fileName);
			case "yaml":
			default:
				configKVFile = configFileService.getConfigYamlFile(namespace, group, fileName);
			}
		}
		// unknown extension is resolved as yaml file
		else if (ConfigFileFormat.isYamlFile(fileName) || ConfigFileFormat.isUnknownFile(fileName)) {
			configKVFile = configFileService.getConfigYamlFile(namespace, group, fileName);
		}
		else if (ConfigFileFormat.isPropertyFile(fileName)) {
			configKVFile = configFileService.getConfigPropertiesFile(namespace, group, fileName);
		}
		else {
			LOGGER.warn("[SCTT Config] Unsupported config file. namespace = {}, group = {}, fileName = {}", namespace,
					group, fileName);

			throw new IllegalStateException("Only configuration files in the format of properties / yaml / yaml"
					+ " can be injected into the spring context");
		}

		Map<String, Object> map = new ConcurrentHashMap<>();
		for (String key : configKVFile.getPropertyNames()) {
			String value = configKVFile.getProperty(key, null);
			if (EncryptConfig.needDecrypt(value)) {
				LOGGER.debug("[SCTT Config] Need Decrypt {}: {}", key, value);
				value = EncryptConfig.getProvider()
						.decrypt(EncryptConfig.realContent(value), EncryptConfig.getPassword());
			}
			map.put(key, value);
		}

		return new PolarisPropertySource(namespace, group, fileName, configKVFile, map);
	}

	private void loadPolarisConfigFile(String namespace, String tsfApplicationId, String tsfGroupId,
			String polarisAdaptorTsfConfigFormat, CompositePropertySource compositePropertySource,
			ConfigFileService configFileService, String configGroup) {
		LOGGER.debug(
				"[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer loadPolarisConfigFile start, namespace:{}, group:{}",
				namespace, configGroup);
		ConfigFileGroup configFileGroup = configFileService.getConfigFileGroup(namespace, configGroup);
		if (configFileGroup == null) {
			throw new IllegalStateException(
					"[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer configFileGroup is null");
		}
		List<ConfigFileMetadata> configFileMetadataList = configFileGroup.getConfigFileMetadataList();
		if (!CollectionUtils.isEmpty(configFileMetadataList)) {
			LOGGER.info("[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer getConfigFileMetadataList:{}",
					configFileMetadataList);
			for (ConfigFileMetadata configFile : configFileMetadataList) {
				PolarisPropertySource polarisPropertySource = loadPolarisPropertySource(configFile.getNamespace(),
						configFile.getFileGroup(), configFile.getFileName(), polarisAdaptorTsfConfigFormat,
						configFileService);

				compositePropertySource.addPropertySource(polarisPropertySource);

				PolarisPropertySourceManager.addPropertySource(polarisPropertySource);

				LOGGER.info(
						"[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer Load and inject polaris config file from config group:{}. file = {}",
						configGroup, configFile);
			}
		}

		String namespaceConfigGroup = namespace + "-" + configGroup;
		// 用ConcurrentHashSet保证不重复添加ConfigFileGroupChangeListener
		if (registedConfigFileGroupListenerSets.add(namespaceConfigGroup)) {
			LOGGER.info(
					"[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer configFileGroup addChangeListener namespaceConfigGroup:{}",
					namespaceConfigGroup);
			configFileGroup.addChangeListener(new ConfigFileGroupChangeListener() {

				@Override
				public void onChange(ConfigFileGroupChangedEvent event) {
					try {
						LOGGER.info("[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer receive onChange event:{}",
								event);
						List<ConfigFileMetadata> configFileMetadataList = event.getConfigFileMetadataList();
						if (CollectionUtils.isEmpty(configFileMetadataList)) {
							LOGGER.info(
									"[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer receive configFileMetadataList is empty");
							return;
						}
						boolean needRefreshAll = false;
						for (ConfigFileMetadata configFile : configFileMetadataList) {
							PolarisPropertySource polarisPropertySource = loadPolarisPropertySource(
									configFile.getNamespace(), configFile.getFileGroup(), configFile.getFileName(),
									polarisAdaptorTsfConfigFormat, configFileService);
							LOGGER.info(
									"[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer Load and inject polaris config file from onChange event config group:{}. file = {}",
									configGroup, configFile);
							// 用ConcurrentHashSet保证不重复注册PolarisConfigPublishChangeListener
							if (executeRegisterPublishChangeListener(polarisPropertySource)) {
								polarisConfigPropertyAutoRefresher.registerPolarisConfigPublishChangeListener(
										polarisPropertySource);
								needRefreshAll = true;
							}
						}
						if (needRefreshAll) {
							LOGGER.info("[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer start refresh All Config");
							polarisConfigPropertyAutoRefresher.refreshConfigurationProperties(null);
						}
					}
					catch (Exception e) {
						LOGGER.info("[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer receive onChange exception:",
								e);
					}
				}
			});
		}

		LOGGER.info(
				"[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer loadPolarisConfigFile end, namespace:{}, group:{}",
				namespace, configGroup);
	}

	/**
	 * @see PolarisConfigCustomExtensionLayer#executeAfterLocateConfigReturning(CompositePropertySource)
	 */
	@Override
	public void executeAfterLocateConfigReturning(CompositePropertySource compositePropertySource) {
		PolarisPropertyCache.getInstance().clear();
		PolarisPropertyCache.getInstance().getCache()
				.addAll(new HashSet<>(Arrays.asList(compositePropertySource.getPropertyNames())));
		LOGGER.info("[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer executeAfterLocateConfigReturning finished");
	}

	/**
	 * @see PolarisConfigCustomExtensionLayer#initRegisterConfig(PolarisConfigPropertyAutoRefresher)
	 */
	@Override
	public void initRegisterConfig(PolarisConfigPropertyAutoRefresher polarisConfigPropertyAutoRefresher) {
		LOGGER.info(
				"[SCTT Config] PolarisAdaptorTsfConfigExtensionLayer initRegisterConfig polarisConfigPropertyAutoRefresher:{}",
				polarisConfigPropertyAutoRefresher.getClass());
		this.polarisConfigPropertyAutoRefresher = polarisConfigPropertyAutoRefresher;
	}

	/**
	 * @see PolarisConfigCustomExtensionLayer#executeRegisterPublishChangeListener(PolarisPropertySource)
	 */
	@Override
	public boolean executeRegisterPublishChangeListener(PolarisPropertySource polarisPropertySource) {
		boolean isRegisterSuccess = registedPolarisPropertySets.add(polarisPropertySource.getPropertySourceName());
		if (isRegisterSuccess) {
			// 已防止重复注册，仅打印注册成功的即可
			LOGGER.info("[SCTT Config] start to register configFile polarisConfigPublishChangeListener:{}",
					polarisPropertySource);
		}
		return isRegisterSuccess;
	}
}
