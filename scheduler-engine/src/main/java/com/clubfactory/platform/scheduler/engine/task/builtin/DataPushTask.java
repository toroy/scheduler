package com.clubfactory.platform.scheduler.engine.task.builtin;

import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import org.slf4j.Logger;


/**
 * 数据回流任务
 * @author xiejiajun
 */
public class DataPushTask extends DataPullTask{
    public DataPushTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }
}
