package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

@Data
public class TableLineageDepend extends BasePO {

	private Long jobId;

	/**
	 * 父节点id
	 */
	private Long parentId;
	
	/**
	 * 任务id
	 */
	private Long LineageId;
}
