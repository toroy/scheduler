package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.JobReflueVO;
import com.clubfactory.platform.scheduler.dal.dao.JobReflueMapper;
import com.clubfactory.platform.scheduler.dal.po.JobReflue;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class JobReflueService extends BaseNewService<JobReflueVO,JobReflue> {

    @Resource
    JobReflueMapper jobReflueMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(jobReflueMapper);
    }

}
