package com.bigdata.platform.scheduler.server.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.bigdata.platform.scheduler.server.BaseTest;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.bigdata.platform.scheduler.core.service.impl.MachineService;

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
