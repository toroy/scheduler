package com.clubfactory.platform.scheduler.common.utils.placeholder;

import com.clubfactory.platform.scheduler.common.Constants;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.clubfactory.platform.scheduler.common.Constants.*;
import static com.clubfactory.platform.scheduler.common.utils.DateUtils.addDays;
import static com.clubfactory.platform.scheduler.common.utils.DateUtils.format;


/**
 * 业务时间宏变量初始化工具类
 * @author xiejiajun
 */
public class BusinessTimeUtils {

    /**
     * @param  instanceTime 实例时间
     * @param  timeZoneId   时区ID（北京为GMT+8， UTC为GMT)
     */
    public static Map<String, String> getBusinessTime(Date instanceTime, String timeZoneId) {
        Date businessDate;
        if ( instanceTime != null) {
            businessDate = addDays(instanceTime, -1);
        } else {
            businessDate = addDays(new Date(), -1);
        }
        Date systemDate = new Date();
        Map<String, String> result = new HashMap<>();
        result.put(Constants.PARAMETER_CURRENT_DATE, format(instanceTime, PARAMETER_FORMAT_DATE, timeZoneId));
        result.put(Constants.PARAMETER_BUSINESS_DATE, format(businessDate, PARAMETER_FORMAT_DATE_MINUS, timeZoneId));
        result.put(Constants.PARAMETER_DATETIME, format(instanceTime, PARAMETER_FORMAT_TIME, timeZoneId));
        result.put(Constants.PARAMETER_INSTANCE_DATE, format(instanceTime, PARAMETER_FORMAT_TIME));
        result.put(Constants.PARAMETER_INSTANCE_DATE_CN, format(instanceTime, PARAMETER_FORMAT_TIME_DASH, timeZoneId));
        result.put(Constants.PARAMETER_SYS_DATE, format(systemDate, PARAMETER_FORMAT_DATE_MINUS, timeZoneId));
        result.put(Constants.PARAMETER_SYS_TIME, format(systemDate, PARAMETER_FORMAT_TIME_DASH, timeZoneId));
        return result;
    }
}
