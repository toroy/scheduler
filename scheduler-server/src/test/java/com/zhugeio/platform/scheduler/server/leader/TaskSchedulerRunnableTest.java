package com.zhugeio.platform.scheduler.server.leader;

import java.util.List;

import com.zhugeio.platform.scheduler.server.leader.runnable.TaskSechdeulerRunnable;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.zhugeio.platform.scheduler.dal.enums.JobCategoryEnum;
import com.zhugeio.platform.scheduler.dal.po.Task;
import com.google.common.collect.Lists;

public class TaskSchedulerRunnableTest extends ZookeeperBaseTest {

	
	@Test
	public void runTest() {
		TaskSechdeulerRunnable taskSchedulerRunnable = new TaskSechdeulerRunnable(getClient(), getTaskPath(), getWorkPath());
		taskSchedulerRunnable.run();
	}
	
	@Test
	public void filterByDbSlotsTest() {
		TaskSechdeulerRunnable taskSchedulerRunnable = new TaskSechdeulerRunnable(getClient(), getTaskPath(), getWorkPath());
		
		List<Task> tasks = Lists.newArrayList();
		tasks.add(genTasks(130L, JobCategoryEnum.REFLUE));
		tasks.add(genTasks(20L, JobCategoryEnum.COLLECT));
		tasks.add(genTasks(136L, JobCategoryEnum.COLLECT));
		tasks.add(genTasks(135L, JobCategoryEnum.COLLECT));
		tasks.add(genTasks(118L, JobCategoryEnum.COLLECT));
		tasks.add(genTasks(129L, JobCategoryEnum.COLLECT));
		
		taskSchedulerRunnable.filterByDbSlots(tasks);
		System.out.println(JSON.toJSONString(tasks));
	}

	private Task genTasks(Long jobId, JobCategoryEnum categoryEnum) {
		Task task = new Task();
		task.setJobId(jobId);
		task.setCategory(categoryEnum);
		return task;
	}
	
}
