package com.clubfactory.platform.scheduler.server.alarm.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class InvalidEmailDto implements Serializable {

	private static final long serialVersionUID = 9145120295739723073L;

	private String userName;
	
	private Date date;
}
