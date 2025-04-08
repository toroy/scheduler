package com.clubfactory.platform.scheduler.dal.po;


import com.clubfactory.platform.scheduler.dal.enums.DeployModeEnum;

import lombok.Data;

/**
 * 任务
 * 
 * @author zhoulijiang
 *
 */
@Data
public class Job extends BaseJob {
	
	/**
	 * 是否检查循环依赖
	 */
	private Boolean isCheckCycle;
	
	/**
	 * 错误信息
	 */
	private String errMsg;
	
	/**
	 * 部署模式
	 */
	private DeployModeEnum deployMode;
	
}
