package com.clubfactory.platform.scheduler.server.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.clubfactory.platform.scheduler.core.service.impl.TaskService;
import com.clubfactory.platform.scheduler.dal.po.Task;
import com.clubfactory.platform.scheduler.server.BaseTest;
import com.google.common.collect.Lists;

public class TaskServiceTest extends BaseTest {

	@Resource
	TaskService taskService;
	
	@Test
	public void listTest() {
		List<Task> tasks = taskService.listReadyTasksBySlot(500);
		Assert.assertNotNull(tasks);
	}
	
	@Test
	public void updateNoticeTest() {
		taskService.updateNotice(Lists.newArrayList(64L));
	}
	
	@Test
	public void initReadyByWorkFailedTest() {
		taskService.initReadyByWorkFailed("192.168.1.1","2019-03-02");
	}
	
	@Test
	public void listByRunningAndScheduled() {
		taskService.listByRunningAndScheduled();
	}

	@Test
	public void incrementRetryCountTest() {

		Long taskId = 354620L;
		Boolean isSuccess = taskService.taskStateRunning(taskId);
		Assert.assertTrue(isSuccess);
	}

	
}
