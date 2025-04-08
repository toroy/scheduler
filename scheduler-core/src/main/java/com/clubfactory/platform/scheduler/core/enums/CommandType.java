package com.clubfactory.platform.scheduler.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * command types
 */
@AllArgsConstructor
@Getter
public enum CommandType {

    COMPLEMENT_DATA("补数据"),
    SCHEDULER("正常调度");
	
	private String desc;
}
