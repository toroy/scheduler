package com.clubfactory.platform.scheduler.core.service;

import com.clubfactory.platform.scheduler.core.vo.*;
import com.clubfactory.platform.scheduler.dal.enums.ConfigType;
import com.clubfactory.platform.scheduler.dal.enums.JobCategoryEnum;
import com.clubfactory.platform.scheduler.dal.po.*;

import java.util.List;

/**
 * @author xiejiajun
 */
public interface ICommonService {

    /**
     * 初始化
     */
    void init();


    /**
     * 方法执行前置操作
     */
    default void preInvoke(){

    }


    /**
     * 根据taskId获取task实例
     * @param taskId
     * @return
     */
    TaskVO getTaskInstance(long taskId);


    /**
     * 根据host拉取对应worker的slots数量
     * @param ip
     * @return
     */
    Integer getWorkerSlots(String ip);


    /**
     * 分类别获取slots
     * @param ip
     * @return
     */
     List<SlotMap> getWorkerSlotMap(String ip);


    /**
     * 更新Task Instance状态
     * @param task
     */
    void updateTaskInfo(Task task);

    /**
     * 将Task状态设置为RUNNING并且运行次数加1
     * @param taskId
     */
    void taskStateRunning(Long taskId);

    /**
     * 更新调度机状态
     * @param machine
     */
    void changeMachineStatus(Machine machine);


    /**
     * 根据用户ID获取对应的英文用户名
     * @param userId
     * @return
     */
    String getUsernameById(Long userId);


    /**
     * 根据task对应的jobId获取Job执行参数和超时时间（execParam、timeout) d
     * @param jobId
     * @return
     */
    JobOnline getParamsByJobId(Long jobId);


    Job getParamsByIdFromJob(Long jobId);

    /**
     * 根据clusterId获取集群信息
     * @param clusterId
     * @return
     */
     Cluster getClusterInfoById(Long clusterId);

    /**
     * 根据脚本ID获取脚本信息
     * @param scriptId
     * @return
     */
     ScriptVO getScriptInfoById(Long scriptId);


    /**
     * 保存日志映射关系
     * @param logMap
     * @return
     */
     int saveLogMap(LogMap logMap);


    /**
     * 根据workerIp刷新数据源连通状态
     */
    void refreshDBConnState(String workerIp);

    /**
     * 查询当前机器相关配置
     * @param host
     * @param configType
     * @return
     */
    List<SysConfigVO> listValidConf(String host, ConfigType configType);

    /**
     * 获取当前机器上状态为Running的task
     * @param host
     * @return
     */
    List<TaskVO> listRunningTasksByHost(String host);


    /**
     * 获取所有全局变量
     * @return
     */
    List<MacroVarVO> listGlobalMacroVars();

    /**
     * 获取作业类型
     * @param jobTypeId
     * @return
     */
    JobType getJobType(Long jobTypeId);

    /**
     * 根据插件名称获取插件信息
     * @param pluginName
     * @param category
     * @return
     */
    JobType getJobTypeByName(String pluginName, JobCategoryEnum category);

}
