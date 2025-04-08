package com.clubfactory.platform.scheduler.common.utils;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.common.utils.placeholder.BusinessTimeUtils;
import com.clubfactory.platform.scheduler.common.utils.placeholder.MacroVarConvertUtils;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * 参数转换工具类
 * @author xiejiajun
 */
public class ParamUtils {

    /**
     * parameter conversion
     * 初始化全局/当前任务参数且合并全局参数、当前任务参数到一个Map
     * @param allParams
     * @param instanceTime
     * @param timeZoneId
     * @return
     */
    public static Map<String,String> convert(Map<String,String> allParams, Date instanceTime, String timeZoneId){
        if (allParams == null){
            return null;
        }
        // 根据调度时间获取业务时间
        Map<String,String> timeParams = BusinessTimeUtils.getBusinessTime(instanceTime, timeZoneId);
        allParams.forEach((k, v) -> {
            if (!timeParams.containsKey(k)) {
                timeParams.put(k, v);
            }
        });
        Iterator<Map.Entry<String, String>> iter = allParams.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry<String, String> en = iter.next();
            String value = en.getValue();
            if (value == null || value.length() <= 0){
                continue;
            }
            if (value.startsWith("$")){
                // 自定义参数引用系统变量或者其他变量时替换成对应的值
                String val = MacroVarConvertUtils.convertParameterPlaceholders(value, timeParams, timeZoneId);
                en.setValue(val);
            }
        }
        allParams.put(Constants.PARAMETER_CURRENT_DATE,timeParams.get(Constants.PARAMETER_CURRENT_DATE));
        allParams.put(Constants.PARAMETER_BUSINESS_DATE,timeParams.get(Constants.PARAMETER_BUSINESS_DATE));
        allParams.put(Constants.PARAMETER_DATETIME,timeParams.get(Constants.PARAMETER_DATETIME));
        allParams.put(Constants.PARAMETER_INSTANCE_DATE,timeParams.get(Constants.PARAMETER_INSTANCE_DATE));
        allParams.put(Constants.PARAMETER_INSTANCE_DATE_CN,timeParams.get(Constants.PARAMETER_INSTANCE_DATE_CN));
        allParams.put(Constants.PARAMETER_SYS_DATE,timeParams.get(Constants.PARAMETER_SYS_DATE));
        allParams.put(Constants.PARAMETER_SYS_TIME,timeParams.get(Constants.PARAMETER_SYS_TIME));
        allParams.put(Constants.CURRENT_DATE,timeParams.get(Constants.PARAMETER_SYS_DATE));
        allParams.put(Constants.CURRENT_TIME,timeParams.get(Constants.PARAMETER_SYS_TIME));

        return allParams;
    }

    /**
     * @param allParams
     * @param instanceTime
     * @return
     */
    public static Map<String,String> convert(Map<String,String> allParams, Date instanceTime){
        return convert(allParams, instanceTime, null);
    }

    /**
     * 变量替换
     * @param allParams
     * @param instanceTime
     * @param originStr
     * @param timeZoneId
     * @return
     */
    public static String convertVariable(Map<String, String> allParams,Date instanceTime, String originStr, String timeZoneId){
        Map<String,String> paramsMap = convert(allParams,instanceTime, timeZoneId);
        if (MapUtils.isNotEmpty(paramsMap) && StringUtils.isNotBlank(originStr)){
            originStr = MacroVarConvertUtils.convertParameterPlaceholders(originStr, paramsMap, timeZoneId);
        }
        return originStr;
    }

    /**
     * @param allParams
     * @param instanceTime
     * @param originStr
     * @return
     */
    public static String convertVariable(Map<String, String> allParams,Date instanceTime, String originStr){
        return convertVariable(allParams, instanceTime, originStr, null);
    }

    /**
     * 指定时区转换转换日期变量
     * @param paramString
     * @param baseTime
     * @param timeZoneId
     * @return
     */
    public static String convertVariable(String paramString, Date baseTime, String timeZoneId) {
        return convertVariable(Maps.newHashMap(), baseTime, paramString, timeZoneId);
    }

    /**
     * 根据默认时区转换日期变量
     * @param paramString
     * @param baseTime
     * @return
     */
    public static String convertVariable(String paramString, Date baseTime) {
        return convertVariable(paramString, baseTime, null);
    }
}