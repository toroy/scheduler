package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DbFeatureEnum implements IEnum{

	COLLECTION("采集"),
	SYNC("回流"),
	COMMON("公共(采集+回流)");
	
	private String desc;
}
