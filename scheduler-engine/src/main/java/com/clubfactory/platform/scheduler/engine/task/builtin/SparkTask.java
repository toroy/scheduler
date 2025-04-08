package com.clubfactory.platform.scheduler.engine.task.builtin;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.SparkTaskType;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.SparkParameters;
import com.clubfactory.platform.scheduler.spi.param.IParameters;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractYarnTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiejiajun
 */
public class SparkTask extends AbstractYarnTask {

    protected static final String SPARK_COMMAND = "spark-submit";

    public SparkTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    protected Class<? extends IParameters> getParameterType() {
        return SparkParameters.class;
    }

    @Override
    protected List<String> buildCommandList() throws Exception {
        List<String> commandList = new ArrayList<>();
        String sparkBinDir = this.getSparkBinDir();
        String defaultSparkAppName = String.format("'Gaia-Spark-%s-%s'", taskInfo.getName(), taskInfo.getId());
        if (StringUtils.isNotBlank(sparkBinDir)){
            commandList.add( sparkBinDir + "/" + SPARK_COMMAND);
        }else {
            commandList.add(SPARK_COMMAND);
        }
        commandList.add("\\\n");
        commandList.addAll(SparkParameters.SparkArgsUtils.buildArgs(this.getParameter(), defaultSparkAppName));
        return commandList;
    }

    /**
     * 获取Spark bin目录
     * @return
     */
    protected String getSparkBinDir(){
        return this.getString(Constants.SPARK_BIN);
    }

    @Override
    protected String fileExt() {
        return isPySpark() ? "py" : "jar";
    }

    /**
     * 判断是否为pySpark
     * @return
     */
    protected boolean isPySpark(){
        SparkParameters sparkParameters = this.getParameter();
        return sparkParameters != null && sparkParameters.getLanguageType() == SparkTaskType.PYTHON;
    }
}
