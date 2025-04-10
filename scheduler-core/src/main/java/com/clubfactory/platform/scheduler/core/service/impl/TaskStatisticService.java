//package com.clubfactory.platform.scheduler.core.service.impl;
//
//import com.clubfactory.platform.scheduler.common.bean.PageUtils;
//import com.clubfactory.platform.scheduler.common.util.Assert;
//import com.clubfactory.platform.scheduler.common.util.DateUtil;
//import com.clubfactory.platform.scheduler.core.vo.TaskStatisticVO;
//import com.clubfactory.platform.scheduler.dal.dao.TaskStatisticMapper;
//import com.clubfactory.platform.scheduler.dal.enums.TaskStatisticDim;
//import com.clubfactory.platform.scheduler.dal.po.TaskStatistic;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.util.Date;
//
///**
// * @author xiejiajun
// */
//@Service("taskStatisticService")
//public class TaskStatisticService extends BaseNewService<TaskStatisticVO, TaskStatistic> {
//
//    @Value("${task.exec.allow.delay-minutes}")
//    private Integer allowDelayMinutes;
//
//    @Resource
//    TaskStatisticMapper taskStatisticMapper;
//
//    @PostConstruct
//    public void init(){
//        setBaseMapper(taskStatisticMapper);
//    }
//
//
//    /**
//     * 按日期维度统计任务运行概览情况
//     * @param startTime
//     * @param endTime
//     */
//    public void statisticTaskOverviewByDate(String startTime,String endTime){
//        ensureNotNull(startTime,endTime);
//        String currentTime = DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss");
//        taskStatisticMapper.statisticOverviewByDate(currentTime,startTime,endTime,allowDelayMinutes);
//    }
//
//    /**
//     * 按任务类型维度统计任务运行概览情况
//     * @param startTime
//     * @param endTime
//     */
//    public void statisticTaskOverviewByTaskType(String startTime,String endTime){
//        ensureNotNull(startTime,endTime);
//        String currentTime = DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss");
//        taskStatisticMapper.statisticOverviewByTaskType(currentTime,startTime,endTime,allowDelayMinutes);
//
//    }
//
//    /**
//     * 按团队维度统计任务运行概览情况
//     * @param startTime
//     * @param endTime
//     */
//    public void statisticTaskOverviewByTeam(String startTime,String endTime){
//        ensureNotNull(startTime,endTime);
//        String currentTime = DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss");
//        taskStatisticMapper.statisticOverviewByTeam(currentTime,startTime,endTime,allowDelayMinutes);
//    }
//
//
//    /**
//     * 确保参数非空
//     * @param startTime
//     * @param endTime
//     */
//    private void ensureNotNull(String startTime,String endTime){
//        Assert.notNull(startTime);
//        Assert.notNull(endTime);
//        if (allowDelayMinutes == null){
//            allowDelayMinutes = 30;
//        }
//    }
//
//    /**
//     * 按维度获取当前统计结果的最大日期
//     * @param statisticDim
//     * @return
//     */
//    public String getMaxDateWithDim(TaskStatisticDim statisticDim){
//        return taskStatisticMapper.getMaxDateWithDim(statisticDim.name());
//    }
//
//    /**
//     * 获取task表中最小的startTime
//     * @return
//     */
//    public String getMinTaskDate(){
//        return taskStatisticMapper.getMinTaskDate();
//    }
//
//    @Override
//    public PageUtils<TaskStatistic> pageList(TaskStatistic po) {
//        return super.pageList(po);
//    }
//}
