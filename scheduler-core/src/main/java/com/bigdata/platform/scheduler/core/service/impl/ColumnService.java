package com.bigdata.platform.scheduler.core.service.impl;

import com.bigdata.platform.scheduler.core.vo.ColumnVO;
import com.bigdata.platform.scheduler.dal.dao.ColumnMapper;
import com.bigdata.platform.scheduler.dal.po.Column;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class ColumnService extends BaseNewService<ColumnVO,Column> {

    @Resource
    ColumnMapper columnMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(columnMapper);
    }

}
