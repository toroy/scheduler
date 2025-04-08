package com.clubfactory.platform.scheduler.engine.task.builtin.param;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.utils.JSONUtils;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.spi.param.AbstractParameter;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;



/**
 * @author xiejiajun
 */
@Data
public class SparkParameters extends AbstractParameter {

    /**
     * major class
     */
    private String mainClass;

    /**
     * deploy mode
     */
    private String deployMode;

    /**
     * The YARN queue to submit to
     */
    private String queue;

    /**
     * program type
     * 0 JAVA,1 SCALA,2 PYTHON
     */
    private SparkTaskType languageType;

    @Override
    public boolean checkParameters() {
        if (languageType == null){
            return false;
        }
        if (StringUtils.isBlank(startFile)){
            return false;
        }
        if (languageType == SparkTaskType.JAVA || languageType == SparkTaskType.SCALA){
            return StringUtils.isNotBlank(mainClass);
        }
        return true;
    }

    /**
     * 构建任务参数
     * @param taskInfo
     * @param logger
     * @return
     */
    public static SparkParameters create(TaskVO taskInfo, Logger logger) {
        logger.info("spark task params {}", taskInfo.getParams());
        SparkParameters sparkParameters = JSONUtils.parseObject(taskInfo.getParams(), SparkParameters.class);
        if (sparkParameters == null ) {
            throw new RuntimeException("spark task params is null");
        }
        sparkParameters.setStartFile(taskInfo.getFileName());
        if (!sparkParameters.checkParameters()){
            throw new RuntimeException("spark task params is not valid");
        }
        return sparkParameters;
    }

    public static class SparkArgsUtils {

        public static List<String> buildArgs(SparkParameters param, String sparkAppName) {
            return buildArgs(param, sparkAppName, false);
        }

        /**
         * @param param
         * @param sparkAppName
         * @param limitResource
         * @return
         */
        public static List<String> buildArgs(SparkParameters param, String sparkAppName, boolean limitResource) {
            List<String> args = new ArrayList<>();
            if (StringUtils.isNotBlank(param.getSysConfigs())) {
                buildWithDeployConfig(args, param, sparkAppName, limitResource);
            } else {
                buildDefaultDeployConfig(args, param, sparkAppName, limitResource);
            }

            if (StringUtils.isNotBlank(param.getStartFile())) {
                args.add(param.getStartFile());
            }
            if (StringUtils.isNotBlank(param.getMainArgs())) {
                args.add(param.getMainArgs());
            }
            return args;
        }

        /**
         * 根据用户指定的部署配置创建命令
         * @param args
         * @param param
         * @param sparkAppName
         * @param limitResource
         */
        private static void buildWithDeployConfig(List<String> args, SparkParameters param, String sparkAppName, boolean limitResource) {
            String sysConfigs = param.getSysConfigs();
            if (!sysConfigs.contains(Constants.MASTER) && !sysConfigs.contains("spark.master")){
                args.add(Constants.MASTER);
                String deployMode = "cluster";
                if(StringUtils.isNotBlank(param.getDeployMode())){
                    deployMode = param.getDeployMode();
                }
                if(!"local".equalsIgnoreCase(deployMode)){
                    args.add("yarn");
                    args.add("\\\n");
                    if (!sysConfigs.contains("--deploy-mode") && !sysConfigs.contains("spark.submit.deployMode")) {
                        args.add(Constants.DEPLOY_MODE);
                        args.add(deployMode);
                        args.add("\\\n");
                    }
                }else {
                    args.add(deployMode);
                    args.add("\\\n");
                }
            }

            if (!sysConfigs.contains(Constants.CLASS) && param.getLanguageType() !=null
                    && param.getLanguageType()!= SparkTaskType.PYTHON){
                if (StringUtils.isNotBlank(param.getMainClass())) {
                    args.add(Constants.CLASS);
                    args.add(param.getMainClass());
                    args.add("\\\n");
                }
            }

            if(!sysConfigs.contains("--queue") && !sysConfigs.contains("spark.yarn.queue")){
                if (StringUtils.isNotBlank(param.getQueue())) {
                    args.add(Constants.SPARK_QUEUE);
                    args.add(param.getQueue());
                    args.add("\\\n");
                }
            }

            if (!sysConfigs.contains("--name") && !sysConfigs.contains("spark.app.name")) {
                args.add("--name");
                args.add(sparkAppName);
                args.add("\\\n");
            }
            if (limitResource) {
                if (!sysConfigs.contains("spark.executor.cores")) {
                    args.add("--conf");
                    args.add("spark.executor.cores=2");
                    args.add("\\\n");
                }
                if (!sysConfigs.contains("spark.executor.memory")) {
                    args.add("--conf");
                    args.add("spark.executor.memory=8G");
                    args.add("\\\n");
                }
                if (!sysConfigs.contains("spark.dynamicAllocation.maxExecutors")) {
                    args.add("--conf");
                    args.add("spark.dynamicAllocation.maxExecutors=10");
                    args.add("\\\n");
                }
            }
            // 其他参数放这里（包括PySpark提交参数)
            param.buildSysConfig(args);
        }

        /**
         * 参数转化
         * @param args
         * @param param
         * @param sparkAppName
         * @param limitResource
         */
        private static void buildDefaultDeployConfig(List<String> args,SparkParameters param, String sparkAppName, boolean limitResource){
            args.add(Constants.MASTER);
            String deployMode = "cluster";
            if(StringUtils.isNotBlank(param.getDeployMode())){
                deployMode = param.getDeployMode();

            }
            if(!"local".equalsIgnoreCase(deployMode)){
                args.add("yarn");
                args.add("\\\n");
                args.add(Constants.DEPLOY_MODE);
            }
            args.add(deployMode);
            args.add("\\\n");

            if (limitResource) {
                args.add("--conf");
                args.add("spark.executor.cores=2");
                args.add("\\\n");
                args.add("--conf");
                args.add("spark.executor.memory=8G");
                args.add("\\\n");
                args.add("--conf");
                args.add("spark.dynamicAllocation.maxExecutors=10");
                args.add("\\\n");
            }

            args.add("--name");
            args.add(sparkAppName);
            args.add("\\\n");
            if (param.getLanguageType() !=null && param.getLanguageType()!= SparkTaskType.PYTHON){
                if (StringUtils.isNotBlank(param.getMainClass())) {
                    args.add(Constants.CLASS);
                    args.add(param.getMainClass());
                    args.add("\\\n");
                }
            }
            if (StringUtils.isNotBlank(param.getQueue())) {
                args.add(Constants.SPARK_QUEUE);
                args.add(param.getQueue());
                args.add("\\\n");
            }
        }

    }
}
