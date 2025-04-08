package com.clubfactory.platform.scheduler.server.config;


import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.engine.config.ExecutorConfig;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * worker 服务配置
 * @author xiejiajun
 */
@Setter
@Component
public class WorkerProperties {

    @Value("${worker.exec.threads}")
    private int execThreadNum;

    @Value("${worker.heartbeat.interval}")
    private int heartbeatInterval;

    @Value("${worker.kill.threads}")
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

    public ExecutorConfig buildExecutorConfig() {
        ExecutorConfig config = new ExecutorConfig();
        config.setExecThreadNum(this.execThreadNum);
        config.setHeartbeatInterval(this.heartbeatInterval);
        config.setKillThreadNum(this.killThreadNum);
        return config;
    }
}
