package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CheckTypeEnum implements IEnum {


    NUMBER("数值型"),
    ROLLING("波动型");

    private String desc;
}
