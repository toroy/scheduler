package com.zhugeio.platform.scheduler.server.leader;

import com.zhugeio.platform.scheduler.server.BaseTest;
import com.zhugeio.platform.scheduler.server.leader.runnable.DqcTaskRunnable;
import org.junit.Test;

import javax.annotation.Resource;

public class DqcTaskRunnableTest extends BaseTest {


    @Resource
    DqcTaskRunnable dqcTaskRunnable;

    @Test
    public void runTest() {
        dqcTaskRunnable.run();
    }
}
