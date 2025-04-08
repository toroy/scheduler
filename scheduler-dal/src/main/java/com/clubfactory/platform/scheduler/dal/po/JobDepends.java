package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.DependTypeEnum;

import lombok.Data;

@Data
public class JobDepends extends BasePO {


	/**
	 * 父节点id
	 */
	private Long parentId;
	
	/**
	 * 任务id
	 */
	private Long jobId;
	
	/**
	 * 依赖类型
	 */
	private DependTypeEnum type;
}
