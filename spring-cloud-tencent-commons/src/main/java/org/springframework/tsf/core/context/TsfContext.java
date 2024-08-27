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

package org.springframework.tsf.core.context;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import com.tencent.cloud.common.metadata.MetadataContext;
import com.tencent.cloud.common.metadata.MetadataContextHolder;
import com.tencent.polaris.metadata.core.MetadataType;
import com.tencent.polaris.metadata.core.TransitiveType;

import org.springframework.tsf.core.entity.Tag;

public final class TsfContext {

	static final int MAX_KEY_LENGTH = 32;
	static final int MAX_VALUE_LENGTH = 128;

	private TsfContext() {

	}

	public static void putTags(Map<String, String> tagMap, Tag.ControlFlag... flags) {
		if (tagMap == null) {
			return;
		}
		MetadataContext tsfCoreContext = MetadataContextHolder.get();
		TransitiveType transitive = TransitiveType.NONE;
		if (null != flags) {
			for (Tag.ControlFlag flag : flags) {
				if (flag == Tag.ControlFlag.TRANSITIVE) {
					transitive = TransitiveType.PASS_THROUGH;
					break;
				}
			}
		}
		for (Map.Entry<String, String> entry : tagMap.entrySet()) {
			validateTag(entry.getKey(), entry.getValue());
		}
		tsfCoreContext.putMetadataAsMap(MetadataType.CUSTOM, transitive, false, tagMap);
	}

	public static void putTag(String key, String value, Tag.ControlFlag... flags) {
		putTags(Collections.singletonMap(key, value), flags);
	}

	private static void validateTag(String key, String value) {
		int keyLength = key.getBytes(StandardCharsets.UTF_8).length;
		int valueLength = value.getBytes(StandardCharsets.UTF_8).length;

		if (keyLength > MAX_KEY_LENGTH) {
			throw new RuntimeException(String.format("Key \"%s\" length (after UTF-8 encoding) exceeding limit (%d)", key,
					MAX_KEY_LENGTH));
		}
		if (valueLength > MAX_VALUE_LENGTH) {
			throw new RuntimeException(String.format("Value \"%s\" length (after UTF-8 encoding) exceeding limit (%d)", value,
					MAX_VALUE_LENGTH));
		}
	}
}
