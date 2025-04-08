package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO
 *
 * @author zhoulijiang
 * @date 2022/2/28 5:23 下午
 **/
@AllArgsConstructor
@Getter
public enum ConnectorTypeEnum implements IEnum {

    HIVE("hive"),
    PRESTO("presto");

    private String desc;
}
