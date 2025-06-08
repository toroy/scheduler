package com.zhugeio.platform.scheduler.core.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.zhugeio.platform.scheduler.core.vo.JobOnlineDependsVO;
import org.springframework.stereotype.Service;

import com.zhugeio.platform.scheduler.dal.dao.JobOnlineDependsMapper;
import com.zhugeio.platform.scheduler.dal.po.JobOnlineDepends;

@Service
public class JobOnlineDependsService extends BaseNewService<JobOnlineDependsVO,JobOnlineDepends> {

    @Resource
    JobOnlineDependsMapper jobOnlineDependsMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(jobOnlineDependsMapper);
    }

    public List<JobOnlineDependsVO> listJobDepends(List<Long> jobIds) {
    	JobOnlineDepends job = new JobOnlineDepends();
    	job.setIsDeleted(false);
    	job.setIds(jobIds);
    	job.setQueryListFieldName("job_id");
    	List<JobOnlineDependsVO> jobs = this.list(job);
    	return jobs;
    }
}
