package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.JobCategoryEnum;

import lombok.Data;

@Data
public class TaskCountDay extends BasePO {

	/**
	 * 总数
	 */
	private Integer total;
	
	/**
	 * 成功数
	 */
	private Integer successAmount;
	
	/**
	 * 延迟数
	 */
	private Integer delayAmount;
	
	/**
	 * 失败数
	 */
	private Integer failedAmount;
	
	/**
	 * 类型
	 */
	private String type;
	
	/**
	 * 类别
	 */
	private JobCategoryEnum category;
}
