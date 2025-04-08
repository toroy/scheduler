package com.clubfactory.platform.scheduler.server.leader;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.clubfactory.platform.scheduler.server.leader.runnable.*;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.CancelLeadershipException;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.zookeeper.CreateMode;

import com.clubfactory.platform.scheduler.core.service.impl.TaskService;
import com.clubfactory.platform.scheduler.core.thread.ThreadUtils;
import com.clubfactory.platform.scheduler.core.utils.SpringBean;
import com.clubfactory.platform.scheduler.core.utils.SysConfigUtil;
import com.clubfactory.platform.scheduler.server.constant.Constant;
import com.clubfactory.platform.scheduler.server.dto.NoticeDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 成为主后的一切准备工作
 *
 * @author zhoulijiang
 * @date 2019-12-11 20:10
 *
 */
@Slf4j
public class LeaderSelectorAdapter extends LeaderSelectorListenerAdapter implements Closeable {

	private String leadPath;
	private String taskPath;
	private String workPath;
	private final String name;
	private final LeaderSelector leaderSelector;
	private CuratorFramework client;
	private TaskService taskService = SpringBean.getBean(TaskService.class);
	private JobInitRunnable jobInitRunnable = SpringBean.getBean(JobInitRunnable.class);
	private AlertRunnable alertRunnable = SpringBean.getBean(AlertRunnable.class);
	private AlertConsumerRunnable alertConsumerRunnable = SpringBean.getBean(AlertConsumerRunnable.class);

	private ScheduledExecutorService scheduledPool = ThreadUtils.newDaemonThreadScheduledExecutor(
			"Yarn-Task-Monitor-Thread-Executor",
			1);
	private TaskMonitorRunnable taskMonitorRunnable = SpringBean.getBean(TaskMonitorRunnable.class);

	// 告警队列
	private PriorityBlockingQueue<NoticeDto> NOTICE_QUEUE = new PriorityBlockingQueue<NoticeDto>(5000);
	
	private static final int MAX_QUEUE_NUM = 2000;
	ExecutorService executors = new ThreadPoolExecutor(20, 20,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(MAX_QUEUE_NUM));
	
	public LeaderSelectorAdapter(CuratorFramework client, String leadPath, String taskPath,String workPath, String name) {
		this.client = client;
		this.name = name;
		this.leadPath = leadPath;
		this.taskPath = taskPath;
		this.workPath = workPath;
		leaderSelector = new LeaderSelector(client, leadPath, this);
		leaderSelector.autoRequeue();
	}

	public void start() throws IOException {
		leaderSelector.start();
	}

	@Override
	public void close() throws IOException {
		leaderSelector.close();
	}

	@Override
	public void takeLeadership(CuratorFramework client) throws Exception {
		log.info("I am leader");
		
		// 初始化节点
		createNodePath(leadPath);
		createNodePath(taskPath);
		
		// 清理表
		//taskService.initReadyByLeaderFailed();
		
		// 设置为主
		jobInitRunnable.setIsLeader(true);
		
		// 告警消息消费
		alertRunnable.setQueue(NOTICE_QUEUE, NOTICE_QUEUE, NOTICE_QUEUE);
		alertConsumerRunnable.setQueue(NOTICE_QUEUE, NOTICE_QUEUE, NOTICE_QUEUE);
		executors.submit(alertConsumerRunnable);
		
		// 通过观察workers临时节点，确定是否存活
		executors.submit(new ListenerWorkService(client, workPath));

		//定时监控任务
		int period = Optional.ofNullable(SysConfigUtil.getNumberByKey(Constant.TASK_MONITOR_PERIOD)).orElse(10);
		scheduledPool.scheduleAtFixedRate(taskMonitorRunnable, 0, period, TimeUnit.SECONDS);
		
		ThreadPoolExecutor tpe = ((ThreadPoolExecutor) executors);
		while(true) {
			try {
				holdExecutorsQueue(tpe);
				
				// 初始化job任务
				log.info("--------------- 开始任务 -----------");
				executors.submit(jobInitRunnable);
				// task改为ready
				executors.submit(SpringBean.getBean(TaskReadyRunnable.class));
				// 调度task干活
				executors.submit(new TaskSechdeulerRunnable(client, taskPath, workPath));
				// 补录场景
				executors.submit(new LoseListenRunnable(client, taskPath));
				// 寻找杀死的作业
				executors.submit(new KillRunnable(client, taskPath));
				// 告警通知
				executors.submit(alertRunnable);
				// dqc实例检测
				executors.submit(new DqcTaskRunnable());

				// 睡5秒
				Thread.sleep(10_000);
				log.info("--------------- 结束任务 -----------");
			} catch (Exception e) {
				log.error("scheduler main error" + e);
			}
			
		}

	}

	// 排队超过一半，睡一分钟
	private void holdExecutorsQueue(ThreadPoolExecutor tpe) throws InterruptedException {
		int queueSize = tpe.getQueue().size();
		if (queueSize > MAX_QUEUE_NUM / 2) {
			log.error("thread num quote overflow, queue:" + queueSize);
			Thread.sleep(60_000);
		}
	}
	
	private void createNodePath(String zNodeParentPath) throws Exception {
	    if (client.checkExists().forPath(zNodeParentPath) == null) {
	    	client.create().creatingParentContainersIfNeeded()
					.withMode(CreateMode.PERSISTENT).forPath(zNodeParentPath);
		}
	}
	
    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        if (newState == ConnectionState.LOST) {
        	log.info("------------------ lose leader -----------------");
        	// 失去主，清理工作
    		// taskService.initReadyByLeaderFailed();
    		jobInitRunnable.setIsLeader(false);
            throw new CancelLeadershipException();
        }
    }
    
	
}
