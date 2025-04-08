package com.clubfactory.platform.scheduler.dal.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.clubfactory.platform.scheduler.dal.po.Task;

@Mapper
public interface TaskMapper extends BaseMapper<Task>{
	
	public List<Task> listMaxTask(@Param("nowDate") String date);
	
	public List<Task> listByFailedNotice(@Param("date") String date);
	
	public List<Task> listByDataFailedNotice(@Param("date") String date);
	
	public List<Task> listBySuccessNotice(@Param("date") String date);
	
	public List<Task> listByRetryNotice(@Param("date") String date);
	
	public List<Task> listByTimeover(@Param("time") String date, @Param("status") TaskStatusEnum status);
	
	public List<Task> listByDateAndJobIds(@Param("nowDate") Date date, @Param("ids") List<Long> jobIds);
	
	public List<Task> listByAlarmDelay(@Param("date") String date);

	/**
	 * 重试次数自增并置为RUNNING
	 *
	 * @param id
	 * @return
	 */
	public int setTaskRunning(@Param("id") Long id);
	
	
	public List<Task> listByDate(Task task);
	
	public List<Task> listInitTaskByDate(Task task);
	
	/**
	 * 查询超时的task列表，spark 跟 非spark，超时规定时间有区别
	 * 
	 * @param task
	 * @return
	 */
	public List<Task> listByDelay(Task task);
	
	//public int countByDelay(Task task, @Param("delaySpark") Integer delaySpark, @Param("delayNotSpark") Integer delayNotSpark);
	
	public int countByDelay(Task task);
	
	public int editNotice(Task task);
	
	public int editIfNull(Task task);
	
	public List<Task> listByTaskTime(Task task);
	
	public int editByUpdateTime(Task task);
}