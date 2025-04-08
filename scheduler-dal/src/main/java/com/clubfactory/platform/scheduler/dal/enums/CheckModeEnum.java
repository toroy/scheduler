package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 校验方式
 */
@AllArgsConstructor
@Getter
public enum CheckModeEnum implements ICheckTypeEnum {

    FIXED(CheckTypeEnum.NUMBER, null, null, "固定值"),
    AVG_7(CheckTypeEnum.ROLLING, CheckModeTypeEnum.AVG, 7, "7天平均值波动"),
    AVG_30(CheckTypeEnum.ROLLING,CheckModeTypeEnum.AVG, 30, "30天平均值波动"),
    CYCLE_1(CheckTypeEnum.ROLLING,CheckModeTypeEnum.CYCLE, 1, "1天周期比较"),
    CYCLE_7(CheckTypeEnum.ROLLING,CheckModeTypeEnum.CYCLE, 7, "7天周期比较"),
    CYCLE_30(CheckTypeEnum.ROLLING,CheckModeTypeEnum.CYCLE, 30, "30天周期比较");
    //VARIANCE_7(CheckTypeEnum.ROLLING,CheckModeTypeEnum.VARIANCE,7,"7天方差波动"),
    //VARIANCE_30(CheckTypeEnum.ROLLING,CheckModeTypeEnum.VARIANCE, 30, "30天方差波动");


    private CheckTypeEnum checkTypeEnum;

    private CheckModeTypeEnum type;

    private Integer day;

    private String desc;
}
