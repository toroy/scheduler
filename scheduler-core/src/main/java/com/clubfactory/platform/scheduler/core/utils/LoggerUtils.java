package com.clubfactory.platform.scheduler.core.utils;

import com.clubfactory.platform.scheduler.common.Constants;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  logger utils
 */
public class LoggerUtils {

    /**
     * rules for extracting application ID
     */
    private static final Pattern APPLICATION_REGEX = Pattern.compile(Constants.APPLICATION_REGEX);

    /**
     * Task Logger's prefix
     */
    public static final String TASK_LOGGER_INFO_PREFIX = "TASK";

    public static final String TASK_LOGGER_THREAD_NAME = "TaskLogInfo";

    /**
     *  build job id
     * @param affix
     * @param taskId
     * @return
     */
    public static String buildTaskId(String affix, long taskId,String logVersion){
        // - [taskAppId=TASK-task_${taskId}_${yyyy-MM-dd_HH:mm:ss}]
        return String.format(" - [taskAppId=%s-%s_%s_%s]",affix, "task", taskId,logVersion);
    }


    /**
     * task日志 logger前缀
     * @return
     */
    public static String getTaskLoggerInfoPrefix(){
        // - [taskAppId=TASK-task
        return String.format("taskAppId=%s-%s",TASK_LOGGER_INFO_PREFIX,
                "task");
    }


    /**
     * 从字符串中匹配appId
     * @param line
     * @param logger
     * @return
     */
    public static List<String> getAppIds(String line, Logger logger) {
        List<String> appIds = new ArrayList<>();
        if (line == null || !line.contains("application_")) {
            return appIds;
        }
        Matcher matcher = APPLICATION_REGEX.matcher(line);
        // analyse logs to get all submit yarn application id
        while (matcher.find()) {
            String appId = matcher.group();
            if(!appIds.contains(appId)){
                logger.info("find app id: {}", appId);
                appIds.add(appId);
            }
        }
        return appIds;
    }
}