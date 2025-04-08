package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

@Data
public class SubGroupRel extends BasePO {

	/**
	 * 组id
	 */
	private Long groupId;
	
	/**
	 * 订阅id
	 */
	private Long subId;
}
