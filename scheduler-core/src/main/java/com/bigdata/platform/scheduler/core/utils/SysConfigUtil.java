package com.bigdata.platform.scheduler.core.utils;



import com.bigdata.platform.scheduler.common.util.Assert;
import com.bigdata.platform.scheduler.dal.dao.SysConfigMapper;
import com.bigdata.platform.scheduler.dal.enums.ConfigType;
import com.bigdata.platform.scheduler.dal.po.SysConfig;

public class SysConfigUtil {

	static SysConfigMapper sysConfigMapper;
	
	static {
		sysConfigMapper = SpringBean.getBean(SysConfigMapper.class);
	}
	
    public static String getByKey(String key) {
    	Assert.notNull(key);
    	
    	SysConfig sysConfig = SysConfig.builder().paramKey(key).configType(ConfigType.MASTER).build();
    	SysConfig res = sysConfigMapper.get(sysConfig);
    	if (res == null) {
    		return null;
    	}
    	return res.getParamValue();
    }
    
    public static String getByKey(String key, ConfigType configType) {
    	Assert.notNull(key);
    	
    	SysConfig sysConfig = SysConfig.builder().paramKey(key).configType(configType).build();
    	SysConfig res = sysConfigMapper.get(sysConfig);
    	if (res == null) {
    		return null;
    	}
    	return res.getParamValue();
    }
    
    public static Integer getNumberByKey(String key) {
    	
    	String value = SysConfigUtil.getByKey(key);
    	if (value == null) {
    		return null;
    	}
    	
    	try {
    		return Integer.valueOf(value);
		} catch (Exception e) {
			return null;
		}
    }
    
    public static Long getLongByKey(String key) {
    	
    	String value = SysConfigUtil.getByKey(key);
    	if (value == null) {
    		return null;
    	}
    	
    	try {
    		return Long.valueOf(value);
		} catch (Exception e) {
			return null;
		}
    }
}
