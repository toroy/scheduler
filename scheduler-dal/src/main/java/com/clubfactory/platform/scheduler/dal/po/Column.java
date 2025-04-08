package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

@Data
public class Column extends BasePO {

	/**
	 * 字段名
	 */
	private String name;
	
	/**
	 * 字段类型
	 */
	private String type;
	
	/**
	 * 字段描述
	 */
	private String desc;
	
	/**
	 * 外键id
	 */
	private Long foreignId;
}
