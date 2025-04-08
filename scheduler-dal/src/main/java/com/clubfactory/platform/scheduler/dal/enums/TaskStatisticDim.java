package com.clubfactory.platform.scheduler.dal.enums;

/**
 * @author xiejiajun
 */

public enum TaskStatisticDim {
    /**
     * 运行总览页面统计维度
     */
    DATE("按天统计"),
    DEPT("按部门统计"),
    TASKTYPE("按任务类型统计");

    TaskStatisticDim(String desc){
        this.desc = desc;
    }
    private String desc;
}
