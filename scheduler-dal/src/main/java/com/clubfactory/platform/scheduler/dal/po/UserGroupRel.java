package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

@Data
public class UserGroupRel extends BasePO {
	
	/**
	 * 用户id
	 */
	private Long userInfoId;
	
	/**
	 * 组id
	 */
	private Long groupId;

	
	
}
