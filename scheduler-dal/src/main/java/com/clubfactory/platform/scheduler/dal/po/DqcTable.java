package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

@Data
public class DqcTable extends BasePO {

    /**
     * 库名
     */
    private String dbName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 关联的任务id
     */
    private Long relJobId;
}
