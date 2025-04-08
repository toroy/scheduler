package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MqDataTypeEnum implements IEnum {

	JSON("json格式"),
	TEXT("文本格式");
	
	private String desc;
}
