package com.clubfactory.platform.scheduler.core.enums;

import lombok.Getter;

/**
 * @author xiejiajun
 */
@Getter
public enum ServerType {

    /**
     * 服务类型
     */
    MASTER_SERVER("MasterServer","scheduler-master"),
    WORKER_SERVER("WorkerServer","scheduler-worker"),
    LOGGER_SERVER("LoggerServer","scheduler-logger");

    private String desc;

    /**
     * 日志文件名称
     */
    private String logName;

    ServerType(String desc,String logName){
        this.desc = desc;
        this.logName = logName;
    }

    public static ServerType keyOf(String desc){
        for (ServerType serverType : ServerType.values()){
            if (serverType.desc.equalsIgnoreCase(desc)){
                return serverType;
            }
        }
        return MASTER_SERVER;
    }
}
