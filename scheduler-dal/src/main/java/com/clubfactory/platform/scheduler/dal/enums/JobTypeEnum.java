package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JobTypeEnum implements IEnum{

    NORMAL("正常任务"),
    DQC("DQC任务");

    private String desc;
}
