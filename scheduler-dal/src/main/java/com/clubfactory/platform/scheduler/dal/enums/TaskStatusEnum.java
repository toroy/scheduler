package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaskStatusEnum implements IEnum {

	INIT("初始化"),
	RUNNING("正在运行"),
	READY("已准备"),
	SCHEDULED("已调度"),
	MANUAL_SUCCESS("强制成功"),
	SUCCESS("任务成功"),
	PAUSE("暂停调度"),
	KILLING("杀任务中"),
	KILLED("任务已杀"),
	FAILED("任务失败"),
	WAIT_VALID("等待数据校验"),
	DATA_FAILED("数据校验失败");
	
	private String desc;
}
