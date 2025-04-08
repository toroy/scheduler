package com.clubfactory.platform.scheduler.core.utils;


import com.clubfactory.platform.scheduler.common.Constants;
import com.google.common.collect.Maps;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.util.*;


/**
 * property utils
 * single instance
 */
public class PropertyUtils {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(PropertyUtils.class);

    private static boolean isInit;

    private static Configuration conf;

    /**
     * 初始化PropertyUtils
     * @param props
     */
    public static void init(Properties props){
        synchronized (PropertyUtils.class) {
            if (!isInit) {
                try {
                    String workerConf = Constants.WORKER_PROPERTIES_FILE;
                    String workerConfDir = System.getProperty(Constants.WORKER_CONFIG_DIR);
                    if (workerConfDir != null && new File(workerConfDir, workerConf).exists()) {
                        workerConf = workerConfDir + "/" + workerConf;
                    }
                    logger.info("worker properties config path is : {}",workerConf);
                    conf = new PropertiesConfiguration(workerConf);
                } catch (ConfigurationException e) {
                    logger.error("load configuration failed", e);
                    System.exit(1);
                }
                if (props != null) {
                    for (Map.Entry<Object, Object> entry : props.entrySet()) {
                        if (entry.getKey() != null) {
                            conf.setProperty(entry.getKey().toString(), entry.getValue());
                        }
                    }
                }

                isInit = true;
            }
        }
    }

    /**
     * 刷新配置
     * @param props
     */
    public static void refreshConfig(Properties props){
        if (props != null){
            synchronized (PropertyUtils.class) {
                for (Map.Entry<Object, Object> entry : props.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        String key = entry.getKey().toString();
                        String value = entry.getValue().toString();
                        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                            conf.setProperty(key.trim(), value.trim());
                        }
                    }
                }
            }
        }
    }

    /**
     * get property value
     *
     * @param key property name
     * @return
     */
    public  static String getString(String key) {
        if (!isInit){
            throw new RuntimeException("PropertyUtils 尚未初始化");
        }
        if (conf == null){
            return null;
        }
        synchronized(PropertyUtils.class) {
            return conf.getString(key.trim());
        }
    }

    public static String[] getArray(String key){
        if (!isInit){
            throw new RuntimeException("PropertyUtils 尚未初始化");
        }
        if (conf == null){
            return null;
        }
        synchronized (PropertyUtils.class){
            String arrStr = conf.getString(key);
            if (StringUtils.isNotBlank(arrStr) && arrStr.contains(",")){
                return arrStr.split(",");
            }
            return conf.getStringArray(key);
        }
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(String key,String defaultValue){
        String value = getString(key);
        return StringUtils.isNotBlank(value) ? value : defaultValue;
    }

    /**
     * get property value
     *
     * @param key property name
     * @return  get property int value , if key == null, then return -1
     */
    public static int getInt(String key) {
        return getInt(key, -1);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getInt(String key, int defaultValue) {
        String value = getString(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.info(e.getMessage(),e);
        }
        return defaultValue;
    }

    public static Integer getInt(Integer value, String key) {
        if (value != null) {
            return value;
        }
        String defaultValue = getString(key);
        if (defaultValue == null) {
            return null;
        }

        try {
            return Integer.parseInt(defaultValue);
        } catch (NumberFormatException e) {
            logger.info(e.getMessage(),e);
        }
        return null;
    }


    /**
     * get property value
     * @param key property name
     * @return
     */
    public static Boolean getBoolean(String key) {
       return getBoolean(key,false);
    }

    /**
     * get property value
     * @param key property name
     * @return
     */
    public static Boolean getBoolean(String key,boolean defaultValue) {
        String value = getString(key);
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
     * get property long value
     * @param key
     * @param defaultVal
     * @return
     */
    public static long getLong(String key, long defaultVal) {
        String val = getString(key);
        if (StringUtils.isBlank(val)){
            return defaultVal;
        }
        try {
            return Long.parseLong(val);
        }catch (Exception e){
            return defaultVal;
        }
    }

    /**
     *
     * @param key
     * @return
     */
    public static long getLong(String key) {
        return getLong(key,-1);
    }

    /**
     *
     * @param key
     * @param defaultVal
     * @return
     */
    public static double getDouble(String key, double defaultVal) {
        String val = getString(key);
        double doubleVal;
        try {
            doubleVal = StringUtils.isNotBlank(val) ?  Double.parseDouble(val) : defaultVal;
        }catch (Exception e){
            doubleVal = defaultVal;
        }
        return  doubleVal;
    }


    /**
     *  get array
     * @param key       property name
     * @param splitStr  separator
     * @return
     */
    public static String[] getArray(String key, String splitStr) {
        String value = getString(key);
        if (value == null) {
            return null;
        }
        try {
            String[] propertyArray = value.split(splitStr);
            return propertyArray;
        } catch (NumberFormatException e) {
            logger.info(e.getMessage(),e);
        }
        return null;
    }

    /**
     *
     * @param key
     * @param type
     * @param defaultValue
     * @param <T>
     * @return  get enum value
     */
    public <T extends Enum<T>> T getEnum(String key, Class<T> type,
                                         T defaultValue) {
        String val = getString(key);
        return val == null ? defaultValue : Enum.valueOf(type, val);
    }

    /**
     * get all properties with specified prefix, like: fs.
     * @param prefix prefix to search
     * @return
     */
    public static synchronized Map<String, String> getPrefixedProperties(String prefix) {
        Map<String, String> matchedProperties = Maps.newHashMap();
        Iterator<String> keysItor = conf.getKeys();
        while (keysItor.hasNext()){
            String key = keysItor.next();
            if (key.startsWith(prefix)) {
                matchedProperties.put(key, conf.getString(key));
            }
        }
        return matchedProperties;
    }
}
