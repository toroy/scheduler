package com.clubfactory.platform.scheduler.server.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.clubfactory.platform.scheduler.core.utils.LoggerUtils;
import org.apache.commons.lang.StringUtils;

/**
 * worker logback filter
 * @author xiejiajun
 */
public class ServerLogFilter extends Filter<ILoggingEvent> {
    Level level;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        // 非task log处理线程打印的日志通过这个Filter拦截处理
        if (!StringUtils.contains(event.getLoggerName(),LoggerUtils.getTaskLoggerInfoPrefix())){
            return FilterReply.ACCEPT;
        }
        return FilterReply.DENY;
    }
    public void setLevel(String level) {
        this.level = Level.toLevel(level);
    }
}