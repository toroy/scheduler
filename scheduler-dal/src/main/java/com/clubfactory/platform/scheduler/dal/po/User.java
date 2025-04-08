package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

@Data
public class User extends BasePO {

	/**
	 * 微信用户id
	 */
	private String uid;
	
	/**
	 * 名字
	 */
	private String name;
	
	/**
	 * 部门id
	 */
	private Integer departId;
	
	/**
	 * 部门名称
	 */
	private String departName;
	
	/**
	 * 是否管理员
	 */
	private Boolean isAdmin;
	
	/**
	 * 别名
	 */
	private String alias;
}
