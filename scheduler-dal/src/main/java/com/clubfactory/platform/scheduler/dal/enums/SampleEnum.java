package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum  SampleEnum implements IEnum {

    COUNT("统计"),
    SUM("累加"),
    AVG("平均值"),
    SQL("自定义SQL");


    private String desc;

}
