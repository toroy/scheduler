package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RunCountEnum implements IEnum {
	
	SIMPLE(1, "单并发(1)"),
	MILLION(4, "百万级别(4)"),
	TEN_MILLION(8, "千万级别(8)"),
	REFULE_MILLION(10, "百万级别(10)"),
	REFULE_TEN_MILLION(20, "千万级别(20)");
	
	private int code;
	private String desc;

}
