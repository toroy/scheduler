package com.clubfactory.platform.scheduler.spi.launcher;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.common.utils.OSUtils;
import com.clubfactory.platform.scheduler.core.thread.ThreadUtils;
import com.clubfactory.platform.scheduler.core.utils.CommonUtils;
import com.clubfactory.platform.scheduler.core.utils.LoggerUtils;
import com.clubfactory.platform.scheduler.spi.exception.TaskException;
import com.clubfactory.platform.scheduler.spi.utils.ProcessUtils;
import com.clubfactory.platform.scheduler.core.utils.PropertyUtils;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.po.Task;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractTask;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 命令执行器
 * @author xiejiajun
 */
public abstract class AbstractLauncher implements Launcher {
    /**
     * process
     */
    private Process process;

    /**
     * log handler
     */
    protected Consumer<List<String>> logHandler;

    /**
     * task 启动脚本名称
     */
    protected final String taskName;

    /**
     * logger
     */
    protected Logger logger;

    /**
     * log list
     */
    protected final List<String> logBuffer;

    /**
     * task任务实例
     */
    protected final TaskVO taskInfo;

    private final AbstractTask task;

    private final Object logLock = new Object();

    private volatile boolean logFinished = false;

    private volatile boolean logExceedMaxAllowSize = false;
    private volatile boolean logExceedAlert = false;

    private long allowLogMaxBytes;

    private long currentLogSize = 0;

    private final String logExceedMsg;

    /**
     * 换行符+日志格式话信息所占字节
     */
    private final int lineFormatByte = 21;

    private final AbstractTask.StateTracker stateTracker;


    public AbstractLauncher(AbstractTask task, Logger logger, AbstractTask.StateTracker stateTracker) {
        this.taskInfo = task.getTaskInfo();
        this.logger = logger;
        this.logHandler = this::logHandle;
        this.logBuffer = Collections.synchronizedList(new ArrayList<>());
        this.taskName = String.format("task_%s", taskInfo.getId());
        this.task = task;
        this.stateTracker = stateTracker;
        this.allowLogMaxBytes = PropertyUtils.getLong(Constants.ALLOW_MAX_LOG_BYTES, Constants.DEFAULT_ALLOW_MAX_LOG_BYTES);

        this.logExceedMsg  = "当前任务日志大小超过允许的最大值 " + allowLogMaxBytes + " bytes, 后面的日志将直接丢弃";

        long sysLogSize = 2 * 1024 * 1024;
        this.allowLogMaxBytes -= (logExceedMsg.getBytes().length + lineFormatByte + sysLogSize );
    }

    /**
     * 日志打印
     */
    protected void logHandle(List<String> logs) {
        for (String log : logs) {
            logger.info(log);
        }
    }

    /**
     * task specific execution logic
     *
     * @param execCommand
     * @return
     */
    @Override
    public int launch(String execCommand) throws TaskException {
        int exitStatusCode;
        if (taskInfo.getStartTime() == null) {
            return -1;
        }
        try {
            if (StringUtils.isEmpty(execCommand)) {
                exitStatusCode = 0;
                return exitStatusCode;
            }
            String commandFilePath = getExecutableFilePath();
            // 创建任务启动脚本
            createExecutableFileIfNotExists(execCommand, commandFilePath);
            //创建并启动Shell进程
            buildAndStartProcess(commandFilePath);
            // task执行日志处理
            processTaskLogs(process);
            // 获取任务执行进程PID
            int pid = getProcessId(process);
            // 将TaskInstance 运行时pid更新到实例对象，方便杀任务
            taskInfo.setPid(pid);
            // 将pid保存到DB
            this.updateProcessId();
            logger.info("process start, process id is: {}", pid);
            // 判断是否设置任务超时限制，未设置时不限制超时时间
            boolean status = true;
            if (taskInfo.getTimeout() <= 0) {
                process.waitFor();
            }else {
                long remainTime = taskInfo.getTimeout();
                status = process.waitFor(remainTime, TimeUnit.SECONDS);
            }
            if (!status){
                if(!process.isAlive()){
                    status = true;
                }else {
                    // 任务超时，杀掉
                    ProcessUtils.kill(taskInfo);
                }
            }
            // 等待日志接收结束
            synchronized (logLock) {
                if (!logFinished) {
                    logLock.wait();
                }
            }
            taskInfo.setTaskExit(true);
            if (status) {
                exitStatusCode = process.exitValue();
                logger.info("process has exited, work dir:{}, pid:{} ,exitStatusCode:{}",
                        taskInfo.getExecuteDir() , pid, exitStatusCode);
                // 进程退出码不为0时，检查Yarn Application运行状态来决定task最终状态
                exitStatusCode = ensureState(exitStatusCode);
            } else {
                exitStatusCode = -1;
                logger.warn("process timeout, work dir:{}, pid:{}", taskInfo.getExecuteDir(), pid);
            }
        } catch (InterruptedException e) {
            ProcessUtils.kill(taskInfo);
            exitStatusCode = -1;
            logger.error(String.format("interrupt exception: %s, task may be cancelled or killed", e.getMessage()), e);
            throw new TaskException("interrupt exception. exitCode is :  " + exitStatusCode);
        } catch (Exception e) {
            ProcessUtils.kill(taskInfo);
            exitStatusCode = -1;
            logger.error(e.getMessage(), e);
            throw new TaskException("process error . exitCode is :  " + exitStatusCode);
        } finally {
            if (this.process != null) {
                IOUtils.closeQuietly(this.process.getInputStream());
                IOUtils.closeQuietly(this.process.getOutputStream());
                IOUtils.closeQuietly(this.process.getErrorStream());
            }
        }
        return exitStatusCode;
    }

    /**
     * build process
     *
     * @param commandFile
     * @throws IOException
     */
    private void buildAndStartProcess(String commandFile) throws IOException {
        // init process builder
        ProcessBuilder processBuilder = new ProcessBuilder();
        // setting up a working directory
        processBuilder.directory(new File(taskInfo.getExecuteDir()));
        // merge error information to standard output stream
        processBuilder.redirectErrorStream(true);
        // setting up user to run commands
        List<String> taskLaunchCommand = Lists.newArrayList();
        if (CommonUtils.isMultiTenantModeStartup()) {
            taskLaunchCommand.add("sudo");
            taskLaunchCommand.add("-u");
            taskLaunchCommand.add(taskInfo.getTenant());
        }
        taskLaunchCommand.addAll(launchCommand());
        taskLaunchCommand.add(commandFile);
        processBuilder.command(taskLaunchCommand);
        process = processBuilder.start();

        // print command
        printCommand(processBuilder);
    }

    /**
     * 确认最终上状态
     * @param
     * @param exitStatusCode
     * @return
     */
    private int ensureState(int exitStatusCode) {
        return task.ensureState(exitStatusCode);
    }

    /**
     * soft kill
     *
     * @param processId
     * @return
     * @throws InterruptedException
     */
    private boolean softKill(int processId) {
        if (processId != 0 && process.isAlive()) {
            try {
                // sudo -u user command to run command
                String cmd = String.format("sudo kill %d", processId);
                logger.info("soft kill task:{}, process id:{}, cmd:{}", taskName, processId, cmd);
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                logger.info("kill attempt failed." + e.getMessage(), e);
            }
        }
        return process.isAlive();
    }

    /**
     * hard kill
     *
     * @param processId
     */
    private void hardKill(int processId) {
        if (processId != 0 && process.isAlive()) {
            try {
                String cmd = String.format("sudo kill -9 %d", processId);
                logger.info("hard kill task:{}, process id:{}, cmd:{}", taskName, processId, cmd);
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                logger.error("kill attempt failed." + e.getMessage(), e);
            }
        }
    }

    /**
     * 打印执行命令
     *
     * @param processBuilder
     */
    private void printCommand(ProcessBuilder processBuilder) {
        String cmdStr;
        try {
            cmdStr = ProcessUtils.buildCommandStr(processBuilder.command());
            logger.info("task run command: {}", cmdStr);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * clear
     */
    private void clear() {
        if (logBuffer.isEmpty()) {
            return;
        }
        if (logExceedMaxAllowSize && logExceedAlert) {
            return;
        }
        if (logExceedMaxAllowSize) {
            this.logExceedAlert = true;
        }
        logHandler.accept(logBuffer);
        logBuffer.clear();
    }

    /**
     * task执行日志处理
     */
    private void processTaskLogs(Process process) {
        String taskLoggerThreadName = String.format(LoggerUtils.TASK_LOGGER_THREAD_NAME + "-%s", taskName);
        ExecutorService  executorService = ThreadUtils.newDaemonSingleThreadExecutor(taskLoggerThreadName);
        executorService.submit(() -> {
            BufferedReader inReader = null;
            try {
                inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                long lastFlushTime = System.currentTimeMillis();
                while ((line = inReader.readLine()) != null) {
                    if (logExceedMaxAllowSize) {
                        continue;
                    }
                    this.currentLogSize += (line.getBytes().length + lineFormatByte);
                    if (this.currentLogSize >= this.allowLogMaxBytes){
                        this.logExceedMaxAllowSize = true;
                        logBuffer.add(logExceedMsg);
                        lastFlushTime = flush(lastFlushTime, true);
                    } else {
                        logBuffer.add(line);
                        lastFlushTime = flush(lastFlushTime, false);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                clear();
                close(inReader);
                synchronized (logLock) {
                    logFinished = true;
                    logLock.notifyAll();
                }
            }
        });
        executorService.shutdown();
    }

    public int getPid() {
        return getProcessId(process);
    }


    /**
     * 获取
     *
     * @return
     */
    private long getRemainTime() throws TaskException {
        long remainTime = taskInfo.getTimeout();
        if (remainTime < 0) {
            throw new TaskException("task execution time out");
        }
        return remainTime;
    }

    /**
     * get process id
     *
     * @param process
     * @return
     */
    private int getProcessId(Process process) {
        int processId = 0;
        try {
            Field f = process.getClass().getDeclaredField(Constants.PID);
            f.setAccessible(true);
            processId = f.getInt(process);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
        return processId;
    }

    /**
     * when log buffer siz or flush time reach condition , then flush
     *
     * @param lastFlushTime last flush time
     * @param force 是否强制将日志刷到磁盘
     * @return
     */
    private long flush(long lastFlushTime, boolean force) {
        long now = System.currentTimeMillis();
        /**
         * 每1秒或者buffer size超过64条时将日志刷到文件
         */
        if (logBuffer.size() < Constants.DEFAULT_LOG_ROWS_NUM &&
                now - lastFlushTime <= Constants.DEFAULT_LOG_FLUSH_INTERVAL && !force) {
            return lastFlushTime;
        }
        lastFlushTime = now;
        if (logExceedMaxAllowSize && logExceedAlert) {
            return lastFlushTime;
        }
        logHandler.accept(logBuffer);
        if (logExceedMaxAllowSize) {
            this.logExceedAlert = true;
        }
        logBuffer.clear();
        return lastFlushTime;
    }

    /**
     * close buffer reader
     *
     * @param inReader
     */
    private void close(BufferedReader inReader) {
        if (inReader != null) {
            try {
                inReader.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void updateProcessId() throws Exception {
        TaskVO task = new TaskVO();
        task.setId(taskInfo.getId());
        task.setPid(taskInfo.getPid());
        this.stateTracker.update(task);
    }

    @Override
    public boolean destroy(){
        return true;
    }

    @Override
    public void cancel() {
        if (process == null) {
            return;
        }
        this.clear();
        int processId = getProcessId(process);
        logger.info("cancel process: {}", processId);
        // kill , waiting for completion
        boolean killed = softKill(processId);
        if (!killed) {
            // hard kill
            hardKill(processId);
            // destroy
            process.destroy();
            process = null;
        }
    }

    /**
     * 根据模版生成任务启动脚本绝对路径
     *
     * @return
     */
    protected abstract String getExecutableFilePath();

    /**
     * 脚本启动命令
     *
     * @return
     */
    protected abstract List<String> launchCommand();

    /**
     * 创建任务启动脚本
     *
     * @param execCommand
     * @param commandFile
     * @throws IOException
     */
    protected abstract void createExecutableFileIfNotExists(String execCommand, String commandFile) throws IOException;
}
