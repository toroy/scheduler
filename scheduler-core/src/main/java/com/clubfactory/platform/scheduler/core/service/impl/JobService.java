package com.clubfactory.platform.scheduler.core.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.clubfactory.platform.scheduler.core.vo.JobVO;
import com.clubfactory.platform.scheduler.dal.dao.JobMapper;
import com.clubfactory.platform.scheduler.dal.enums.JobCycleTypeEnum;
import com.clubfactory.platform.scheduler.dal.po.Job;
import com.google.common.collect.Lists;

@Service
public class JobService extends BaseNewService<JobVO,Job> {

    @Resource
    JobMapper jobMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(jobMapper);
    }
    
    public List<JobVO> listByJobId(List<Long> jobIds) {
    	Job job = new Job();
    	job.setIsDeleted(false);
    	job.setIds(jobIds);
    	List<JobVO> jobs = this.list(job);
    	return jobs;
    }
    
    public List<JobVO> listJob() {
    	Job job = new Job();
    	job.setIsDeleted(false);
    	List<JobVO> jobs = this.list(job);
    	return jobs;
    }
    
    public List<Long> listDependJob() {
    	Job job = new Job();
    	job.setIsDeleted(false);
    	job.setQueryListFieldName("cycle_type");
    	job.setIdsString(Lists.newArrayList(JobCycleTypeEnum.DAY.name()
    			, JobCycleTypeEnum.DAYT1.name()));
    	
    	List<JobVO> jobs = this.list(job);
    	if (CollectionUtils.isEmpty(jobs)) {
    		return Lists.newArrayList();
    	}
    	
    	return jobs.stream().map(JobVO::getId).collect(Collectors.toList());
    }


	/**
	 * 根据jobId查询对应的job信息
	 * @param jobId
	 * @return
	 */
	public Job getByJobId(Long jobId){
		Job job = new Job();
		job.setId(jobId);
		job.setIsDeleted(false);

		return this.get(job);
	}

}
