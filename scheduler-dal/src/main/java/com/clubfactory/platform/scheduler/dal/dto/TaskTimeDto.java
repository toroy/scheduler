package com.clubfactory.platform.scheduler.dal.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class TaskTimeDto implements Serializable {

	private static final long serialVersionUID = 5334184101450523126L;

	/**
	 * 实例的时间
	 */
	private Date taskTime;
	
	/**
	 * 起始时间
	 */
	private Date startTime;
}
