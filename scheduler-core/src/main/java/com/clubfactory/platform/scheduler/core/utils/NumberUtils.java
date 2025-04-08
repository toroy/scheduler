package com.clubfactory.platform.scheduler.core.utils;

/**
 * 数字处理工具类
 * @author xiejiajun
 */
public class NumberUtils {

    /**
     * 字符串转Long
     * @param longStr
     * @return
     */
    public static Long stringToLong(String longStr){
        try {
            if (longStr == null){
                return null;
            }
            return Long.parseLong(longStr);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 字符串转Int
     * @param intStr
     * @return
     */
    public static Integer stringToInt(String intStr){
        try {
            if (intStr == null){
                return null;
            }
            return Integer.parseInt(intStr);
        }catch (Exception e){
            return null;
        }
    }
}
