package com.clubfactory.platform.scheduler.common.utils.placeholder;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xiejiajun
 */

public enum TimeZoneId {

    /**
     * 时区名称到时区ID映射
     */
    UTC("GMT"),
    BJ("GMT+8");

    @Getter
    private String zoneId;

    TimeZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public static TimeZoneId fromName(String name) {
        for (TimeZoneId timeZoneId : TimeZoneId.values()) {
            if (timeZoneId.name().equalsIgnoreCase(name)) {
                return timeZoneId;
            }
        }
        return null;
    }

    public static String name2ZoneId(String name) {
        TimeZoneId timeZoneId = fromName(name);
        if (timeZoneId == null) {
            return StringUtils.isNotBlank(name) ? name.toUpperCase() : null;
        }
        return timeZoneId.getZoneId();
    }
}
