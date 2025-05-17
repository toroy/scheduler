package com.bigdata.platform.scheduler.engine.config;

import com.bigdata.platform.scheduler.common.Constants;
import lombok.Setter;

/**
 * @author xiejiajun
 */
@Setter
public class ExecutorConfig {

    private int execThreadNum;

    private int heartbeatInterval;

    private int killThreadNum;

    public int getExecThreadNum() {
        if (execThreadNum <= 0){
            execThreadNum = Runtime.getRuntime().availableProcessors();
        }
        return execThreadNum;
    }

    public int getHeartbeatInterval() {
        if (heartbeatInterval <= 0){
            heartbeatInterval = Constants.DEFAULT_WORKER_HEARTBEAT_INTERVAL;
        }
        return heartbeatInterval;
    }

    public int getKillThreadNum() {
        if (killThreadNum <= 0){
            killThreadNum = 2;
        }
        return killThreadNum;
    }
}
