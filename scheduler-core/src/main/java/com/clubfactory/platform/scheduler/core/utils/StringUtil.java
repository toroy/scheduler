package com.clubfactory.platform.scheduler.core.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author xiejiajun
 */
public class StringUtil {


    /**
     * 截取字符串中指定前缀开头的连续子串（遇到空格停止）
     * @param str
     * @param prefix
     * @return
     */
    public static String substringWithWordPrefix(String str, String prefix){
        String result = StringUtils.substringAfter(str,prefix);
        int endIndex = StringUtils.indexOf(result," ");
        if (endIndex == -1){
            endIndex = result.length();
        }
        return StringUtils.substring(result,0,endIndex);
    }


    /**
     * 移除字符串中指定前缀开头的连续子串（遇到空格停止)
     * @param str
     * @param prefix
     * @return
     */
    public static String replaceWordWithPrefix(String str,String prefix){
        String preStr = StringUtils.substringBefore(str,prefix);
        String suffixStr = StringUtils.substringAfter(str,prefix);
        int startIndex = StringUtils.indexOf(suffixStr," ");
        if (startIndex != -1) {
            suffixStr = StringUtils.substring(suffixStr, startIndex + 1);
        }else {
            suffixStr = "";
        }
        if (StringUtils.isBlank(preStr)){
            preStr = "";
        }
        if (StringUtils.isBlank(suffixStr)){
            suffixStr = "";
        }
        if (StringUtils.isBlank(preStr)){
            return suffixStr.trim();
        }
        if (StringUtils.isBlank(suffixStr)){
            return preStr.trim();
        }
        return preStr.trim() + " " + suffixStr.trim();
    }


}
