package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.MqDataTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.MqOffsetTypeEnum;

import lombok.Data;

@Data
public class Mq extends BasePO {

	/**
	 * topic名字
	 */
	private String topicName;
	
	/**
	 * topic分区数
	 */
	private Integer topicPartition;
	
	/**
	 * topic描述
	 */
	private String topicDesc;
	
	/**
	 * dbId
	 */
	private Long dbId;
	
	/**
	 * 任务id
	 */
	private Long jobId;
	
	/**
	 * kafka消费类型
	 */
	private MqOffsetTypeEnum offsetType;
	
	/**
	 * kafka数据类型
	 */
	private MqDataTypeEnum dataType;

	/**
	 * 分隔符
	 */
	private String fieldDelimiter;
	
	/**
	 * 表名
	 */
	private String tableName;
	
}
