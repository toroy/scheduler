package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.ClusterVO;
import com.clubfactory.platform.scheduler.dal.dao.ClusterMapper;
import com.clubfactory.platform.scheduler.dal.po.Cluster;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class ClusterService extends BaseNewService<ClusterVO,Cluster> {

    @Resource
    ClusterMapper clusterMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(clusterMapper);
    }


    /**
     * 根据ID查询cluster信息
     * @param clusterId
     * @return
     */
    public Cluster getById(Long clusterId){
        Cluster cluster = new Cluster();
        cluster.setId(clusterId);
        cluster.setIsDeleted(false);

        return  this.get(cluster);
    }

}
