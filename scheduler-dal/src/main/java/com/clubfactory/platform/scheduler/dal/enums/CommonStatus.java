package com.clubfactory.platform.scheduler.dal.enums;

import lombok.Getter;

/**
 * @author xiejiajun
 */

@Getter
public enum CommonStatus implements IEnum{
    /**
     * 启停状态
     */
    ENABLED("已启用"),
    DISABLED("已禁用");

    private String desc;

    CommonStatus(String desc){
        this.desc = desc;
    }


}
