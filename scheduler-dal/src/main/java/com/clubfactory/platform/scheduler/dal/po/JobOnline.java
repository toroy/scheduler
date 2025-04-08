package com.clubfactory.platform.scheduler.dal.po;


import com.clubfactory.platform.scheduler.dal.enums.DeployModeEnum;
import com.clubfactory.platform.scheduler.dal.enums.FormatEnum;
import com.clubfactory.platform.scheduler.dal.enums.IncrementTypeEnum;

import lombok.Data;

/**
 * 任务
 * 
 * @author zhoulijiang
 *
 */
@Data
public class JobOnline extends BaseJob {

	/**
	 * 层级
	 */
	private Integer level;
	
	/**
	 * 部署模式
	 */
	private DeployModeEnum deployMode;

	// 额外字段
	/**
	 * 任务id
	 */
	private Long jobId;
	
	/**
	 * 目标表
	 */
	private String targetTable;
	
	/**
	 * 目标id
	 */
	private Long dbTargetId;
	
	/**
	 * 源表
	 */
	private String sourceTable;
	
	/**
	 * 源id
	 */
	private Long dbSourceId;
	
	/**
	 * 存储格式
	 */
	private FormatEnum storageFormat;
	
	/**
	 * 增量字段
	 */
	private String incrementColumn;
	
	/**
	 * 增量类型
	 */
	private IncrementTypeEnum incrementType;
	
	/**
	 * 配置源字段
	 */
	private String targetColumns;
	
	/**
	 * 配置源字段
	 */
	private String sourceColumns;
	
	/**
	 * where条件
	 */
	private String whereSql;
	
	/**
	 * 分片字段
	 */
	private String splitPk;
	
	/**
	 * 并发数
	 */
	private Integer runCount;
	

}
