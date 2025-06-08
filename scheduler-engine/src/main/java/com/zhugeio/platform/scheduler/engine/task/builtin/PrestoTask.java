package com.zhugeio.platform.scheduler.engine.task.builtin;

import com.zhugeio.platform.scheduler.core.vo.TaskVO;
import org.slf4j.Logger;

public class PrestoTask extends PythonTask {
    public PrestoTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }
}
