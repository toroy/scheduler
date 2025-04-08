package com.clubfactory.platform.scheduler.engine.runner;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.clubfactory.platform.common.exception.BizException;
import com.clubfactory.platform.scheduler.common.utils.DateUtils;
import com.clubfactory.platform.scheduler.common.utils.OSUtils;
import com.clubfactory.platform.scheduler.core.service.ICommonService;
import com.clubfactory.platform.scheduler.core.utils.*;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.clubfactory.platform.scheduler.dal.po.*;
import com.clubfactory.platform.scheduler.engine.config.TaskLogDiscriminator;
import com.clubfactory.platform.scheduler.engine.dao.DataAccessFactory;
import com.clubfactory.platform.scheduler.engine.mgr.LogicMemoryMgr;
import com.clubfactory.platform.scheduler.engine.mgr.TaskRunnerMgr;
import com.clubfactory.platform.scheduler.engine.utils.TaskLock;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractTask;
import com.clubfactory.platform.scheduler.engine.task.TaskManager;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.sift.SiftingAppender;

/**
 * @author xiejiajun
 */
public class TaskRunner implements Runnable {
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(TaskRunner.class);

    /**
     *  task instance
     */
    private TaskVO taskInstance;

    /**
     *  process database access
     */
    private final ICommonService dataAccessProxy;


    private final TaskRunnerMgr taskRunnerMgr;

    /**
     * 用于执行的task
     */
    private AbstractTask executableTask;

    private LogicMemoryMgr logicMemoryMgr;

    private int mbMemPeak;

    public TaskRunner(TaskVO taskInstance,
                      TaskRunnerMgr taskRunnerMgr,
                      int mbMemPeak ){
        this.taskInstance = taskInstance;
        this.taskRunnerMgr = taskRunnerMgr;
        this.mbMemPeak = mbMemPeak;
        this.logicMemoryMgr = LogicMemoryMgr.getInstance();
        this.dataAccessProxy = DataAccessFactory.getDataAccessProxy();
    }

    @Override
    public void run() {
        String logVersion = DateUtils.format(new Date(),"yyyy-MM-dd_HH:mm:ss");
        taskInstance.setLogVersion(logVersion);
        Logger taskLogger = LoggerFactory.getLogger(
                // - [taskAppId=TASK-task_${taskId}_${yyyy-MM-dd_HH:mm:ss}]
                LoggerUtils.buildTaskId(LoggerUtils.TASK_LOGGER_INFO_PREFIX, taskInstance.getId(),logVersion)
        );
        try {
            // 1. 初始化task,生成并创建execPath,logPath,proxyUser
            taskLogger.info("===========current task run at worker:{}===========", OSUtils.getHost());
            this.taskInitialize();
            executableTask = this.newTask(taskLogger);
            executableTask.preHandle();
            logger.info("start execute task {} ...",taskInstance.getId());
            executableTask.handle();
            logger.info("task {} execute finished...",taskInstance.getId());
        }catch (Exception e){
            logger.error(String.format("task %s scheduler failure",taskInstance.getId()), e);
            taskLogger.error(String.format("task %s scheduler failure",taskInstance.getId()), e);
            if (executableTask != null) {
                executableTask.setExitStatusCode(Constants.EXIT_CODE_FAILURE);
                kill();
            }
        }finally {
            if (executableTask != null) {
                try {
                    executableTask.postHandle();
                }catch (Exception e){
                    logger.error("task post handle call failed");
                    taskLogger.error(e.getMessage(),e);
                }
            }
            if (!taskInstance.isRunOnTmpEmr()) {
                logicMemoryMgr.releaseMemory(this.mbMemPeak);
            }else {
                logger.info("临时集群未占用逻辑内存资源,无须释放");
            }
            TaskStatusEnum exitStatus = TaskStatusEnum.FAILED;
            if (executableTask != null){
                exitStatus = executableTask.getExitStatus();
            }
            logger.info("task instance id : {},task final status : {}", taskInstance.getId(), exitStatus);
            taskLogger.info("task instance id : {},task final status : {}", taskInstance.getId(), exitStatus);
            try {
                updateFinishState();
            }catch (Exception e){
                logger.error("update task {} exit state failed: {}", taskInstance.getId(),e.getMessage());
            }finally {
                try {
                    clearExecutionEnv();
                } catch (Exception e) {
                    logger.error("clear task {} data failed: {}", taskInstance.getId(), e.getMessage());
                }
            }
        }
    }

    /**
     * 构建task实例
     * @return
     */
    private AbstractTask newTask(Logger taskLogger) {
        JobType jobType = taskInstance.getJobTypeInfo();
        if (jobType == null || !jobType.isValid()){
            throw new BizException("作业类型配置信息缺失或不完整");
        }
        if (taskInstance.isRunOnTmpEmr() && StringUtils.isBlank(jobType.getPluginEmrClazz())) {
            throw new BizException("任务类型" + jobType.getPluginName() + "暂不支持临时集群");
        }
        return TaskManager.newTask(taskInstance, taskLogger, taskInfo -> {
            this.dataAccessProxy.updateTaskInfo(taskInfo);
            return true;
        });
    }

    /**
     * 检查任务相关属性
     */
    private void checkTaskValid(){
        if (StringUtils.isBlank(taskInstance.getScriptBasePath())){
            logger.error("脚本存储路径为空,退出执行...");
            throw new RuntimeException("脚本存储路径为空,退出执行...");
        }
        if (taskInstance.getVersion() == null){
            logger.error("脚本版本号为空,退出执行...");
            throw new RuntimeException("脚本版本号为空,退出执行...");
        }
        if (StringUtils.isBlank(taskInstance.getFileName())){
            logger.error("脚本文件名为空,退出执行...");
            throw new RuntimeException("脚本文件名为空,退出执行...");
        }
    }


    /**
     * 任务初始化
     * @throws IOException
     */
    private void taskInitialize() throws IOException {
        String execLocalDir = FileUtils.getProcessExecDir(taskInstance.getId());
        // 创建执行目录
        FileUtils.createWorkDirAndUserIfAbsent(execLocalDir,
                taskInstance.getTenant(), logger);
        taskInstance.setExecuteDir(execLocalDir);

        // 添加日志映射关系
        addLogMap();

        // 更新taskInstance信息：execTime,execPath,logPath
        updateTaskInfo();

        // 检查任务属性
        checkTaskValid();

        // 补全taskVo中需要查DB得到的属性
        completeTaskPros();

        logger.info("Task {} 初始化完成",taskInstance.getId());

    }

    /**
     * 添加日志映射关系
     */
    private void addLogMap(){
        String logBase = ((TaskLogDiscriminator) ((SiftingAppender) ((LoggerContext) LoggerFactory.getILoggerFactory())
                .getLogger("ROOT")
                .getAppender("TASKLOGFILE"))
                .getDiscriminator()).getLogBase();

        String logPath;
        // ${logDir}/task_instance/task_${taskId}_${yyyy-MM-dd_HH:mm:ss}.log
        if (logBase.startsWith(Constants.SINGLE_SLASH)){
            logPath =  logBase + File.separator +
                    "task_instance" + File.separator +
                    "task" + Constants.UNDERLINE +
                    taskInstance.getId() + Constants.UNDERLINE +
                    taskInstance.getLogVersion() + ".log";
        }else {
            logPath = System.getProperty("user.dir") + File.separator +
                    logBase + File.separator +
                    "task_instance" + File.separator +
                    "task" + Constants.UNDERLINE +
                    taskInstance.getId() + Constants.UNDERLINE +
                    taskInstance.getLogVersion() + ".log";
        }
        // 更新任务日志文件路径
        taskInstance.setLogPath(logPath);
        Date createTime = new Date();
        LogMap logMap = LogMap.builder()
                .logHost(OSUtils.getHost())
                .logName(taskInstance.getLogVersion())
                .logPath(logPath)
                .taskId(taskInstance.getId())
                .createTime(createTime)
                .updateTime(createTime)
                .build();
        dataAccessProxy.saveLogMap(logMap);
    }

    /**
     * 补全task解析时需要用到的属性
     */
    private void completeTaskPros(){
        // 初始化环境变量路径
        taskInstance.setEnvFile(CommonUtils.getSystemEnvPath());
        // 设置任务owner的英文名称
        taskInstance.setTenant(dataAccessProxy.getUsernameById(taskInstance.getCreateUser()));

        int timeout = -1;
        String jobParam;
        // 获取作业信息
        BaseJob jobInfo = taskInstance.getBaseJob();
        if (jobInfo != null){
            jobParam = jobInfo.getExecParam();
            timeout = jobInfo.getTimeOut() != null ? jobInfo.getTimeOut() : timeout;
        }else {
            throw new RuntimeException(String.format("未找到task:[%s]对应的jobId:[%s]相关的Job信息",taskInstance.getId(),
                    taskInstance.getJobId()));
        }

        if (jobParam == null){
            jobParam = JSONUtils.toJsonString(Maps.newHashMap());
        }

        // 设置任务运行参数模版
        taskInstance.setParams(jobParam);
        if (timeout > 0){
            taskInstance.setTimeout(timeout);
        }

    }

    /**
     * 更新任务信息
     */
    private void updateTaskInfo(){
        Task task = new Task();
        task.setId(taskInstance.getId());
        Date execTime = new Date();
        task.setExecTime(execTime);
        taskInstance.setExecTime(execTime);
        task.setLogPath(taskInstance.getLogPath());
        task.setIp(OSUtils.getHost());
        task.setExecuteDir(taskInstance.getExecuteDir());
        dataAccessProxy.updateTaskInfo(task);
        logger.info("Task {} 信息更新完成....",taskInstance.getId());
    }

    /**
     *  kill task
     */
    public void kill(){
        if (executableTask != null){
            try {
                executableTask.cancelApplication(true);
            }catch (Exception e){
                logger.error(e.getMessage(),e);
            }
        }
    }

    /**
     * 任务执行完成，更新状态和结束时间
     */
    private void updateFinishState(){
        logger.info("update task exit status ...");
        TaskStatusEnum status = TaskStatusEnum.FAILED;
        if (executableTask != null){
            status = executableTask.getExitStatus();
        }
        if (BooleanUtils.isTrue(taskInstance.getHasBlockDqcChildren())
                && TaskStatusEnum.SUCCESS == status) {
            status = TaskStatusEnum.WAIT_VALID;
        }
        if (status == TaskStatusEnum.KILLED){
            TaskVO taskVO = dataAccessProxy.getTaskInstance(taskInstance.getId());
            if (taskVO.getStatus() == TaskStatusEnum.MANUAL_SUCCESS){
                status = TaskStatusEnum.SUCCESS;
            }
        }
        Task task = new Task();
        task.setId(taskInstance.getId());
        task.setEndTime(new Date());
        task.setStatus(status);
        task.setEmrClusterId(taskInstance.getEmrClusterId());
        task.setIp(OSUtils.getHost());
        dataAccessProxy.updateTaskInfo(task);
    }

    /**
     * zNode、runningQueue清理
     */
    private void clearExecutionEnv() throws Exception {
        logger.info("开始清理运行环境 ...");
        // 从运行队列中移除当前Task
        try {
            taskRunnerMgr.unsetRunning(taskInstance);
            logger.info("remove task {} from running queue  success , running queue current size : {}" ,
                    taskInstance.getName(), taskRunnerMgr.getRunningSize());
        }catch (Exception e){
            logger.error("从Running状态队列移除任务{}失败,{}",taskInstance.getId(),e.getMessage());
        }
        // 删除当前task的执行目录
        String execDir = taskInstance.getExecuteDir();
        // 防止获取到的execDir为不正确的路径，导致删错目录
        if (StringUtils.isNotBlank(execDir) && StringUtils.startsWith(execDir,FileUtils.getProcessBasePath())){
            FileUtils.deleteIfExists(execDir);
            logger.info("clear task {} execute dir {}  success" , taskInstance.getName(),taskInstance.getExecuteDir());
        }

        // 删除task对应的zNode
        taskRunnerMgr.getZkClient().delete().forPath(taskInstance.getZNodePath());
        logger.info("{} deleted task {} from zk  success" , OSUtils.getHost(), taskInstance.getZNodePath());

        // 删除Task锁地址: 在释放锁时已经删除，无需重复操作
        this.deleteTaskLockPathIfExists();

        logger.info("清理运行环境结束 ...");

    }

    /**
     * 确保删除taskLockPath
     */
    private void deleteTaskLockPathIfExists() {
        TaskLock taskLock = new TaskLock(taskRunnerMgr.getZkClient(), taskInstance.getId());
        taskLock.forceClear();
    }

}
