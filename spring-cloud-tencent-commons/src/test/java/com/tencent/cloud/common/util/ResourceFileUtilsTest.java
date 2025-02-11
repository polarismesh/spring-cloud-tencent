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

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * test for {@link ResourceFileUtils}.
 *
 * @author lepdou 2022-05-27
 */
@ExtendWith(MockitoExtension.class)
public class ResourceFileUtilsTest {

	@Test
	public void testReadExistedFile() throws IOException {
		String content = ResourceFileUtils.readFile("test.txt");
		assertThat(content).isEqualTo("just for test");
	}

	@Test
	public void testReadNotExistedFile() throws IOException {
		String content = ResourceFileUtils.readFile("not_existed_test.txt");
		assertThat(content).isEqualTo("");
	}
}
