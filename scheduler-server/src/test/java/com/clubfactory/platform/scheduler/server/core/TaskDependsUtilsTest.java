package com.clubfactory.platform.scheduler.server.core;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.clubfactory.platform.common.constant.DateFormatPattern;
import com.clubfactory.platform.common.util.DateUtil;
import com.clubfactory.platform.scheduler.common.utils.TaskDependsUtil;
import com.clubfactory.platform.scheduler.dal.enums.JobCycleTypeEnum;
import com.clubfactory.platform.scheduler.dal.po.JobOnline;
import com.clubfactory.platform.scheduler.dal.po.Task;
import com.clubfactory.platform.scheduler.dal.po.TaskDepends;
import com.google.common.collect.Lists;

public class TaskDependsUtilsTest {

	public static void main(String[] args) {
		
		JobOnline jobOnline = new JobOnline();
		jobOnline.setCycleType(JobCycleTypeEnum.WEEK);
		jobOnline.setJobId(2L);
		
		JobOnline parentJobOnline = new JobOnline();
		parentJobOnline.setCycleType(JobCycleTypeEnum.WEEK);
		parentJobOnline.setJobId(1L);
		
		List<Task> tasks = Lists.newArrayList();
		Task task = getTask(2L);
		tasks.add(task);
		
		
		List<Task> parentTasks = Lists.newArrayList();
		
		List<Task> parentTasksByWeek = Lists.newArrayList();
		parentTasksByWeek.add(getParentTask(1L));
		List<Task> parentTasksByMonth = Lists.newArrayList(getParentTask(1L));
		
		List<Task> allParentTasks = TaskDependsUtil.mergeTaskById(parentTasks, parentTasksByWeek, parentTasksByMonth);
		List<TaskDepends> taskDepends =  TaskDependsUtil.genSameDependsByCycleType(jobOnline, parentJobOnline, tasks, allParentTasks);
		System.out.println(JSON.toJSONString(taskDepends));
		
		taskDepends =  TaskDependsUtil.genAllDependsByCycleType(jobOnline, parentJobOnline, tasks, allParentTasks);
		System.out.println(JSON.toJSONString(taskDepends));
	}

	private static Task getTask(Long id) {
		Task task = new Task();
		task.setTaskTime(DateUtil.parse("2020-06-23 01:00:00", DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
		task.setCreateUser(1L);
		task.setId(id);
		return task;
	}
	
	private static Task getParentTask(Long id) {
		Task task = new Task();
		task.setTaskTime(DateUtil.parse("2020-06-23 01:00:00", DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
		task.setCreateUser(1L);
		task.setId(id);
		return task;
	}
	
	private static Date getMinDate(int num) {
		return DateUtil.getDate(LocalDate.now().minusDays(num));
	}
	
	private static Date getPlusDate(int num) {
		return DateUtil.getDate(LocalDate.now().plusDays(num));
	}
}
