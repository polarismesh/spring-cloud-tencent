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

package com.tencent.cloud.polaris.context.tsf;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.tencent.cloud.common.util.inet.PolarisInetUtils;
import com.tencent.cloud.polaris.context.tsf.config.TsfCoreProperties;

import org.springframework.util.StringUtils;

/**
 *
 *
 * @author Haotian Zhang
 */
public final class TsfUtils {

	/**
	 * IPV4.
	 */
	public static String TSF_ADDRESS_IPV4 = "TSF_ADDRESS_IPV4";

	/**
	 * IPV6.
	 */
	public static String TSF_ADDRESS_IPV6 = "TSF_ADDRESS_IPV6";

	private TsfUtils() {
	}

	public static List<String> createTags(TsfCoreProperties properties) {
		List<String> tags = new LinkedList<>(properties.getTags());

		if (StringUtils.hasText(properties.getInstanceZone())) {
			tags.add(properties.getDefaultZoneMetadataName() + "=" + properties.getInstanceZone());
		}
		if (StringUtils.hasText(properties.getInstanceGroup())) {
			tags.add("group=" + properties.getInstanceGroup());
		}

		//store the secure flag in the tags so that clients will be able to figure out whether to use http or https automatically
		tags.add("secure=" + properties.getScheme().equalsIgnoreCase("https"));

		return tags;
	}

	public static Map<String, String> appendMetaIpAddress(Map<String, String> meta) {
		if (meta == null) {
			return null;
		}
		String ipv4Address = PolarisInetUtils.getIpString(false);
		if (ipv4Address != null) {
			meta.put(TSF_ADDRESS_IPV4, ipv4Address);
		}

		String ipv6Address = PolarisInetUtils.getIpString(true);
		if (ipv6Address != null) {
			meta.put(TSF_ADDRESS_IPV6, ipv6Address);
		}
		return meta;
	}
}
