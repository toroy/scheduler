package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ClusterStatusEnum {

	INIT("未使用"),
	START("使用中"),
	STOP("停止使用");
	
	private String desc;
}
