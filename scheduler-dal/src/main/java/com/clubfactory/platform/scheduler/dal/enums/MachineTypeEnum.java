package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MachineTypeEnum implements IEnum{

	MASTER("Master节点"),
	WORKER("Worker节点");
	
	private String desc;
}
