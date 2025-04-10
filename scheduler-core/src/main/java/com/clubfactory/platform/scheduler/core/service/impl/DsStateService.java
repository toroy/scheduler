package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.common.util.Assert;
import com.clubfactory.platform.scheduler.dal.dao.DsStateMapper;
import com.clubfactory.platform.scheduler.dal.po.DsState;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xiejiajun
 */
@Service
public class DsStateService {

    @Resource
    private DsStateMapper dsStateMapper;

    public int save(DsState dsState){
        Assert.notNull(dsState,"dsState");
        Assert.notNull(dsState.getWorkerIp(),"调度机IP");
        Assert.notNull(dsState.getDbHost(),"数据库服务host");
        Assert.notNull(dsState.isConnSuccess(),"连接状态");
        return dsStateMapper.save(dsState);
    }

    public int saveBatch(List<DsState> list){
        Assert.collectionNotEmpty(list,"测试结果");
        return dsStateMapper.saveBatch(list);
    }

    public int clearOldState(String workerIp){
        Assert.notNull(workerIp,"调度机IP");
        return dsStateMapper.deleteByWorkerIp(workerIp);
    }

    public DsState getById(Long id){
        return dsStateMapper.select(id);
    }

    public List<DsState> list(DsState dsState){
        return dsStateMapper.list(dsState);
    }
}
