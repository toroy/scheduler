//package com.clubfactory.platform.scheduler.spi.launcher;
//
//import com.amazonaws.AmazonClientException;
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
//import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
//import com.amazonaws.services.elasticmapreduce.model.*;
//import com.clubfactory.platform.scheduler.common.Constants;
//import com.clubfactory.platform.scheduler.core.exception.MultiActiveStepException;
//import com.clubfactory.platform.scheduler.core.model.AppInfo;
//import com.clubfactory.platform.scheduler.core.utils.DFSUtils;
//import com.clubfactory.platform.scheduler.core.utils.PropertyUtils;
//import com.clubfactory.platform.scheduler.core.utils.YarnUtils;
//import com.clubfactory.platform.scheduler.core.vo.TaskVO;
//import com.clubfactory.platform.scheduler.spi.exception.TaskException;
//import com.clubfactory.platform.scheduler.spi.plugin.AbstractEmrTask;
//import com.clubfactory.platform.scheduler.spi.plugin.AbstractTask;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Sets;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang.StringUtils;
//import org.apache.http.HttpStatus;
//import org.slf4j.Logger;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.Charset;
//import java.util.List;
//import java.util.Set;
//
//import static com.clubfactory.platform.scheduler.spi.constants.AwsEmrConstants.*;
//import static com.clubfactory.platform.scheduler.spi.constants.AwsEmrConstants.DEFAULT_AWS_EMR_JOB_FLOW_ROLE;
//import static com.clubfactory.platform.scheduler.common.Constants.DEFAULT_SCHEDULER_SCRIPT_BASE_SCHEME;
//
//
///**
// * EMR临时集群启动器
// * @author xiejiajun
// */
//public class TransientEmrLauncher implements Launcher {
//
//    private final String SCRIPT_RUNNER = "s3://%s.elasticmapreduce/libs/script-runner/script-runner.jar";
//
//    private final AbstractEmrTask emrTask;
//    private final Logger taskLogger;
//    private final AbstractTask.StateTracker stateTracker;
//    private AmazonElasticMapReduce emr;
//    private String clusterId;
//
//
//    public TransientEmrLauncher(AbstractEmrTask emrTask, Logger logger, AbstractTask.StateTracker stateTracker) {
//        this.emrTask = emrTask;
//        this.taskLogger = logger;
//        this.stateTracker = stateTracker;
//
//    }
//
//
//
//    @Override
//    public int launch(String execCommand) throws TaskException {
//        try {
//            this.initEmrClient();
//            clusterId = createCluster(execCommand);
//            taskLogger.info("create emr cluster request send success");
//            if (this.emrTask.isStreamTask()) {
//                return this.ensureApplicationState() ? Constants.EXIT_CODE_SUCCESS : Constants.EXIT_CODE_FAILURE;
//            }
//            return this.waitTerminate() ? Constants.EXIT_CODE_SUCCESS : Constants.EXIT_CODE_FAILURE;
//        } catch (Exception e) {
//            throw new TaskException(e.getMessage());
//        }
//
//    }
//
//
//    /**
//     * 初始化EMR Client
//     *
//     * @return
//     */
//    private void initEmrClient() {
//        AWSCredentials credentialsProfile;
//        try {
//            final DefaultAWSCredentialsProviderChain provider = new DefaultAWSCredentialsProviderChain();
//            credentialsProfile = provider.getCredentials();
//        } catch (Exception e) {
//            throw new AmazonClientException("EMR身份凭据创建失败", e);
//        }
//
//        String awsRegion = PropertyUtils.getString(AWS_EMR_REGION, DEFAULT_AWS_EMR_REGION);
//        this.emr = AmazonElasticMapReduceClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentialsProfile))
//                .withRegion(Regions.fromName(awsRegion))
//                .build();
//    }
//
//
//    /**
//     * 创建临时集群
//     * @return
//     */
//    private String createCluster(String execCommand) throws Exception {
//        List<Configuration> configurations = emrTask.configurations();
//        List<Application> applications = emrTask.applications();
//        String stepScript = this.stepScript(execCommand);
//        String stepType = emrTask.stepType();
//        String username = emrTask.getTaskInfo().getTenant();
//        String taskName = emrTask.getTaskInfo().getName();
//
//
//        String awsRegion = emrTask.getString(AWS_EMR_REGION, DEFAULT_AWS_EMR_REGION);
//        HadoopJarStepConfig stepConfig = new HadoopJarStepConfig()
//                .withJar(String.format(SCRIPT_RUNNER, awsRegion))
//                // 脚本的s3绝对路径
//                .withArgs(stepScript);
//
//        StepConfig emrStep = new StepConfig()
//                .withName(String.format("%s-%s", username, taskName))
//                .withActionOnFailure(ActionOnFailure.TERMINATE_CLUSTER)
//                .withHadoopJarStep(stepConfig);
//
//        Tag clusterTag = new Tag(emrTask.getString(AWS_EMR_TAG_KEY, DEFAULT_AWS_EMR_TAG_KEY), username);
//
//        RunJobFlowRequest request = new RunJobFlowRequest()
//                .withName(String.format("%s-%s-%s", stepType, username, taskName))
//                .withReleaseLabel(emrTask.getString(AWS_EMR_RELEASE_LABLE, DEFAULT_AWS_EMR_RELEASE_LABLE))
//                .withApplications(applications)
//                .withLogUri(emrTask.getString(AWS_EMR_LOG_URI, DEFAULT_AWS_EMR_LOG_URI))
//                .withServiceRole(emrTask.getString(AWS_EMR_SERVICE_ROLE, DEFAULT_AWS_EMR_SERVICE_ROLE))
//                .withAutoScalingRole(emrTask.getString(AWS_EMR_AUTO_SCALING_ROLE, DEFAULT_AWS_EMR_AUTO_SCALING_ROLE))
//                .withConfigurations(configurations)
//                .withSteps(emrStep)
//                .withEbsRootVolumeSize(emrTask.getInt(AWS_EMR_ROOT_VOLUME_SIZE, DEFAULT_AWS_EMR_ROOT_VOLUME_SIZE))
//                .withJobFlowRole(emrTask.getString(AWS_EMR_JOB_FLOW_ROLE, DEFAULT_AWS_EMR_JOB_FLOW_ROLE))
//                .withTags(clusterTag)
//                .withInstances(buildInstances());
//        request.setVisibleToAllUsers(true);
//
//        String clusterId = emr.runJobFlow(request).getJobFlowId();
//        this.emrTask.getTaskInfo().setEmrClusterId(clusterId);
//        return clusterId;
//    }
//
//    /**
//     * stepScript s3路径
//     * @return
//     */
//    private String stepScript(String execCommand) throws Exception {
//
//        TaskVO taskInfo = this.emrTask.getTaskInfo();
//        String stepScriptPath = getStepJobScriptPath(taskInfo);
//        boolean isDebugEnable = this.emrTask.getBoolean(AWS_EMR_TASK_DEBUG_ENABLE,DEFAULT_AWS_EMR_TASK_DEBUG_ENABLE);
//
//        String launchFile = String.format("%s/%s.sh", taskInfo.getExecuteDir(), taskInfo.getId());
//        taskLogger.info("generate emr step script {} with task {}", launchFile, taskInfo.getName());
//        StringBuilder sb = new StringBuilder();
//        sb.append("#!/bin/bash\n");
//        sb.append("source /etc/profile\n");
//        sb.append("source ~/.bashrc\n");
//
//        sb.append("\n");
//        sb.append("base_dir=$(cd `dirname $0`; pwd)\n");
//        sb.append("cd $base_dir\n");
//
//        sb.append("\n\n");
//        if (StringUtils.isNotBlank(stepScriptPath)) {
//            sb.append("aws s3 cp ").append(stepScriptPath).append(" ").append(taskInfo.getFileName()).append("\n\n");
//        }
//        sb.append(execCommand).append("\n\n");
//
//        FileUtils.writeStringToFile(new File(launchFile), sb.toString(), Charset.forName("UTF-8"));
//        if (isDebugEnable) {
//            taskLogger.info("write command to file {} success, command is {}", launchFile, sb.toString());
//        }
//        taskLogger.info("task step config complete,start build emr cluster");
//
//        String stepScriptRuntimePath = stepScriptPath();
//
//        DFSUtils.getInstance().copyLocalToDfs(launchFile, stepScriptRuntimePath, false, true);
//        if (isDebugEnable) {
//            taskLogger.info("upload step script to {} success", stepScriptRuntimePath);
//        }
//        return stepScriptRuntimePath;
//    }
//
//    /**
//     * step脚本路径
//     *
//     * @return
//     */
//    private String stepScriptPath() {
//        String stepExecBaseScheme = this.emrTask.getString(Constants.SCHEDULER_SCRIPT_BASE_SCHEME,
//                DEFAULT_SCHEDULER_SCRIPT_BASE_SCHEME);
//        String stepExecBaseDir = this.emrTask.getString(AWS_TEMP_CLUSTER_SCRIPT_BASE_DIR
//                , DEFAULT_AWS_TEMP_CLUSTER_SCRIPT_BASE_DIR);
//        if (stepExecBaseDir != null && stepExecBaseDir.startsWith("/")) {
//            stepExecBaseDir = StringUtils.substringAfter(stepExecBaseDir, "/");
//        }
//        if (stepExecBaseDir != null && stepExecBaseDir.endsWith("/")) {
//            stepExecBaseDir = StringUtils.substringBeforeLast(stepExecBaseDir, "/");
//        }
//        return String.format("%s/%s/%s_%s.sh", stepExecBaseScheme, stepExecBaseDir,
//                this.emrTask.getTaskInfo().getName(), this.emrTask.getTaskInfo().getId());
//    }
//
//    /**
//     * 获取任务脚本模版路径
//     *
//     * @param taskInfo
//     * @return
//     */
//    private String getStepJobScriptPath(TaskVO taskInfo) {
//        String taskScriptName = taskInfo.getRawScriptName();
//        Integer version = taskInfo.getVersion();
//        String fileName = String.format("%s_%s", taskScriptName, version);
//        String scriptBasePath = taskInfo.getScriptBasePath();
//        return DFSUtils.getDfsFileFullPath(scriptBasePath, fileName);
//    }
//
//
//    /**
//     * 配置EMR MASTER CORE TASK三个实例组
//     *
//     * @return
//     */
//    private JobFlowInstancesConfig buildInstances() {
//        InstanceGroupConfig masterInstance = new InstanceGroupConfig()
//                .withInstanceCount(1)
//                .withEbsConfiguration(new EbsConfiguration()
//                        .withEbsBlockDeviceConfigs(new EbsBlockDeviceConfig()
//                                .withVolumeSpecification(new VolumeSpecification()
//                                        .withSizeInGB(30)
//                                        .withVolumeType("gp2"))
//                                .withVolumesPerInstance(1)))
//                .withInstanceRole(InstanceRoleType.MASTER)
//                .withInstanceType(emrTask.getString(AWS_EMR_MASTER_INSTANCE_TYPE, DEFAULT_AWS_EMR_MASTER_INSTANCE_TYPE))
//                .withName("Master Group");
//
//
//        InstanceGroupConfig coreInstance = new InstanceGroupConfig()
//                .withInstanceCount(emrTask.getInt(AWS_EMR_CORE_INSTANCE_COUNT, DEFAULT_AWS_EMR_CORE_INSTANCE_COUNT))
//                .withEbsConfiguration(new EbsConfiguration()
//                        .withEbsBlockDeviceConfigs(new EbsBlockDeviceConfig()
//                                .withVolumeSpecification(new VolumeSpecification()
//                                        .withSizeInGB(emrTask.getInt(AWS_EMR_CORE_INSTANCE_PER_DISK_SIZE, DEFAULT_AWS_EMR_CORE_INSTANCE_PER_DISK_SIZE))
//                                        .withVolumeType("gp2"))
//                                .withVolumesPerInstance(emrTask.getInt(AWS_EMR_CORE_VOLUMES_PER_INSTANCE, DEFAULT_AWS_EMR_CORE_VOLUMES_PER_INSTANCE)))
//                        .withEbsOptimized(true)
//                ).withInstanceRole(InstanceRoleType.CORE)
//                .withInstanceType(emrTask.getString(AWS_EMR_CORE_INSTANCE_TYPE, DEFAULT_AWS_EMR_CORE_INSTANCE_TYPE))
//                .withMarket(emrTask.getString(AWS_EMR_WORKER_INSTANCE_MARKET_TYPE, DEFAULT_AWS_EMR_WORKER_INSTANCE_MARKET_TYPE))
//                .withName("Core Group");
//
//
//        InstanceGroupConfig taskInstance = new InstanceGroupConfig()
//                .withInstanceCount(1)
//                .withMarket(MarketType.SPOT)
//                .withInstanceRole(InstanceRoleType.TASK)
//                .withInstanceType(emrTask.getString(AWS_EMR_TASK_INSTANCE_TYPE, DEFAULT_AWS_EMR_TASK_INSTANCE_TYPE))
//                .withAutoScalingPolicy(buildAutoScalingPolicy())
//                .withName("Task Group");
//
//        List<InstanceGroupConfig> instanceGroupConfigs = Lists.newLinkedList();
//        instanceGroupConfigs.add(masterInstance);
//        instanceGroupConfigs.add(coreInstance);
//        if (emrTask.getBoolean(AWS_EMR_TASK_AUTO_SCALING_ENABLE, DEFAULT_AWS_EMR_TASK_AUTO_SCALING_ENABLE)) {
//            instanceGroupConfigs.add(taskInstance);
//        }
//
//        // 默认keep alive为false（即auto-terminate为true)
//        boolean emrKeepAliveEnable = this.emrTask.getBoolean(AWS_EMR_KEEP_ALIVE_ENABLE, DEFAULT_AWS_EMR_KEEP_ALIVE_ENABLE);
//        return new JobFlowInstancesConfig()
//                .withEc2SubnetId(emrTask.getString(AWS_EMR_EC2_SUBNET_ID, DEFAULT_AWS_EMR_EC2_SUBNET_ID))
//                .withEmrManagedMasterSecurityGroup(emrTask.getString(AWS_EMR_MASTER_SECURITY_GROUP, DEFAULT_AWS_EMR_MASTER_SECURITY_GROUP))
//                .withEmrManagedSlaveSecurityGroup(emrTask.getString(AWS_EMR_CORE_SECURITY_GROUP, DEFAULT_AWS_EMR_CORE_SECURITY_GROUP))
//                .withEc2KeyName(emrTask.getString(AWS_EMR_EC2_KEY_NAME, DEFAULT_AWS_EMR_EC2_KEY_NAME))
//                // 自动终止集群
//                .withKeepJobFlowAliveWhenNoSteps(emrKeepAliveEnable)
//                .withInstanceGroups(instanceGroupConfigs);
//    }
//
//
//    /**
//     * emr集群自动扩展策略
//     *
//     * @return AutoScalingPolicy
//     */
//    private AutoScalingPolicy buildAutoScalingPolicy() {
//
//        ScalingRule scaleOut = new ScalingRule()
//                .withName("scale-out")
//                .withDescription("scale out rule")
//                .withAction(new ScalingAction()
//                        .withSimpleScalingPolicyConfiguration(new SimpleScalingPolicyConfiguration()
//                                .withScalingAdjustment(1)
//                                .withAdjustmentType("CHANGE_IN_CAPACITY")
//                                .withCoolDown(300)))
//                .withTrigger(new ScalingTrigger()
//                        .withCloudWatchAlarmDefinition(new CloudWatchAlarmDefinition()
//                                .withComparisonOperator("LESS_THAN")
//                                .withEvaluationPeriods(1)
//                                .withPeriod(300)
//                                .withMetricName("YARNMemoryAvailablePercentage")
//                                .withNamespace("AWS/ElasticMapReduce")
//                                // 资源可用百分比低于15%扩容
//                                .withThreshold(15d)
//                                .withStatistic("AVERAGE")
//                                .withUnit("PERCENT")
//                                .withDimensions(new MetricDimension()
//                                        .withKey("JobFlowId")
//                                        .withValue("${emr.clusterId}"))));
//
//        ScalingRule scaleIn = new ScalingRule()
//                .withName("scale-in")
//                .withDescription("scale in rule")
//                .withAction(new ScalingAction()
//                        .withSimpleScalingPolicyConfiguration(new SimpleScalingPolicyConfiguration()
//                                .withScalingAdjustment(-1)
//                                .withAdjustmentType("CHANGE_IN_CAPACITY")
//                                .withCoolDown(300)))
//                .withTrigger(new ScalingTrigger()
//                        .withCloudWatchAlarmDefinition(new CloudWatchAlarmDefinition()
//                                .withComparisonOperator("GREATER_THAN")
//                                .withEvaluationPeriods(1)
//                                .withPeriod(300)
//                                .withMetricName("YARNMemoryAvailablePercentage")
//                                .withNamespace("AWS/ElasticMapReduce")
//                                // 资源可用百分比高于75%缩容
//                                .withThreshold(75d)
//                                .withStatistic("AVERAGE")
//                                .withUnit("PERCENT")
//                                .withDimensions(new MetricDimension()
//                                        .withKey("JobFlowId")
//                                        .withValue("${emr.clusterId}"))));
//        return new AutoScalingPolicy()
//                .withConstraints(new ScalingConstraints()
//                        .withMinCapacity(emrTask.getInt(AWS_EMR_CORE_INSTANCE_MIN_CAPACITY, DEFAULT_AWS_EMR_CORE_INSTANCE_MIN_CAPACITY))
//                        .withMaxCapacity(emrTask.getInt(AWS_EMR_CORE_INSTANCE_MAX_CAPACITY, DEFAULT_AWS_EMR_CORE_INSTANCE_MAX_CAPACITY))
//                ).withRules(scaleOut, scaleIn);
//    }
//
//
//    /**
//     * 等待退出
//     *
//     * @return 作业完成退出：true ， else false
//     */
//    protected boolean waitTerminate() {
//        int clusterTimeout = this.emrTask.getInt(AWS_EMR_CREATE_WAITING_TIMEOUT, DEFAULT_AWS_EMR_CREATE_WAITING_TIMEOUT);
//        long deadline = clusterTimeout * 60 * 1000 + System.currentTimeMillis();
//        Set<String> initStates = Sets.newHashSet("BOOTSTRAPPING", "STARTING");
//        Set<String> runningStates = Sets.newHashSet("WAITING", "RUNNING", "TERMINATING");
//        Set<String> failedStates = Sets.newHashSet("FAILED", "TERMINATED", "TERMINATED_WITH_ERRORS");
//        while (true) {
//            try {
//                DescribeClusterResult result = emr.describeCluster(new DescribeClusterRequest().withClusterId(clusterId));
//                Cluster cluster = result.getCluster();
//                ClusterStatus status = cluster.getStatus();
//                String state = status.getState();
//                String message = status.getStateChangeReason().getMessage();
//
//                taskLogger.info("emr cluster {}  current state is {}, cluster auto terminate is {}", clusterId, state,
//                        cluster.isAutoTerminate());
//                if (initStates.contains(state)) {
//                    if (System.currentTimeMillis() > deadline) {
//                        // 等待超时,手动发起终止请求
//                        this.terminateCluster();
//                    }
//                    Thread.sleep(30_000);
//                    continue;
//                }
//
//                if (runningStates.contains(state)) {
//                    Thread.sleep(30_000);
//                    continue;
//                }
//                if (ClusterState.fromValue(state) == ClusterState.TERMINATED && "Steps completed".equals(message)) {
//                    return true;
//                }
//
//                if (failedStates.contains(state)) {
//                    taskLogger.error(message);
//                    return false;
//                }
//
//            } catch (Exception e) {
//                taskLogger.error(e.getMessage());
//                try {
//                    Thread.sleep(30_000);
//                } catch (InterruptedException ignored) {
//                    taskLogger.error("wait temp emr cluster error");
//                }
//            }
//        }
//    }
//
//    /**
//     * 等待退出
//     * @return 作业完成退出：true ， else false
//     */
//    protected boolean ensureApplicationState() {
//        int clusterTimeout = this.emrTask.getInt(AWS_EMR_CREATE_WAITING_TIMEOUT, DEFAULT_AWS_EMR_CREATE_WAITING_TIMEOUT);
//        long deadline = clusterTimeout * 60 * 1000 + System.currentTimeMillis();
//        Set<String> initStates = Sets.newHashSet("BOOTSTRAPPING", "STARTING");
//        Set<String> runningStates = Sets.newHashSet("WAITING", "RUNNING");
//        Set<String> failedStates = Sets.newHashSet("FAILED", "TERMINATED_WITH_ERRORS","TERMINATED","TERMINATING");
//        while (true) {
//            try {
//                DescribeClusterResult result = emr.describeCluster(new DescribeClusterRequest().withClusterId(clusterId));
//                Cluster cluster = result.getCluster();
//                String state = cluster.getStatus().getState();
//
//                taskLogger.info("emr cluster {}  current state is {}", clusterId, state);
//                if (initStates.contains(state)) {
//                    if (System.currentTimeMillis() > deadline) {
//                        // 等待超时,手动发起终止请求
//                        this.terminateCluster();
//                    }
//                    Thread.sleep(30_000);
//                    continue;
//                }
//
//                if (runningStates.contains(state)) {
//                    String masterPublicDNS = this.getMasterDNS();
//                    if (StringUtils.isBlank(masterPublicDNS)) {
//                        taskLogger.error("fetch master public dns failed, close this cluster");
//                        this.terminateCluster();
//                        return false;
//                    } else {
//                        try {
//                            AppInfo appInfo = YarnUtils.getInstance().getTempClusterAppInfo(masterPublicDNS);
//                            if (appInfo != null) {
//                                // 任务提交成功，添加到监控
//                                this.emrTask.addAppMonitor(appInfo,masterPublicDNS);
//                                taskLogger.info("streaming task launched from cluster {} success, exit schedule",clusterId);
//                                return true;
//                            }
//                            Thread.sleep(30_000);
//                        } catch (MultiActiveStepException e) {
//                            taskLogger.error(e.getMessage());
//                            this.terminateCluster();
//                            return false;
//                        }
//                    }
//                }
//                if (failedStates.contains(state)) {
//                    taskLogger.error("streaming step submit failed");
//                    return false;
//                }
//            } catch (Exception e) {
//                try {
//                    Thread.sleep(30_000);
//                } catch (InterruptedException ignored) {
//                    taskLogger.error("wait temp emr cluster error");
//                }
//            }
//        }
//    }
//
//    /**
//     * 获取Master节点公网DNS
//     *
//     * @return
//     */
//    private String getMasterDNS() {
//        DescribeClusterResult result = emr.describeCluster(new DescribeClusterRequest().withClusterId(clusterId));
//        if (result.getSdkHttpMetadata().getHttpStatusCode() != HttpStatus.SC_OK) {
//            return null;
//        } else {
//            return result.getCluster().getMasterPublicDnsName();
//        }
//    }
//
//    /**
//     * 手动终止集群
//     */
//    protected void terminateCluster() {
//        taskLogger.error("start terminate emr cluster {}", clusterId);
//        try {
//            emr.setTerminationProtection(new SetTerminationProtectionRequest()
//                    .withJobFlowIds(clusterId)
//                    .withTerminationProtected(false));
//            emr.terminateJobFlows(new TerminateJobFlowsRequest().withJobFlowIds(clusterId));
//        } catch (Exception e) {
//            taskLogger.error("终止集群{},请求发送失败:{}",clusterId,e.getMessage());
//        }
//        taskLogger.error("emr cluster {} terminate request send success", clusterId);
//    }
//
//    @Override
//    public boolean destroy(){
//        try {
//            if (emr != null) {
//                emr.shutdown();
//            }
//        } catch (Exception e) {
//            taskLogger.error(e.getMessage());
//        }
//        String stepScriptPath = this.stepScriptPath();
//
//        taskLogger.info("start clear step temp script:{}", stepScriptPath);
//        try {
//            DFSUtils.getInstance().deleteIfExists(stepScriptPath, false);
//            taskLogger.info("delete step script {} success", stepScriptPath);
//        } catch (IOException e) {
//            taskLogger.info("delete step script {} filed:{}", stepScriptPath, e.getMessage());
//        }
//        return true;
//    }
//
//    @Override
//    public void cancel() {
//        this.terminateCluster();
//    }
//}
