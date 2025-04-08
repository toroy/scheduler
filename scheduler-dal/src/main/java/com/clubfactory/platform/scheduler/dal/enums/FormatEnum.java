package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FormatEnum implements IEnum {

	ORC("orc"),
	SEQUENCEFILE("sequencefile"),
	TEXTFILE("textfile");
	
	private String desc;
}
