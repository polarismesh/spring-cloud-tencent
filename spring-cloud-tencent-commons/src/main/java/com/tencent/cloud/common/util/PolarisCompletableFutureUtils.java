package com.tencent.cloud.common.util;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.tencent.polaris.threadlocal.cross.CompletableFutureUtils;

import static com.tencent.cloud.common.metadata.CrossThreadMetadataContext.CROSS_THREAD_METADATA_CONTEXT_CONSUMER;
import static com.tencent.cloud.common.metadata.CrossThreadMetadataContext.CROSS_THREAD_METADATA_CONTEXT_SUPPLIER;

/**
 * Polaris CompletableFuture Utils.
 *
 * @author Haotian Zhang
 */
public final class PolarisCompletableFutureUtils {

	private PolarisCompletableFutureUtils() {
	}

	public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
		return CompletableFutureUtils.supplyAsync(supplier, CROSS_THREAD_METADATA_CONTEXT_SUPPLIER, CROSS_THREAD_METADATA_CONTEXT_CONSUMER);
	}

	public static CompletableFuture<Void> runAsync(Runnable runnable) {
		return CompletableFutureUtils.runAsync(runnable, CROSS_THREAD_METADATA_CONTEXT_SUPPLIER, CROSS_THREAD_METADATA_CONTEXT_CONSUMER);
	}
}
