package com.clubfactory.platform.scheduler.core.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.clubfactory.platform.scheduler.common.util.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import com.clubfactory.platform.scheduler.core.vo.JobOnlineVO;
import com.clubfactory.platform.scheduler.dal.dao.JobOnlineMapper;
import com.clubfactory.platform.scheduler.dal.enums.JobCycleTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.JobStatusEnum;
import com.clubfactory.platform.scheduler.dal.po.JobOnline;
import com.google.common.collect.Lists;

@Service
public class JobOnlineService extends BaseNewService<JobOnlineVO,JobOnline> {

    @Resource
    JobOnlineMapper jobOnlineMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(jobOnlineMapper);
    }
    
    public Boolean fixOneTime(List<Long> jobIds) {
    	Assert.collectionNotEmpty(jobIds, "任务id");
    	
    	JobOnline job = new JobOnline();
    	job.setIds(jobIds);
    	job.setIsDeleted(false);
    	job.setQueryListFieldName("job_id");
    	Map<String, Object> updateParam = Maps.newHashMap();
    	updateParam.put("is_running", false);
    	job.setUpdateParam(updateParam);
    	this.edit(job);
    	return true;
    }
    
    public List<JobOnlineVO> listByIds(List<Long> jobIds) {
    	Assert.collectionNotEmpty(jobIds, "任务id");
    	
    	JobOnline job = new JobOnline();
    	job.setIds(jobIds);
    	job.setIsDeleted(false);
    	job.setQueryListFieldName("job_id");
    	return this.list(job);
    }
    
    public Map<Long, List<Long>> getDbSlotSizeMap(List<Long> jobIds) {
    	List<JobOnlineVO> jobVOs =  this.listByIds(jobIds);
    	if (CollectionUtils.isEmpty(jobVOs)) {
    		return Maps.newHashMap();
    	}
    	Map<Long, List<JobOnlineVO>> dbMap = jobVOs.stream().collect(Collectors.groupingBy(JobOnlineVO::getDbTargetId));
    	Map<Long, List<Long>> dbSlotSizeMap = Maps.newHashMap();
    	for (Entry<Long, List<JobOnlineVO>> entries : dbMap.entrySet()) {
    		List<Long> repJobIds = entries.getValue().stream().map(JobOnlineVO::getJobId).collect(Collectors.toList());
			dbSlotSizeMap.put(entries.getKey(), repJobIds);
    	}
		return dbSlotSizeMap;
    }
    
    public List<JobOnlineVO> listAllByIds(List<Long> jobIds) {
    	Assert.collectionNotEmpty(jobIds, "任务id");
    	
    	JobOnline job = new JobOnline();
    	job.setIds(jobIds);
    	job.setQueryListFieldName("job_id");
    	job.setOrderBy("id asc");
    	return this.list(job);
    }
    
    public List<JobOnlineVO> listPause() {
    	JobOnline job = new JobOnline();
    	job.setIsDeleted(false);
    	job.setStatus(JobStatusEnum.PAUSE);
    	return this.list(job);
    }
    
    public List<JobOnlineVO> listJob(Boolean isRunning) {
    	JobOnline job = new JobOnline();
    	job.setIsDeleted(false);
    	job.setIsRunning(isRunning);
    	List<JobOnlineVO> jobs = this.list(job);
    	return jobs;
    }
    
    public List<Long> listDependJob() {
    	JobOnline job = new JobOnline();
    	job.setIsDeleted(false);
    	job.setQueryListFieldName("cycle_type");
    	job.setIdsString(Lists.newArrayList(JobCycleTypeEnum.DAY.name()
    			, JobCycleTypeEnum.DAYT1.name()));
    	
    	List<JobOnlineVO> jobs = this.list(job);
    	if (CollectionUtils.isEmpty(jobs)) {
    		return Lists.newArrayList();
    	}
    	
    	return jobs.stream().map(JobOnlineVO::getJobId).collect(Collectors.toList());
    }

	/**
	 * 根据jobId查询对应的job信息
	 * @param jobId
	 * @return
	 */
    public JobOnline getByJobId(Long jobId){
    	JobOnline jobOnline = new JobOnline();
		jobOnline.setJobId(jobId);
		jobOnline.setIsDeleted(false);

		return this.get(jobOnline);
	}
    
}
