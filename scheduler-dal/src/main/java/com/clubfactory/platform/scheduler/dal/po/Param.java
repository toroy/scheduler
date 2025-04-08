package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.ProgramTypeEnum;

import lombok.Data;

@Data
public class Param extends BasePO {

	/**
	 * 任务类型
	 */
	private  String jobType;
	
	/**
	 * 语言类型
	 */
	private ProgramTypeEnum programType;
	
	/**
	 * 字段名
	 */
	private String name;
	
	/**
	 * 默认值
	 */
	private String defaultValue;
	
	/**
	 * 是否系统参数
	 */
	private Boolean isSystem;
}
