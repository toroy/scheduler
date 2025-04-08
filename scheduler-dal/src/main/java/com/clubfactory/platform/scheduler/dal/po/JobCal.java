package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;


@Data
public class JobCal extends BasePO {

	/**
	 * 任务id
	 */
	private Long jobId;
	
	/**
	 * 目标表
	 */
	private String targetTable;
	
	/**
	 * 源id
	 */
	private Long dbTargetId;
}
