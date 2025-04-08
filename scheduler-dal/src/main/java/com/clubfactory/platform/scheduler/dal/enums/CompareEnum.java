package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompareEnum implements ICheckTypeEnum {

    //ABSOLUTE(CheckTypeEnum.ROLLING, "绝对值","abs"),
    //UP(CheckTypeEnum.ROLLING, "上升","up"),
    //DOWN(CheckTypeEnum.ROLLING, "下降","down"),
    RANGE(CheckTypeEnum.ROLLING, "范围","range"),
    LT(CheckTypeEnum.NUMBER, "小于","<"),
    LTE(CheckTypeEnum.NUMBER,"小于等于","<="),
    EQUAL(CheckTypeEnum.NUMBER,"等于","=="),
    NOT_EQUAL(CheckTypeEnum.NUMBER,"不等于","!="),
    GT(CheckTypeEnum.NUMBER,"大于",">"),
    GTE(CheckTypeEnum.NUMBER,"大于等于", ">=");

    private CheckTypeEnum checkTypeEnum;

    private String desc;

    private String symbol;
}
