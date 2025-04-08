package com.clubfactory.platform.scheduler.engine.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.clubfactory.platform.scheduler.core.utils.LoggerUtils;
import org.apache.commons.lang.StringUtils;

/**
 * task logback filter
 * @author xiejiajun
 */
public class TaskLogFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {

        if (StringUtils.contains(event.getLoggerName(),LoggerUtils.getTaskLoggerInfoPrefix())) {
            return FilterReply.ACCEPT;
        }
        return FilterReply.DENY;
    }
}