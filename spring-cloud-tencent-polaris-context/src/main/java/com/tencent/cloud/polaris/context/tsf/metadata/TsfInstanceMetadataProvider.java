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

package com.tencent.cloud.polaris.context.tsf.metadata;

import java.util.HashMap;
import java.util.Map;

import com.tencent.cloud.common.constant.SdkVersion;
import com.tencent.cloud.common.constant.WarmupCons;
import com.tencent.cloud.common.spi.InstanceMetadataProvider;
import com.tencent.cloud.common.util.JacksonUtils;
import com.tencent.cloud.common.util.inet.PolarisInetUtils;
import com.tencent.cloud.polaris.context.tsf.TsfUtils;
import com.tencent.cloud.polaris.context.tsf.config.TsfCoreProperties;
import com.tencent.polaris.api.utils.StringUtils;
import com.tencent.polaris.metadata.core.constant.TsfMetadataConstants;

import static com.tencent.cloud.polaris.context.tsf.TsfUtils.TSF_ADDRESS_IPV4;
import static com.tencent.cloud.polaris.context.tsf.TsfUtils.TSF_ADDRESS_IPV6;


/**
 * InstanceMetadataProvider for TSF.
 *
 * @author Hoatian Zhang
 */
public class TsfInstanceMetadataProvider implements InstanceMetadataProvider {

	private final TsfCoreProperties tsfCoreProperties;

	public TsfInstanceMetadataProvider(TsfCoreProperties tsfCoreProperties) {
		this.tsfCoreProperties = tsfCoreProperties;
	}

	@Override
	public Map<String, String> getMetadata() {
		return new HashMap<>() {{
			put(TsfMetadataConstants.TSF_PROG_VERSION, tsfCoreProperties.getTsfProgVersion());
			put(TsfMetadataConstants.TSF_APPLICATION_ID, tsfCoreProperties.getTsfApplicationId());
			put(TsfMetadataConstants.TSF_GROUP_ID, tsfCoreProperties.getTsfGroupId());
			put(TsfMetadataConstants.TSF_APPLICATION_ID, tsfCoreProperties.getTsfApplicationId());
			put(TsfMetadataConstants.TSF_PROG_VERSION, tsfCoreProperties.getTsfProgVersion());
			put(TsfMetadataConstants.TSF_GROUP_ID, tsfCoreProperties.getTsfGroupId());
			put(TsfMetadataConstants.TSF_NAMESPACE_ID, tsfCoreProperties.getTsfNamespaceId());
			put(TsfMetadataConstants.TSF_INSTNACE_ID, tsfCoreProperties.getInstanceId());
			put(TsfMetadataConstants.TSF_REGION, tsfCoreProperties.getTsfRegion());
			put(TsfMetadataConstants.TSF_ZONE, tsfCoreProperties.getTsfZone());
			// 处理预热相关的参数
			put(WarmupCons.TSF_START_TIME, String.valueOf(System.currentTimeMillis()));
			put(TsfMetadataConstants.TSF_SDK_VERSION, SdkVersion.get());
			put(TsfMetadataConstants.TSF_TAGS, JacksonUtils.serialize2Json(TsfUtils.createTags(tsfCoreProperties)));
			String ipv4Address = PolarisInetUtils.getIpString(false);
			if (StringUtils.isNotBlank(ipv4Address)) {
				put(TSF_ADDRESS_IPV4, ipv4Address);
			}
			String ipv6Address = PolarisInetUtils.getIpString(true);
			if (StringUtils.isNotBlank(ipv6Address)) {
				put(TSF_ADDRESS_IPV6, ipv6Address);
			}
		}};
	}

	@Override
	public String getRegion() {
		return tsfCoreProperties.getTsfRegion();
	}

	@Override
	public String getZone() {
		return tsfCoreProperties.getTsfZone();
	}
}
