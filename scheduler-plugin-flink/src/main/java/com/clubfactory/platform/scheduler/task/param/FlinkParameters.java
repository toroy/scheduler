package com.clubfactory.platform.scheduler.task.param;

import com.clubfactory.platform.scheduler.core.utils.JSONUtils;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.spi.param.AbstractParameter;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.List;

/**
 * @author xiejiajun
 */
@Data
public class FlinkParameters extends AbstractParameter {

    /**
     * major jar/ major python file
     */
    private String startFile;

    /**
     * major class
     */
    private String mainClass;

    /**
     * arguments
     */
    private String mainArgs;


    /**
     * The YARN queue to submit to
     */
    private String queue;

    /**
     * 0 JAVA,1 SCALA,2 PYTHON
     */
    private FlinkTaskType languageType;


    @Override
    public boolean checkParameters() {
        if (languageType == null){
            return false;
        }
        return StringUtils.isNotBlank(startFile);
    }

    /**
     * @param taskInfo
     * @param logger
     * @return
     */
    public static FlinkParameters create(TaskVO taskInfo, Logger logger) {
        logger.info("Flink task params {}", taskInfo.getParams());
        FlinkParameters flinkParameters = JSONUtils.parseObject(taskInfo.getParams(), FlinkParameters.class);
        if (flinkParameters == null ) {
            throw new RuntimeException("Flink task params is null");
        }
        flinkParameters.setStartFile(taskInfo.getFileName());
        if (!flinkParameters.checkParameters()){
            throw new RuntimeException("Flink task params is invalid");
        }
        return flinkParameters;
    }


    public static class FlinkArgsBuilder{

        /**
         * @param param
         * @param taskName
         * @param isStream
         * @return
         */
        public static List<String> buildArgs(FlinkParameters param, String taskName, boolean isStream) {
            List<String> args = Lists.newArrayList();
            if (StringUtils.isNotBlank(param.getSysConfigs())) {
                buildWithDeployConfig(args, param, taskName, isStream);
            }else {
                defaultDeployConfig(args, param, taskName, isStream);
            }

            if (param.languageType == FlinkTaskType.PYTHON) {
                args.add("-py");
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
         * 根据用户提供的部署配置组装部署魔力
         * @param args
         * @param param
         * @param taskName
         * @param isStream
         */
        private static void buildWithDeployConfig(List<String> args , FlinkParameters param, String taskName, boolean isStream){
            String sysConfigs = param.getSysConfigs();
            if (!sysConfigs.contains(" -d ") && isStream) {
                args.add("-d");
                args.add("\\\n");
            }
            if (!sysConfigs.contains("yarn-cluster")) {
                args.add("-m");
                args.add("yarn-cluster");
                args.add("\\\n");
            }
            if (!sysConfigs.contains(" -c ") && param.getLanguageType() !=null
                    && param.getLanguageType()!= FlinkTaskType.PYTHON){
                if (StringUtils.isNotBlank(param.getMainClass())) {
                    args.add("-c");
                    args.add(param.getMainClass());
                    args.add("\\\n");
                }
            }
            if(!sysConfigs.contains("-yqu")){
                if (StringUtils.isNotBlank(param.getQueue())) {
                    args.add("-yqu");
                    args.add(param.getQueue());
                    args.add("\\\n");
                }
            }

            if (!sysConfigs.contains("-ynm")) {
                args.add("-ynm");
                args.add("'Gaia-Flink-" + taskName + "'");
                args.add("\\\n");
            }
            param.buildSysConfig(args);
        }

        /**
         * 根据默认部署参数组装命令
         * @param args
         * @param param
         * @param taskName
         * @param isStream
         */
        private static void defaultDeployConfig(List<String> args , FlinkParameters param, String taskName, boolean isStream){
            args.add("-m");
            args.add("yarn-cluster");
            args.add("\\\n");
            if (isStream) {
                args.add("-d");
                args.add("\\\n");
            }

            args.add("-ynm");
            args.add("'Gaia-Flink-" + taskName + "'");
            args.add("\\\n");
            if (param.getLanguageType() != null && param.getLanguageType()!= FlinkTaskType.PYTHON){
                if (StringUtils.isNotBlank(param.getMainClass())) {
                    args.add("-c");
                    args.add(param.getMainClass());
                    args.add("\\\n");
                }
            }
            if (StringUtils.isNotBlank(param.getQueue())) {
                args.add("-yqu");
                args.add(param.getQueue());
                args.add("\\\n");
            }
        }
    }
}
