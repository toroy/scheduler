package com.clubfactory.platform.scheduler.engine.task.builtin;

import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.SparkParameters;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiejiajun
 */
public class SparkStreamingTask extends SparkTask {

    public SparkStreamingTask(TaskVO taskInfo, Logger logger, AbstractTask.StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    protected List<String> buildCommandList() throws Exception {
        List<String> commandList = new ArrayList<>();
        String sparkBinDir = getSparkBinDir();
        String defaultSparkAppName = String.format("'Gaia-SparkStreaming-%s-%s'", taskInfo.getName(), taskInfo.getId());
        if (StringUtils.isNotBlank(sparkBinDir)){
            commandList.add(sparkBinDir + "/" + SPARK_COMMAND);
        }else {
            commandList.add(SPARK_COMMAND);
        }
        commandList.add("\\\n");
        commandList.add("--conf spark.yarn.submit.waitAppCompletion=false \\\n");
        commandList.addAll(SparkParameters.SparkArgsUtils.buildArgs(this.getParameter(), defaultSparkAppName, true));
        return commandList;
    }

    @Override
    public boolean isStreamTask() {
        return true;
    }


}
