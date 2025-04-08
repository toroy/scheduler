package com.clubfactory.platform.scheduler.core.vo;

import com.clubfactory.platform.scheduler.dal.po.JobOnline;
import lombok.Data;

@Data
public class JobOnlineVO extends JobOnline {

	private String userName;
}
