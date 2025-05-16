package com.bigdata.platform.scheduler.dal.po;

import com.bigdata.platform.scheduler.dal.enums.DbType;
import com.bigdata.platform.scheduler.dal.enums.LineageTypeEnum;

import lombok.Data;

@Data
public class TableOnlineLineage extends BasePO {

	private Long lineageId;
	
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
