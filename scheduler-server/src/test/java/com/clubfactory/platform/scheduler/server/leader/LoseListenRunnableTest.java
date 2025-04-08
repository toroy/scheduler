package com.clubfactory.platform.scheduler.server.leader;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;

import com.clubfactory.platform.scheduler.server.leader.runnable.LoseListenRunnable;

public class LoseListenRunnableTest extends ZookeeperBaseTest {

	@Test
	public void runTest() {
		CuratorFramework client = super.getClient();
		client.start();
		String taskPath = super.getTaskPath();
		
		LoseListenRunnable loseListenRunnable = new LoseListenRunnable(client, taskPath);
		loseListenRunnable.run();
	}
}
