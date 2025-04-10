package com.clubfactory.platform.scheduler.dal.enums;


import com.clubfactory.platform.scheduler.common.util.Assert;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PriorityEnum implements IEnum {

	LOW(0,"低（普通报表+非时效）"),
	NORMAL(1,"一般（非核心业务+时效）"),
	MIDDLE(2,"中（核心业务+时效）"),
	HIGH(3,"高（线上+时效）");
	
	private int code;
	
	private String desc;
	
	public static PriorityEnum getByCode(int code) {
		Assert.notNull(code);
		for (PriorityEnum priorityEnum : PriorityEnum.values()) {
			if (priorityEnum.getCode() == code) {
				return priorityEnum;
			}
		}
		return PriorityEnum.HIGH;
	}
}
