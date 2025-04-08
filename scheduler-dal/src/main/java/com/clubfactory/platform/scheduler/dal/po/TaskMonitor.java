package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

@Data
public class TaskMonitor extends BasePO {

    private Long taskId;

    private String yarnAppId;

    private Integer retryCounts;

    private String rmHost;

    private String rmPort;

    /**
     * 最大连续重试次数
     */
   private  Integer maxRetryNum;


}
