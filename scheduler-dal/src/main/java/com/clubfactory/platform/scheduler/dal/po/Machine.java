package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.CommonStatus;
import com.clubfactory.platform.scheduler.dal.enums.MachineTypeEnum;

import lombok.Data;

/**
 * 调度机器
 * 
 * @author zhoulijiang
 *
 */
@Data
public class Machine extends BasePO {

	/**
	 * 机器名称
	 */
	private String name;
	
	/**
	 * 机器功能类型
	 */
	private MachineTypeEnum type;
	
	/**
	 * 调度机功能
	 */
	private String functions;
	
	/**
	 * 状态
	 */
	private CommonStatus status;
	

	/**
	 * 槽位
	 */
	private Integer slots;
	
	/**
	 * 主机ip
	 */
	private String ip;
	
	/**
	 * 是否私有
	 */
	private Boolean isSelf;
}
