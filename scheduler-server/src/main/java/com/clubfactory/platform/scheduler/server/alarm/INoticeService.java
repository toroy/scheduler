package com.clubfactory.platform.scheduler.server.alarm;

import java.util.List;

import com.clubfactory.platform.scheduler.core.vo.JobOnlineVO;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;

public interface INoticeService {

	/**
	 * 发送成功信息
	 * 
	 * @param addresses
	 * @param task
	 */
	public void sendSuccessMsg(List<String> addresses, TaskVO task);
	
	/**
	 * 发送延迟信息
	 * 
	 * @param addresses
	 * @param delayDur
	 * @param task
	 */
	public void sendDelayMsg(List<String> addresses,Integer delayDur, TaskVO task);
	
	/**
	 * 发送暂停信息
	 * 
	 * @param addresses
	 * @param jobOnline
	 */
	public void sendPauseMsg(List<String> addresses, JobOnlineVO jobOnline);
	
	/**
	 * 重试信息
	 * 
	 * @param addresses
	 * @param task
	 */
	public void sendRetryMsg(List<String> addresses, TaskVO task);
	
	/**
	 * 错误信息
	 * 
	 * @param addresses
	 * @param task
	 */
	public void sendErrorMsg(List<String> addresses, TaskVO task);
	
	/**
	 * 数据错误信息
	 * 
	 * @param addresses
	 * @param task
	 */
	public void sendDataErrorMsg(List<String> addresses, TaskVO task);
}
