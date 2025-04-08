package com.clubfactory.platform.scheduler.dal.po;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.clubfactory.platform.scheduler.dal.dto.SchedulerTimeDto;
import com.clubfactory.platform.scheduler.dal.enums.*;

import lombok.Data;

/**
 * @author xiejiajun
 */
@Data
public class BaseJob extends BasePO{

    /**
     * 类别
     */
    private JobCategoryEnum categroy;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 类型
     */
    private String type;

    /**
     * 调度机id
     */
    private Long machineId;

    /**
     * 任务名
     */
    private String name;

    /**
     * 重试次数限制
     */
    private Integer retryMax;

    /**
     * 重试间隔（分钟）
     */
    private Integer retryDur;

    /**
     * 部门名称
     */
    private String departName;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 集群id
     */
    private Long clusterId;

    /**
     * 任务状态
     */
    private JobStatusEnum status;

    /**
     * 命令行参数
     */
    private String argsParam;

    /**
     * 系统参数
     */
    private String params;

    /**
     * 审核人
     */
    private Long checkUser;

    /**
     * 审核时间
     */
    private Date checkTime;

    /**
     * 脚本id
     */
    private Long scriptId;

    /**
     * 周期类型
     */
    private JobCycleTypeEnum cycleType;

    /**
     * 直接用来执行，组装好的参数
     */
    private String execParam;

    /**
     * 调度时间
     */
    protected String schedulerTime;

    /**
     * 是否立即就跑
     */
    private Boolean isRunning;

    /**
     * 主函数
     */
    private String mainClass;

    /**
     * 程序类型
     */
    private ProgramTypeEnum programType;

    /**
     * 超时时间，单位秒，-1表示不超时
     */
    private Integer timeOut;

    /**
     * 是否使用临时集群运行
     */
    private Boolean runOnTmpEmr;

    /**
     * 作业个性化配置：Map<String,String>型json
     */
    private String jobConf;
    
    /**
     * 目标表
     */
    private String targetTable;
    
    /**
     * 项目id
     */
    private Long projectId;
    
    /**
     * 组id
     */
    private Long groupId;

    /**
     * 任务类型，NORMAL: 普通类型，DQC: DQC任务，默认NORMAL
     */
    private JobTypeEnum jobType;
    
    /**
     * 状态多选项
     */
    private List<JobStatusEnum> statuses;


	public void setSchedulerTimeDto(SchedulerTimeDto schedulerTimeDto) {
		if (schedulerTimeDto != null) {
			this.schedulerTime = JSON.toJSONString(schedulerTimeDto);
		}
	}
	
	public SchedulerTimeDto getSchedulerTimeDto() {
		return JSON.parseObject(this.schedulerTime, SchedulerTimeDto.class);
	}

    /**
     * 作业类型id
     */
    private Long jobTypeId;
}
