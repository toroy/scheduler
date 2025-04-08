package com.clubfactory.platform.scheduler.core.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author xiejiajun
 */
@Data
@Builder
public class SlotMap {

    private String feature;

    private Integer slot;
}
