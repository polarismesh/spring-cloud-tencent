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

package com.tencent.cloud.metadata.service.frontend;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Metadata callee feign client.
 *
 * @author Palmer Xu
 */
@FeignClient(value = "MetadataMiddleService",
		fallback = MetadataMiddleServiceFallback.class)
public interface MetadataMiddleService {

	/**
	 * Get information of callee.
	 * @return information of callee
	 */
	@GetMapping("/metadata/service/middle/info")
	Map<String, Map<String, String>> info();
}
