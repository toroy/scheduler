package com.zhugeio.platform.scheduler.core.service.impl;

import com.zhugeio.platform.scheduler.core.vo.TableOnlineLineageVO;
import com.zhugeio.platform.scheduler.dal.dao.TableOnlineLineageMapper;
import com.zhugeio.platform.scheduler.dal.po.TableOnlineLineage;
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
