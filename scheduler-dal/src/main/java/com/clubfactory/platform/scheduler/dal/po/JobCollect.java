package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.FormatEnum;
import com.clubfactory.platform.scheduler.dal.enums.IncrementTypeEnum;

import lombok.Data;

@Data
public class JobCollect extends BasePO {

	/**
	 * 任务id
	 */
	private Long jobId;
	
	/**
	 * 增量字段
	 */
	private String incrementColumn;
	
	/**
	 * 增量类型
	 */
	private IncrementTypeEnum incrementType;
	
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
	
	/**
	 * 配置源字段
	 */
	private String targetColumns;
	
	/**
	 * 配置源字段
	 */
	private String sourceColumns;
	
	/**
	 * 目标表
	 */
	private String targetTable;
	
	/**
	 * 源表
	 */
	private String sourceTable;
	
	/**
	 * 源id
	 */
	private Long dbSourceId;
	
	/**
	 * 目标id
	 */
	private Long dbTargetId;
	
	/**
	 * 存储格式
	 */
	private FormatEnum storageFormat;
	
	/**
	 * 是否全选字段
	 */
	private Boolean isAllColumn;
}
