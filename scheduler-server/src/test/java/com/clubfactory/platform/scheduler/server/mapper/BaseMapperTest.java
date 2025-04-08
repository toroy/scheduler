package com.clubfactory.platform.scheduler.server.mapper;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.clubfactory.platform.scheduler.core.service.impl.TaskService;
import com.clubfactory.platform.scheduler.dal.po.Task;
import com.clubfactory.platform.scheduler.server.BaseTest;
import com.google.common.collect.Lists;

public class BaseMapperTest extends BaseTest {

	@Resource
	TaskService taskService;
	
	@Test
	public void addBatchTest() throws InterruptedException {
		List<Task> tasks = Lists.newArrayList();
		for (long i = 0; i < 150_000; i++) {
			Task task = new Task();
			task.setJobId(i);
			tasks.add(task);
		}
		
		taskService.saveBatch(tasks);
		
		Thread.sleep(5_000);
		
	}
	
}
