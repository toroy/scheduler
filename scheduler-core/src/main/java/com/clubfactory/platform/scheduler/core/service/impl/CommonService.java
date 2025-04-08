package com.clubfactory.platform.scheduler.core.service.impl;


import com.clubfactory.platform.common.exception.BizException;
import com.clubfactory.platform.common.util.Assert;
import com.clubfactory.platform.scheduler.common.utils.AESUtils;
import com.clubfactory.platform.scheduler.core.service.ICommonService;
import com.clubfactory.platform.scheduler.core.utils.DBConnTestUtil;
import com.clubfactory.platform.scheduler.core.vo.*;
import com.clubfactory.platform.scheduler.dal.enums.*;
import com.clubfactory.platform.scheduler.dal.po.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author xiejiajun
 */
@Slf4j
@Service("commonService")
public class CommonService implements ICommonService {


    @Value("${db.conn.test.timeout}")
    private Integer connTestTimeout;


    @Autowired
    private TaskService taskService;

    @Autowired
    private MachineService machineService;

    @Autowired
    private JobOnlineService jobOnlineService;

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClusterService clusterService;


    @Autowired
    private ScriptService scriptService;

    @Autowired
    private LogMapService logMapService;

    @Autowired
    private DsStateService dsStateService;

    @Autowired
    private CollectDbService collectDbService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private MacroVarService macroVarService;

    @Autowired
    private JobTypeService jobTypeService;

    private AESUtils aesUtils;

    @Override
    @PostConstruct
    public void init() {
        if (connTestTimeout == null) {
            connTestTimeout = 5;
        }
        this.aesUtils = new AESUtils();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskVO getTaskInstance(long taskId) {
        Task task = new Task();
        task.setId(taskId);
        return taskService.get(task);
    }

    @Override
    public Integer getWorkerSlots(String ip) {
        return machineService.countSlotsByIp(ip);
    }

    @Override
    public List<SlotMap> getWorkerSlotMap(String ip) {
        return machineService.listSlotMapByIp(ip);
    }

    @Override
    public void updateTaskInfo(Task task) {
        taskService.updateTaskInfo(task);
    }

    @Override
    public void taskStateRunning(Long taskId) {
        taskService.taskStateRunning(taskId);
    }

    @Override
    public void changeMachineStatus(Machine machine) {
        machineService.changeServerStatus(machine);
    }

    @Override
    public String getUsernameById(Long userId) {
        Assert.notNull(userId, "用户ID");
        String username = userService.getUserAlias(userId);
        if (StringUtils.isBlank(username)) {
            username = "unnamed";
        }
        return username;
    }

    @Override
    public JobOnline getParamsByJobId(Long jobId) {
        Assert.notNull(jobId);
        JobOnline jobOnline = jobOnlineService.getByJobId(jobId);
        Assert.notNull(jobOnline, String.format("JobId【%s】的Job信息", jobId));
        return jobOnline;
    }

    @Override
    public Job getParamsByIdFromJob(Long jobId) {
        Assert.notNull(jobId);
        Job job = jobService.getByJobId(jobId);
        Assert.notNull(job, String.format("JobId【%s】的Job信息", jobId));
        return job;
    }

    @Override
    public Cluster getClusterInfoById(Long clusterId) {
        Assert.notNull(clusterId, "集群信息");
        return clusterService.getById(clusterId);
    }

    @Override
    public ScriptVO getScriptInfoById(Long scriptId) {
        Assert.notNull(scriptId, "脚本ID");
        return scriptService.getScriptInfoById(scriptId);
    }

    @Override
    public int saveLogMap(LogMap logMap) {
        return logMapService.save(logMap);
    }

    @Override
    public void refreshDBConnState(String workerIp) {
        Assert.notNull(workerIp, "调度机IP");
        Machine machine = new Machine();
        machine.setIsDeleted(false);
        machine.setIp(workerIp);
        machine.setType(MachineTypeEnum.WORKER);
        List<String> featureList = machineService.list(machine)
                .stream()
                .map(Machine::getFunctions)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(featureList)) {
            log.warn("暂无活动的Worker");
            return;
        }
        boolean hasEtlWorker = featureList.stream().anyMatch(feature -> feature != null
                && (feature.startsWith(JobCategoryEnum.COLLECT.name()) || feature.startsWith(JobCategoryEnum.REFLUE.name())));
        if (!hasEtlWorker) {
            log.warn("暂无采集/回流类型的Worker");
            return;
        }
        this.updateDBConnState(workerIp);
    }

    @Override
    public List<SysConfigVO> listValidConf(String host, ConfigType configType) {
        return sysConfigService.listValidConf(host, configType);
    }

    @Override
    public List<TaskVO> listRunningTasksByHost(String host) {
        return taskService.listRunningTaskByHost(host);
    }

    @Override
    public List<MacroVarVO> listGlobalMacroVars() {

        return macroVarService.listGlobalMacroVar();
    }

    @Override
    public JobType getJobType(Long jobTypeId) {
        return jobTypeService.get(jobTypeId);
    }

    @Override
    public JobType getJobTypeByName(String pluginName, JobCategoryEnum category) {
        return jobTypeService.getByName(pluginName, category.name());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateDBConnState(String workerIp) {
        CollectDb collectDb = new CollectDb();
        collectDb.setIsDeleted(false);
        collectDb.setStatus(CommonStatus.ENABLED);
        List<CollectDbVO> dataSourceList = collectDbService.list(collectDb)
                .stream()
                .filter(po -> (po.getDsType() == DbType.MYSQL || po.getDsType() == DbType.POSTGRESQL))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dataSourceList)) {
            log.error("暂无需要测试连通性的数据源");
            return;
        }

        List<DsState> dsStates = Lists.newArrayList();
        for (CollectDb dataSource : dataSourceList) {
            String errMsg = "";
            boolean connSuccess = false;
            try {
                String password = this.decrypt(dataSource.getEncryptPwd(), dataSource.getPwdKey());
                connSuccess = DBConnTestUtil.isConnSuccess(dataSource.getDsUrl(), dataSource.getDsUser(),
                        password, dataSource.getDsType(), connTestTimeout);
            } catch (Exception e) {
                errMsg = e.getMessage();
            }
            String dbHost = Optional.ofNullable(dataSource.getDbHost()).orElse("");
            String dbPort = Optional.ofNullable(dataSource.getDbPort()).orElse("");
            String dbUser = Optional.ofNullable(dataSource.getDsUser()).orElse("");
            DbType dbType = Optional.ofNullable(dataSource.getDsType()).orElse(DbType.UN_KNOW);
            Long dsId = Optional.ofNullable(dataSource.getId()).orElse(-1L);
            DsState dsState = DsState.builder()
                    .connSuccess(connSuccess)
                    .failedReason(errMsg)
                    .createTime(new Date())
                    .dbHost(dbHost)
                    .dbPort(dbPort)
                    .dbType(dbType)
                    .dbUser(dbUser)
                    .dsId(dsId)
                    .workerIp(workerIp)
                    .build();
            dsStates.add(dsState);
        }
        if (CollectionUtils.isEmpty(dsStates)) {
            log.warn("暂无测试完成的数据源");
            return;
        }
        dsStateService.clearOldState(workerIp);
        dsStateService.saveBatch(dsStates);
        log.info("更新数据源连接信息成功...");
    }


    /**
     * 数据解密
     *
     * @param cipherText
     * @param pwdKey
     * @return
     */
    private String decrypt(String cipherText, String pwdKey) {
        try {
            return this.aesUtils.decrypt(cipherText, pwdKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException(String.format("数据源密码解密失败: %s", e.getMessage()));
        }
    }


}
