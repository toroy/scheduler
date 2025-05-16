package com.bigdata.platform.scheduler.core.vo;

import com.bigdata.platform.scheduler.dal.po.JobOnline;
import lombok.Data;

@Data
public class JobOnlineVO extends JobOnline {

	private String userName;
}
