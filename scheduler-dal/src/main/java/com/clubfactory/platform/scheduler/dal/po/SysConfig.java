package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.ConfigType;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author xiejiajun
 */
@Data
@Builder
public class SysConfig {

    private Long id;

    private String applyHost;

    private String paramKey;

    private String paramValue;

    private String paramDesc;

    private ConfigType configType;

    private Date createTime;

    private Date updateTime;

}
