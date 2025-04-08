package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.JobCalVO;
import com.clubfactory.platform.scheduler.dal.dao.JobCalMapper;
import com.clubfactory.platform.scheduler.dal.po.JobCal;
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
