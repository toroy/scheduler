package com.clubfactory.platform.scheduler.task;

import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import org.slf4j.Logger;


/**
 * @author xiejiajun
 */
public class FlinkStreamTask extends FlinkBatchTask {

    public FlinkStreamTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    public boolean isStreamTask() {
        return true;
    }
}
