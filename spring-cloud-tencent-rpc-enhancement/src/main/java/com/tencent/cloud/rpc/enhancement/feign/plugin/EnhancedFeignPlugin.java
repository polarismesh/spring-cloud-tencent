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

package com.tencent.cloud.rpc.enhancement.feign.plugin;

import org.springframework.core.Ordered;

/**
 * Pre plugin used by EnhancedFeignClient.
 *
 * @author Haotian Zhang
 */
public interface EnhancedFeignPlugin extends Ordered {

	/**
	 * Get name of plugin.
	 *
	 * @return name
	 */
	default String getName() {
		return this.getClass().getName();
	}

	/**
	 * Get type of plugin.
	 *
	 * @return {@link EnhancedFeignPluginType}
	 */
	EnhancedFeignPluginType getType();

	/**
	 * Run the plugin.
	 *
	 * @param context context in enhanced feign client.
	 */
	void run(EnhancedFeignContext context) throws Throwable;

	/**
	 * Handler throwable from {@link EnhancedFeignPlugin#run(EnhancedFeignContext)}.
	 *
	 * @param context context in enhanced feign client.
	 * @param throwable throwable thrown from run method.
	 */
	default void handlerThrowable(EnhancedFeignContext context, Throwable throwable) {

	}
}
