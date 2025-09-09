package com.zhugeio.platform.scheduler.server.leader;

import com.zhugeio.platform.scheduler.server.BaseTest;
import com.zhugeio.platform.scheduler.server.leader.runnable.TaskMachineMonitorRunnable;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * TODO
 *
 * @author zhoulijiang
 * @date 2025/9/10 00:13
 **/
public class TaskMachineMonitorRunnableTest  extends BaseTest {

    @Resource
    TaskMachineMonitorRunnable taskMachineMonitorRunnable;

    @Test
    public void runTest() {
        taskMachineMonitorRunnable.run();
    }
}
