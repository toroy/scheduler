package com.clubfactory.platform.scheduler.server.leader;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.junit.Test;

import com.clubfactory.platform.scheduler.core.vo.JobOnlineDependsVO;
import com.clubfactory.platform.scheduler.core.vo.JobOnlineVO;
import com.clubfactory.platform.scheduler.server.BaseTest;
import com.clubfactory.platform.scheduler.server.leader.runnable.JobInitRunnable;
import com.google.common.collect.Lists;

public class JobInitRunnableTest extends BaseTest {

	@Resource
	JobInitRunnable jobInitRunnable;
	
	@Test
	public void runTest() {
		jobInitRunnable.setIsLeader(true);
		jobInitRunnable.fixedTimeRun();
		try {
			Thread.sleep(10_000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void runNowTest() {
		jobInitRunnable.setIsLeader(true);
		jobInitRunnable.run();
		try {
			Thread.sleep(10_000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 计算调度值
	 */
	@Test
	public void genTree() {
		List<JobOnlineVO> jobs = Lists.newArrayList();
		for (long i = 19; i < 23; i++) {
			JobOnlineVO job = new JobOnlineVO();
			job.setJobId(i);
			jobs.add(job);
		}
		
		List<JobOnlineDependsVO> jobDependsVOs = Lists.newArrayList();
//		genDepends(jobDependsVOs, 3L, 0L);
//		genDepends(jobDependsVOs, 4L, 1L);
//		genDepends(jobDependsVOs, 6L, 1L);
//		genDepends(jobDependsVOs, 4L, 2L);
//		genDepends(jobDependsVOs, 5L, 2L);
//		genDepends(jobDependsVOs, 6L, 3L);
//		genDepends(jobDependsVOs, 8L, 4L);
//		genDepends(jobDependsVOs, 8L, 5L);
//		genDepends(jobDependsVOs, 6L, 7L);
//		genDepends(jobDependsVOs, 9L, 6L);
//		genDepends(jobDependsVOs, 10L, 8L);
//		genDepends(jobDependsVOs, 12L, 9L);
//		genDepends(jobDependsVOs, 12L, 10L);
//		genDepends(jobDependsVOs, 13L, 10L);
//		genDepends(jobDependsVOs, 13L, 11L);
		genDepends(jobDependsVOs, 21L, 21L);
		genDepends(jobDependsVOs, 21L, 20L);
		genDepends(jobDependsVOs, 21L, 19L);
		genDepends(jobDependsVOs, 22L, 21L);
		
		
		Map<Long, Integer>  map = jobInitRunnable.getSchedulerValueMap(jobs, jobDependsVOs);
		for (Entry<Long, Integer> entries : map.entrySet() ) {
			System.out.println("id:"+ entries.getKey() + " score:"+entries.getValue());
		}
	}

	private void genDepends(List<JobOnlineDependsVO> jobDependsVOs,Long jobId,Long parentId) {
		JobOnlineDependsVO dependsVO = new JobOnlineDependsVO();
		dependsVO.setJobId(jobId);
		dependsVO.setParentId(parentId);
		jobDependsVOs.add(dependsVO);
	}
	
}
