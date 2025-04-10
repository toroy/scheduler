/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clubfactory.platform.scheduler.core.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.*;

/**
 * thread utils
 */
public class ThreadUtils {


    private static final ThreadMXBean threadBean =  ManagementFactory.getThreadMXBean();
    private static final int STACK_DEPTH = 20;

    /**
       Wrapper over newCachedThreadPool. Thread names are formatted as prefix-ID, where ID is a
     * unique, sequentially assigned integer.
     * @param prefix
     * @return
     */
    public static ThreadPoolExecutor newDaemonCachedThreadPool(String prefix){
        ThreadFactory threadFactory = namedThreadFactory(prefix);
        return ((ThreadPoolExecutor) Executors.newCachedThreadPool(threadFactory));
    }

    /**
     * Create a thread factory that names threads with a prefix and also sets the threads to daemon.
     * @param prefix
     * @return
     */
    private static ThreadFactory namedThreadFactory(String prefix) {
        return new ThreadFactoryBuilder().setDaemon(true).setNameFormat(prefix + "-%d").build();
    }


    /**
     * Create a cached thread pool whose max number of threads is `maxThreadNumber`. Thread names
     * are formatted as prefix-ID, where ID is a unique, sequentially assigned integer.
     * @param prefix
     * @param maxThreadNumber
     * @param keepAliveSeconds
     * @return
     */
    public static ThreadPoolExecutor newDaemonCachedThreadPool(String prefix ,
                                                               int maxThreadNumber,
                                                               int keepAliveSeconds){
        ThreadFactory threadFactory = namedThreadFactory(prefix);
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                // corePoolSize: the max number of threads to create before queuing the tasks
                maxThreadNumber,
                // maximumPoolSize: because we use LinkedBlockingDeque, this one is not used
                maxThreadNumber,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory);
        threadPool.allowCoreThreadTimeOut(true);
        return threadPool;
    }


    /**
     * Wrapper over newFixedThreadPool. Thread names are formatted as prefix-ID, where ID is a
     * unique, sequentially assigned integer.
     * @param nThreads
     * @param prefix
     * @return
     */
    public static ThreadPoolExecutor newDaemonFixedThreadPool(int nThreads , String prefix){
        ThreadFactory threadFactory = namedThreadFactory(prefix);
        return ((ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads, threadFactory));
    }

    /**
     * Wrapper over newSingleThreadExecutor.
     * @param threadName
     * @return
     */
    public static ExecutorService newDaemonSingleThreadExecutor(String threadName){
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat(threadName)
                .build();
        return Executors.newSingleThreadExecutor(threadFactory);
    }

    /**
     * Wrapper over newDaemonFixedThreadExecutor.
     * @param threadName
     * @param threadsNum
     * @return
     */
    public static ExecutorService newDaemonFixedThreadExecutor(String threadName,int threadsNum){
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat(threadName)
                .build();
        return Executors.newFixedThreadPool(threadsNum,threadFactory);
    }


    /**
     * Wrapper over newDaemonFixedThreadExecutor.
     * @param threadName
     * @param corePoolSize
     * @return
     */
    public static ExecutorService newDaemonFixedThreadExecutor(String threadName,int corePoolSize,int maximumPoolSize){
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat(threadName)
                .build();
        return new ThreadPoolExecutor(corePoolSize,maximumPoolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(2048), threadFactory);
    }

    /**
     * Wrapper over ScheduledThreadPoolExecutor
     * @param corePoolSize
     * @return
     */
    public static ScheduledExecutorService newDaemonThreadScheduledExecutor(String threadName,int corePoolSize) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat(threadName)
                .build();
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
        // By default, a cancelled task is not automatically removed from the work queue until its delay
        // elapses. We have to enable it manually.
        executor.setRemoveOnCancelPolicy(true);
        return executor;
    }


    public static ThreadInfo getThreadInfo(Thread t) {
        long tid = t.getId();
        return threadBean.getThreadInfo(tid, STACK_DEPTH);
    }


    /**
     * Format the given ThreadInfo object as a String.
     * @param indent a prefix for each line, used for nested indentation
     */
    public static String formatThreadInfo(ThreadInfo threadInfo, String indent) {
        StringBuilder sb = new StringBuilder();
        appendThreadInfo(sb, threadInfo, indent);
        return sb.toString();
    }


    /**
     * Print all of the thread's information and stack traces.
     *
     * @param sb
     * @param info
     * @param indent
     */
    public static void appendThreadInfo(StringBuilder sb,
                                        ThreadInfo info,
                                        String indent) {
        boolean contention = threadBean.isThreadContentionMonitoringEnabled();

        if (info == null) {
            sb.append(indent).append("Inactive (perhaps exited while monitoring was done)\n");
            return;
        }
        String taskName = getTaskName(info.getThreadId(), info.getThreadName());
        sb.append(indent).append("Thread ").append(taskName).append(":\n");

        Thread.State state = info.getThreadState();
        sb.append(indent).append("  State: ").append(state).append("\n");
        sb.append(indent).append("  Blocked count: ").append(info.getBlockedCount()).append("\n");
        sb.append(indent).append("  Waited count: ").append(info.getWaitedCount()).append("\n");
        if (contention) {
            sb.append(indent).append("  Blocked time: " + info.getBlockedTime()).append("\n");
            sb.append(indent).append("  Waited time: " + info.getWaitedTime()).append("\n");
        }
        if (state == Thread.State.WAITING) {
            sb.append(indent).append("  Waiting on ").append(info.getLockName()).append("\n");
        } else  if (state == Thread.State.BLOCKED) {
            sb.append(indent).append("  Blocked on ").append(info.getLockName()).append("\n");
            sb.append(indent).append("  Blocked by ").append(
                    getTaskName(info.getLockOwnerId(), info.getLockOwnerName())).append("\n");
        }
        sb.append(indent).append("  Stack:").append("\n");
        for (StackTraceElement frame: info.getStackTrace()) {
            sb.append(indent).append("    ").append(frame.toString()).append("\n");
        }
    }

    private static String getTaskName(long id, String name) {
        if (name == null) {
            return Long.toString(id);
        }
        return id + " (" + name + ")";
    }
}
