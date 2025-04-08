package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MqOffsetTypeEnum implements IEnum {

	EARLIEST("从头开始消费"),
	LATEST("最新开始消费");
	
	private String desc;
}
