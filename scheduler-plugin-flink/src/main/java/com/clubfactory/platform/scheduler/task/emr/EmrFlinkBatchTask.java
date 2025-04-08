package com.clubfactory.platform.scheduler.task.emr;

import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.spi.param.IParameters;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractEmrTask;
import com.clubfactory.platform.scheduler.task.param.FlinkParameters;
import com.clubfactory.platform.scheduler.task.param.FlinkTaskType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.clubfactory.platform.scheduler.common.Constants.DEFAULT_STREAMING_TASK_QUEUE;
import static com.clubfactory.platform.scheduler.common.Constants.STREAMING_TASK_QUEUE_KEY;

/**
 * @author xiejiajun
 */
public class EmrFlinkBatchTask extends AbstractEmrTask {

    private final static String FLINK_COMMAND = "flink";

    public EmrFlinkBatchTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    protected Class<? extends IParameters> getParameterType() {
        return FlinkParameters.class;
    }

//    @Override
//    public void appendEmrConfig(List<Configuration> emrConfigs) {
//    }

    @Override
    public String stepType() {
        return "flink";
    }

//    @Override
//    public List<Application> applications() {
//        List<Application> applications = Lists.newArrayList();
//        applications.add(new Application().withName("Ganglia"));
//        applications.add(new Application().withName("Flink"));
//        return applications;
//    }

    @Override
    protected List<String> buildCommandList() throws Exception {
        List<String> commandList = new ArrayList<>();
        String flinkBinDir = this.getString("flink.bin.dir");
        if (StringUtils.isNotBlank(flinkBinDir)){
            commandList.add(flinkBinDir + "/" + FLINK_COMMAND);
        }else {
            commandList.add(FLINK_COMMAND);
        }
        commandList.add("run");
        commandList.add("\\\n");
        FlinkParameters flinkParameters = this.getParameter();
        if (isStreamTask() && StringUtils.isBlank(flinkParameters.getQueue())) {
            flinkParameters.setQueue(this.getString(STREAMING_TASK_QUEUE_KEY, DEFAULT_STREAMING_TASK_QUEUE));
        }
        commandList.addAll(FlinkParameters.FlinkArgsBuilder
                .buildArgs(flinkParameters, String.format("%s-%s", this.taskInfo.getName(), taskInfo.getId()), this.isStreamTask()));
        return commandList;
    }

    @Override
    protected String fileExt() {
        return isPyFlink() ? "py" : "jar";
    }

    /**
     * 判断是否为pyFlink
     * @return
     */
    private boolean isPyFlink(){
        FlinkParameters flinkParameters = this.getParameter();
        return flinkParameters != null && flinkParameters.getLanguageType() == FlinkTaskType.PYTHON;
    }
}
