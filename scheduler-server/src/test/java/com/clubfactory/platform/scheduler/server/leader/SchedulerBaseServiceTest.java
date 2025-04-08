package com.clubfactory.platform.scheduler.server.leader;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.junit.Test;

import com.clubfactory.platform.scheduler.dal.po.Task;
import com.clubfactory.platform.scheduler.server.BaseTest;
import com.clubfactory.platform.scheduler.server.leader.runnable.SchedulerBaseService;
import com.google.common.collect.Lists;

public class SchedulerBaseServiceTest extends BaseTest {

	@Resource(name="taskReadyRunnable")
	SchedulerBaseService  SchedulerBaseService;
	
	@Test
	public void getVersionMapTest() {
		SchedulerBaseService.init();
		
		List<Task> tasks = Lists.newArrayList();
		addTask(true, 10224410L, 3L, 10000051L, tasks);
		addTask(false, 10224273L, 20L, 10000034L, tasks);
		
		Map<Long, Integer> taskMap = SchedulerBaseService.getVersionMap(tasks);
		for (Entry<Long, Integer>  entries : taskMap.entrySet()) {
			System.out.println(String.format("taskId: %s, version:%s", entries.getKey(), entries.getValue()));
		}
		
		
		
	}
	
	private void addTask(Boolean isTemp, Long id, Long scriptId, Long jobId, List<Task> tasks) {
		Task task = new Task();
		task.setIsTemp(isTemp);
		task.setId(id);
		task.setJobId(jobId);
		task.setScriptId(scriptId);
		tasks.add(task);
		
	}
}
