package com.clubfactory.platform.scheduler.server.leader;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.clubfactory.platform.scheduler.common.exception.BizException;
import com.clubfactory.platform.scheduler.core.enums.ErrorCode;
import com.clubfactory.platform.scheduler.core.service.impl.MachineService;

import lombok.extern.slf4j.Slf4j;



/**
 * 
 * 选举
 * 
 * @author zhoulijiang
 * @date 2019-12-11 20:10
 *
 */
@Service
@Slf4j
public class LeaderServer {

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
	@Resource
	MachineService machineService;
	
	private Object mutex = new Object();


	private LeaderSelectorAdapter selectorAdapter;

	public void run() {
		log.info(" ------- start -------- ");
		// 不在主ip列表的，return;
//		Boolean isMaster = machineService.isMaster(OSUtils.getHost());
//		if (BooleanUtils.isFalse(isMaster)) {
//			return;
//		}
		
		CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_PORT, new ExponentialBackoffRetry(INTERVAL, MAX_TIMES));
		selectorAdapter = new LeaderSelectorAdapter(client, LEADER_PATH, TASK_PATH, WORK_PATH, "Master");
		try {
			client.start();
			selectorAdapter.start();
		} catch (IOException e) {
			throw new BizException(ErrorCode.START_CURATOR_ERROR, e);
		} 
		synchronized (mutex) {
			try {
				mutex.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		log.info(" ------- end -------- ");
	}
}
