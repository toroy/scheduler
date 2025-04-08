package com.clubfactory.platform.scheduler.dal.po;

import java.util.Date;
import java.util.List;

import com.clubfactory.platform.scheduler.dal.enums.JobCategoryEnum;
import com.clubfactory.platform.scheduler.dal.enums.JobCycleTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.JobTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;

import lombok.Data;

@Data
public class Task extends BasePO {

	/**
	 * 作业名称
	 */
	private String name;
	
	/**
	 * 任务id
	 */
	private Long jobId;
	
	/**
	 * 类别
	 */
	private JobCategoryEnum category;
	
	/**
	 * 类型
	 */
	private String type;
	
	/**
	 * 作业状态
	 */
	private TaskStatusEnum status;
	
	/**
	 * 开始时间
	 */
	private Date startTime;


	/**
	 * 任务启动执行时间
	 */
	private Date execTime;

	/**
	 * task时间
	 */
	private Date taskTime;
	
	/**
	 * 当前重试次数
	 */
	private Integer retryCount;
	
	/**
	 * 最大重试限制次数
	 */
	private Integer retryMax;
	
	/**
	 * 重试间隔（分钟）
	 */
	private Integer retryDur;
	
	/**
	 * 告警超时时间
	 */
	private Date alarmOverTime;
	
	/**
	 * 优先级
	 */
	private Integer priority;
	
	/**
	 * 调度机器id
	 */
	private Long machineId;
	
	/**
	 * 集群id
	 */
	private Long clusterId;
	
	/**
	 * 结束时间
	 */
	private Date endTime;
	
	/**
	 * 机器ip
	 */
	private String ip;
	
	/**
	 * 日志地址:${log.path}/task_instance/task_${taskId}_${yyyyMMddHHmmss}.log
	 */
	private String logPath;

	/**
	 * 日志文件版本：yyyyMMddHHmmss
	 */
	private String logVersion;
	
	/**
	 * 分值，通过job的level计算
	 */
	private Integer score;
	
	/**
	 * 脚本id
	 */
	private Long scriptId;
	
	
	/**
	 * 部门名称
	 */
	private String departName;

	/**
	 * 任务执行目录：${process.exec.basepath}/task_instance/${taskId}_${timestamp}
	 */
	private String executeDir;

	/**
	 * 周期类型
	 */
	private JobCycleTypeEnum cycleType;
	
	/**
	 * 成功/失败是否通知
	 */
	private Boolean isNotice;
	
	/**
	 * 通知次数
	 */
	private Integer noticeCount;
	
	/**
	 * 通知时间
	 */
	private Date noticeTime;
	
	/**
	 * 是否临时节点
	 */
	private Boolean isTemp;

	/**
	 * process pid
	 */
	private Integer pid;

	/**
	 * EMR 临时集群ID
	 */
	private String emrClusterId;

	/**
	 * 任务类型， NORMAL:正常任务，DQC:DQC任务，默认 NORMAL
	 */
	private JobTypeEnum jobType;
	
	/**
	 * 状态多选
	 */
	private List<TaskStatusEnum> statuses;

	public String taskCategory() {
		return String.format("%s_%s",this.category.name(),this.type);
	}
	
}