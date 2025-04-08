package com.clubfactory.platform.scheduler.server.alarm;

import java.util.Date;

import com.clubfactory.platform.common.constant.DateFormatPattern;
import com.clubfactory.platform.common.util.DateUtil;
import com.clubfactory.platform.scheduler.core.vo.JobOnlineVO;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.po.Task;

public abstract class AbastractNoticeService {
	
	private long getDur(Integer delayDur, Task task) {
		Date date = new Date();
		long dur = (date.getTime() - task.getStartTime().getTime()) / 1000 / 60 - delayDur;
		return dur;
	}
	
	private long getDur(Task task) {
		return (task.getEndTime().getTime() - task.getExecTime().getTime()) / 1000;
	}

	protected String getSuccessMsgShort(TaskVO task) {
		return String.format(AlarmConstants.SUCCESS_MSG_SHORT, task.getName());
	}

	protected String getFailedMsgShort(TaskVO task) {
		return String.format(AlarmConstants.FAILED_MSG_SHORT, task.getName());
	}

	protected String getRetryMsgShort(TaskVO task) {
		return String.format(AlarmConstants.RETRY_MSG_SHORT, task.getName());
	}

	protected String getDelayMsgShort(TaskVO task) {
		return String.format(AlarmConstants.DELAY_MSG_SHORT, task.getName());
	}

	protected String getPauseMsgShort(JobOnlineVO jobOnlineVO) {
		return String.format(AlarmConstants.PAUSED_MSG_SHORT, jobOnlineVO.getName());
	}

	protected String getDataErrorMsgShort(TaskVO task) {
		return String.format(AlarmConstants.DATA_FAILED_MSG_SHORT, task.getName());
	}

	protected String getSuccessMsg(TaskVO task) {
		return String.format(AlarmConstants.SUCCESS_BODY
				, task.getName()
				, task.getUsername()
				, task.getCategory().getDesc()
				, task.getName()
				, task.getJobId()
				, task.getId()
				, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, DateUtil.format(task.getStartTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, DateUtil.format(task.getExecTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, DateUtil.format(task.getEndTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, getDur(task));
	}
	
	protected String getRetryMsg(TaskVO task) {
		return String.format(AlarmConstants.RETRY_BODY
				, task.getName()
				, task.getUsername()
				, task.getCategory().getDesc()
				, task.getName()
				, task.getJobId()
				, task.getId()
				, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, DateUtil.format(task.getStartTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, DateUtil.format(task.getExecTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, DateUtil.format(task.getUpdateTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, task.getRetryCount()
				, task.getRetryMax()
				, task.getLogUrl());
	}
	
	protected String getFailedMsg(TaskVO task) {
		return String.format(AlarmConstants.FAILED_BODY
				, task.getName()
				, task.getUsername()
				, task.getCategory().getDesc()
				, task.getName()
				, task.getJobId()
				, task.getId()
				, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, DateUtil.format(task.getStartTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, DateUtil.format(task.getExecTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, DateUtil.format(task.getEndTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, task.getRetryCount()
				, task.getRetryMax()
				, getDur(task)
				, task.getLogUrl());
	}

	protected String getDelayMsg(TaskVO task, Integer delayDur) {
		long dur = getDur(delayDur, task);
		return String.format(AlarmConstants.DELAY_BODY
				, task.getName()
				, task.getUsername()
				, task.getCategory().getDesc()
				, task.getName()
				, task.getJobId()
				, task.getId()
				, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, DateUtil.format(task.getStartTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, delayDur
				, dur);
	}

	protected String getPauseMsg(JobOnlineVO jobOnline) {
		return String.format(AlarmConstants.PAUSE_BODY
				, jobOnline.getName()
				, jobOnline.getUserName()
				, jobOnline.getCategroy().getDesc()
				, jobOnline.getName()
				, jobOnline.getJobId());
	}

	protected String getDataErrorMsg(TaskVO task) {
		return String.format(AlarmConstants.DATA_FAILED_BODY
				, task.getName()
				, task.getUsername()
				, task.getCategory().getDesc()
				, task.getName()
				, task.getJobId()
				, task.getId()
				, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, DateUtil.format(task.getStartTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, DateUtil.format(task.getExecTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, DateUtil.format(task.getEndTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
				, getDur(task)
				, task.getLogUrl());
	}
}
