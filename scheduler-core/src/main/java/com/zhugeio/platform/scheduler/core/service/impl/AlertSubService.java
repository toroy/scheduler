package com.zhugeio.platform.scheduler.core.service.impl;

import com.zhugeio.platform.scheduler.core.vo.AlertSubVO;
import com.zhugeio.platform.scheduler.dal.dao.AlertSubMapper;
import com.zhugeio.platform.scheduler.dal.po.AlertSub;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class AlertSubService extends BaseNewService<AlertSubVO,AlertSub> {

    @Resource
    AlertSubMapper alertSubMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(alertSubMapper);
    }

}
