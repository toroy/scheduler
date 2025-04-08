package com.clubfactory.platform.scheduler.spi.plugin;

import com.clubfactory.platform.scheduler.common.utils.placeholder.MacroVarConvertUtils;
import com.clubfactory.platform.scheduler.common.utils.ParamUtils;
import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.spi.constants.JobConf;
import com.clubfactory.platform.scheduler.core.utils.*;
import com.clubfactory.platform.scheduler.core.vo.MacroVarVO;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.clubfactory.platform.scheduler.spi.exception.TaskException;
import com.clubfactory.platform.scheduler.spi.launcher.Launcher;
import com.clubfactory.platform.scheduler.spi.launcher.ShellLauncher;
import com.clubfactory.platform.scheduler.spi.param.AbstractParameter;
import com.clubfactory.platform.scheduler.spi.param.IParameters;
import com.clubfactory.platform.scheduler.spi.utils.JobParameterUtils;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.clubfactory.platform.scheduler.common.Constants.*;

/**
 * Task抽象
 * @author xiejiajun
 */
public abstract class AbstractTask implements JobConf {

    /**
     * task描述信息
     **/
    @Getter
    protected TaskVO taskInfo;

    /**
     * 日志打印Logger
     */
    protected Logger logger;

    /**
     * task启动脚本名称
     */
    protected final String taskName;

    /**
     * 任务执行目录
     */
    protected final String execDir;

    /**
     * 任务取消标志
     */
    protected volatile boolean cancel = false;

    /**
     * 任务退出码
     */
    protected volatile int exitStatusCode = -1;

    /**
     * 任务全局宏变量
     */
    private final Map<String, String> globalMacroVars;

    /**
     * 脱敏全局宏变量
     */
    private final Map<String, String> maskGlobalMacroVars;

    /**
     * 任务私有系统参数
     */
    protected final Map<String, String> taskParams;

    /**
     * 作业状态跟踪(主要用于保存pid)
     */
    protected transient StateTracker stateTracker;

    /**
     * 作业启动器
     */
    protected transient Launcher taskLauncher;

    /**
     * 指定时间宏变量基准的时区
     */
    private String timeZoneId;

    /**
     * Task执行参数
     */
    private IParameters executeParameter;

    public AbstractTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        this.taskInfo = taskInfo;
        this.logger = logger;
        this.taskName = String.format("task_%s", taskInfo.getId());
        this.execDir = taskInfo.getExecuteDir();
        this.stateTracker = stateTracker;
        this.globalMacroVars = Maps.newHashMap();
        this.maskGlobalMacroVars = Maps.newHashMap();
        this.taskParams = Maps.newHashMap();
    }

    /**
     * 获取参数类型
     * @return
     */
    protected abstract Class<? extends IParameters> getParameterType();

    /**
     * 指定脚本文件扩展名
     * @return
     */
    protected abstract String fileExt();

    /**
     * 创建任务启动器
     */
    protected void createLauncher() {
        this.taskLauncher = new ShellLauncher(this, this.logger, this.stateTracker);
    }

    /**
     * 自动参数类型转换
     * @param <T>
     * @return
     */
    @Deprecated
    protected <T extends IParameters> T getParameter(Class<T> tClass) {
        return (T) this.executeParameter;
    }

    /**
     * 自动参数类型转换
     * @param <T>
     * @return
     */
    protected <T extends IParameters> T getParameter() {
        return (T) this.executeParameter;
    }

    /**
     * 参数预处理
     */
    private void preProcessParameter() {
        if (this.executeParameter instanceof AbstractParameter) {
            ((AbstractParameter) this.executeParameter).setStartFile(taskInfo.getFileName());
            ((AbstractParameter) this.executeParameter).setTaskContext(taskInfo);
            if (this.isEtlJob()) {
                JobParameterUtils.normalizedEtlParameter((AbstractParameter) this.executeParameter);
            }
        }
        this.executeParameter.normalizedParameter();
    }

    /**
     * 解析任务入参
     */
    protected void parseTaskParameter() throws TaskException {
        String taskType = taskInfo.getJobTypeInfo().getPluginName().toLowerCase();
        logger.info("{} task params {}", taskType, taskInfo.getParams());
        this.executeParameter  = JSONUtils.parseObject(taskInfo.getParams(), this.getParameterType());
        if (this.executeParameter == null ) {
            throw new TaskException(String.format("%s task params is null", taskType));
        }
        this.generateTaskExecFileName();
        this.preProcessParameter();
        if (!this.executeParameter.checkParameters()){
            throw new TaskException(String.format("%s task params is not valid", taskType));
        }
    }

    /**
     * 任务执行前置操作
     * @throws Exception
     */
    public void preHandle() throws Exception {
        this.parseTaskParameter();
        this.downloadScriptToExecDir();
        this.parseGlobalConfigs();
        this.parseTaskConfigs();
        this.createLauncher();
        this.initTimeZone();
    }

    /**
     * 初始化时区
     */
    protected void initTimeZone() {
        if (this.getBoolean(USE_BEIJING_TIMEZONE, DEFAULT_USE_BEIJING_TIMEZONE)) {
            this.timeZoneId = BEIJING_TIMEZONE_ID;
        } else {
            String bizTimeZoneId = this.getString(BIZ_TIMEZONE_ID);
            this.timeZoneId = StringUtils.isNotBlank(bizTimeZoneId) ? bizTimeZoneId : timeZoneId;
        }
    }

    /**
     * 作业启动逻辑
     * @throws Exception
     */
    public void handle() throws Exception {
        try {
            exitStatusCode = this.taskLauncher.launch(buildCommand());
        } catch (Exception e) {
            logger.error("{} task failure", taskInfo.getJobTypeInfo().getPluginName(), e);
            exitStatusCode = Constants.EXIT_CODE_FAILURE;
        }
    }

    /**
     * 作业执行结束清理工作
     */
    public void postHandle() throws Exception {
        switch (getExitStatusCode()) {
            case Constants.EXIT_CODE_SUCCESS:
            case Constants.EXIT_CODE_KILL:
            case Constants.EXIT_CODE_DATA_ILLEGAL:
                break;
            default:
                setExitStatusCode(Constants.EXIT_CODE_FAILURE);
        }
        this.taskLauncher.destroy();
    }

    /**
     *  从DFS下载任务运行需要的依赖文件
     */
    protected void downloadScriptToExecDir() throws TaskException {
        String taskScriptName = taskInfo.getRawScriptName();
        Integer version = taskInfo.getVersion();
        String execDir = taskInfo.getExecuteDir();
        logger.info("开始下载Task {} 资源...", taskInfo.getId());
        String scriptBasePath = taskInfo.getScriptBasePath();
        String taskExecFile = taskInfo.getFileName();
        if (StringUtils.isNotBlank(taskScriptName) && StringUtils.isNotBlank(taskExecFile)) {
            File resFile = new File(execDir, taskExecFile);
            if (!resFile.exists()) {
                try {
                    // ${dfs_base_dir}/${scriptUserName}_${scriptUserId}/resources/${scriptName}_${scriptVersion}
                    // ${scriptName}_${scriptVersion}为DFS上文件存储名称
                    String fileName = String.format("%s_%s", taskScriptName, version);
                    String dfsFilePath = DFSUtils.getDfsFilePath(scriptBasePath, fileName);
                    logger.info("get resource file from dfs :{}, to path {}/{}", dfsFilePath, execDir, taskExecFile);
                    DFSUtils.getInstance()
                            .copyDfsToLocal(dfsFilePath, execDir + File.separator + taskExecFile, false, true);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new TaskException(e.getMessage());
                }
            } else {
                logger.info("file : {} exists ", resFile.getName());
            }
        }

    }

    /**
     * 生成用于执行的作业文件名称
     * @return
     */
    protected String generateTaskExecFileName() {
        String fileExt = fileExt();
        String taskExecFile = String.format("task%s_%s", taskInfo.getId(), System.currentTimeMillis());
        if (StringUtils.isNotBlank(fileExt)) {
            taskExecFile = String.format("%s.%s", taskExecFile, fileExt);
        }
        taskInfo.setRawScriptName(taskInfo.getFileName());
        taskInfo.setFileName(taskExecFile);
        return taskExecFile;
    }

    /**
     * 解析全局配置
     */
    private void parseGlobalConfigs() {
        try {
            List<MacroVarVO> globalParams = this.taskInfo.getGlobalParams();
            if (CollectionUtils.isNotEmpty(globalParams)) {
                globalParams.forEach(po -> {
                    if (po.getIsMask() != null && po.getIsMask()) {
                        this.maskGlobalMacroVars.put(po.getVarName(), po.getVarExpr());
                    } else {
                        this.globalMacroVars.put(po.getVarName(), po.getVarExpr());
                    }
                });
            }
        } catch (Exception e) {
            logger.error("parse global params error", e);
        }
    }

    /**
     * 解析task局部配置
     */
    protected void parseTaskConfigs() {
        try {
            Map<String, String> taskParamList = JSONUtils.toMap(taskInfo.getParams());
            if (MapUtils.isNotEmpty(taskParamList)) {
                this.taskParams.putAll(taskParamList);
            }
        } catch (Exception e) {
            logger.error("parse task params error", e);
        }
    }

    /**
     * 获取合并后的参数
     *
     * @return
     */
    protected Map<String, String> getAllParams() {
        Map<String, String> allParams = Maps.newHashMap();
        allParams.putAll(this.globalMacroVars);
        allParams.putAll(this.taskParams);
        return allParams;
    }

    /**
     * 分两阶段构建命令，第一阶段不处理敏感变量，方便将命令打印到日志
     * 第二阶段处理完敏感变量后作为最终启动命令
     * @return
     * @throws Exception
     */
    protected String buildCommand() throws Exception {
        List<String> commandList = this.buildCommandList();
        if (commandList == null) {
            throw new TaskException("任务启动命令构建方法未实现，该任务类型暂不可用");
        }
        String command = this.convertVariable(String.join(" ", commandList));
        logger.info("\n\nTask launch command :\n{}\n\n", genCommandLog(command));

        command = this.convertMaskVariable(command);
        return command;
    }

    /**
     * buildCommand / buildCommandList至少二选一进行重写
     * 创建命令列表
     *
     * @return
     * @throws Exception
     */
    protected abstract List<String> buildCommandList() throws Exception;

    /**
     * 取消任务
     * @param status
     * @throws Exception
     */
    public void cancelApplication(boolean status) throws Exception {
        this.cancel = status;
        try {
            this.taskLauncher.cancel();
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    /**
     * 获取任务退出码
     */
    protected int getExitStatusCode() {
        return exitStatusCode;
    }

    /**
     * 设置退出码
     * @param exitStatusCode
     */
    public void setExitStatusCode(int exitStatusCode) {
        this.exitStatusCode = exitStatusCode;
    }

    /**
     * Streaming任务标记
     * @return
     */
    public boolean isStreamTask() {
        return false;
    }

    /**
     * ETL 任务标记
     * @return
     */
    public boolean isEtlJob() {
        return false;
    }

    /**
     * 作业退出状态转换
     * @return
     */
    public TaskStatusEnum getExitStatus() {
        TaskStatusEnum status;
        switch (getExitStatusCode()) {
            case Constants.EXIT_CODE_SUCCESS:
                if (isStreamTask()) {
                    status = TaskStatusEnum.RUNNING;
                } else {
                    status = TaskStatusEnum.SUCCESS;
                }
                break;
            case Constants.EXIT_CODE_KILL:
                status = TaskStatusEnum.KILLED;
                break;
            case Constants.EXIT_CODE_DATA_ILLEGAL:
                status = TaskStatusEnum.DATA_FAILED;
                break;
            default:
                status = TaskStatusEnum.FAILED;
                break;
        }
        return status;
    }

    /**
     * @param exitStatusCode
     * @return
     */
    public int ensureState(int exitStatusCode) {
        return exitStatusCode;
    }

    /**
     * 获取实例时间
     * @return
     */
    public Date instanceTime() {
        boolean useStartTime = this.getBoolean(TIME_BASIC_USE_START_TIME, DEFAULT_TIME_BASIC_USE_START_TIME);
        if (isStreamTask() || useStartTime){
            return taskInfo.getCreateTime();
        }
        return taskInfo.getTaskTime();
    }

    /**
     * 变量转换
     * @param taskScript
     * @return
     */
    protected String convertVariable(String taskScript) {
        Map<String,String> paramsMap = ParamUtils.convert(this.getAllParams(), this.instanceTime(), this.timeZoneId);
        if (MapUtils.isNotEmpty(paramsMap) && StringUtils.isNotBlank(taskScript)) {
            taskScript = MacroVarConvertUtils.convertParameterPlaceholders(taskScript, paramsMap, this.timeZoneId);
        }
        return taskScript;
    }

    /**
     * 第二阶段变量转换：补全需要脱敏的变量
     *
     * @param taskScript
     * @return
     */
    public String convertMaskVariable(String taskScript) {
        if (MapUtils.isNotEmpty(this.maskGlobalMacroVars) && StringUtils.isNotBlank(taskScript)) {
            taskScript = MacroVarConvertUtils.convertParameterPlaceholders(taskScript, this.maskGlobalMacroVars, this.timeZoneId);
        }
        return taskScript;
    }

    /**
     * 生成命令日志
     * @return
     */
    private String genCommandLog(String command){
        String outLog = command;
        if (MapUtils.isEmpty(this.maskGlobalMacroVars)){
            return outLog;
        }
        for (String maskKey: this.maskGlobalMacroVars.keySet()) {
            outLog = outLog.replaceAll(String.format("\\$\\{%s\\}",maskKey), "*********");
        }
        return outLog;
    }

    @Override
    public Map<String,String> getAllJobConf() {
        return this.taskInfo.getJobConf();
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(this, object, false);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }

    public interface StateTracker {
        /**
         * 更新任务状态
         * @param taskInfo
         * @return
         * @throws Exception
         */
        boolean update(TaskVO taskInfo) throws Exception;
    }
}
