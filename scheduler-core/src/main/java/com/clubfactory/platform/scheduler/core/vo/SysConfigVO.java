package com.clubfactory.platform.scheduler.core.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author xiejiajun
 */
@Data
@Builder
public class SysConfigVO {

    private String paramKey;

    private String paramValue;
}
