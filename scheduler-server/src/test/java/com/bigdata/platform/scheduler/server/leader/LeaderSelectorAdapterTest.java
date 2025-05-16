package com.bigdata.platform.scheduler.server.leader;

import com.bigdata.platform.scheduler.server.BaseTest;
import org.junit.Test;

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
