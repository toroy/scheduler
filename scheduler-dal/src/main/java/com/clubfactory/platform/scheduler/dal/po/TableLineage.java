package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.DbType;
import com.clubfactory.platform.scheduler.dal.enums.LineageTypeEnum;

import lombok.Data;

@Data
public class TableLineage extends BasePO {

	
	/**
	 * 任务id
	 */
	private Long jobId;
	
	
	private LineageTypeEnum type;
	
	/**
	 * 数据库名
	 */
	private String dbName;
	
	/**
	 * 数据库类型
	 */
	private DbType dbType;
	
	/**
	 * dbhost
	 */
	private String dbHost;
	
	/**
	 * 表名
	 */
	private String tableName;
	
	
}
