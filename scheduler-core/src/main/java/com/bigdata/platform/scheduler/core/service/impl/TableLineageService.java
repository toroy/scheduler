package com.bigdata.platform.scheduler.core.service.impl;

import com.bigdata.platform.scheduler.core.vo.TableLineageVO;
import com.bigdata.platform.scheduler.dal.dao.TableLineageMapper;
import com.bigdata.platform.scheduler.dal.po.TableLineage;
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
