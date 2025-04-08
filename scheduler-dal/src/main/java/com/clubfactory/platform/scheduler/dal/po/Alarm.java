package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.AlarmNoticeTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.AlarmTypeEnum;

import lombok.Data;

/**
 * 报警
 * 
 * @author zhoulijiang
 *
 */
@Data
public class Alarm extends BasePO {

	/**
	 * 告警方式
	 */
	private AlarmTypeEnum type;
	
	/**
	 * 通知方式
	 */
	private AlarmNoticeTypeEnum noticeType;
	
	/**
	 * 邮件地址/手机号
	 */
	@Deprecated
	private String addresses;
	
	/**
	 * 延迟时间，单位分钟
	 */
	private Integer delayDur;
	
	/**
	 * 任务id
	 */
	private Long jobId;

	/**
	 * 联系人组
	 */
	private Long userGroupId;
	
}
