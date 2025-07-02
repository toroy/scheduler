package com.zhugeio.platform.scheduler.engine.task.builtin.emr;

import com.zhugeio.platform.scheduler.core.vo.TaskVO;
import com.zhugeio.platform.scheduler.engine.task.builtin.param.SparkParameters;
import com.zhugeio.platform.scheduler.spi.plugin.AbstractTask;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiejiajun
 */
public class EmrSparkStreamingTask extends EmrSparkTask {

    public EmrSparkStreamingTask(TaskVO taskInfo, Logger logger, AbstractTask.StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    public boolean isStreamTask() {
        return true;
    }

    @Override
    public String stepType() {
        return "sparkStreaming";
    }

    @Override
    protected List<String> buildCommandList(List<String> fileParamCommand) throws Exception {
        List<String> commandList = new ArrayList<>();
        String defaultSparkAppName = String.format("'Gaia-SparkStreaming-%s-%s'", taskInfo.getName(), taskInfo.getId());
        commandList.add(SPARK_COMMAND);
        commandList.add("\\\n");
        if (CollectionUtils.isNotEmpty(fileParamCommand)) {
            fileParamCommand = fileParamCommand.stream().map(s -> s + " \\\n").collect(Collectors.toList());
            commandList.addAll(fileParamCommand);
        }
        commandList.add("--conf spark.yarn.submit.waitAppCompletion=true \\\n");
        commandList.addAll(SparkParameters.SparkArgsUtils.buildArgs(this.getParameter(), defaultSparkAppName));
        return commandList;
    }

}
