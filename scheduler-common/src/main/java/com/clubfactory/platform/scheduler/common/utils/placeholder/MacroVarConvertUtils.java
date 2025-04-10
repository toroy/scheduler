package com.clubfactory.platform.scheduler.common.utils.placeholder;

import com.alibaba.fastjson.JSONObject;
import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.common.enums.DataType;
import com.clubfactory.platform.scheduler.common.enums.SystemDateType;
import com.clubfactory.platform.scheduler.common.util.DateUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 宏变量转换工具
 * @author xiejiajun
 */
public class MacroVarConvertUtils {
    private static final Logger logger = LoggerFactory.getLogger(MacroVarConvertUtils.class);

    private static final Pattern p1 = Pattern.compile("\\$\\{TIME(#[a-zA-z0-9\\-\\+]+)?\\}");
    private static final Pattern p2 = Pattern.compile("\\$\\{TIME_(N|WEEK|RN)\\w+(#[a-zA-z0-9\\-\\+]+)?\\}");
    private static final Pattern p3 = Pattern.compile("\\$\\{DT(#[a-zA-z0-9\\-\\+]+)?\\}");
    private static final Pattern p4 = Pattern.compile("\\$\\{MONTH_BEGIN(#[a-zA-z0-9\\-\\+]+)?\\}");
    private static final Pattern p5 = Pattern.compile("\\$\\{MONTH_END(#[a-zA-z0-9\\-\\+]+)?\\}");
    private static final Pattern p6 = Pattern.compile("\\$\\{DATA_BEGIN_TIME(#[a-zA-z0-9\\-\\+]+)?\\}");
    private static final Pattern p7 = Pattern.compile("\\$\\{DATA_END_TIME(#[a-zA-z0-9\\-\\+]+)?\\}");

    /**
     * 将日期表达式转换为具体的日期
     *
     * @param parameterString 日期表达式
     * @param dateTime 日期
     * @return 解析表达式后返回的日期
     */
    public static String coverPlaceholders(String parameterString, Date dateTime) {
        Map<String, String> parameterMap = Maps.newHashMap();
        parameterMap.put(Constants.PARAMETER_INSTANCE_DATE, DateUtil.format(dateTime, Constants.PARAMETER_FORMAT_TIME));
        return convertParameterPlaceholders(parameterString, parameterMap, "UTC");
    }

    /**
     * 参数占位符转换
     * @param parameterString
     * @param parameterMap
     * @param timeZoneId
     * @return
     */
    public static String convertParameterPlaceholders(String parameterString, Map<String, String> parameterMap, String timeZoneId) {
        if (StringUtils.isEmpty(parameterString)) {
            return parameterString;
        }
        // 时间运算时取实例时间作为基准时间
        String cronTimeStr = parameterMap.get(Constants.PARAMETER_INSTANCE_DATE);
        Date cronTime = null;
        if (StringUtils.isNotBlank(cronTimeStr)) {
            try {
                cronTime = DateUtils.parseDate(cronTimeStr, new String[]{Constants.PARAMETER_FORMAT_TIME});
            } catch (ParseException e) {
                logger.error(String.format("parse %s exception", cronTimeStr), e);
            }
        } else {
            cronTime = new Date();
        }
        // 替换形如${TIME_NF_*} ${TIME_NH_*}  ${TIME_ND_*}  ${TIME_NW_*} ${TIME_NM_*}的常量表达式为TimePlaceholderUtils支持的expr
        parameterString = sysConstantReplace(parameterString);
        // replace variable ${} form,refers to the replacement of system variables and custom variables
        parameterString = PlaceholderUtils.replacePlaceholders(parameterString, parameterMap, true);
        // replace time $[...] form, eg. $[yyyyMMdd]
        if (cronTime != null) {
            parameterString = TimePlaceholderUtils.replacePlaceholders(parameterString, cronTime, true, timeZoneId);
        }
        return parameterString;
    }

    /**
     * @param parameterString
     * @param parameterMap
     * @return
     */
    public static String convertParameterPlaceholders(String parameterString, Map<String, String> parameterMap) {
        return convertParameterPlaceholders(parameterString, parameterMap, null);
    }

    /**
     * JDBC参数类型设置
     * @param index
     * @param stmt
     * @param dataType
     * @param value
     * @throws Exception
     */
    public static void setInParameter(int index, PreparedStatement stmt, DataType dataType, String value) throws Exception {
        if (dataType.equals(DataType.VARCHAR)) {
            stmt.setString(index, value);
        } else if (dataType.equals(DataType.INTEGER)) {
            stmt.setInt(index, Integer.parseInt(value));
        } else if (dataType.equals(DataType.LONG)) {
            stmt.setLong(index, Long.parseLong(value));
        } else if (dataType.equals(DataType.FLOAT)) {
            stmt.setFloat(index, Float.parseFloat(value));
        } else if (dataType.equals(DataType.DOUBLE)) {
            stmt.setDouble(index, Double.parseDouble(value));
        } else if (dataType.equals(DataType.DATE)) {
            stmt.setString(index, value);
        } else if (dataType.equals(DataType.TIME)) {
            stmt.setString(index, value);
        } else if (dataType.equals(DataType.TIMESTAMP)) {
            stmt.setString(index, value);
        } else if (dataType.equals(DataType.BOOLEAN)) {
            stmt.setBoolean(index, Boolean.parseBoolean(value));
        }
    }

    /**
     * 用户自定义参数固化
     * @return
     */
    public static String curingGlobalParams(Map<String, String> globalParamMap, Date instanceTime, String timeZoneId) {
        Map<String, String> globalMap = Maps.newHashMap();
        if (globalParamMap != null) {
            globalMap.putAll(globalParamMap);
        }

        Map<String, String> allParamMap = Maps.newHashMap();
        //如果是补数，需要传入一个补数时间，根据任务类型
        Map<String, String> timeParams = BusinessTimeUtils.getBusinessTime(instanceTime, timeZoneId);
        allParamMap.putAll(timeParams);
        allParamMap.putAll(globalMap);
        Set<Map.Entry<String, String>> entries = allParamMap.entrySet();

        Map<String, String> resolveMap = Maps.newHashMap();
        for (Map.Entry<String, String> entry : entries) {
            String val = entry.getValue();
            if (val.startsWith("$")) {
                String str = MacroVarConvertUtils.convertParameterPlaceholders(val, allParamMap, timeZoneId);
                resolveMap.put(entry.getKey(), str);
            }
        }
        globalMap.putAll(resolveMap);
        if (globalParamMap != null && globalParamMap.size() > 0) {
            for (Map.Entry<String, String> property : globalParamMap.entrySet()) {
                String val = globalMap.get(property.getKey());
                if (val != null) {
                    property.setValue(val);
                }
            }
            return JSONObject.toJSONString(globalParamMap);
        }
        return null;
    }

    /**
     * 转义字符处理
     * @param inputString
     * @return
     */
    public static String handleEscapes(String inputString) {
        if (StringUtils.isNotBlank(inputString)) {
            String [] searchList = {"[", "]", "{", "}", "$", "+","-"};
            String [] replacementList = {"\\[", "\\]", "\\{", "\\}", "\\$", "\\+", "\\-"};
            inputString = StringUtils.replaceEach(inputString, searchList, replacementList);
        }
        return inputString;
    }

    /**
     * 替换形如${TIME_NF_*} ${TIME_NH_*}  ${TIME_ND_*}  ${TIME_NW_*} ${TIME_NM_*}的常量表达式解析
     * @param parameterString
     */
    private static String sysConstantReplace(String parameterString) {
        parameterString = processTimeVar(parameterString, p1, "\\$\\[yyyy-MM-dd HH:mm:ss%s\\]");
        parameterString = processTimeNVar(parameterString);
        parameterString = processTimeVar(parameterString, p3, "\\$\\[yyyy-MM-dd%s\\]");
        parameterString = processTimeVar(parameterString, p4, "\\$\\[month_begin(yyyy-MM-dd, 0)%s\\]");
        parameterString = processTimeVar(parameterString, p5, "\\$\\[month_end(yyyy-MM-dd, 0)%s\\]");
        parameterString = processTimeVar(parameterString, p6, "\\$\\[yyyy-MM-dd HH:mm:ss-8/24%s\\]");
        parameterString = processTimeVar(parameterString, p7, "\\$\\[yyyy-MM-dd HH:mm:ss+16/24%s\\]");
        return parameterString;
    }

    /**
     * 时间变量处理
     * @param parameterString
     * @param p
     * @param exprTemplate
     * @return
     */
    private static String processTimeVar(String parameterString, Pattern p, String exprTemplate) {
        Matcher matcher = p.matcher(parameterString);
        while (matcher.find()) {
            String var = matcher.group();
            String [] timeVariable = StringUtils.isNotBlank(var) ? var.split(Constants.TIME_EXPR_DELIMITED) : new String[0];
            String timeZone = "";
            if (timeVariable.length == 2) {
                timeZone = String.format("#%s",timeVariable[1].replace("}", "").trim());
            }
            parameterString = parameterString.replaceAll(handleEscapes(var), String.format(exprTemplate, timeZone));
        }
        return parameterString;
    }

    /**
     * 时间算数运算表达式处理
     * @param parameterString
     * @return
     */
    private static String processTimeNVar(String parameterString) {
        Matcher matcher = p2.matcher(parameterString);
        while (matcher.find()) {
            String var = matcher.group();
            SystemDateType type;
            String timeZone = "";
            int num;
            try {
                String [] timeVariable = StringUtils.isNotBlank(var) ? var.split(Constants.TIME_EXPR_DELIMITED) : new String[0];
                if (timeVariable.length == 2) {
                    var = timeVariable[0].trim();
                    timeZone = String.format("#%s",timeVariable[1].replace("}", "").trim());
                }
                String [] vars = var.replace("}", "").split("_");
                type = SystemDateType.keyOf(vars[1]);
                num = Integer.parseInt(vars[2]);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                continue;
            }

            switch (type) {
                case NF:
                    var = String.format("\\$\\[yyyy-MM-dd HH:mm:ss+%s/24/60%s\\]", num, timeZone);
                    parameterString = parameterString.replaceAll(String.format("\\$\\{TIME_NF_%s%s\\}", num, handleEscapes(timeZone)), var);
                    break;
                case RNF:
                    var = String.format("\\$\\[yyyy-MM-dd HH:mm:ss-%s/24/60%s\\]", num, timeZone);
                    parameterString = parameterString.replaceAll(String.format("\\$\\{TIME_RNF_%s%s\\}", num, handleEscapes(timeZone)), var);
                    break;
                case NH:
                    var = String.format("\\$\\[yyyy-MM-dd HH:mm:ss+%s/24%s\\]", num, timeZone);
                    parameterString = parameterString.replaceAll(String.format("\\$\\{TIME_NH_%s%s\\}", num, handleEscapes(timeZone)), var);
                    break;
                case RNH:
                    var = String.format("\\$\\[yyyy-MM-dd HH:mm:ss-%s/24%s\\]", num, timeZone);
                    parameterString = parameterString.replaceAll(String.format("\\$\\{TIME_RNH_%s%s\\}", num, handleEscapes(timeZone)), var);
                    break;
                case ND:
                    var = String.format("\\$\\[yyyy-MM-dd HH:mm:ss+%s%s\\]", num, timeZone);
                    parameterString = parameterString.replaceAll(String.format("\\$\\{TIME_ND_%s%s\\}", num, handleEscapes(timeZone)), var);
                    break;
                case RND:
                    var = String.format("\\$\\[yyyy-MM-dd HH:mm:ss-%s%s\\]", num, timeZone);
                    parameterString = parameterString.replaceAll(String.format("\\$\\{TIME_RND_%s%s\\}", num, handleEscapes(timeZone)), var);
                    break;
                case NYMD:
                    var = String.format("\\$\\[yyyy-MM-dd+%s%s\\]", num, timeZone);
                    parameterString = parameterString.replaceAll(String.format("\\$\\{TIME_NYMD_%s%s\\}", num, handleEscapes(timeZone)), var);
                    break;
                case RNYMD:
                    var = String.format("\\$\\[yyyy-MM-dd-%s%s\\]", num, timeZone);
                    parameterString = parameterString.replaceAll(String.format("\\$\\{TIME_RNYMD_%s%s\\}", num, handleEscapes(timeZone)), var);
                    break;
                case NM:
                    var = String.format("\\$\\[add_months\\(yyyy-MM-dd HH:mm:ss, %s\\)%s\\]", num, timeZone);
                    parameterString = parameterString.replaceAll(String.format("\\$\\{TIME_NM_%s%s\\}", num, handleEscapes(timeZone)), var);
                    break;
                case RNM:
                    var = String.format("\\$\\[add_months\\(yyyy-MM-dd HH:mm:ss, -%s\\)%s\\]", num, timeZone);
                    parameterString = parameterString.replaceAll(String.format("\\$\\{TIME_RNM_%s%s\\}", num, handleEscapes(timeZone)), var);
                    break;
                case NW:
                    var = String.format("\\$\\[yyyy-MM-dd HH:mm:ss+7*%s%s\\]", num, timeZone);
                    parameterString = parameterString.replaceAll(String.format("\\$\\{TIME_NW_%s%s\\}", num, handleEscapes(timeZone)), var);
                    break;
                case WEEK:
                    if (num < 1 || num > 7) {
                        throw new RuntimeException("week number should between 1 and 7");
                    }
                    int weekNo = num - 1;
                    var = String.format("\\$\\[week_begin\\(yyyy-MM-dd HH:mm:ss, %s\\)%s\\]", weekNo, timeZone);
                    parameterString = parameterString.replaceAll(String.format("\\$\\{TIME_WEEK_%s%s\\}", num, handleEscapes(timeZone)), var);
                    break;
                default:
                    break;
            }
        }
        return parameterString;
    }
}
