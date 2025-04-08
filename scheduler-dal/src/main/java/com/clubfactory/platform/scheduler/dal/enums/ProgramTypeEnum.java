package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProgramTypeEnum implements IEnum {

	
	JAVA("java"),
	SCALA("scala"),
	PYTHON("python");
	
	private String desc;
	
}
