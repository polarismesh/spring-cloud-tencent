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

import java.util.Map;

import com.tencent.cloud.common.constant.SdkVersion;
import com.tencent.cloud.common.util.JacksonUtils;
import com.tencent.cloud.polaris.registry.PolarisRegistration;
import com.tencent.cloud.polaris.registry.PolarisRegistrationCustomizer;
import com.tencent.cloud.polaris.tsf.TsfDiscoveryProperties;
import com.tencent.cloud.polaris.tsf.consts.WarmupCons;
import com.tencent.cloud.polaris.tsf.util.RegistrationUtil;

/**
 *
 *
 * @author Haotian Zhang
 */
public class TsfMetadataPolarisRegistrationCustomizer implements PolarisRegistrationCustomizer {

	private final TsfDiscoveryProperties tsfDiscoveryProperties;

	public TsfMetadataPolarisRegistrationCustomizer(TsfDiscoveryProperties tsfDiscoveryProperties) {
		this.tsfDiscoveryProperties = tsfDiscoveryProperties;
	}

	@Override
	public void customize(PolarisRegistration registration) {
		Map<String, String> metadata = registration.getMetadata();

		metadata.put("TSF_APPLICATION_ID", tsfDiscoveryProperties.getTsfApplicationId());
		metadata.put("TSF_PROG_VERSION", tsfDiscoveryProperties.getTsfProgVersion());
		metadata.put("TSF_GROUP_ID", tsfDiscoveryProperties.getTsfGroupId());
		metadata.put("TSF_NAMESPACE_ID", tsfDiscoveryProperties.getTsfNamespaceId());
		metadata.put("TSF_INSTNACE_ID", tsfDiscoveryProperties.getInstanceId());
		metadata.put("TSF_REGION", tsfDiscoveryProperties.getTsfRegion());
		metadata.put("TSF_ZONE", tsfDiscoveryProperties.getTsfZone());
		// 处理预热相关的参数
		metadata.put(WarmupCons.TSF_START_TIME, String.valueOf(System.currentTimeMillis()));
		metadata.put("TSF_SDK_VERSION", SdkVersion.get());
		metadata.put("TSF_TAGS", JacksonUtils.serialize2Json(RegistrationUtil.createTags(tsfDiscoveryProperties)));
		RegistrationUtil.appendMetaIpAddress(metadata);
	}
}
