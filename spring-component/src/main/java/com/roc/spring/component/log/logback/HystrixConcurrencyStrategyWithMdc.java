package com.roc.spring.component.log.logback;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import com.netflix.hystrix.util.PlatformSpecific;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chenpeng
 * @version 1.0
 * @date 2017/12/28
 */
public class HystrixConcurrencyStrategyWithMdc extends HystrixConcurrencyStrategy {

    private static HystrixConcurrencyStrategyWithMdc INSTANCE = new HystrixConcurrencyStrategyWithMdc();

    private HystrixConcurrencyStrategyWithMdc() {
    }

    public static HystrixConcurrencyStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public ThreadPoolExecutor getThreadPool(final HystrixThreadPoolKey threadPoolKey, HystrixProperty<Integer> corePoolSize, HystrixProperty<Integer> maximumPoolSize, HystrixProperty<Integer> keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        ThreadFactory threadFactory;
        if (!PlatformSpecific.isAppEngine()) {
            threadFactory = new ThreadFactory() {
                final AtomicInteger threadNumber = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "hystrix-" + threadPoolKey.name() + "-" + threadNumber.incrementAndGet());
                    thread.setDaemon(true);
                    return thread;
                }

            };
        } else {
            threadFactory = PlatformSpecific.getAppEngineThreadFactory();
        }

        return new ThreadPoolTaskExecutorWithMdcPropagation(corePoolSize.get(), maximumPoolSize.get(), keepAliveTime.get(), unit, workQueue, threadFactory);
    }
}
