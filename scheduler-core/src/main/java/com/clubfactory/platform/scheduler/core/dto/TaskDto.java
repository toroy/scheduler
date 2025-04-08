package com.clubfactory.platform.scheduler.core.dto;

import java.io.Serializable;

import com.clubfactory.platform.scheduler.core.enums.CommandType;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;

import lombok.Data;

/**
 * @author xiejiajun
 */
@Data
public class TaskDto implements Serializable {

	private static final long serialVersionUID = -9070577931513352312L;
	
	/**
	 * 作业状态
	 */
	private TaskStatusEnum status;


	/**
	 * 补数据/正常调度
	 */
	private CommandType commandType;


	/**
	 * 脚本版本号
	 */
	private Integer version;
	
	/**
	 * 脚本名称
	 */
	private String fileName;

	/**
	 * 脚本创建人ID，用于组装DFS脚本存储路径
	 */
	private Long scriptUserId;

	/**
	 * 是否dqc关联
	 */
	private Boolean isBlock = false ;

}
