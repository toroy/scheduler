package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.dal.dao.JobTypeMapper;
import com.clubfactory.platform.scheduler.dal.po.JobType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xiejiajun
 */
@Service
public class JobTypeService {

    @Resource
    private JobTypeMapper jobTypeMapper;


    public JobType get(Long jobTypeId) {
        return jobTypeMapper.select(jobTypeId);
    }

    public JobType getByName(String jobName, String category) {
        return jobTypeMapper.selectByName(jobName, category);
    }
}
