package com.bigdata.platform.scheduler.core.service.impl;

import com.bigdata.platform.scheduler.core.vo.JobCalVO;
import com.bigdata.platform.scheduler.dal.dao.JobCalMapper;
import com.bigdata.platform.scheduler.dal.po.JobCal;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class JobCalService extends BaseNewService<JobCalVO,JobCal> {

    @Resource
    JobCalMapper jobCalMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(jobCalMapper);
    }

}
