package com.clubfactory.platform.scheduler.server.leader;

import javax.annotation.Resource;

import org.junit.Test;

import com.clubfactory.platform.scheduler.server.BaseTest;
import com.clubfactory.platform.scheduler.server.leader.runnable.TaskReadyRunnable;

public class TaskReadyRunnableTest extends BaseTest {

	@Resource
	TaskReadyRunnable taskReadyRunnable;
	
	@Test
	public void runTest() {
		taskReadyRunnable.run();
	}
}
