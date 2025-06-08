package com.zhugeio.platform.scheduler.server.service;

import javax.annotation.Resource;

import com.zhugeio.platform.scheduler.server.BaseTest;
import com.zhugeio.platform.scheduler.server.leader.runnable.AlertRunnable;
import com.zhugeio.platform.scheduler.server.leader.runnable.TaskReadyRunnable;
import com.zhugeio.platform.scheduler.server.leader.runnable.JobInitRunnable;
import org.junit.Test;

import com.zhugeio.platform.scheduler.core.service.impl.JobDependsService;
import com.zhugeio.platform.scheduler.core.service.impl.JobService;
import com.zhugeio.platform.scheduler.core.utils.SpringBean;
import com.zhugeio.platform.scheduler.dal.dto.SchedulerTimeDto;
import com.zhugeio.platform.scheduler.dal.enums.DependTypeEnum;
import com.zhugeio.platform.scheduler.dal.enums.JobCycleTypeEnum;
import com.zhugeio.platform.scheduler.dal.enums.JobStatusEnum;
import com.zhugeio.platform.scheduler.dal.po.Job;
import com.zhugeio.platform.scheduler.dal.po.JobDepends;

public class JobServiceTest extends BaseTest {

	@Resource
	JobService jobService;
	@Resource
	JobDependsService jobDependsService;
	@Resource
    TaskReadyRunnable taskReadyRunnable;
	@Resource
    AlertRunnable alertRunnable;
	
	@Test
	public void addTest() {
		Job jobParent = new Job();
		jobParent.setCycleType(JobCycleTypeEnum.DAY);
		jobParent.setStatus(JobStatusEnum.ONLINE);
		SchedulerTimeDto time = new SchedulerTimeDto();
		time.setDay(18);
		time.setHour(1);
		time.setMinute(1);
		time.setSecond(1);
		time.setWeek(3);
		jobParent.setSchedulerTimeDto(time);
		jobService.save(jobParent);
		
		Job job = new Job();
		job.setCycleType(JobCycleTypeEnum.HOURS);
		job.setStatus(JobStatusEnum.ONLINE);
		SchedulerTimeDto time2 = new SchedulerTimeDto();
		time2.setDay(18);
		time2.setHour(3);
		time2.setMinute(1);
		time2.setSecond(1);
		time2.setWeek(3);
		job.setSchedulerTimeDto(time2);
		jobService.save(job);
		
		
		JobDepends depends = new JobDepends();
		depends.setJobId(job.getId());
		depends.setParentId(jobParent.getId());
		depends.setType(DependTypeEnum.ALL);
		jobDependsService.save(depends);
	}
	
	@Test
	public void runTest() {
		JobInitRunnable initRunnable = SpringBean.getBean(JobInitRunnable.class);
		initRunnable.run();
		try {
			Thread.sleep(100000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void fixedTimeRunTest() {
		JobInitRunnable initRunnable = SpringBean.getBean(JobInitRunnable.class);
		initRunnable.setIsLeader(true);
		initRunnable.fixedTimeRun();
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void taskReadyRunnableTest() {
		taskReadyRunnable.run();
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void alertRunnableTest() {
		alertRunnable.run();
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
