package com.bigdata.platform.scheduler.server.mapper;

import java.util.List;

import javax.annotation.Resource;

import com.bigdata.platform.scheduler.server.BaseTest;
import org.junit.Test;

import com.bigdata.platform.scheduler.core.service.impl.TaskService;
import com.bigdata.platform.scheduler.dal.po.Task;
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
