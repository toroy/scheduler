package com.bigdata.platform.scheduler.engine.task.builtin;

import com.bigdata.platform.scheduler.core.vo.TaskVO;
import org.slf4j.Logger;

/**
 * @author xiejiajun
 */
public class SparkStreamingEtlTask extends SparkStreamingTask {

    public SparkStreamingEtlTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    public boolean isEtlJob() {
        return true;
    }
}
