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

package com.tencent.cloud.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.BeanFactoryUtils.beansOfTypeIncludingAncestors;

/**
 * the utils for bean factory.
 * @author lepdou 2022-05-23
 */
@Component
public final class BeanFactoryUtils implements BeanFactoryAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanFactoryUtils.class);

	private static BeanFactory beanFactory;

	/**
	 * Dynamic parsing of spring @Value.
	 *
	 * @param value something like ${}
	 * @return return null if the parsing fails or the object is not found.
	 */
	public static String resolve(String value) {
		try {
			if (beanFactory instanceof ConfigurableBeanFactory) {
				return ((ConfigurableBeanFactory) beanFactory).resolveEmbeddedValue(value);
			}
		}
		catch (Exception e) {
			LOGGER.error("resolve {} failed.", value, e);
		}

		return null;
	}

	public static <T> List<T> getBeans(BeanFactory beanFactory, Class<T> requiredType) {
		if (!(beanFactory instanceof ListableBeanFactory)) {
			throw new RuntimeException("bean factory not support get list bean. factory type = " + beanFactory.getClass()
					.getName());
		}

		Map<String, T> beanMap = beansOfTypeIncludingAncestors((ListableBeanFactory) beanFactory, requiredType);
		return new ArrayList<>(beanMap.values());
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		BeanFactoryUtils.beanFactory = beanFactory;
	}
}
