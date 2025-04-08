package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

@Data
public class UserInfo extends BasePO {

	/**
	 * 姓名
	 */
	private String name;
	
	/**
	 * 手机号
	 */
	private String phoneNo;
	
	/**
	 * 邮箱地址
	 */
	private String email;
	
	/**
	 * 机器人
	 */
	private String imRobot;
	
	/**
	 * 默认联系人组id
	 */
	private Long mainGroupId;
	
	/**
	 * 用户id
	 */
	private Long userId;
}
