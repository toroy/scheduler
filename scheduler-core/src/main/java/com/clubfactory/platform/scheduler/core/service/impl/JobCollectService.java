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

import com.clubfactory.platform.scheduler.core.vo.JobCollectVO;
import com.clubfactory.platform.scheduler.dal.dao.JobCollectMapper;
import com.clubfactory.platform.scheduler.dal.po.JobCollect;

@Service
public class JobCollectService extends BaseNewService<JobCollectVO,JobCollect> {

    @Resource
    JobCollectMapper jobCollectMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(jobCollectMapper);
    }
    
    public JobCollect getByJobId(Long jobId) {
    	Assert.notNull(jobId);
    	JobCollect job = new JobCollect();
    	job.setIsDeleted(false);
    	job.setJobId(jobId);
    	return job;
    }
    
    public List<JobCollectVO> listByJobIds(List<Long> jobIds) {
    	Assert.collectionNotEmpty(jobIds, "jobIds");
    	JobCollect job = new JobCollect();
    	job.setIsDeleted(false);
    	job.setIds(jobIds);
    	job.setQueryListFieldName("job_id");
    	return this.list(job);
    }
    
    public Map<Long, List<Long>> getDbSlotSizeMap(List<Long> jobIds) {
    	List<JobCollectVO> collectVOs =  this.listByJobIds(jobIds);
    	if (CollectionUtils.isEmpty(collectVOs)) {
    		return Maps.newHashMap();
    	}
    	Map<Long, List<JobCollectVO>> dbMap = collectVOs.stream().collect(Collectors.groupingBy(JobCollectVO::getDbSourceId));
    	Map<Long, List<Long>> dbSlotSizeMap = Maps.newHashMap();
    	for (Entry<Long, List<JobCollectVO>> entries : dbMap.entrySet()) {
    		List<Long> repJobIds = entries.getValue().stream().map(JobCollectVO::getJobId).collect(Collectors.toList());
			dbSlotSizeMap.put(entries.getKey(), repJobIds);
    	}
		return dbSlotSizeMap;
    }

}
