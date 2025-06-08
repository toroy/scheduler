package com.zhugeio.platform.scheduler.server.service;

import com.zhugeio.platform.scheduler.server.BaseTest;
import com.zhugeio.platform.scheduler.server.leader.DemoBizService;
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
