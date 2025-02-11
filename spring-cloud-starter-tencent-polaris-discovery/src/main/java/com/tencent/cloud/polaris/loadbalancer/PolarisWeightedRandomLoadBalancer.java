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

package com.tencent.cloud.polaris.loadbalancer;

import com.tencent.polaris.api.config.consumer.LoadBalanceConfig;
import com.tencent.polaris.api.rpc.Criteria;
import com.tencent.polaris.router.api.core.RouterAPI;
import com.tencent.polaris.router.api.rpc.ProcessLoadBalanceRequest;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;

/**
 * Loadbalancer of Polaris.
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 * @author <a href="mailto:veteranchen@tencent.com">veteranchen</a>
 */
public class PolarisWeightedRandomLoadBalancer extends AbstractPolarisLoadBalancer {

	public PolarisWeightedRandomLoadBalancer(String serviceId, ObjectProvider<ServiceInstanceListSupplier> supplierObjectProvider, RouterAPI routerAPI) {
		super(serviceId, supplierObjectProvider, routerAPI);
	}

	@Override
	protected ProcessLoadBalanceRequest setProcessLoadBalanceRequest(ProcessLoadBalanceRequest req) {
		req.setLbPolicy(LoadBalanceConfig.LOAD_BALANCE_WEIGHTED_RANDOM);
		req.setCriteria(new Criteria());
		return req;
	}
}
