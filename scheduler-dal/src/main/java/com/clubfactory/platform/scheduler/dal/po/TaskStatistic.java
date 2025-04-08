package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.TaskStatisticDim;
import lombok.Data;

import java.util.Date;

/**
 * @author xiejiajun
 */
@Data
public class TaskStatistic extends BasePO{


    /**
     * 任务执行日期
     */
    private Date taskDate;

    /**
     * 任务类型
     */
    private String type;

    /**
     * 任务所属部门
     */
    private String departName;


    /**
     * 任务所属团队ID
     */
    private Long departId;

    /**
     * 总任务数
     */
    private Integer totalTask;

    /**
     * 成功任务数
     */
    private Integer succeedTaskCount;

    /**
     * 延迟任务数
     */
    private Integer delayTaskCount;

    /**
     * 失败任务数
     */
    private Integer failedTaskCount;

    /**
     * 任务成功率
     */
    private String succeedRate;

    /**
     * 任务统计维度
     */
    private TaskStatisticDim statisticDim;
}
