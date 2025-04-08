package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

@Data
public class Duty extends BasePO {

	/**
	 * 值班周
	 */
	private Integer week;
	
	/**
	 * 电话
	 */
	private Long mobile;
	
	/**
	 * 值班人名字
	 */
	private String name;
}
