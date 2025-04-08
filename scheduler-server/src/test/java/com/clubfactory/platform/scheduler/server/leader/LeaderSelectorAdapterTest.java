package com.clubfactory.platform.scheduler.server.leader;

import org.junit.Test;

import com.clubfactory.platform.scheduler.server.BaseTest;

public class LeaderSelectorAdapterTest extends BaseTest {

	@Test
	public void test() {
		LeaderSelectorAdapter adapter = new LeaderSelectorAdapter(null, null, null, null, null);
		try {
			adapter.takeLeadership(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
