package com.clubfactory.platform.scheduler.server.constant;

public final class AlarmConstant {

	// 失败通知，间隔1小时
	public static int FAILED_NOTICE_DUR = 1; //单位小时
	
	// 失败重试 次数
	public static int FAILED_RETRY_MAX_COUNT = 24 * 3;
}
