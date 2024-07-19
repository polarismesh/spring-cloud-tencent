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
package com.tencent.cloud.tsf.demo.provider;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletResponse;

import com.tencent.cloud.tsf.demo.provider.config.ProviderNameConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author leoziltong@tencent.com
 * @Date: 2021/5/11 17:10
 */
@RestController
public class ProviderController {

	private static final Logger LOG = LoggerFactory.getLogger(ProviderController.class);

	@Value("${spring.application.name:}")
	private String applicationName;

	@Autowired
	private ProviderNameConfig providerNameConfig;

	// 获取本机ip
	public static String getInet4Address() {
		Enumeration<NetworkInterface> nis;
		String ip = null;
		try {
			nis = NetworkInterface.getNetworkInterfaces();
			for (; nis.hasMoreElements(); ) {
				NetworkInterface ni = nis.nextElement();
				Enumeration<InetAddress> ias = ni.getInetAddresses();
				for (; ias.hasMoreElements(); ) {
					InetAddress ia = ias.nextElement();
					if (ia instanceof Inet4Address && !ia.getHostAddress().equals("127.0.0.1")) {
						ip = ia.getHostAddress();
					}
				}
			}
		}
		catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip;
	}

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String hello() {
		String echoHello = "say hello";
		LOG.info(echoHello);
		return echoHello;
	}

	@RequestMapping(value = "/echo/{param}", method = RequestMethod.GET)
	public String echo(@PathVariable String param, HttpServletResponse response) throws IOException {
		switch (param) {
		case "1xx":
			response.setStatus(HttpServletResponse.SC_CONTINUE);
			response.getWriter().write("mock 1xx return.");
			response.getWriter().flush();
			return "mock 1xx return.";
		case "3xx":
			response.setStatus(HttpServletResponse.SC_FOUND);
			response.getWriter().write("mock 3xx return.");
			response.getWriter().flush();
			return "mock 3xx return.";
		case "4xx":
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("mock 4xx return.");
			response.getWriter().flush();
			return "mock 4xx return.";
		case "5xx":
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().write("mock 5xx return.");
			response.getWriter().flush();
			return "mock 5xx return.";
		default:
			LOG.info("provider-demo -- request param: [" + param + "]");
			String result = "from host-ip: " + getInet4Address() + ", request param: " + param + ", response from " + providerNameConfig.getName();
			LOG.info("provider-demo -- provider config name: [" + providerNameConfig.getName() + ']');
			LOG.info("provider-demo -- response info: [" + result + "]");
			return result;
		}

	}

	@RequestMapping(value = "/echo/error/{param}", method = RequestMethod.GET)
	public String echoError(@PathVariable String param) {
		LOG.info("Error request param: [" + param + "], throw exception");

		throw new RuntimeException("mock-ex");
	}

	/**
	 * 延迟返回.
	 * @param param 参数
	 * @param delay 延时时间，单位毫秒
	 * @throws InterruptedException InterruptedException
	 */
	@RequestMapping(value = "/echo/slow/{param}", method = RequestMethod.GET)
	public String echoSlow(@PathVariable String param, @RequestParam(required = false) Integer delay) throws InterruptedException {
		int sleepTime = delay == null ? 1000 : delay;
		LOG.info("slow request param: [" + param + "], Start sleep: [" + sleepTime + "]ms");
		Thread.sleep(sleepTime);
		LOG.info("slow request param: [" + param + "], End sleep: [" + sleepTime + "]ms");

		String result = "request param: " + param
				+ ", slow response from " + applicationName
				+ ", sleep: [" + sleepTime + "]ms";
		return result;
	}

	@RequestMapping(value = "/echo/unit/{param}", method = RequestMethod.GET)
	public String echoUnit(@PathVariable String param) {
		LOG.info("provider-demo -- unit request param: [" + param + "]");
		String result = "request param: " + param + ", response from " + applicationName;
		LOG.info("provider-demo -- unit provider config name: [" + applicationName + ']');
		LOG.info("provider-demo -- unit response info: [" + result + "]");
		return result;
	}
}
