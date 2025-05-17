package com.bigdata.platform.scheduler.core.service.impl;

import com.bigdata.platform.scheduler.core.vo.SubGroupRelVO;
import com.bigdata.platform.scheduler.dal.dao.SubGroupRelMapper;
import com.bigdata.platform.scheduler.dal.po.SubGroupRel;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class SubGroupRelService extends BaseNewService<SubGroupRelVO,SubGroupRel> {

    @Resource
    SubGroupRelMapper subGroupRelMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(subGroupRelMapper);
    }

}
