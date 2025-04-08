package com.clubfactory.platform.scheduler.dal.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class SchedulerTimeDto implements Serializable {
	
	private static final long serialVersionUID = -3556738811038276221L;

	/**
	 * 秒
	 */
	private Integer second;
	
	/**
	 * 分钟
	 */
	private Integer minute;
	
	/**
	 * 小时
	 */
	private Integer hour;
	
	/**
	 * 日
	 */
	private Integer day;
	
	/**
	 * 周
	 */
	private Integer week;
}
