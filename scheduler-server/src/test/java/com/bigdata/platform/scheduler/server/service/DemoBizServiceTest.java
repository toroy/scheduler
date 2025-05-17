package com.bigdata.platform.scheduler.server.service;

import com.bigdata.platform.scheduler.server.BaseTest;
import com.bigdata.platform.scheduler.server.leader.DemoBizService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DemoBizServiceTest extends BaseTest {
 
	@Autowired
    DemoBizService demoBizService;
	
	@Test
	public void getTest() {
		System.out.println(demoBizService.get());
	}
	
	@Test
	public void test() {
		demoBizService.test();
	}
	
}
