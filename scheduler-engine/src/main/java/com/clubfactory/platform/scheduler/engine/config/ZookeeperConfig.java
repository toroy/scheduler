package com.clubfactory.platform.scheduler.engine.config;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xiejiajun
 */
@Data
public class ZookeeperConfig {

    private String quorum;

    private String workerParentZNode;

    private String instanceParentZNode;

    private int sessionTimeout;

    private int connectionTimeout;

    private int retryInterval;

    private int retryMaxTimes;


    public String getQuorum() {
        return quorum;
    }

    public String getWorkerParentZNode() {
        if (StringUtils.isEmpty(workerParentZNode)){
            workerParentZNode = ZkConstant.DEFAULT_WORKER_PATH;
        }
        return workerParentZNode;
    }

    public String getInstanceParentZNode() {
        if (StringUtils.isEmpty(instanceParentZNode)){
            instanceParentZNode = ZkConstant.TASK_PATH;
        }
        return instanceParentZNode;
    }

    public int getSessionTimeout() {
        if (sessionTimeout <= 0){
            sessionTimeout = 300;
        }
        return sessionTimeout;
    }

    public int getConnectionTimeout() {
        if (connectionTimeout <= 0){
            connectionTimeout = 300;
        }
        return connectionTimeout;
    }

    public int getRetryInterval() {
        if (retryInterval <= 0){
            retryInterval = 1000;
        }
        return retryInterval;
    }

    public int getRetryMaxTimes() {
        if (retryMaxTimes <= 0){
            retryMaxTimes = 5;
        }
        return retryMaxTimes;
    }
}
