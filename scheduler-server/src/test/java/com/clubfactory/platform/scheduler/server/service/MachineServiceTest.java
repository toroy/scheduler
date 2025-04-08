package com.clubfactory.platform.scheduler.server.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.clubfactory.platform.scheduler.core.service.impl.MachineService;
import com.clubfactory.platform.scheduler.server.BaseTest;

public class MachineServiceTest extends BaseTest {

	@Resource
	MachineService machineService;
	
	@Test
	public void getMapTest() {
		Map<String, List<String>> maps = machineService.getIpsByJobTypeMap();
		System.out.println(JSON.toJSONString(maps));
		
		
//		for (int i = 0; i < 100; i++) {
//			for(Entry<String, List<String>> entries : maps.entrySet()) {
//				int rand = new Random().nextInt(entries.getValue().size());
//				System.out.println(entries.getKey() + " : " + entries.getValue().get(rand));
//			}
//		}
	}
	
}
