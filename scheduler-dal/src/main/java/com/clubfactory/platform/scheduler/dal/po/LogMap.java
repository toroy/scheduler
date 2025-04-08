package com.clubfactory.platform.scheduler.dal.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author xiejiajun
 */
@Data
@Builder
public class LogMap {

    private Long id;

    private Long taskId;

    private String logName;

    private String logHost;

    private String logPath;

    /**
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 更新时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
