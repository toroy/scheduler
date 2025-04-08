package com.clubfactory.platform.scheduler.server.worker.task;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.sift.SiftingAppender;
import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.enums.CommandType;
import com.clubfactory.platform.scheduler.core.utils.PropertyUtils;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.engine.config.TaskLogDiscriminator;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.Properties;

public class HttpTaskTest {

    private TaskVO taskInstance;

    private  Logger taskLogger;
    private String logVersion;

    @Before
    public void before(){
        PropertyUtils.init(new Properties());
        taskInstance = new TaskVO();
        String username = System.getProperty("user.name");
        taskInstance.setTenant(username);
        taskInstance.setExecTime(new Date());
        String execDir = Thread.currentThread().getContextClassLoader().getResource("tasks").getPath();
        taskInstance.setExecuteDir(execDir);
        taskInstance.setCommandType(CommandType.SCHEDULER);
        taskInstance.setTaskTime(new Date());

        String logBase = ((TaskLogDiscriminator) ((SiftingAppender) ((LoggerContext) LoggerFactory.getILoggerFactory())
                .getLogger("ROOT")
                .getAppender("TASKLOGFILE"))
                .getDiscriminator()).getLogBase();
        String taskLogPath  =  logBase + File.separator +
                "task_instance" + File.separator +
                "tasks" + Constants.UNDERLINE +
                taskInstance.getId() + ".log";
        taskInstance.setLogPath(taskLogPath);

    }

    @Test
    public void httpTaskTest() throws Exception {
//        taskInstance.setId(1225L);
//        taskLogger = LoggerFactory.getLogger(
//                LoggerUtils.buildTaskId(LoggerUtils.TASK_LOGGER_INFO_PREFIX, taskInstance.getId())
//        );
//        taskInstance.setType(JobTypeEnum.HTTP);
//        Map<String,Object> params = Maps.newHashMap();
//        String url = "http://zeppelinUri/api/login";
//        params.put("url",url);
//        String httpMethod = "POST";
//        params.put("httpMethod",httpMethod);
//        List<HttpProperty> httpParams = Lists.newArrayList();
//        httpParams.add(new HttpProperty("userName", HttpParametersType.PARAMETER,"zeppelinUser"));
//        httpParams.add(new HttpProperty("password", HttpParametersType.PARAMETER,"zeppelinPassword"));
//        params.put("httpParams",httpParams);
//        taskInstance.setParams(JSONUtils.toJsonString(params));
//
//        AbstractTask task = TaskManager.newTask(taskInstance,taskLogger);
//
//        task.preHandle();
//        task.handle();
//        task.postHandle();

    }

}
