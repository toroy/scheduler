package com.clubfactory.platform.scheduler.server.leader;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;

import com.clubfactory.platform.scheduler.server.BaseTest;

public class ZookeeperBaseTest extends BaseTest {

	@Value("${zookeeper.quorum}")
	private String ZK_PORT;
	@Value("${zookeeper.scheduler.leader.path}")
	private String LEADER_PATH;
	@Value("${zookeeper.retry.interval}")
	private Integer INTERVAL;
	@Value("${zookeeper.retry.max-times}")
	private Integer MAX_TIMES;
	@Value("${zookeeper.scheduler.task.instance.path}")
	private String TASK_PATH;
	@Value("${zookeeper.scheduler.workers.path}")
	private String WORK_PATH;
	
	public CuratorFramework getClient() {
		CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(ZK_PORT, new ExponentialBackoffRetry(INTERVAL, MAX_TIMES));
		curatorFramework.start();
		return curatorFramework;
	}
	
	public String getTaskPath() {
		return TASK_PATH;
	}
	
	public String getWorkPath() {
		return WORK_PATH;
	}
}
