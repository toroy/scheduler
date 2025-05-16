package com.bigdata.platform.scheduler.core.service.impl;

import com.bigdata.platform.scheduler.core.vo.TableOnlineLineageVO;
import com.bigdata.platform.scheduler.dal.dao.TableOnlineLineageMapper;
import com.bigdata.platform.scheduler.dal.po.TableOnlineLineage;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class TableOnlineLineageService extends BaseNewService<TableOnlineLineageVO,TableOnlineLineage> {

    @Resource
    TableOnlineLineageMapper tableOnlineLineageMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(tableOnlineLineageMapper);
    }

}
