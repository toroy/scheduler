package com.clubfactory.platform.scheduler.spi.constants;

import com.clubfactory.platform.scheduler.core.utils.PropertyUtils;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.Optional;

/**
 * @author xiejiajun
 */
public interface JobConf {
    /**
     * 获取配置列表
     * @return
     */
    Map<String,String> getAllJobConf();

    /**
     * 获取配置
     * @param key
     * @return
     */
    default String getString(String key){
        try {
            Map<String,String> jobConf = getAllJobConf();
            String value = null;
            if (MapUtils.isNotEmpty(jobConf)){
                value = jobConf.get(key);
            }
            return Optional.ofNullable(value).orElse(PropertyUtils.getString(key));
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 获取配置
     * @param key
     * @param defaultValue
     * @return
     */
    default String getString(String key,String defaultValue){
        String value = this.getString(key);
        return Optional.ofNullable(value).orElse(defaultValue);
    }

    /**
     * 获取 bool 型配置
     * @param key
     * @param defaultValue
     * @return
     */
    default Boolean getBoolean(String key,boolean defaultValue) {
        String value = this.getString(key);
        if (null == value){
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(value);
        }catch (Exception e){
            return defaultValue;
        }
    }

    /**
     * 获取 Int 型配置
     * @param key
     * @param defaultValue
     * @return
     */
    default Integer getInt(String key,Integer defaultValue) {
        String value = this.getString(key);
        if (null == value){
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        }catch (Exception e){
            return defaultValue;
        }
    }
}
