package com.zhugeio.platform.scheduler.core.service.impl;

import com.zhugeio.platform.scheduler.core.vo.TableOnlineLineageDependVO;
import com.zhugeio.platform.scheduler.dal.dao.TableOnlineLineageDependMapper;
import com.zhugeio.platform.scheduler.dal.po.TableOnlineLineageDepend;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class TableOnlineLineageDependService extends BaseNewService<TableOnlineLineageDependVO,TableOnlineLineageDepend> {

    @Resource
    TableOnlineLineageDependMapper tableOnlineLineageDependMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(tableOnlineLineageDependMapper);
    }

}
