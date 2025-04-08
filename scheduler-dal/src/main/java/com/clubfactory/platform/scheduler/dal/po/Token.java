package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

@Data
public class Token extends BasePO {

	/**
	 * token的value值
	 */
	private String value;
	
	/**
	 * 描述
	 */
	private String description;
}
