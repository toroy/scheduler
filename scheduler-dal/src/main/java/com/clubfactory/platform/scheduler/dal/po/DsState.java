package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.DbType;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author xiejiajun
 */
@Data
@Builder
public class DsState {

    private Long id;

    private Long dsId;

    private String dbHost;

    private String dbPort;

    private String dbUser;

    private DbType dbType;

    private String workerIp;

    private boolean connSuccess;

    private String failedReason;

    private Date createTime;
}
