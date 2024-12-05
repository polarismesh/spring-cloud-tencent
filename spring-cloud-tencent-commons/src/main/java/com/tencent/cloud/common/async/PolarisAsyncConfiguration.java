package com.tencent.cloud.common.async;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.tencent.cloud.plugin.threadlocal.TaskExecutorWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static com.tencent.cloud.common.metadata.CrossThreadMetadataContext.CROSS_THREAD_METADATA_CONTEXT_CONSUMER;
import static com.tencent.cloud.common.metadata.CrossThreadMetadataContext.CROSS_THREAD_METADATA_CONTEXT_SUPPLIER;

/**
 * polaris async executor for @Async .
 *
 * @author Haotian Zhang
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@ConditionalOnProperty(name = "spring.cloud.tencent.async.enabled")
public class PolarisAsyncConfiguration implements AsyncConfigurer {

	private static final Logger logger = LoggerFactory.getLogger(PolarisAsyncConfiguration.class);

	@Primary
	@Bean("polarisAsyncExecutor")
	public TaskExecutor polarisAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		int corePoolSize = 10;
		executor.setCorePoolSize(corePoolSize);
		int maxPoolSize = 50;
		executor.setMaxPoolSize(maxPoolSize);
		int queueCapacity = 10;
		executor.setQueueCapacity(queueCapacity);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		String threadNamePrefix = "polaris-async-executor-";
		executor.setThreadNamePrefix(threadNamePrefix);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(5);
		executor.initialize();
		TaskExecutor executorWrapper = new TaskExecutorWrapper<>(executor, CROSS_THREAD_METADATA_CONTEXT_SUPPLIER, CROSS_THREAD_METADATA_CONTEXT_CONSUMER);
		logger.info("Created async executor with corePoolSize:{}, maxPoolSize:{}, queueCapacity:{}", corePoolSize, maxPoolSize, queueCapacity);
		return executorWrapper;
	}

	@Override
	public Executor getAsyncExecutor() {
		return polarisAsyncExecutor();
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return (ex, method, params) -> logger.error("Execute asynchronous tasks '{}' failed.", method, ex);
	}
}
