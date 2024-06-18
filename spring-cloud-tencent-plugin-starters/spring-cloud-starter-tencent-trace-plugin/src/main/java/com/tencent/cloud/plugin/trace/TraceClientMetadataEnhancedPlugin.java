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

package com.tencent.cloud.plugin.trace;

import java.util.HashMap;
import java.util.Map;

import com.tencent.cloud.common.metadata.MetadataContext;
import com.tencent.cloud.common.metadata.MetadataContextHolder;
import com.tencent.cloud.polaris.context.PolarisSDKContextManager;
import com.tencent.cloud.rpc.enhancement.plugin.EnhancedPlugin;
import com.tencent.cloud.rpc.enhancement.plugin.EnhancedPluginContext;
import com.tencent.cloud.rpc.enhancement.plugin.EnhancedPluginType;
import com.tencent.cloud.rpc.enhancement.plugin.PluginOrderConstant;
import com.tencent.polaris.api.utils.CollectionUtils;
import com.tencent.polaris.assembly.api.AssemblyAPI;
import com.tencent.polaris.assembly.api.pojo.TraceAttributes;

public class TraceClientMetadataEnhancedPlugin implements EnhancedPlugin {

	private final PolarisSDKContextManager polarisSDKContextManager;

	private final SpanAttributesProvider spanAttributesProvider;

	public TraceClientMetadataEnhancedPlugin(PolarisSDKContextManager polarisSDKContextManager, SpanAttributesProvider spanAttributesProvider) {
		this.polarisSDKContextManager = polarisSDKContextManager;
		this.spanAttributesProvider = spanAttributesProvider;
	}

	@Override
	public EnhancedPluginType getType() {
		return EnhancedPluginType.Client.PRE;
	}

	@Override
	public void run(EnhancedPluginContext context) throws Throwable {
		AssemblyAPI assemblyAPI = polarisSDKContextManager.getAssemblyAPI();
		Map<String, String> attributes = new HashMap<>();
		if (null != spanAttributesProvider) {
			Map<String, String> additionalAttributes = spanAttributesProvider.getConsumerSpanAttributes(context);
			if (CollectionUtils.isNotEmpty(additionalAttributes)) {
				attributes.putAll(additionalAttributes);
			}
		}
		MetadataContext metadataContext = MetadataContextHolder.get();
		Map<String, String> transitiveCustomAttributes = metadataContext.getFragmentContext(MetadataContext.FRAGMENT_TRANSITIVE);
		if (CollectionUtils.isNotEmpty(transitiveCustomAttributes)) {
			for (Map.Entry<String, String> entry : transitiveCustomAttributes.entrySet()) {
				attributes.put("custom." + entry.getKey(), entry.getValue());
			}
		}
		Map<String, String> disposableCustomAttributes = metadataContext.getFragmentContext(MetadataContext.FRAGMENT_DISPOSABLE);
		if (CollectionUtils.isNotEmpty(disposableCustomAttributes)) {
			for (Map.Entry<String, String> entry : disposableCustomAttributes.entrySet()) {
				attributes.put("custom." + entry.getKey(), entry.getValue());
			}
		}
		TraceAttributes traceAttributes = new TraceAttributes();
		traceAttributes.setAttributes(attributes);
		traceAttributes.setAttributeLocation(TraceAttributes.AttributeLocation.SPAN);
		assemblyAPI.updateTraceAttributes(traceAttributes);
	}

	@Override
	public int getOrder() {
		return PluginOrderConstant.ClientPluginOrder.CONSUMER_TRACE_METADATA_PLUGIN_ORDER;
	}

}
