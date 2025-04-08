package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.TableOnlineLineageVO;
import com.clubfactory.platform.scheduler.dal.dao.TableOnlineLineageMapper;
import com.clubfactory.platform.scheduler.dal.po.TableOnlineLineage;
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
