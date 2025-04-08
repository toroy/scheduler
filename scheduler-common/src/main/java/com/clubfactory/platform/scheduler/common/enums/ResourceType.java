package com.clubfactory.platform.scheduler.common.enums;

import lombok.Getter;

/**
 * @author xiejiajun
 */
@Getter
public enum ResourceType {

    /**
     * Mysql分布式锁支持的资源类型
     */
	LOG_CLEANER_LOCK(1,"日志清理任务锁"),
    TEAM_POLLER_LOCK(2,"团队信息拉取任务锁"),
    TASK_INT_LOCK(3,"实例初始化"),
    DQC_TASK_LOCK(4, "dqc实例生成锁")
    ;


    private String desc;

    private Integer resourceId;


    ResourceType(Integer resourceId,String desc){
        this.desc = desc;
        this.resourceId = resourceId;
    }
}
