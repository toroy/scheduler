package com.clubfactory.platform.scheduler.server.utils;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.clubfactory.platform.scheduler.dal.enums.JobCategoryEnum;

public class BizUtils {
	
	public static String getRandIpRemoveIp(Map<String, List<String>> ipMap, JobCategoryEnum categroy, String type, String ip) {
		String key = categroy.name() + "_" + type;
		List<String> ips = ipMap.get(key);
		if (CollectionUtils.isEmpty(ips)) {
			return ip;
		}
		
		if (StringUtils.isNotBlank(ip)) {
			ips.remove(ip);
		}
		return BizUtils.getRand(ips);
	}
	
	public static String getRandIp(Map<String, List<String>> ipMap, JobCategoryEnum categroy, String type) {
		return BizUtils.getRandIpRemoveIp(ipMap, categroy, type, null);
	}
	
	public static String getRand(List<String> datas) {
		if (CollectionUtils.isEmpty(datas)) {
			return null;
		}
		
		if (datas.size() == 1) {
			return datas.get(0);
		}
		
		int rand = new Random().nextInt(datas.size());
		return datas.get(rand);
	}
}

