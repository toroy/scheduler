package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JobStatusEnum implements IEnum {

	DOING("编辑"),
	CHECK("审核"),
	FAILED("审核失败"),
	PAUSE("暂停调度"),
	OFF("下线"),
	ONLINE("上线");
	
	private String desc;
}
