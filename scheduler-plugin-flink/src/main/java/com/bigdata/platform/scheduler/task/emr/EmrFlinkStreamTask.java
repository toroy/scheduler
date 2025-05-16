package com.bigdata.platform.scheduler.task.emr;

import com.bigdata.platform.scheduler.core.vo.TaskVO;
import org.slf4j.Logger;

/**
 * @author xiejiajun
 */
public class EmrFlinkStreamTask extends EmrFlinkBatchTask {
    public EmrFlinkStreamTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    public boolean isStreamTask() {
        return true;
    }

    @Override
    public String stepType() {
        return "Flink-Stream";
    }

}
