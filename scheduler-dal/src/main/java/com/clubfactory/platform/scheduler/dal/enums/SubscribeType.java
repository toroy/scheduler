package com.clubfactory.platform.scheduler.dal.enums;

import lombok.Getter;

/**
 * @author xiejiajun
 */
@Getter
public enum SubscribeType {

    /**
     * MANUAL_SUBSCRIBE: 通过新增订阅方式添加
     * DEP_SUBSCRIBE: 任务上线时，通过血缘解析到上游表列表, 自动订阅
     */
    MANUAL_SUBSCRIBE("主动订阅"),
    DEP_SUBSCRIBE("依赖订阅");


    private String desc;

    SubscribeType(String desc) {
        this.desc = desc;
    }
}
