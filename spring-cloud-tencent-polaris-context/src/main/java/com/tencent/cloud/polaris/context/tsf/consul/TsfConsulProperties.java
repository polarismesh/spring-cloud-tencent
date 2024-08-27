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

package com.tencent.cloud.polaris.context.tsf.consul;


import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;

/**
 * @author Spencer Gibb
 */
@Validated
public class TsfConsulProperties {

	/** Consul agent hostname. Defaults to '127.0.0.1'. */
	@Value("${tsf_consul_ip:${spring.cloud.consul.host:${SPRING_CLOUD_CONSUL_HOST:localhost}}}")
	@NotNull
	private String host = "localhost";

	/**
	 * Consul agent scheme (HTTP/HTTPS). If there is no scheme in address - client
	 * will use HTTP.
	 */
	@Value("${spring.cloud.consul.scheme:${SPRING_CLOUD_CONSUL_SCHEME:}}")
	private String scheme;

	/** Consul agent port. Defaults to '8500'. */
	@Value("${tsf_consul_port:${spring.cloud.consul.port:${SPRING_CLOUD_CONSUL_PORT:8500}}}")
	@NotNull
	private int port = 8500;

	/** Is spring cloud consul enabled. */
	@Value("${spring.cloud.consul.enabled:${SPRING_CLOUD_CONSUL_ENABLED:true}}")
	private boolean enabled = true;

	@Value("${tsf_consul_ttl_read_timeout:5000}")
	private int ttlReadTimeout = 5000; // default 5s

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public int getTtlReadTimeout() {
		return ttlReadTimeout;
	}

	public void setTtlReadTimeout(int ttlReadTimeout) {
		this.ttlReadTimeout = ttlReadTimeout;
	}


	@Override
	public String toString() {
		return "ConsulProperties{" +
				"host='" + host + '\'' +
				", scheme='" + scheme + '\'' +
				", port=" + port +
				", enabled=" + enabled +
				", ttlReadTimeout=" + ttlReadTimeout +
				'}';
	}
}
