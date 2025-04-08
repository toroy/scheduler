package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.*;
import lombok.Data;

@Data
public class DqcRule extends BasePO {

	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 分区表达式id
	 */
	private Long dqcPartitionId;
	
	/**
	 * 规则类型
	 */
	private String type;
	
	/**
	 * 规则字段
	 */
	private String field;
	
	/**
	 * 采样方式 count/sum/avg
	 */
	private SampleEnum sample;
	
	/**
	 * 过滤条件
	 */
	private String filter;

	/**
	 * 自定义sql
	 */
	private String userSql;

	/**
	 /**
	 * 校验类型
	 */
	private CheckTypeEnum checkType;
	
	/**
	 * 校验方式
	 */
	private CheckModeEnum checkMode;
	
	/**
	 * 比较方式
	 */
	private CompareEnum compare;
	
	/**
	 * 期望值
	 */
	private String expectValue;
	
	/**
	 * 阈值上界
	 */
	private Long upperThreshold;
	
	/**
	 * 阈值下界
	 */
	private Long lowerThreshold;
	
	/**
	 * 描述
	 */
	private String description;
	
	/**
	 * 是否阻塞
	 */
	private Boolean isBlock;
	
	/**
	 * 责任人id
	 */
	private Long userId;
	
	/**
	 * 库名
	 */
	private String dbName;
	
	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 任务id
	 */
	private Long jobId;
	
	/**
	 * 关联任务id
	 */
	private Long relJobId;

	/**
	 * 链接方式
	 */
	private ConnectorTypeEnum connectorType;
	
}
