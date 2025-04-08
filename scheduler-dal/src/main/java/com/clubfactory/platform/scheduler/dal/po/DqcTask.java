package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

import java.util.Date;

@Data
public class DqcTask extends BasePO {

    /**
     * 库名
     */
    private String dbName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 目标实例id
     */
    private Long targetTaskId;

    /**
     * 实例时间
     */
    private Date taskTime;

    /**
     * 拥有人
     */
    private String ownerName;

    /**
     * 异常数
     */
    private Integer exceptionNum;

    /**
     * 分区数据
     */
    private String partitionValue;

    /**
     * 规则数
     */
    private Integer ruleNum;

    /**
     * 执行时间
     */
    private Date execTime;

    /**
     * 状态
     */
    private String status;

}
