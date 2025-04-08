package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeployModeEnum implements IEnum {
	CLUSTER("集群模式"),
	CLIENT("客户端模式");

	private String desc;

}
