package com.clubfactory.platform.scheduler.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 集群类型
 *
 * @author zhoulijiang
 * @date 2020-02-24 09:57
 *
 */
@AllArgsConstructor
@Getter
public enum ClusterTypeEnum implements IEnum{

	COMMON("公共集群"),
	TEST("测试集群"),
	ONLINE("线上集群");
	
	private String desc;
}
