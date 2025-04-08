package com.clubfactory.platform.scheduler.server.alarm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MsgTypeEnum {

	TEXT("text"),
	MARKDOWN("markdown");
	
	private String value;
}
