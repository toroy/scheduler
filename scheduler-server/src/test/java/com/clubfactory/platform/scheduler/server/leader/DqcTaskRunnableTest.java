package com.clubfactory.platform.scheduler.server.leader;

import com.clubfactory.platform.scheduler.server.BaseTest;
import com.clubfactory.platform.scheduler.server.leader.runnable.DqcTaskRunnable;
import com.clubfactory.platform.scheduler.server.leader.runnable.JobInitRunnable;
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
