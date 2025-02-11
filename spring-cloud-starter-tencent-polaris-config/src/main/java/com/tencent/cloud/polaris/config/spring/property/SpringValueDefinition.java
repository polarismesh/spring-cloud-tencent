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

package com.tencent.cloud.polaris.config.spring.property;

/**
 * Spring value.
 * <p>
 * This source file was originally from:
 * <code><a href=https://github.com/apolloconfig/apollo/blob/master/apollo-client/src/main/java/com/ctrip/framework/apollo/spring/property/SpringValueDefinition.java>
 *     SpringValueDefinition</a></code>
 *
 * @author weihubeats 2022-7-10
 */

public class SpringValueDefinition {

	private final String key;
	private final String placeholder;
	private final String propertyName;

	public SpringValueDefinition(String key, String placeholder, String propertyName) {
		this.key = key;
		this.placeholder = placeholder;
		this.propertyName = propertyName;
	}

	public String getKey() {
		return key;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public String getPropertyName() {
		return propertyName;
	}
}
