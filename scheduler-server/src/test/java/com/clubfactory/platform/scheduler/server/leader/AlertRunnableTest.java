package com.clubfactory.platform.scheduler.server.leader;

import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Resource;

import org.junit.Test;

import com.clubfactory.platform.scheduler.server.BaseTest;
import com.clubfactory.platform.scheduler.server.dto.NoticeDto;
import com.clubfactory.platform.scheduler.server.leader.runnable.AlertConsumerRunnable;
import com.clubfactory.platform.scheduler.server.leader.runnable.AlertRunnable;

public class AlertRunnableTest extends BaseTest {

	@Resource
	AlertRunnable alertRunnable;
	@Resource
	AlertConsumerRunnable alertConsumerRunnable;
	
	private LinkedBlockingQueue<NoticeDto> NOTICE_QUEUE = new LinkedBlockingQueue<NoticeDto>(5000);
	
	private void init() {
		alertRunnable.setQueue(NOTICE_QUEUE, NOTICE_QUEUE, NOTICE_QUEUE);
		alertConsumerRunnable.setQueue(NOTICE_QUEUE, NOTICE_QUEUE, NOTICE_QUEUE);
		alertConsumerRunnable.run();
	}
	
	@Test
	public void runPauseTest() {
		init();
		alertRunnable.runPause();
		end();
	}
	
	@Test
	public void runDelayTest() {
		init();
		alertRunnable.runDelay();
		end();
	}
	
	@Test
	public void runRetryTest() {
		init();
		alertRunnable.runRetry();
		end();
	}
	
	@Test
	public void runFailedTest() {
		init();
		for (int i = 0; i < 10; i++) {
			alertRunnable.runFailed();
		}
		end();
	}
	
	@Test
	public void runDataFailedTest() {
		init();
		alertRunnable.runDataFailed();
		end();
	}
	
	@Test
	public void runSuccessTest() {
		init();
		alertRunnable.runSuccess();
		end();
	}
	

	private void end() {
		try {
			Thread.sleep(5_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
