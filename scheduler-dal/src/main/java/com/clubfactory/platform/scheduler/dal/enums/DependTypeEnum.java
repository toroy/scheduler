package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DependTypeEnum {

	SELF("自依赖"),
	SAME("同周期"),
	PREV("上个周期"),
	ALL("全周期依赖");
	
	private String desc;
	
}
