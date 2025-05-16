package com.bigdata.platform.scheduler.server.leader;

import javax.annotation.Resource;

import com.bigdata.platform.scheduler.server.BaseTest;
import com.bigdata.platform.scheduler.server.leader.runnable.TaskReadyRunnable;
import org.junit.Test;

public class TaskReadyRunnableTest extends BaseTest {

	@Resource
    TaskReadyRunnable taskReadyRunnable;
	
	@Test
	public void runTest() {
		taskReadyRunnable.run();
	}
}
