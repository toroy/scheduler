package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.TableLineageVO;
import com.clubfactory.platform.scheduler.dal.dao.TableLineageMapper;
import com.clubfactory.platform.scheduler.dal.po.TableLineage;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class TableLineageService extends BaseNewService<TableLineageVO,TableLineage> {

    @Resource
    TableLineageMapper tableLineageMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(tableLineageMapper);
    }

}
