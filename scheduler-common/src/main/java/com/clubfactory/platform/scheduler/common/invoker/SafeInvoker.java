package com.clubfactory.platform.scheduler.common.invoker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * @author xiejiajun
 */
@Slf4j
public class SafeInvoker {
    private static final long MAX_SLEEP_MILLISECOND = 256 * 1000L;

    /**
     * @param runnable
     * @param log
     */
    public static void invoke(Runner runnable, Logger log, String errMsg) {
        try {
            runnable.run();
        } catch (Exception e) {
            if (StringUtils.isBlank(errMsg)) {
                log.error("{}", e.getMessage(), e);
                return;
            }
            log.error("{}:{}", errMsg, e.getMessage(), e);
        }
    }

    /**
     * @param runner
     * @param errorConsumer
     */
    public static void invoke(Runner runner, Consumer<Exception> errorConsumer) {
        try {
            runner.run();
        } catch (Exception e) {
            if (Objects.nonNull(errorConsumer)) {
                errorConsumer.accept(e);
            }
        }
    }

    /**
     * @param runner
     * @param errorConsumer
     */
    public static void invoke(Runner runner, Consumer<Exception> errorConsumer, Runnable finallyFunc) {
        try {
            runner.run();
        } catch (Exception e) {
            if (Objects.nonNull(errorConsumer)) {
                errorConsumer.accept(e);
            }
        } finally {
            if (Objects.nonNull(finallyFunc)) {
                finallyFunc.run();
            }
        }
    }

    /**
     * @param runnable
     * @param log
     */
    public static void invoke(Runner runnable, Logger log) {
        invoke(runnable, log, null);
    }

    /**
     * @param runnable
     */
    public static void invoke(Runner runnable) {
        invoke(runnable, log, null);
    }

    /**
     * @param runner
     * @param msg
     */
    public static void invoke(Runner runner, String msg) {
        invoke(runner, log, msg);
    }

    /**
     * @param callable
     * @param log
     */
    public static <R> R call(Callable<R> callable, Logger log, String errMsg) {
        try {
            return callable.call();
        } catch (Exception e) {
            if (StringUtils.isBlank(errMsg)) {
                log.error("{}", e.getMessage(), e);
                return null;
            }
            log.error("{}:{}", errMsg, e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param callable
     * @param errorHandler
     * @param <R>
     * @return
     */
    public static <R> R call(Callable<R> callable, Consumer<Exception> errorHandler) {
        try {
            return callable.call();
        } catch (Exception e) {
            if (Objects.nonNull(errorHandler)) {
                errorHandler.accept(e);
            }
        }
        return null;
    }

    /**
     * @param callable
     * @param log
     */
    public static <R> R call(Callable<R> callable, Logger log) {
        return call(callable, log, null);
    }

    /**
     * @param callable
     */
    public static <R> R call(Callable<R> callable) {
        return call(callable, log, null);
    }

    /**
     * @param callable
     * @param msg
     */
    public static <R> R call(Callable<R> callable, String msg) {
        return call(callable, log, msg);
    }


    /**
     * 支持重试的安全调用
     * @param callable
     * @param retryTimes
     * @param sleepTimeInMilliSecond
     * @param exponential
     * @param <T>
     * @return
     */
    public static <T> T callWithRetry(Callable<T> callable,
                                      int retryTimes,
                                      long sleepTimeInMilliSecond,
                                      boolean exponential,
                                      Consumer<Exception> errorHandler){
        if (errorHandler == null) {
            return Retry.doRetryCall(callable, retryTimes, sleepTimeInMilliSecond, exponential, null);
        }
        return SafeInvoker.call(() -> Retry.doRetryCall(callable, retryTimes, sleepTimeInMilliSecond, exponential, null),
                errorHandler);
    }

    /**
     * @param runner
     * @param retryTimes
     * @param sleepTimeInMilliSecond
     * @param exponential
     */
    public static void invokeWithRetry(Runner runner,
                                       int retryTimes,
                                       long sleepTimeInMilliSecond,
                                       boolean exponential,
                                       Consumer<Exception> errorHandler) {
        if (errorHandler == null) {
            Retry.doRetryRun(runner, retryTimes, sleepTimeInMilliSecond, exponential, null);
            return;
        }
        SafeInvoker.invoke(() -> Retry.doRetryRun(runner, retryTimes, sleepTimeInMilliSecond, exponential, null),
                errorHandler);
    }

    private static class Retry {

        public static void doRetryRun(Runner runner, int retryTimes, long sleepTimeInMilliSecond, boolean exponential, List<Class<?>> retryExceptionClasses) {
            validateParams(runner, retryTimes);
            for (int i = 0; i < retryTimes; i++) {
                try {
                    runner.run();
                    return;
                } catch (Exception e) {
                    doRetryGap(i, e, retryExceptionClasses, retryTimes, sleepTimeInMilliSecond, exponential);
                }
            }
            throw new RuntimeException("Retry Failed");
        }

        public static  <T> T doRetryCall(Callable<T> callable, int retryTimes, long sleepTimeInMilliSecond, boolean exponential, List<Class<?>> retryExceptionClasses){
            validateParams(callable, retryTimes);
            for (int i = 0; i < retryTimes; i++) {
                try {
                    return call(callable);
                } catch (Exception e) {
                    doRetryGap(i, e, retryExceptionClasses, retryTimes, sleepTimeInMilliSecond, exponential);
                }
            }
            throw new RuntimeException("Retry Failed");
        }

        private static <T> T call(Callable<T> callable) throws Exception {
            return callable.call();
        }

        /**
         * 方法入参校验
         * @param runner
         * @param retryTimes
         */
        private static void validateParams(Object runner, int retryTimes) {
            if (null == runner) {
                throw new IllegalArgumentException("系统编程错误, 入参callable不能为空 ! ");
            }

            if (retryTimes < 1) {
                throw new IllegalArgumentException(String.format(
                        "系统编程错误, 入参retryTime[%d]不能小于1 !", retryTimes));
            }
        }

        /**
         * 重试间隙
         * @param e
         * @param retryExceptionClasses
         */
        private static void doRetryGap(int currRunTime,
                                       Exception e,
                                       List<Class<?>> retryExceptionClasses,
                                       int retryTimes,
                                       long sleepTimeInMilliSecond,
                                       boolean exponential) {
            if (currRunTime == 0) {
                log.error(String.format("Exception when calling callable, 异常Msg:%s", e.getMessage()), e);
            }
            if (CollectionUtils.isNotEmpty(retryExceptionClasses) && Objects.nonNull(e)) {
                boolean needRetry = false;
                for (Class<?> eachExceptionClass : retryExceptionClasses) {
                    if (eachExceptionClass == e.getClass()) {
                        needRetry = true;
                        break;
                    }
                }
                if (!needRetry) {
                    throw new RuntimeException(e);
                }
            }
            if (currRunTime + 1 < retryTimes && sleepTimeInMilliSecond > 0) {
                long startTime = System.currentTimeMillis();

                long timeToSleep = sleepTimeInMilliSecond;;
                if (exponential) {
                    timeToSleep = sleepTimeInMilliSecond * (long) Math.pow(2, currRunTime);
                }
                if(timeToSleep >= MAX_SLEEP_MILLISECOND) {
                    timeToSleep = MAX_SLEEP_MILLISECOND;
                }

                long finalTimeToSleep = timeToSleep;
                SafeInvoker.invoke(() -> Thread.sleep(finalTimeToSleep), (Consumer<Exception>) null);
                long realTimeSleep = System.currentTimeMillis()-startTime;
                log.error(String.format("Exception when calling callable, 即将尝试执行第%s次重试.本次重试计划等待[%s]ms,实际等待[%s]ms, 异常Msg:[%s]",
                        currRunTime+1, timeToSleep,realTimeSleep, e.getMessage()));

            }
        }
    }
}
