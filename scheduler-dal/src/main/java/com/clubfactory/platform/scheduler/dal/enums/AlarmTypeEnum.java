package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AlarmTypeEnum implements IEnum  {

	FAILED("失败告警"),
	RETRY("重试告警"),
	DELAY("延迟告警"),
	SUCCESS("成功信息");
	
	private String desc;
}
