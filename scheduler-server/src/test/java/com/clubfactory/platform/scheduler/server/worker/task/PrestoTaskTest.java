package com.clubfactory.platform.scheduler.server.worker.task;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.sift.SiftingAppender;
import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.common.utils.DateUtils;
import com.clubfactory.platform.scheduler.core.enums.CommandType;
import com.clubfactory.platform.scheduler.core.utils.CommonUtils;
import com.clubfactory.platform.scheduler.core.utils.LoggerUtils;
import com.clubfactory.platform.scheduler.core.utils.PropertyUtils;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.po.JobType;
import com.clubfactory.platform.scheduler.engine.config.TaskLogDiscriminator;
import com.clubfactory.platform.scheduler.engine.task.TaskManager;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractTask;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.Properties;

public class PrestoTaskTest {
    private TaskVO taskInstance;

    private Logger taskLogger;

    private String logVersion;

    @Before
    public void before(){
        PropertyUtils.init(new Properties());
        logVersion = DateUtils.format(new Date(),"yyyyMMddHHmmss");
        taskInstance = new TaskVO();
        String username = System.getProperty("user.name");
        taskInstance.setTenant(username);
        taskInstance.setExecTime(new Date());
        String execDir = Thread.currentThread().getContextClassLoader().getResource("tasks").getPath();
        taskInstance.setExecuteDir(execDir);
        taskInstance.setCommandType(CommandType.SCHEDULER);
        taskInstance.setTaskTime(new Date());
        taskInstance.setStartTime(new Date());

        JobType jobType = new JobType();
        jobType.setPluginClazz("com.clubfactory.platform.scheduler.engine.task.builtin.PrestoTask");
        taskInstance.setJobTypeInfo(jobType);


        String logBase = ((TaskLogDiscriminator) ((SiftingAppender) ((LoggerContext) LoggerFactory.getILoggerFactory())
                .getLogger("ROOT")
                .getAppender("TASKLOGFILE"))
                .getDiscriminator()).getLogBase();
        String taskLogPath  =  logBase + File.separator +
                "task_instance" + File.separator +
                "tasks" + Constants.UNDERLINE +
                taskInstance.getId() + ".log";
        taskInstance.setLogPath(taskLogPath);
        taskInstance.setEnvFile(CommonUtils.getSystemEnvPath());

    }

    @Test
    public void pythonTaskTest() throws Exception {
        taskInstance.setId(1227L);
        taskLogger = LoggerFactory.getLogger(
                LoggerUtils.buildTaskId(LoggerUtils.TASK_LOGGER_INFO_PREFIX, taskInstance.getId(),logVersion)
        );
        taskInstance.setType("PYTHON");
        taskInstance.setParams("{\"sysConfigs\":\"null\"}");
        taskInstance.setFileName("presto-task.sql");
//        taskInstance.setTimeout(10);


        AbstractTask task = TaskManager.newTask(taskInstance,taskLogger, taskInfo -> {
            return true;
        });

        task.preHandle();
        task.handle();
        task.postHandle();

    }
}
