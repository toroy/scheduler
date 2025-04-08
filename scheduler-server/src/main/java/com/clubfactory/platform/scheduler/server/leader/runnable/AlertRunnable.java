package com.clubfactory.platform.scheduler.server.leader.runnable;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.clubfactory.platform.common.exception.BizException;
import com.clubfactory.platform.scheduler.server.utils.GuavaCacheUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clubfactory.platform.common.util.BeanUtil;
import com.clubfactory.platform.scheduler.core.service.impl.AlarmService;
import com.clubfactory.platform.scheduler.core.service.impl.JobOnlineService;
import com.clubfactory.platform.scheduler.core.service.impl.MachineService;
import com.clubfactory.platform.scheduler.core.service.impl.TaskService;
import com.clubfactory.platform.scheduler.core.service.impl.UserService;
import com.clubfactory.platform.scheduler.core.vo.AlarmVO;
import com.clubfactory.platform.scheduler.core.vo.JobOnlineVO;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.enums.AlarmNoticeTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.AlarmTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.clubfactory.platform.scheduler.dal.po.JobOnline;
import com.clubfactory.platform.scheduler.dal.po.Task;
import com.clubfactory.platform.scheduler.server.alarm.INoticeService;
import com.clubfactory.platform.scheduler.server.dto.NoticeDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlertRunnable extends AlertBaseService implements Runnable {

	@Resource
	TaskService taskService;
	@Resource
	AlarmService alarmService;
	@Resource
	JobOnlineService jobOnlineService;
	@Resource
	UserService userService;
	
	private static final int RELAY_NOTICE_COUNT = 3;

	private static final Long WAIT_ALARM = 30 * 60 * 1000L;
	
	@Override
	public void run() {
		
		try {
			runDelay();
		} catch (Exception e) {
			log.error("runDelay error", e);
		}
		try {
			//runPause();
		} catch (Exception e) {
			log.error("runPause error", e);
		}

		try {
			runSuccess();
		} catch (Exception e) {
			log.error("runSuccess error", e);
		}

		try {
			runRetry();
		} catch (Exception e) {
			log.error("runRetry error", e);
		}

		try {
			runFailed();
		} catch (Exception e) {
			log.error("runFailed error", e);
		}

		try {
			runDataFailed();
		} catch (Exception e) {
			log.error("runDataFailed error", e);
		}
		
		
	}

	public void runPause() {
		List<JobOnlineVO> onlineVOs = jobOnlineService.listPause();
		if (CollectionUtils.isEmpty(onlineVOs)) {
			return;
		}
		List<Long> jobIds = onlineVOs.stream().map(JobOnline::getJobId).collect(Collectors.toList());
		List<AlarmVO> alarms = alarmService.listByJobIds(jobIds, null);
		if (CollectionUtils.isEmpty(alarms)) {
			return;
		}
		Map<Long, List<AlarmVO>> alarmMap = alarms.stream().collect(Collectors.groupingBy(AlarmVO::getJobId));
		
		Date nowDate = new Date();
		for (JobOnlineVO jobOnlineVO : onlineVOs) {
			if (!isNotice(nowDate, jobOnlineVO.getUpdateTime())) {
				continue;
			}
			List<AlarmVO> alarmVos = alarmMap.get(jobOnlineVO.getJobId());
			if (CollectionUtils.isEmpty(alarmVos)) {
				continue;
			}
			generateAndSendPauseMsg(jobOnlineVO, alarmVos);
		}
	}

	private void generateAndSendPauseMsg(JobOnlineVO jobOnlineVO,
			List<AlarmVO> alarmVos) {
		Map<AlarmNoticeTypeEnum, List<AlarmVO>> alarmTypeMap = alarmVos.stream()
				.collect(Collectors.groupingBy(AlarmVO::getNoticeType));
		
		jobOnlineVO.setUserName(userService.getUserName(jobOnlineVO.getCreateUser()));
		for (Entry<AlarmNoticeTypeEnum, List<AlarmVO>> entries : alarmTypeMap.entrySet()) {
			List<AlarmVO> subAlarms = entries.getValue();
			List<String> addresses = listPauseAddress(subAlarms);
			INoticeService iNoticeService = super.getNoticeService(entries.getKey());
			iNoticeService.sendPauseMsg(addresses, jobOnlineVO);
		}
	}

	private List<String> listPauseAddress(List<AlarmVO> subAlarms) {
		Set<String> set = Sets.newHashSet();
		for (AlarmVO alarmVO : subAlarms) {
			String addreess = alarmVO.getAddresses();
			String[] strings = StringUtils.split(addreess, ",");
			set.addAll(Sets.newHashSet(strings));
		}
		return Lists.newArrayList(set);
	}

	// 3分钟后的，整秒通知
	private boolean isNotice(Date nowDate, Date date) {
		if (date.getMinutes() + 3 == nowDate.getMinutes()
				&& nowDate.getSeconds() == 0) {
			return true;
		} else {
			return false;
		}
	}

	// 延迟报警
	public void runDelay() {
		List<Task> taskVos = taskService.listByAlarmDelay();
		if (CollectionUtils.isEmpty(taskVos)) {
			return;
		}
		List<Long> jobIds = taskVos.stream().map(Task::getJobId).distinct().collect(Collectors.toList());
		List<AlarmVO> alarms = alarmService.listDelayByJobIds(jobIds);
		Map<Long, List<AlarmVO>> alarmMap = alarms.stream().filter(alarm -> alarm.getDelayDur() != null).collect(Collectors.groupingBy(AlarmVO::getJobId));
		Map<Long, Integer> taskCountMap = taskVos.stream().collect(Collectors.toMap(Task::getId, Task::getNoticeCount, (oldkey, newkey) -> newkey));
		
		Date date = new Date();
		List<Long> noticeIds = Lists.newArrayList();
		for (Task task : taskVos) {
			List<AlarmVO> vos = alarmMap.get(task.getJobId());
			if (CollectionUtils.isEmpty(vos)) {
				continue;
			}
			AlarmVO alarmVO = vos.get(0);
			
			Integer noticeCount = taskCountMap.get(task.getId());
			Long dur = getDur(task, alarmVO.getDelayDur());
			if (dur < date.getTime()
					&& noticeCount < RELAY_NOTICE_COUNT) {
				//this.send(alarmVO, task);
				NoticeDto noticeDto = new NoticeDto();
				noticeDto.setAlarm(alarmVO);
				TaskVO taskVO = new TaskVO();
				BeanUtil.copyBeanNotNull2Bean(task, taskVO);
				noticeDto.setTask(taskVO);
				addQueue(alarmVO, noticeDto);
				noticeIds.add(task.getId());
			}
		}
		// 更改通知时间
		if (CollectionUtils.isNotEmpty(noticeIds)) {
			taskService.updateNotice(noticeIds);
		}
	}
	
	private Long getDur(Task task, Integer delayDur) {
		Date current = task.getNoticeTime() != null ? task.getNoticeTime() : task.getStartTime();
		return delayDur * 60 * 1_000 + current.getTime();
	}

	@Transactional(rollbackFor = BizException.class)
	public void runSuccess() {
		List<Task> taskVOs = taskService.listBySuccessNotice();
		if (CollectionUtils.isEmpty(taskVOs)) {
			return;
		}
		// 报警
		List<Long> jobIds = taskVOs.stream().map(Task::getJobId).distinct().collect(Collectors.toList());
		List<AlarmVO> alarmVOs = alarmService.listByJobIds(jobIds, AlarmTypeEnum.SUCCESS);
		
		taskService.updateByTasks(taskVOs);
		prepareNotice(alarmVOs, taskVOs);
	}
	
	@Transactional(rollbackFor = BizException.class)
	public void runRetry() {
		List<Task> taskVOs = taskService.listByRetryNotice();
		if (CollectionUtils.isEmpty(taskVOs)) {
			return;
		}
		
		retryFailedTask(taskVOs);
		if (CollectionUtils.isEmpty(taskVOs)) {
			return;
		}
		log.info("重试实例个数：{}", taskVOs.size());
		
		// 报警
		List<AlarmVO> alarmVOs = listAlarms(taskVOs, AlarmTypeEnum.RETRY);
		// 重试报警
		updateRetry(taskVOs);
		prepareNotice(alarmVOs, taskVOs);
	}
	
	@Transactional(rollbackFor = BizException.class)
	public void runFailed() {
		List<Task> taskVOs = taskService.listByFailedNotice();
		if (CollectionUtils.isEmpty(taskVOs)) {
			return;
		}

		// 判定是否要重复通知, 并去除不通知的信息
		setIsNotice(taskVOs);
		if (CollectionUtils.isEmpty(taskVOs)) {
			return;
		}
		// 报警
		List<AlarmVO> alarmVOs = listAlarms(taskVOs, AlarmTypeEnum.FAILED);		
		// 失败报警
		taskService.updateIsNoticeByTasks(taskVOs);
		prepareNotice(alarmVOs, taskVOs);
	}

	private void setIsNotice(List<Task> taskVOs) {
		Iterator<Task> taskIterator = taskVOs.iterator();
		while (taskIterator.hasNext()) {
			Task task = taskIterator.next();
			Long taskId = task.getId();
			Object object = GuavaCacheUtil.get(taskId);
			if (object == null) {
				task.setIsNotice(false);
				GuavaCacheUtil.put(taskId, System.currentTimeMillis());
			} else {
				Long stamp = Long.parseLong(object.toString());
				if ((System.currentTimeMillis() - stamp) > WAIT_ALARM) {
					task.setIsNotice(true);
					GuavaCacheUtil.clear(taskId);
				} else {
					task.setIsNotice(false);
					taskIterator.remove();
				}
			}
		}
	}

	@Transactional(rollbackFor = BizException.class)
	public void runDataFailed() {
		List<Task> taskVOs = taskService.listByDataFailedNotice();
		if (CollectionUtils.isEmpty(taskVOs)) {
			return;
		}
		// 判定是否要重复通知
		setIsNotice(taskVOs);
		// 报警
		List<AlarmVO> alarmVOs = listAlarms(taskVOs, AlarmTypeEnum.FAILED);		
		// 失败报警
		taskService.updateIsNoticeByTasks(taskVOs);
		prepareNotice(alarmVOs, taskVOs);
	}

	private List<AlarmVO> listAlarms(List<Task> taskVOs, AlarmTypeEnum type) {
		List<Long> jobIds = taskVOs.stream().map(Task::getJobId).distinct().collect(Collectors.toList());
		return alarmService.listByJobIds(jobIds, type);
	}

	private void retryFailedTask(List<Task> taskVOs) {
		Date date = new Date();
		Iterator<Task> iterator = taskVOs.iterator();
		while(iterator.hasNext()) {
			Task task = iterator.next();
			Long timestamp = getDur(task);
			if (timestamp != null 
					&& task.getRetryCount() < task.getRetryMax()
					&& date.getTime() >  timestamp) {
				task.setStatus(TaskStatusEnum.INIT);
				// 通知时，要加1，实际加的值，在调度那
				task.setRetryCount(task.getRetryCount() + 1);
			}  else {
				iterator.remove();
			}
		}
	}

	private void updateRetry(List<Task> tasks) {
		List<Task> lastRetryTask = Lists.newArrayList();
		List<Task> retryTasks = Lists.newArrayList();
		for (Task task : tasks) {
			if (task.getRetryCount() < task.getRetryMax()) {
				retryTasks.add(task);
				continue;
			}
			lastRetryTask.add(task);
		}
		if (CollectionUtils.isNotEmpty(lastRetryTask)) {
			taskService.updateInitAndIpByTasks(lastRetryTask);
		}
		if (CollectionUtils.isNotEmpty(retryTasks)) {
			taskService.updateInitByTasks(retryTasks);
		}
	}

	private Long getDur(Task task) {
		if (task.getEndTime() == null 
				|| task.getRetryDur() == null) {
			return null;
		}
		return task.getEndTime().getTime() +  task.getRetryDur() * 60 * 1_000;
	}


	private void prepareNotice(List<AlarmVO> alarmVOs, List<Task> retryTasks) {
		if (CollectionUtils.isEmpty(alarmVOs)) {
			return;
		}
		
		// 发邮件
		Map<Long, List<AlarmVO>> alarmMap = alarmVOs.stream().collect(Collectors.groupingBy(AlarmVO::getJobId));
		for (Task task : retryTasks) {
			if (alarmMap.get(task.getJobId()) == null) {
				continue;
			}
			List<AlarmVO> subAlarmVOs = alarmMap.get(task.getJobId());
			for (AlarmVO alarmVO : subAlarmVOs) {
				NoticeDto noticeDto = new NoticeDto();
				noticeDto.setAlarm(alarmVO);
				TaskVO taskVO = new TaskVO();
				BeanUtil.copyBeanNotNull2Bean(task, taskVO);
				noticeDto.setTask(taskVO);
				
				addQueue(alarmVO, noticeDto);
				//this.send(alarmVO, task);
			}
		}
	}

	private void addQueue(AlarmVO alarmVO, NoticeDto noticeDto) {
		if (AlarmNoticeTypeEnum.EMAIL == alarmVO.getNoticeType()) {
			super.emailQueue.offer(noticeDto);
		} else if (AlarmNoticeTypeEnum.IM == alarmVO.getNoticeType()) {
			super.imQueue.offer(noticeDto);
		} else if (AlarmNoticeTypeEnum.PHONE_NO == alarmVO.getNoticeType()) {
			super.phoneQueue.offer(noticeDto);
		}
	}

}
