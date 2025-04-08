package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CheckModeTypeEnum implements IEnum{

    AVG("平均"),
    CYCLE("周期"),
    VARIANCE("方差");

    private String desc;
}
