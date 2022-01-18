package org.eclipse.dataspaceconnector.kafka.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class FutureUtil {

    public static <T> CompletableFuture<T> createCompletableFuture(Future<T> future) {
        if (future.isDone()) {
            CompletableFuture<T> completableFuture = new CompletableFuture<>();
            try {
                completableFuture.complete(future.get());
            } catch (Throwable ex) {
                completableFuture.completeExceptionally(ex);
                return completableFuture;
            }
            return completableFuture;
        }

        // makes the future blocking
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!future.isDone()) {
                    // blocks until future is done
                    awaitFuture(future);
                }
                return future.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                // Normally, this should never happen inside ForkJoinPool
                Thread.currentThread().interrupt();
                // Add the following statement if the future doesn't have side effects
                // future.cancel(true);
                throw new RuntimeException(e);
            }
        });
    }

    private static void awaitFuture(Future<?> future) throws InterruptedException {
        ForkJoinPool.managedBlock(new ForkJoinPool.ManagedBlocker() {
            @Override
            public boolean block() throws InterruptedException {
                try {
                    future.get();
                } catch (ExecutionException executionException) {
                    throw new RuntimeException(executionException);
                }
                return true;
            }

            @Override
            public boolean isReleasable() {
                return future.isDone();
            }
        });
    }
}
