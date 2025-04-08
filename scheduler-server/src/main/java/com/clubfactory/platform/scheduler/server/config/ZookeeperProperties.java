package com.clubfactory.platform.scheduler.server.config;

import com.clubfactory.platform.scheduler.engine.config.ZookeeperConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.clubfactory.platform.scheduler.engine.config.ZkConstant;

import lombok.Setter;

/**
 * zk相关配置
 * @author xiejiajun
 */
@Setter
@Component
public class ZookeeperProperties {

    @Value("${zookeeper.quorum}")
    private String quorum;

    @Value("${zookeeper.scheduler.workers.path}")
    private String workerParentZNode;

    @Value("${zookeeper.scheduler.task.instance.path}")
    private String instanceParentZNode;

    @Value("${zookeeper.session.timeout}")
    private int sessionTimeout;

    @Value("${zookeeper.connection.timeout}")
    private int connectionTimeout;

    @Value("${zookeeper.retry.interval}")
    private int retryInterval;

    @Value("${zookeeper.retry.max-times}")
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


    public ZookeeperConfig buildZkConfig() {
        ZookeeperConfig config = new ZookeeperConfig();
        config.setQuorum(this.quorum);
        config.setConnectionTimeout(this.connectionTimeout);
        config.setInstanceParentZNode(this.instanceParentZNode);
        config.setWorkerParentZNode(this.workerParentZNode);
        config.setSessionTimeout(this.sessionTimeout);
        config.setRetryMaxTimes(this.retryMaxTimes);
        config.setRetryInterval(this.retryInterval);
        return config;
    }
}
