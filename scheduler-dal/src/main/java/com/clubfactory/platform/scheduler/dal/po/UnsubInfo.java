package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

@Data
public class UnsubInfo extends BasePO {

	/**
	 * 订阅人ID
	 */
	private Long subUserId;
	
	/**
	 * 库名
	 */
	private String dbName;
	
	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 数据源
	 */
	private String dataSource;
}
