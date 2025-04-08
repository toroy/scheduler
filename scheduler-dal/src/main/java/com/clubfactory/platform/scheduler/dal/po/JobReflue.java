package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.FormatEnum;
import com.clubfactory.platform.scheduler.dal.enums.IncrementTypeEnum;

import lombok.Data;

@Data
public class JobReflue extends BasePO {
	
	/**
	 * @author zhoulijiang
	 * @Ddate Dec 30, 201912:49:43 PM
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 任务id
	 */
	private Long jobId;

	/**
	 * 存储格式
	 */
	private FormatEnum storageFormat;
	
	/**
	 * 增量类型
	 */
	private IncrementTypeEnum incrementType;
	
	/**
	 * 增量字段名
	 */
	private String incrementColumn;
	
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
	 * 是否sql
	 */
	private Boolean isSql;
	
	/**
	 * sql字段
	 */
	private String sqlColumn;
	
	
	/**
	 * 自定义sql
	 */
	private String userSql;
	
	/**
	 * 源表字段
	 */
	private String sourceColumns;
	
	/**
	 * 配置字段
	 */
	private String targetColumns;
	
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
	 * 是否全选字段
	 */
	private Boolean isAllColumn;
}
