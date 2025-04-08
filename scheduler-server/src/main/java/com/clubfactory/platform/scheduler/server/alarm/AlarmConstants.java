package com.clubfactory.platform.scheduler.server.alarm;

public class AlarmConstants {

	public static String FAILED_BODY = "`新盖亚`任务:%s， 实例`失败`通知\n"
			+ "**任务详情**\n"
			+ "> 责任人: <font color=\"info\">%s</font>\n"
			+ "> 调度类型: <font color=\"comment\">%s</font>\n"
			+ "> 任务名称: <font color=\"comment\">%s</font>\n"
			+ "> 任务id: <font color=\"comment\">%s</font>\n"
			+ "> 实例id: <font color=\"comment\">%s</font>\n"
			+ "> 实例时间: <font color=\"comment\">%s</font>\n"
			+ "> 实例开始时间: <font color=\"comment\">%s</font>\n"
			+ "> 实例调度时间: <font color=\"comment\">%s</font>\n"
			+ "> 实例失败时间: <font color=\"comment\">%s</font>\n"
			+ "> 重试次数已用完: <font color=\"comment\">%s/%s</font>\n"
			+ "> 耗时: <font color=\"comment\">%s</font> 秒\n"
			+ "> \n"
			+ "> 请关注，失败日志点击：[失败日志信息](%s)";
	
	
	public static String DATA_FAILED_BODY = "`新盖亚`任务:%s， 实例`数据校验失败`通知\n"
			+ "**任务详情**\n"
			+ "> 责任人: <font color=\"info\">%s</font>\n"
			+ "> 调度类型: <font color=\"comment\">%s</font>\n"
			+ "> 任务名称: <font color=\"comment\">%s</font>\n"
			+ "> 任务id: <font color=\"comment\">%s</font>\n"
			+ "> 实例id: <font color=\"comment\">%s</font>\n"
			+ "> 实例时间: <font color=\"comment\">%s</font>\n"
			+ "> 实例开始时间: <font color=\"comment\">%s</font>\n"
			+ "> 实例调度时间: <font color=\"comment\">%s</font>\n"
			+ "> 实例失败时间: <font color=\"comment\">%s</font>\n"
			+ "> 耗时: <font color=\"comment\">%s</font> 秒\n"
			+ "> \n"
			+ "> 请关注，失败日志点击：[失败日志信息](%s)";
	
	public static String RETRY_BODY = "`新盖亚`任务:%s， 实例<font color=\"warning\"> 失败重试 </font>通知\n"
			+ "**任务详情**\n"
			+ "> 责任人: <font color=\"info\">%s</font>\n"
			+ "> 调度类型: <font color=\"comment\">%s</font>\n"
			+ "> 任务名称: <font color=\"comment\">%s</font>\n"
			+ "> 任务id: <font color=\"comment\">%s</font>\n"
			+ "> 实例id: <font color=\"comment\">%s</font>\n"
			+ "> 实例时间: <font color=\"comment\">%s</font>\n"
			+ "> 实例开始时间: <font color=\"comment\">%s</font>\n"
			+ "> 实例调度时间: <font color=\"comment\">%s</font>\n"
			+ "> 实例重试时间: <font color=\"comment\">%s</font>\n"
			+ "> 重试次数: <font color=\"comment\">%s/%s</font>\n"
			+ "> \n"
			+ "> 请关注，重试日志点击：[重试日志信息](%s)";
	
	public static String SUCCESS_BODY = "`新盖亚`任务:%s， 实例<font color=\"info\"> 运行成功 </font>通知\n"
			+ "**任务详情**\n"
			+ "> 责任人: <font color=\"info\">%s</font>\n"
			+ "> 调度类型: <font color=\"comment\">%s</font>\n"
			+ "> 任务名称: <font color=\"comment\">%s</font>\n"
			+ "> 任务id: <font color=\"comment\">%s</font>\n"
			+ "> 实例id: <font color=\"comment\">%s</font>\n"
			+ "> 实例时间: <font color=\"comment\">%s</font>\n"
			+ "> 实例开始时间: <font color=\"comment\">%s</font>\n"
			+ "> 实例调度时间: <font color=\"comment\">%s</font>\n"
			+ "> 实例结束时间: <font color=\"comment\">%s</font>\n"
			+ "> 耗时: <font color=\"comment\">%s</font> 秒\n";
	
	public static String DELAY_BODY = "`新盖亚`任务:%s， 实例<font color=\"warning\"> 延迟运行 </font>通知\n"
			+ "**任务详情**\n"
			+ "> 责任人: <font color=\"info\">%s</font>\n"
			+ "> 调度类型: <font color=\"comment\">%s</font>\n"
			+ "> 任务名称: <font color=\"comment\">%s</font>\n"
			+ "> 任务id: <font color=\"comment\">%s</font>\n"
			+ "> 实例id: <font color=\"comment\">%s</font>\n"
			+ "> 实例时间: <font color=\"comment\">%s</font>\n"
			+ "> 实例开始时间: <font color=\"comment\">%s</font>\n"
			+ "> 延迟报警设置: <font color=\"comment\">%s</font> 分钟\n"
			+ "> 当前已经延迟: <font color=\"comment\">%s</font> 分钟\n";
	
	public static String PAUSE_BODY = "`新盖亚`任务:%s， `已经停止调度`，请联系管理员恢复，或自行到实例管理->运行管理页面，手动恢复对应实例\n"
			+ "**任务详情**\n"
			+ "> 责任人: <font color=\"info\">%s</font>\n"
			+ "> 调度类型: <font color=\"comment\">%s</font>\n"
			+ "> 任务名称: <font color=\"comment\">%s</font>\n"
			+ "> 任务id: <font color=\"comment\">%s</font>\n";
	
	public static String TASK_INIT_BODY = "`新盖亚`实例 定时初始化通知\n"
			+ "**实例详情**\n"
			+ "> 时间: <font color=\"info\">%s</font>\n"
			+ "> 实例总数 <font color=\"comment\">%s</font>\n"
			+ "> 采集实例总数 <font color=\"comment\">%s</font>\n"
			+ "> 计算实例总数 <font color=\"comment\">%s</font>\n"
			+ "> 回流实例总数 <font color=\"comment\">%s</font>\n"
			+ "> 暂停实例总数 <font color=\"comment\">%s</font>\n";

	public static String FAILED_MSG_SHORT 		= "盖亚任务：%s，任务失败";
	public static String SUCCESS_MSG_SHORT 		= "盖亚任务：%s，任务成功";
	public static String PAUSED_MSG_SHORT 		= "盖亚任务：%s，任务中止";
	public static String RETRY_MSG_SHORT 		= "盖亚任务：%s，失败重试";
	public static String DELAY_MSG_SHORT 		= "盖亚任务：%s，延迟运行";
	public static String DATA_FAILED_MSG_SHORT 	= "盖亚任务：%s，数据校验失败";
	
	public static void main(String[] args) {
		System.out.println(AlarmConstants.FAILED_BODY);
	}
}
