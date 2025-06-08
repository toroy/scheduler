package com.zhugeio.platform.scheduler.core.vo;

import com.zhugeio.platform.scheduler.dal.po.JobOnline;
import lombok.Data;

@Data
public class JobOnlineVO extends JobOnline {

	private String userName;
}
