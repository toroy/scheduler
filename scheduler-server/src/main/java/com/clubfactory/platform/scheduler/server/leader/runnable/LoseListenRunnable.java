package com.clubfactory.platform.scheduler.server.leader.runnable;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;

import com.clubfactory.platform.common.constant.DateFormatPattern;
import com.clubfactory.platform.common.util.DateUtil;
import com.clubfactory.platform.scheduler.core.enums.CommandType;
import com.clubfactory.platform.scheduler.core.vo.ScriptVO;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.clubfactory.platform.scheduler.dal.po.Task;
import com.clubfactory.platform.scheduler.engine.utils.TaskLock;
import com.clubfactory.platform.scheduler.core.dto.TaskDto;
import com.clubfactory.platform.scheduler.server.utils.BizUtils;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhoulijiang
 */
@Slf4j
public class LoseListenRunnable extends SchedulerBaseService implements Runnable {

	private static final int SECOND = 30;

	private static final int WAIT_LOCK_SECOND = 10;
	
	private static final int MAX_SUB_TASKS = 10;
	
	// 超时时间
	private static final int OVER_TIME = 60 * 1000;
	
	public LoseListenRunnable(CuratorFramework client, String taskPath) {
		super.init();
		super.client = client;
		super.taskPath = taskPath;
	}
	
	@Override
	public void run() {
		// 每隔三十秒获取 scheduler状态的task
		LocalDateTime localDateTime = LocalDateTime.now().minusSeconds(SECOND);
		List<Task> tasks = taskService.listByTimeover(DateUtil.format(DateUtil.getDate(localDateTime), DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
		if (CollectionUtils.isEmpty(tasks)) {
			return;
		}

		// 限制提交个数
		List<Task> subTasks = getSubTasks(tasks);
		log.info("漏监听个数 {}, 执行个数 {}", tasks.size(), subTasks.size());

		// 处理漏监听的task
		doLoseTask(subTasks);
	}

	private void doLoseTask(List<Task> subTasks) {
		Date date = new Date();
		for (Task task : subTasks) {
			// 超时
			long dur = getDur(date, task);
			if (dur <= OVER_TIME) {
				continue;
			}

			// 加锁成功，改回ready, 重试次数-1
			Long taskId = task.getId();
			TaskLock taskLock = new TaskLock(super.client, taskId);
			if (taskLock.tryLock(WAIT_LOCK_SECOND)) {
				try {
					// 如果状态没有变更，继续执行
					if (!taskService.checkIsStatus(taskId, TaskStatusEnum.SCHEDULED)) {
						continue;
					}
					String path = super.getPath(task.getIp(), taskId);
					if (super.isExist(path)) {
						super.deleteZkPath(path);
					}
					// 改回ready
					log.info("lose listen taskId : {}, retryCount: {}", taskId, task.getRetryCount());
					taskService.updateByLost(taskId);
				} catch (Exception e) {
					log.warn("taskId: {}, not exist", taskId, e);
				} finally {
					taskLock.unlock();
				}
			}

		}
	}

	private List<Task> getSubTasks(List<Task> tasks) {
		List<Task> subTasks = Lists.newArrayList();
		if (tasks.size() > MAX_SUB_TASKS) {
			subTasks = tasks.subList(0, MAX_SUB_TASKS);
		} else {
			subTasks.addAll(tasks);
		}
		return subTasks;
	}

	private long getDur(Date date, Task task) {
		return date.getTime() - task.getStartTime().getTime();
	}


}
