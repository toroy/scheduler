package com.clubfactory.platform.scheduler.engine.task.builtin;

import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import org.slf4j.Logger;

/**
 * 数据采集任务
 * @author xiejiajun
 */
public class DataPullTask extends PythonTask {
    public DataPullTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    public boolean isEtlJob() {
        return true;
    }
}
