package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

@Data
public class DqcPartition extends BasePO {

	/**
	 * 库名
	 */
	private String dbName;
	
	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 分区表达式
	 */
	private String expression;
}
