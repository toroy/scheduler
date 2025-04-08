package com.clubfactory.platform.scheduler.engine.dao;

import com.clubfactory.platform.scheduler.core.service.ICommonService;
import com.clubfactory.platform.scheduler.core.vo.*;
import com.clubfactory.platform.scheduler.dal.enums.ConfigType;
import com.clubfactory.platform.scheduler.dal.enums.JobCategoryEnum;
import com.clubfactory.platform.scheduler.dal.po.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * 用于保存数据访问接口的单例
 * @author xiejiajun
 */
@Slf4j
public class DataAccessWrapper implements ICommonService{

    private static DataAccessWrapper instance = new DataAccessWrapper();

    private ICommonService commonService;

    private boolean isInit = false;

    private DataAccessWrapper() {
    }

    public static DataAccessWrapper getInstance() {
        return instance;
    }

    public void init(ICommonService commonService) {
        if (!isInit) {
            synchronized (DataAccessWrapper.class) {
                this.commonService = commonService;
                this.isInit = true;
            }
        }
    }

    private void ensureInit() {
        if (!isInit) {
            throw new RuntimeException("DataAccessWrapper 尚未初始化");
        }
    }


    @Override
    public void preInvoke(){
        this.ensureInit();
    }

    @Override
    public void init() {
        commonService.init();
    }

    @Override
    public TaskVO getTaskInstance(long taskId) {
        return commonService.getTaskInstance(taskId);
    }

    @Override
    public Integer getWorkerSlots(String ip) {
        return commonService.getWorkerSlots(ip);
    }

    @Override
    public List<SlotMap> getWorkerSlotMap(String ip) {
        return commonService.getWorkerSlotMap(ip);
    }

    @Override
    public void updateTaskInfo(Task task) {
        commonService.updateTaskInfo(task);

    }

    @Override
    public void taskStateRunning(Long taskId) {
        commonService.taskStateRunning(taskId);
    }

    @Override
    public void changeMachineStatus(Machine machine) {
        commonService.changeMachineStatus(machine);

    }

    @Override
    public String getUsernameById(Long userId) {
        return commonService.getUsernameById(userId);
    }

    @Override
    public JobOnline getParamsByJobId(Long jobId) {
        return commonService.getParamsByJobId(jobId);
    }

    @Override
    public Job getParamsByIdFromJob(Long jobId) {
        return commonService.getParamsByIdFromJob(jobId);
    }

    @Override
    public Cluster getClusterInfoById(Long clusterId) {
        return commonService.getClusterInfoById(clusterId);
    }

    @Override
    public ScriptVO getScriptInfoById(Long scriptId) {
        return commonService.getScriptInfoById(scriptId);
    }

    @Override
    public int saveLogMap(LogMap logMap) {
        return commonService.saveLogMap(logMap);
    }

    @Override
    public void refreshDBConnState(String workerIp) {
        commonService.refreshDBConnState(workerIp);
    }

    @Override
    public List<SysConfigVO> listValidConf(String host, ConfigType configType) {
        return commonService.listValidConf(host,configType);
    }

    @Override
    public List<TaskVO> listRunningTasksByHost(String host) {
        return commonService.listRunningTasksByHost(host);
    }

    @Override
    public List<MacroVarVO> listGlobalMacroVars() {
        return commonService.listGlobalMacroVars();
    }

    @Override
    public JobType getJobType(Long jobTypeId) {
        return commonService.getJobType(jobTypeId);
    }

    @Override
    public JobType getJobTypeByName(String pluginName, JobCategoryEnum category) {
        return commonService.getJobTypeByName(pluginName, category);
    }
}
