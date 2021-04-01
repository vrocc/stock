package com.roc.spring.component.log.logback;


import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link ThreadPoolTaskExecutor} that propagates {@link MDC} context from
 * calling thread to executor thread
 *
 * @author chenpeng
 */
public class ThreadPoolTaskExecutorWithMdcPropagation extends ThreadPoolExecutor {

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default thread factory and rejected execution handler.
     * It may be more convenient to use one of the {@link Executors} factory
     * methods instead of this general purpose constructor.
     *
     * @param corePoolSize    the number of threads to keep in the pool, even
     *                        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *                        pool
     * @param keepAliveTime   when the number of threads is greater than
     *                        the core, this is the maximum time that excess idle threads
     *                        will wait for new tasks before terminating.
     * @param unit            the time unit for the {@code keepAliveTime} argument
     * @param workQueue       the queue to use for holding tasks before they are
     *                        executed.  This queue will hold only the {@code Runnable}
     *                        tasks submitted by the {@code execute} method.
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException     if {@code workQueue} is null
     */
    public ThreadPoolTaskExecutorWithMdcPropagation(int corePoolSize,
                                                    int maximumPoolSize,
                                                    long keepAliveTime,
                                                    TimeUnit unit,
                                                    BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default rejected execution handler.
     *
     * @param corePoolSize    the number of threads to keep in the pool, even
     *                        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *                        pool
     * @param keepAliveTime   when the number of threads is greater than
     *                        the core, this is the maximum time that excess idle threads
     *                        will wait for new tasks before terminating.
     * @param unit            the time unit for the {@code keepAliveTime} argument
     * @param workQueue       the queue to use for holding tasks before they are
     *                        executed.  This queue will hold only the {@code Runnable}
     *                        tasks submitted by the {@code execute} method.
     * @param threadFactory   the factory to use when the executor
     *                        creates a new thread
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException     if {@code workQueue}
     *                                  or {@code threadFactory} is null
     */
    public ThreadPoolTaskExecutorWithMdcPropagation(int corePoolSize,
                                                    int maximumPoolSize,
                                                    long keepAliveTime,
                                                    TimeUnit unit,
                                                    BlockingQueue<Runnable> workQueue,
                                                    ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters and default thread factory.
     *
     * @param corePoolSize    the number of threads to keep in the pool, even
     *                        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *                        pool
     * @param keepAliveTime   when the number of threads is greater than
     *                        the core, this is the maximum time that excess idle threads
     *                        will wait for new tasks before terminating.
     * @param unit            the time unit for the {@code keepAliveTime} argument
     * @param workQueue       the queue to use for holding tasks before they are
     *                        executed.  This queue will hold only the {@code Runnable}
     *                        tasks submitted by the {@code execute} method.
     * @param handler         the handler to use when execution is blocked
     *                        because the thread bounds and queue capacities are reached
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException     if {@code workQueue}
     *                                  or {@code handler} is null
     */
    public ThreadPoolTaskExecutorWithMdcPropagation(int corePoolSize,
                                                    int maximumPoolSize,
                                                    long keepAliveTime,
                                                    TimeUnit unit,
                                                    BlockingQueue<Runnable> workQueue,
                                                    RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters.
     *
     * @param corePoolSize    the number of threads to keep in the pool, even
     *                        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *                        pool
     * @param keepAliveTime   when the number of threads is greater than
     *                        the core, this is the maximum time that excess idle threads
     *                        will wait for new tasks before terminating.
     * @param unit            the time unit for the {@code keepAliveTime} argument
     * @param workQueue       the queue to use for holding tasks before they are
     *                        executed.  This queue will hold only the {@code Runnable}
     *                        tasks submitted by the {@code execute} method.
     * @param threadFactory   the factory to use when the executor
     *                        creates a new thread
     * @param handler         the handler to use when execution is blocked
     *                        because the thread bounds and queue capacities are reached
     * @throws IllegalArgumentException if one of the following holds:<br>
     *                                  {@code corePoolSize < 0}<br>
     *                                  {@code keepAliveTime < 0}<br>
     *                                  {@code maximumPoolSize <= 0}<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException     if {@code workQueue}
     *                                  or {@code threadFactory} or {@code handler} is null
     */
    public ThreadPoolTaskExecutorWithMdcPropagation(int corePoolSize,
                                                    int maximumPoolSize,
                                                    long keepAliveTime,
                                                    TimeUnit unit,
                                                    BlockingQueue<Runnable> workQueue,
                                                    ThreadFactory threadFactory,
                                                    RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void execute(Runnable task) {
        super.execute(new RunnableWrapperWithMdc(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(new CallableWrapperWithMdc<>(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(new RunnableWrapperWithMdc(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(new RunnableWrapperWithMdc(task), result);
    }

    /**
     * Helper {@link Runnable} class that transfers {@link MDC} context values from
     * the origin thread to the execution thread
     */
    static class RunnableWrapperWithMdc implements Runnable {
        private final Runnable wrapped;
        private final Map<String, String> map;

        public RunnableWrapperWithMdc(Runnable wrapped) {
            this.wrapped = wrapped;
            // we are in the origin thread: capture the MDC
            map = MDC.getCopyOfContextMap();
        }

        @Override
        public void run() {
            // we are in the execution thread: set the original MDC
            MDC.setContextMap(map);
            wrapped.run();
        }
    }

    /**
     * Helper {@link Callable} class that transfers {@link MDC} context values from
     * the origin thread to the execution thread
     */
    static class CallableWrapperWithMdc<T> implements Callable<T> {
        private final Callable<T> wrapped;
        private final Map<String, String> map;

        public CallableWrapperWithMdc(Callable<T> wrapped) {
            this.wrapped = wrapped;
            // we are in the origin thread: capture the MDC
            map = MDC.getCopyOfContextMap();
        }

        @Override
        public T call() throws Exception {
            // we are in the execution thread: set the original MDC
            MDC.setContextMap(map);
            return wrapped.call();
        }
    }

}
