package com.bigdata.platform.scheduler.engine.task.builtin;

import com.bigdata.platform.scheduler.core.vo.TaskVO;
import org.slf4j.Logger;

public class PrestoTask extends PythonTask {
    public PrestoTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }
}
