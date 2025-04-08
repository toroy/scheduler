package com.clubfactory.platform.scheduler.dal.enums;

import lombok.Getter;

/**
 * @author xiejiajun
 */
@Getter
public enum ScriptType {

    /**
     * 脚本类型
     */
    SYS_LEVEL("系统级"),
    USER_LEVEL("用户级");

    private String desc;

    ScriptType(String desc){
        this.desc = desc;
    }

}
