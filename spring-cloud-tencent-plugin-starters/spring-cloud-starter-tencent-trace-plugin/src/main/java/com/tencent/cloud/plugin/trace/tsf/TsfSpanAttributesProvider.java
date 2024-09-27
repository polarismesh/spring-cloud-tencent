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
 *
 */

package com.tencent.cloud.plugin.trace.tsf;

import java.util.HashMap;
import java.util.Map;

import com.tencent.cloud.plugin.trace.SpanAttributesProvider;
import com.tencent.cloud.rpc.enhancement.plugin.EnhancedPluginContext;
import com.tencent.polaris.api.utils.CollectionUtils;
import com.tencent.polaris.api.utils.StringUtils;
import com.tencent.polaris.metadata.core.constant.TsfMetadataConstants;

import org.springframework.cloud.client.ServiceInstance;

public class TsfSpanAttributesProvider implements SpanAttributesProvider {

	@Override
	public Map<String, String> getConsumerSpanAttributes(EnhancedPluginContext context) {
		Map<String, String> attributes = new HashMap<>();
		if (null != context.getRequest().getUrl()) {
			attributes.put("remoteInterface", context.getRequest().getUrl().getPath());
		}
		ServiceInstance targetServiceInstance = context.getTargetServiceInstance();
		if (null != targetServiceInstance && CollectionUtils.isNotEmpty(targetServiceInstance.getMetadata())) {
			String nsId = targetServiceInstance.getMetadata().get(TsfMetadataConstants.TSF_NAMESPACE_ID);
			attributes.put("remote.namespace-id", StringUtils.defaultString(nsId));
			String groupId = targetServiceInstance.getMetadata().get(TsfMetadataConstants.TSF_GROUP_ID);
			attributes.put("remote.group-id", StringUtils.defaultString(groupId));
			String applicationId = targetServiceInstance.getMetadata().get(TsfMetadataConstants.TSF_APPLICATION_ID);
			attributes.put("remote.application-id", StringUtils.defaultString(applicationId));
		}
		return attributes;
	}

}
