package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

@Data
public class TaskDepends extends BasePO{

	/**
	 * 父节点id
	 */
	private Long parentId;
	
	/**
	 * 作业id
	 */
	private Long taskId;
}
