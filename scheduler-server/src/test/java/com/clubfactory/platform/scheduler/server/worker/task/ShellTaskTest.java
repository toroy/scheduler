//package com.clubfactory.platform.scheduler.server.worker.task;
//
//import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.classic.sift.SiftingAppender;
//import com.clubfactory.platform.scheduler.core.Constants;
//import com.clubfactory.platform.scheduler.core.enums.CommandType;
//import com.clubfactory.platform.scheduler.core.utils.*;
//import com.clubfactory.platform.scheduler.core.vo.TaskVO;
//import com.clubfactory.platform.scheduler.dal.enums.JobTypeEnum;
//import com.clubfactory.platform.scheduler.engine.task.TaskManager;
//import com.clubfactory.platform.scheduler.server.worker.logger.TaskLogDiscriminator;
//import com.google.common.collect.Maps;
//import org.junit.Before;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.util.Date;
//import java.util.Map;
//import java.util.Properties;
//
//public class ShellTaskTest {
//
//    private TaskVO taskInstance;
//
//    private  Logger taskLogger;
//
//    private String logVersion;
//
//    @Before
//    public void before(){
//        PropertyUtils.init(new Properties());
//        taskInstance = new TaskVO();
//        logVersion = DateUtils.format(new Date(),"yyyyMMddHHmmss");
//        String username = System.getProperty("user.name");
//        taskInstance.setTenant(username);
//        taskInstance.setExecTime(new Date());
//        String execDir = Thread.currentThread().getContextClassLoader().getResource("tasks").getPath();
//        taskInstance.setExecuteDir(execDir);
//        taskInstance.setCommandType(CommandType.SCHEDULER);
//        taskInstance.setTaskTime(new Date());
//
//        String logBase = ((TaskLogDiscriminator) ((SiftingAppender) ((LoggerContext) LoggerFactory.getILoggerFactory())
//                .getLogger("ROOT")
//                .getAppender("TASKLOGFILE"))
//                .getDiscriminator()).getLogBase();
//        String taskLogPath  =  logBase + File.separator +
//                "task_instance" + File.separator +
//                "tasks" + Constants.UNDERLINE +
//                taskInstance.getId() + ".log";
//        taskInstance.setLogPath(taskLogPath);
//        taskInstance.setEnvFile(CommonUtils.getSystemEnvPath());
//
//    }
//
//    @Test
//    public void shellTaskTest() throws Exception {
//        taskInstance.setId(1223L);
//        taskLogger = LoggerFactory.getLogger(
//                LoggerUtils.buildTaskId(LoggerUtils.TASK_LOGGER_INFO_PREFIX, taskInstance.getId(),logVersion)
//        );
//        String rawScript = "cd ~; pwd;" +
//                "echo ${curr_date}";
//        taskInstance.setType(JobTypeEnum.SHELL);
//        Map<String,Object> params = Maps.newHashMap();
//        params.put("rawScript",rawScript);
//        params.put("curr_date","${system.biz.curdate} 111 222");
//        taskInstance.setParams(JSONUtils.toJsonString(params));
//
//        AbstractTask1 task = TaskManager.newTask(taskInstance,taskLogger);
//
//        task.preRun();
//        task.handle();
//        task.postHandle();
//
//    }
//
//    @Test
//    public void shellTaskTest2() throws Exception {
//        taskInstance.setId(122302L);
//        taskLogger = LoggerFactory.getLogger(
//                LoggerUtils.buildTaskId(LoggerUtils.TASK_LOGGER_INFO_PREFIX, taskInstance.getId(),logVersion)
//        );
//        String  startFile = "shell-task.sh";
//        taskInstance.setType(JobTypeEnum.SHELL);
//        Map<String,Object> params = Maps.newHashMap();
//        params.put("mainArgs","11 22 33 44 '${system.biz.date} 55 66' ");
//        taskInstance.setParams(JSONUtils.toJsonString(params));
//        taskInstance.setFileName(startFile);
//
//        AbstractTask1 task = TaskManager.newTask(taskInstance,taskLogger);
//
//        task.preRun();
//        task.handle();
//        task.postHandle();
//    }
//
//    @Test
//    public void shellTaskTest3() throws Exception {
//        taskInstance.setId(122303L);
//        taskLogger = LoggerFactory.getLogger(
//                LoggerUtils.buildTaskId(LoggerUtils.TASK_LOGGER_INFO_PREFIX, taskInstance.getId(),logVersion)
//        );
//        String  startFile = "shell-task.sh";
//        taskInstance.setType(JobTypeEnum.SHELL);
//        Map<String,Object> params = Maps.newHashMap();
//        taskInstance.setParams(JSONUtils.toJsonString(params));
//        taskInstance.setFileName(startFile);
//
//        AbstractTask1 task = TaskManager.newTask(taskInstance,taskLogger);
//
//        task.preRun();
//        task.handle();
//        task.postHandle();
//    }
//
//}
