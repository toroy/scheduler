package com.clubfactory.platform.scheduler.engine.task.builtin.emr;

import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import org.slf4j.Logger;

/**
 * @author xiejiajun
 */
public class EmrSparkStreamingEtlTask extends EmrSparkStreamingTask {

    public EmrSparkStreamingEtlTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    public boolean isEtlJob() {
        return true;
    }
}
