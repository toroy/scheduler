package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IncrementTypeEnum implements IEnum {

	ADD("增量"),
	ALL("全量");
	
	private String desc;
}
