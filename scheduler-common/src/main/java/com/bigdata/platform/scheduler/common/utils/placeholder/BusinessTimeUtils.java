package com.bigdata.platform.scheduler.common.utils.placeholder;

import com.bigdata.platform.scheduler.common.Constants;
import com.bigdata.platform.scheduler.common.utils.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.bigdata.platform.scheduler.common.utils.DateUtils.format;


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
            businessDate = DateUtils.addDays(instanceTime, -1);
        } else {
            businessDate = DateUtils.addDays(new Date(), -1);
        }
        Date systemDate = new Date();
        Map<String, String> result = new HashMap<>();
        result.put(Constants.PARAMETER_CURRENT_DATE, DateUtils.format(instanceTime, Constants.PARAMETER_FORMAT_DATE, timeZoneId));
        result.put(Constants.PARAMETER_BUSINESS_DATE, DateUtils.format(businessDate, Constants.PARAMETER_FORMAT_DATE_MINUS, timeZoneId));
        result.put(Constants.PARAMETER_DATETIME, DateUtils.format(instanceTime, Constants.PARAMETER_FORMAT_TIME, timeZoneId));
        result.put(Constants.PARAMETER_INSTANCE_DATE, DateUtils.format(instanceTime, Constants.PARAMETER_FORMAT_TIME));
        result.put(Constants.PARAMETER_INSTANCE_DATE_CN, DateUtils.format(instanceTime, Constants.PARAMETER_FORMAT_TIME_DASH, timeZoneId));
        result.put(Constants.PARAMETER_SYS_DATE, DateUtils.format(systemDate, Constants.PARAMETER_FORMAT_DATE_MINUS, timeZoneId));
        result.put(Constants.PARAMETER_SYS_TIME, DateUtils.format(systemDate, Constants.PARAMETER_FORMAT_TIME_DASH, timeZoneId));
        return result;
    }
}
