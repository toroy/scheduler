package com.bigdata.platform.scheduler.core.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bigdata.platform.scheduler.core.vo.JobDependsVO;
import com.bigdata.platform.scheduler.dal.dao.JobDependsMapper;
import com.bigdata.platform.scheduler.dal.po.JobDepends;

@Service
public class JobDependsService extends BaseNewService<JobDependsVO,JobDepends> {

    @Resource
    JobDependsMapper jobDependsMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(jobDependsMapper);
    }
    
    public List<JobDependsVO> listJobDepends() {
    	JobDepends job = new JobDepends();
    	job.setIsDeleted(false);
    	List<JobDependsVO> jobs = this.list(job);
    	return jobs;
    }

}
