package com.clubfactory.platform.scheduler.dal.dto;

import com.clubfactory.platform.scheduler.dal.enums.DbType;
import lombok.Data;

/**
 * @author xiejiajun
 */
@Data
public class SubscribeDto {
    private String tableName;
    private String dbName;
    private String dbHost;
    private DbType dbType;
    private Long dataSourceId;
    /**
     * 任务createUserId
     */
    private Long jobCreateUser;
}
