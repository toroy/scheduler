package com.clubfactory.platform.scheduler.server.constant;

public final class Constant {
 
	public static final int RETRY_CHANGE_IP_MAX = 2;
	
	public static final long RAND_MACHINE_ID = 0L;
	
	public static final String DB_SOURCE_SLOT = "db.source.slot";

	public static final String TASK_MONITOR_PERIOD = "task.monitor.period";
	
	public static final String ADMIN_IM_URL = "admin.im.url";
	
	public static final String WEB_HOST = "web.host";

	// dqc没有阻塞实例的最早执行时间
	public static final String DQC_TASK_NOT_BLOCK_TIME = "dqc.task.not.block.time";

	public static final String DQC_TASK_NOT_BLOCK_TIME_END = "dqc.task.not.block.time.end";
}
