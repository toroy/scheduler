package com.clubfactory.platform.scheduler.spi.utils;


//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
//import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
//import com.amazonaws.services.elasticmapreduce.model.*;
import com.clubfactory.platform.scheduler.common.utils.OSUtils;
import com.clubfactory.platform.scheduler.dal.po.Cluster;
import com.clubfactory.platform.scheduler.dal.po.JobType;
import com.clubfactory.platform.scheduler.spi.constants.AwsEmrConstants;
import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.utils.*;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.clubfactory.platform.scheduler.common.Constants.EXIT_CODE_KILL;

/**
 * 进程操作工具类
 *
 * @author xiejiajun
 */
public class ProcessUtils {

    private final static Logger logger = LoggerFactory.getLogger(ProcessUtils.class);

    /**
     * build command line characters
     *
     * @return
     */
    public static String buildCommandStr(List<String> commandList) throws IOException {
        String cmdStr;
        String[] cmd = commandList.toArray(new String[commandList.size()]);
        SecurityManager security = System.getSecurityManager();
        boolean allowAmbiguousCommands = false;
        if (security == null) {
            allowAmbiguousCommands = true;
            String value = System.getProperty("jdk.lang.Process.allowAmbiguousCommands");
            if (value != null) {
                allowAmbiguousCommands = !"false".equalsIgnoreCase(value);
            }
        }
        if (allowAmbiguousCommands) {
            String executablePath = new File(cmd[0]).getPath();
            if (needsEscaping(VERIFICATION_LEGACY, executablePath)) {
                executablePath = quoteString(executablePath);
            }
            cmdStr = createCommandLine(VERIFICATION_LEGACY, executablePath, cmd);
        } else {
            String executablePath;
            try {
                executablePath = getExecutablePath(cmd[0]);
            } catch (IllegalArgumentException e) {
                StringBuilder join = new StringBuilder();
                for (String s : cmd) {
                    join.append(s).append(' ');
                }
                cmd = getTokensFromCommand(join.toString());
                executablePath = getExecutablePath(cmd[0]);
                // Check new executable name once more
                if (security != null) {
                    security.checkExec(executablePath);
                }
            }
            cmdStr = createCommandLine(
                    isShellFile(executablePath) ? VERIFICATION_CMD_BAT : VERIFICATION_WIN32, quoteString(executablePath), cmd);
        }
        return cmdStr;
    }

    private static String getExecutablePath(String path) throws IOException {
        boolean pathIsQuoted = isQuoted(true, path, "Executable name has embedded quote, split the arguments");

        File fileToRun = new File(pathIsQuoted ? path.substring(1, path.length() - 1) : path);
        return fileToRun.getPath();
    }

    private static boolean isShellFile(String executablePath) {
        String upPath = executablePath.toUpperCase();
        return (upPath.endsWith(".CMD") || upPath.endsWith(".BAT"));
    }

    private static String quoteString(String arg) {
        StringBuilder argbuf = new StringBuilder(arg.length() + 2);
        return argbuf.append('"').append(arg).append('"').toString();
    }


    private static String[] getTokensFromCommand(String command) {
        ArrayList<String> matchList = new ArrayList<>(8);
        Matcher regexMatcher = LazyPattern.PATTERN.matcher(command);
        while (regexMatcher.find()) {
            matchList.add(regexMatcher.group());
        }
        return matchList.toArray(new String[matchList.size()]);
    }

    private static class LazyPattern {
        // Escape-support version:
        // "(\")((?:\\\\\\1|.)+?)\\1|([^\\s\"]+)";
        private static final Pattern PATTERN = Pattern.compile("[^\\s\"]+|\"[^\"]*\"");
    }

    private static final int VERIFICATION_CMD_BAT = 0;

    private static final int VERIFICATION_WIN32 = 1;

    private static final int VERIFICATION_LEGACY = 2;

    private static final char[][] ESCAPE_VERIFICATION = {{' ', '\t', '<', '>', '&', '|', '^'},
            {' ', '\t', '<', '>'}, {' ', '\t'}};

    private static String createCommandLine(int verificationType, final String executablePath, final String[] cmd) {
        StringBuilder cmdBuf = new StringBuilder(80);
        cmdBuf.append(executablePath);
        for (int i = 1; i < cmd.length; ++i) {
            cmdBuf.append(' ');
            String s = cmd[i];
            if (needsEscaping(verificationType, s)) {
                cmdBuf.append('"').append(s);
                if ((verificationType != VERIFICATION_CMD_BAT) && s.endsWith("\\")) {
                    cmdBuf.append('\\');
                }
                cmdBuf.append('"');
            } else {
                cmdBuf.append(s);
            }
        }
        return cmdBuf.toString();
    }

    private static boolean isQuoted(boolean noQuotesInside, String arg, String errorMessage) {
        int lastPos = arg.length() - 1;
        if (lastPos >= 1 && arg.charAt(0) == '"' && arg.charAt(lastPos) == '"') {
            // The argument has already been quoted.
            if (noQuotesInside) {
                if (arg.indexOf('"', 1) != lastPos) {
                    // There is ["] inside.
                    throw new IllegalArgumentException(errorMessage);
                }
            }
            return true;
        }
        if (noQuotesInside) {
            if (arg.indexOf('"') >= 0) {
                // There is ["] inside.
                throw new IllegalArgumentException(errorMessage);
            }
        }
        return false;
    }

    private static boolean needsEscaping(int verificationType, String arg) {
        boolean argIsQuoted = isQuoted((verificationType == VERIFICATION_CMD_BAT), arg,
                "Argument has embedded quote, use the explicit CMD.EXE call.");
        if (!argIsQuoted) {
            char[] testEscape = ESCAPE_VERIFICATION[verificationType];
            for (int i = 0; i < testEscape.length; ++i) {
                if (arg.indexOf(testEscape[i]) >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Kill Yarn Application
     * 这种方式一个Worker只能杀一个Yarn集群上的App，不够灵活，后面若Yarn RestApi支持put请求杀任务后换成RestApi方式
     * （现在非认证模式部署的yarn，没法传递remoteUser导致restAPi杀任务是报401）
     *
     * @param logger
     * @param killCommands
     * @throws IOException
     */
    public static void cancelApplication(Logger logger, TaskVO taskVO, List<String> killCommands) {
        String workerDir = taskVO.getExecuteDir();
        if (StringUtils.isEmpty(workerDir)) {
            logger.error("task instance work dir is empty");
            throw new RuntimeException("task instance work dir is empty");
        }
        String tenant = taskVO.getTenant();
        String workDir = taskVO.getExecuteDir();
        if (killCommands.size() > 0) {
            String commandFile = String
                    .format("%s/task_%s_yarn_app_kill.sh", workDir, taskVO.getId());
            logger.info("yarn application kill cmd: {}", StringUtils.join(killCommands, "\r\n"));
            Process p = null;
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("#!/bin/bash\n");
                sb.append("source /etc/profile\n");
                sb.append("BASEDIR=$(cd `dirname $0`; pwd)\n");
                sb.append("cd $BASEDIR\n");
                if (CommonUtils.getSystemEnvPath() != null) {
                    sb.append("source " + CommonUtils.getSystemEnvPath() + "\n");
                }
                sb.append("\n\n");
                for (String killCommand : killCommands) {
                    sb.append(killCommand);
                    sb.append("\n\n");
                }
                File f = new File(commandFile);
                if (!f.exists()) {
                    FileUtils.writeStringToFile(new File(commandFile), sb.toString(), Charset.forName("UTF-8"));
                }
                String runCmd = "sh " + commandFile;
                if (StringUtils.isNotBlank(tenant) && CommonUtils.isMultiTenantModeStartup() && !OSUtils.isMacOS()) {
                    runCmd = "sudo -u " + tenant + " " + runCmd;
                }
                logger.info("kill cmd:{}", runCmd);
                p = Runtime.getRuntime().exec(runCmd);
                p.waitFor();
                logger.info("Kill cmd running exitCode:{}", p.exitValue());
            } catch (Exception e) {
                logger.error("kill application failed : " + e.getMessage(), e);
            } finally {
                if (p != null) {
                    try {
                        IOUtils.closeQuietly(p.getInputStream());
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    try {
                        IOUtils.closeQuietly(p.getErrorStream());
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
    }

    private static boolean terminateEmr(String clusterId, Logger logger) {
        return true;
    }
//    /**
//     * 终止临时集群
//     */
//    private static boolean terminateEmr(String clusterId, Logger logger) {
//        AmazonElasticMapReduce emr = null;
//        Set<String> terminatedStates = Sets.newHashSet("FAILED", "TERMINATED", "TERMINATED_WITH_ERRORS", "TERMINATING");
//        try {
//            final DefaultAWSCredentialsProviderChain provider = new DefaultAWSCredentialsProviderChain();
//            AWSCredentials credentialsProfile = provider.getCredentials();
//            String awsRegion = PropertyUtils.getString(AwsEmrConstants.AWS_EMR_REGION, AwsEmrConstants.DEFAULT_AWS_EMR_REGION);
//            emr = AmazonElasticMapReduceClientBuilder.standard()
//                    .withCredentials(new AWSStaticCredentialsProvider(credentialsProfile))
//                    .withRegion(Regions.fromName(awsRegion))
//                    .build();
//            DescribeClusterResult result = emr.describeCluster(new DescribeClusterRequest().withClusterId(clusterId));
//            String state = null;
//            try {
//                state = result.getCluster().getStatus().getState();
//            }catch (Exception e){
//                logger.error("fetch cluster state failed");
//            }
//            if (state == null || terminatedStates.contains(state)){
//                logger.info("cluster {} is terminated",clusterId);
//                return true;
//            }
//
//            logger.info("start terminate emr cluster {}", clusterId);
//            // 关闭终止保护
//            emr.setTerminationProtection(new SetTerminationProtectionRequest()
//                    .withJobFlowIds(clusterId)
//                    .withTerminationProtected(false));
//            // 发送终止请求
//            emr.terminateJobFlows(new TerminateJobFlowsRequest().withJobFlowIds(clusterId));
//            logger.info("send terminate request to cluster {}  success", clusterId);
//            return true;
//        } catch (Exception e) {
//            logger.error("临时终止失败", e);
//            return false;
//        } finally {
//            if (emr != null) {
//                emr.shutdown();
//            }
//        }
//
//    }

    /**
     * 通过SSH远程执行命令方式Kill掉对应集群的App
     * 前提是所有Worker要和可连接的所有集群中配置的RM服务器打通ssh免密登录
     *
     * @param appIds
     * @param taskInstance
     * @throws IOException
     */
    public static List<String> buildSSHYarnKillCommands(List<String> appIds, TaskVO taskInstance) throws IOException {
        List<String> killCommands = Lists.newArrayList();
        if (appIds.size() > 0) {
            for (String appId : appIds) {
                Cluster  cluster = taskInstance.getCluster();
                String yarnRM = cluster.getYarnRMHosts().split(",")[0];
                String yarnSuperUser = StringUtils.isNotBlank(cluster.getYarnSuperUser()) ?
                        cluster.getYarnSuperUser() : "hadoop";
                String cmd = String.format("ssh %s@%s \"yarn application -kill %s\"",
                        yarnSuperUser, yarnRM, appId);
                killCommands.add(cmd);
            }
        }
        return killCommands;
    }

    /**
     * 本地执行的yarn app kill命令
     *
     * @param appIds
     * @return
     * @throws IOException
     */
    public static List<String> buildYarnKillCommands(List<String> appIds) throws IOException {
        List<String> killCommands = Lists.newArrayList();
        if (appIds.size() > 0) {
            for (String appId : appIds) {
                String cmd = "yarn application -kill " + appId;
                killCommands.add(cmd);
            }
        }
        return killCommands;
    }

    /**
     * kill tasks according to different task types
     *
     * @param taskInstance
     */
    public static boolean kill(TaskVO taskInstance) {
        try {
            if (taskInstance == null) {
                return false;
            }
            if (StringUtils.isNotBlank(taskInstance.getEmrClusterId())){
                // 若是临时集群任务，关闭临时集群即可
                return terminateEmr(taskInstance.getEmrClusterId(),logger);
            }
            Integer processId = taskInstance.getPid();
            if (processId == null || processId == 0) {
                logger.error("process kill failed, process id :{}, task id:{}",
                        processId, taskInstance.getId());
                return false;
            }
            killYarnJob(taskInstance);
            String cmd;
            if (OSUtils.isMacOS()) {
                cmd = String.format("kill -9 %s", processId);
            } else {
                cmd = String.format("sudo kill -9 %s", getPidList(processId));
            }
            logger.info("执行 kill 命令 {}", cmd);
            String msg = OSUtils.exeCmd(cmd);
            logger.info("process id:{}, cmd:{}, kill command exec msg:{}", processId, cmd, msg);
            return true;
        } catch (Exception e) {
            logger.error("kill failed : " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取pid列表
     *
     * @param processId
     * @return
     * @throws Exception
     */
    private static String getPidList(int processId) throws Exception {
        StringBuilder sb = new StringBuilder();
        // pstree -p pid get sub pidStr
        String pidStr = OSUtils.exeCmd("pstree -p " + processId + "");
        Matcher mat = Pattern.compile("(\\d+)").matcher(pidStr);
        while (mat.find()) {
            sb.append(mat.group()).append(" ");
        }
        String pidList = sb.toString().trim();
        if (StringUtils.isBlank(pidList)) {
            pidList = " " + processId;
        }
        return pidList;
    }

    /**
     * 从日志中获取AppId，并kill
     *
     * @param taskInstance
     */
    public static void killYarnJob(TaskVO taskInstance) {
        try {
            if (StringUtils.isNotBlank(taskInstance.getEmrClusterId())){
                // 若是临时集群任务，关闭临时集群即可
                logger.info("开始kill临时集群任务{}", taskInstance.getId());
                terminateEmr(taskInstance.getEmrClusterId(),logger);
                return;
            }
            long logFileSize = com.clubfactory.platform.scheduler.core.utils.FileUtils.getFileSize(taskInstance.getLogPath());
            long allowMaxLogBytes = PropertyUtils.getLong(Constants.ALLOW_MAX_LOG_BYTES, Constants.DEFAULT_ALLOW_MAX_LOG_BYTES);
            if (logFileSize == -1 || logFileSize > allowMaxLogBytes) {
                logger.error("任务 {} 日志文件大小超过最大阈值 {} bytes 或者日志文件为空, 不能提取applicationId，跳过Yarn任务kill环节", taskInstance.getId(), allowMaxLogBytes);
                return;
            }
            List<String> appIds = ProcessUtils.findAppIdsFromLogFile(taskInstance.getLogPath());
            if (appIds.size() > 0) {
                logger.info("等待 kill 的 appIds 为：{}", String.join(",", appIds));
                logger.info("开始 kill 任务 {} 对应的 yarn app", taskInstance.getId());
                List<String> killCommands;
                if (taskInstance.getCluster() != null && StringUtils.isNotBlank(taskInstance.getCluster().getYarnRMHosts())) {
                    killCommands = buildSSHYarnKillCommands(appIds, taskInstance);
                } else {
                    killCommands = buildYarnKillCommands(appIds);
                }
                cancelApplication(logger, taskInstance, killCommands);
            }
            logger.info("task {} yarn app kill finished ...", taskInstance.getId());
        } catch (Exception e) {
            logger.error("kill yarn job failure", e);
        }

    }

    /**
     * Kill Yarn Application
     * @param taskInstance
     * @param applicationId
     */
    public static void killYarnJobByAppId(TaskVO taskInstance, String applicationId) {
        if (StringUtils.isBlank(applicationId)) {
            return;
        }
        if (StringUtils.isNotBlank(taskInstance.getEmrClusterId())){
            // 若是临时集群任务，关闭临时集群即可
            terminateEmr(taskInstance.getEmrClusterId(),logger);
            return;
        }
        try {
            List<String> command = Lists.newArrayList("yarn application -kill " + applicationId);
            cancelApplication(logger, taskInstance, command);
        } catch (Exception e) {
            logger.error("kill yarn job failure: {}", e.getMessage());
        }
    }

    /**
     * 从日志文件中查找appId列表
     * @param logFile
     * @return
     */
    public static List<String> findAppIdsFromLogFile(String logFile) {
        long start = System.currentTimeMillis();
        try {
            logger.info("开始从日志文件中读取 appIds");
            Set<String> appIdSet = new HashSet<>(100);
            if (logFile == null) {
                return new ArrayList<>();
            }
            File file = new File(logFile);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    // 改为流式读取后这里不能再使用TaskLogger打日志，否则会将匹配到的appId写会task日志
                    //  然后又重新读出来，导致死循环
                    List<String> appIds = LoggerUtils.getAppIds(line, logger);
                    appIdSet.addAll(appIds);
                }
            } catch (Exception e) {
                logger.error(String.format("read file: %s failed : ", logFile), e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            return Lists.newArrayList(appIdSet);
        } finally {
            logger.info("从日志文件中查找 appIds 耗时 {} s ", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start));
        }

    }


    /**
     * 检查是否是Yarn任务，如果是则阻塞只到任务完成
     *
     * @param taskProps
     * @param taskLogger
     * @param exitStatusCode
     * @return
     */
    public static int ensureYarnState(TaskVO taskProps, Logger taskLogger, int exitStatusCode) {
        // block until yarn application finish
        if (exitStatusCode == 0) {
            List<String> appIds = ProcessUtils.findAppIdsFromLogFile(taskProps.getLogPath());
            if (appIds.size() > 0) {
                taskLogger.info("appIds is :{}", String.join(",", appIds));
                if (!ProcessUtils.isSuccessOfYarnState(taskProps, appIds, taskLogger)) {
                    if (taskProps.getStatus() == TaskStatusEnum.KILLED) {
                        exitStatusCode = EXIT_CODE_KILL;
                    } else {
                        exitStatusCode = -1;
                    }
                }
            }
        }
        return exitStatusCode;
    }

    /**
     * 检查Yarn上任务是否已完成：未完成则等待任务完成才退出
     *
     * @param appIds
     * @return
     */
    private static boolean isSuccessOfYarnState(TaskVO taskProps, List<String> appIds, Logger taskLogger) {
        boolean result = true;
        if (taskProps.getStatus() == TaskStatusEnum.KILLED) {
            return true;
        }
        try {
            for (String appId : appIds) {
                while (true) {
                    TaskStatusEnum applicationStatus;
                    JobType jobType = taskProps.getJobTypeInfo();
                    boolean isClusterJob = jobType == null || jobType.getIsClusterJob() == null ? false : jobType.getIsClusterJob();
                    if (!isClusterJob || taskProps.getCluster() == null || StringUtils.isBlank(taskProps.getCluster().getYarnRMHosts())) {
                        taskLogger.info("trace yarn application state by default cluster");
                        applicationStatus = YarnUtils.getInstance().getApplicationStatus(appId);
                    } else {
                        // 若taskInstance指定了对应的Yarn RM host,需要连接指定的yarn RM获取状态,不能再使用默认的，默认的只适用于Spark这类CLI提交的app
                        taskLogger.info("trace yarn application state by cluster:{}", taskProps.getCluster().getYarnRMHosts());
                        applicationStatus = YarnUtils.getInstance().getRemoteApplicationStatus(
                                taskProps.getCluster().getYarnRMHosts().split(","),
                                taskProps.getCluster().getYarnRMHttpPort(),
                                appId);
                    }
                    if (applicationStatus == null) {
                        taskLogger.info("appId:{} state is null or not exists", appId);
                        break;
                    }
                    taskLogger.info("appId:{}, current state:{}", appId, applicationStatus.name());
                    if (applicationStatus.equals(TaskStatusEnum.FAILED) ||
                            applicationStatus.equals(TaskStatusEnum.KILLED)) {
                        return false;
                    }
                    if (applicationStatus.equals(TaskStatusEnum.SUCCESS)) {
                        break;
                    }
                    Thread.sleep(Constants.SLEEP_TIME_MILLIS);
                }
            }
        } catch (Exception e) {
            taskLogger.error(String.format("yarn applications: %s  status failed : " + e.getMessage(), appIds.toString()), e);
            result = false;
        }
        return result;
    }

}
