/*
 * Tencent is pleased to support the open source community by making spring-cloud-tencent available.
 *
 * Copyright (C) 2021 THL A29 Limited, a Tencent company. All rights reserved.
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

package com.tencent.cloud.plugin.discovery.adapter.transformer;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.netflix.loadbalancer.Server;
import com.tencent.cloud.polaris.loadbalancer.transformer.InstanceTransformer;
import com.tencent.polaris.api.pojo.DefaultInstance;

/**
 * NacosInstanceTransformer.
 *
 * @author Haotian Zhang
 */
public class NacosInstanceTransformer implements InstanceTransformer {

	@Override
	public void transformCustom(DefaultInstance instance, Server server) {
		if ("com.alibaba.cloud.nacos.ribbon.NacosServer".equals(server.getClass().getName())) {
			NacosServer nacosServer = (NacosServer) server;
			instance.setWeight((int) (nacosServer.getInstance().getWeight() * 100));
			instance.setHealthy(nacosServer.getInstance().isHealthy());
			instance.setMetadata(nacosServer.getMetadata());
		}
	}
}
