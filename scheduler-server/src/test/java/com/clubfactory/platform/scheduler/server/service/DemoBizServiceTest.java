package com.clubfactory.platform.scheduler.server.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.clubfactory.platform.scheduler.server.BaseTest;
import com.clubfactory.platform.scheduler.server.leader.DemoBizService;

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
