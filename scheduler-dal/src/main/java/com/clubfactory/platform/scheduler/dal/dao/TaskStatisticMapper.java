package com.clubfactory.platform.scheduler.dal.dao;

import com.clubfactory.platform.scheduler.dal.po.TaskStatistic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface TaskStatisticMapper extends BaseMapper<TaskStatistic>{

    /**
     * 按日期维度统计任务运行概览情况
     * @param createTime
     * @param startTime
     * @param endTime
     * @param allowDelayMinute :允许调度时间和真正执行时间的差值，超过该值则任务调度延时
     * @return
     */
    int statisticOverviewByDate(@Param("createTime") String createTime,
                                @Param("startTime") String startTime,
                                @Param("endTime") String endTime,
                                @Param("allowDelayMinute") int allowDelayMinute);


    /**
     * 按任务类型统计任务运行概览情况
     * @param createTime
     * @param startTime
     * @param endTime
     * @param allowDelayMinute :允许调度时间和真正执行时间的差值，超过该值则任务调度延时
     * @return
     */
    int statisticOverviewByTaskType(@Param("createTime") String createTime,
                                    @Param("startTime") String startTime,
                                    @Param("endTime") String endTime,
                                    @Param("allowDelayMinute") int allowDelayMinute);


    /**
     * 按团队统计任务运行概览情况
     * @param createTime
     * @param startTime
     * @param endTime
     * @param allowDelayMinute :允许调度时间和真正执行时间的差值，超过该值则任务调度延时
     * @return
     */
    int statisticOverviewByTeam(@Param("createTime") String createTime,
                                @Param("startTime") String startTime,
                                @Param("endTime") String endTime,
                                @Param("allowDelayMinute") int allowDelayMinute);

    /**
     * 获取对应维度的最大统计时间
     * @param statisticDim
     * @return
     */
    String getMaxDateWithDim(@Param("statisticDim") String statisticDim);

    /**
     * 获取task实例表中最小的实例调度时间对应的日期
     * @return
     */
    String getMinTaskDate();


    /**
     * 从指定时间开始删除记录
     * @param startTime
     * @return
     */
    int deleteFromSpecificDay(@Param("startTime") String startTime);

}