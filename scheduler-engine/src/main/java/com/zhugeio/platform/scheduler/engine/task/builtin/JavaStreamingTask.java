package com.zhugeio.platform.scheduler.engine.task.builtin;

import com.zhugeio.platform.scheduler.core.vo.TaskVO;
import org.slf4j.Logger;


public class JavaStreamingTask extends JavaTask {
    public JavaStreamingTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    public boolean isStreamTask() {
        return true;
    }


}
