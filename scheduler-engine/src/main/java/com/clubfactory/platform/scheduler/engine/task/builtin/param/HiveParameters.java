package com.clubfactory.platform.scheduler.engine.task.builtin.param;

import com.clubfactory.platform.common.exception.BizException;
import com.clubfactory.platform.common.util.Assert;
import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.utils.JSONUtils;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.po.Cluster;
import com.clubfactory.platform.scheduler.spi.param.AbstractParameter;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * @author xiejiajun
 */
@Data
public class HiveParameters extends AbstractParameter {

    /**
     * hive JDBC URL
     */
    private String hiveURL;

    /**
     * beeline不走多租户，需要额外指定登录用户
     */
    private String userName;

    /**
     * hs2登录密码
     */
    private String password;

    /**
     * hiveVars
     */
    @Deprecated
    private String hiveVars;

    /**
     * HiveConfigs
     */
    @Deprecated
    private String hiveConfigs;



    @Override
    public boolean checkParameters() {
        return StringUtils.isNotBlank(startFile) && StringUtils.isNotBlank(hiveURL);
    }

    @Override
    public void normalizedParameter() {
        super.normalizedParameter();
        if (this.taskContext.isRunOnTmpEmr()) {
            this.setHiveURL("'jdbc:hive2://localhost:10000/'");
            return;
        }
        Cluster cluster = this.taskContext.getCluster();
        Assert.notNull(cluster, "Hive集群信息");
        String hiveUrl = cluster.getUrl();
        if (!StringUtils.startsWith(hiveUrl, Constants.DOUBLE_QUOTES) &&
                !StringUtils.startsWith(hiveUrl, Constants.SINGLE_QUOTES)) {
            hiveUrl = String.format("'%s'", hiveUrl);
        }
        this.setHiveURL(hiveUrl);
        this.setUserName(cluster.getProxyUser());
        this.setPassword(cluster.getProxyPassword());
    }

    /**
     * @param taskInfo
     * @param logger
     * @return
     */
    public static HiveParameters create(TaskVO taskInfo, Logger logger) {
        logger.info("hive task params {}", taskInfo.getParams());
        HiveParameters hiveParameters = JSONUtils.parseObject(taskInfo.getParams(), HiveParameters.class);
        if (hiveParameters == null) {
            throw new RuntimeException("hive task params is null");
        }
        hiveParameters.setStartFile(taskInfo.getFileName());
        Cluster cluster = taskInfo.getCluster();
        hiveParameters.setHiveURL(cluster.getUrl());
        hiveParameters.setUserName(cluster.getProxyUser());
        hiveParameters.setPassword(cluster.getProxyPassword());
        if (!hiveParameters.checkParameters()) {
            throw new RuntimeException("hive task params is invalid");
        }
        return hiveParameters;
    }

    public static class HiveArgsUtils {

        /**
         * beeline
         */
        private static final String BEELINE = "beeline";

        /**
         *  build args
         * @param param
         * @return
         */
        public static List<String> buildArgs(HiveParameters param, String appNameKey, String appName) {
            if (StringUtils.isNotBlank(param.getHiveConfigs()) || StringUtils.isNotBlank(param.getHiveVars())) {
                return buildArgs_old(param, appNameKey, appName);
            }
            // beeline -u ${jdbc_url}  --hiveconf/--hivevar -n user -p password -f hive_demo.sql
            List<String> args = new ArrayList<>();
            if (StringUtils.isNotBlank(param.getHiveURL())) {
                args.add(BEELINE);
                args.add("-u");
                args.add(param.getHiveURL());
                String hiveUser = StringUtils.isNotBlank(param.getUserName()) ? param.getUserName() : "hadoop";
                String hivePassword = StringUtils.isNotBlank(param.getPassword()) ? param.getPassword() : "hive";
                args.add("-n");
                args.add(hiveUser);
                args.add("-p");
                args.add(hivePassword);
            }else {
                throw new BizException("Hive URL不能为空");
            }
            if (StringUtils.isNotBlank(param.getSysConfigs())){
                if (!param.getSysConfigs().contains(appNameKey)) {
                    args.add("--hiveconf");
                    args.add(appNameKey + "=" + appName);
                    args.add("\\\n");
                }
                args.add(parseHiveConf(param.getSysConfigsList(), "--hiveconf"));
            } else {
                args.add("--hiveconf");
                args.add(appNameKey + "=" + appName);
                args.add("\\\n");
            }
            if (StringUtils.isNotEmpty(param.getMainArgs())){
                args.add(parseHiveConf(param.getMainArgsList(), "--hivevar"));
            }
            args.add("-f");
            args.add(param.getStartFile());
            return args;
        }

        static String parseHiveConf(List<String> hiveConfs, String prefix) {
            if (CollectionUtils.isEmpty(hiveConfs)) {
                return "";
            }

            StringBuilder hiveConfCmd = new StringBuilder();
            for (String hiveConf : hiveConfs) {
                if (!hiveConf.contains("=")){
                    continue;
                }
                if (hiveConf.contains(prefix)) {
                    if (hiveConf.equalsIgnoreCase(prefix)) {
                        continue;
                    }
                    hiveConfCmd.append(hiveConf).append(" ");
                } else {
                    hiveConfCmd.append(prefix).append(" ").append(hiveConf).append(" ");
                }
            }
            return hiveConfCmd.toString();
        }

        @Deprecated
        static List<String> buildArgs_old(HiveParameters param, String appNameKey, String appName) {
            List<String> args = new ArrayList<>();
            String hiveURL = param.getHiveURL();
            if (StringUtils.isNotBlank(hiveURL)) {
                args.add(BEELINE);
                args.add("-u");
                args.add(hiveURL);
                if (StringUtils.isNotEmpty(param.getUserName())){
                    args.add("-n");
                    args.add(param.getUserName());
                }

                if (StringUtils.isNotEmpty(param.getPassword())){
                    args.add("-p");
                    args.add(param.getPassword());
                }
            }else {
                throw new RuntimeException("Hive URL 未设置");
            }

            if (StringUtils.isNotBlank(param.getHiveConfigs())){
                if (!param.getHiveConfigs().contains(appNameKey)) {
                    args.add("--hiveconf");
                    args.add(appNameKey + "=" + appName);
                    args.add("\\\n");
                }
                args.add(param.getHiveConfigs());
            } else {
                args.add("--hiveconf");
                args.add(appNameKey + "=" + appName);
                args.add("\\\n");
            }

            if (StringUtils.isNotEmpty(param.getHiveVars())){
                args.add(param.getHiveVars());
            }

            args.add("-f");
            args.add(param.getStartFile());

            return args;

        }

    }
}
