package com.zhugeio.platform.scheduler.server.leader;

import com.zhugeio.platform.scheduler.server.leader.runnable.LoseListenRunnable;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;

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
