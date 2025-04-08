package com.clubfactory.platform.scheduler.engine.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;
import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.utils.LoggerUtils;

/**
 * @author xiejiajun
 */
public class TaskLogDiscriminator extends AbstractDiscriminator<ILoggingEvent> {

    private String key;

    private String logBase;

    /**
     * logger name should be like:
     *     Task Logger name should be like: Task-task_${taskId}
     */
    @Override
    public String getDiscriminatingValue(ILoggingEvent event) {
        String loggerName = event.getLoggerName()
                .split(Constants.EQUAL_SIGN)[1];
        String prefix = LoggerUtils.TASK_LOGGER_INFO_PREFIX + "-";
        if (loggerName.startsWith(prefix)) {
            return loggerName.substring(prefix.length(), loggerName.length() - 1);
        } else {
            return "unknown_task";
        }
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLogBase() {
        return logBase;
    }

    public void setLogBase(String logBase) {
        this.logBase = logBase;
    }
}
