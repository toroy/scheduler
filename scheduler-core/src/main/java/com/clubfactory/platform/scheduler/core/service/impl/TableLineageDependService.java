package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.TableLineageDependVO;
import com.clubfactory.platform.scheduler.dal.dao.TableLineageDependMapper;
import com.clubfactory.platform.scheduler.dal.po.TableLineageDepend;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class TableLineageDependService extends BaseNewService<TableLineageDependVO,TableLineageDepend> {

    @Resource
    TableLineageDependMapper tableLineageDependMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(tableLineageDependMapper);
    }

}
