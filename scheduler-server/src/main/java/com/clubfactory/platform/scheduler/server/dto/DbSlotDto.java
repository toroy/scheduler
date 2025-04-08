package com.clubfactory.platform.scheduler.server.dto;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class DbSlotDto implements Serializable {

	private static final long serialVersionUID = -5234916361998110015L;

	private Long dbId;
	
	private List<Long> jobIds = Lists.newArrayList();
	
	private Integer taskSize = 0;
}
