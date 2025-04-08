package com.clubfactory.platform.scheduler.server.leader;

import org.junit.Test;

public class ListenerWorkServiceTest extends ZookeeperBaseTest {

	@Test
	public void runTest() {
		ListenerWorkService listenerWorkService = new ListenerWorkService(getClient(),getWorkPath());
		listenerWorkService.run();
	}
}
