package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobCategoryEnum implements IEnum {

	CAL("计算"),
	COLLECT("采集"),
	REFLUE("回流");
	
	private String desc;
}
