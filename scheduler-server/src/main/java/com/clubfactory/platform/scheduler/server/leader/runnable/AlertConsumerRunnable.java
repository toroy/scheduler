package com.clubfactory.platform.scheduler.server.leader.runnable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.clubfactory.platform.scheduler.core.service.impl.UserInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.clubfactory.platform.scheduler.core.service.impl.UserService;
import com.clubfactory.platform.scheduler.core.utils.SysConfigUtil;
import com.clubfactory.platform.scheduler.core.vo.AlarmVO;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.enums.AlarmNoticeTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.AlarmTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.ConfigType;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.clubfactory.platform.scheduler.server.alarm.INoticeService;
import com.clubfactory.platform.scheduler.server.constant.Constant;
import com.clubfactory.platform.scheduler.server.dto.NoticeDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AlertConsumerRunnable extends AlertBaseService implements Runnable {

	ExecutorService executors = Executors.newFixedThreadPool(5);

	@Resource
	UserInfoService userInfoService;

	@Value("${task.logger.uri}")
	private String taskLoggerUri;
	@Resource
	private UserService userService;
	
	private String WEB_HOST;
	
	@Override
	public void run() {
		counsumerQueue(super.emailQueue);
		counsumerQueue(super.imQueue);
		counsumerQueue(super.phoneQueue);
	}

	private void counsumerQueue(BlockingQueue<NoticeDto> queue) {
		executors.submit(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						NoticeDto data = queue.poll(1, TimeUnit.SECONDS);
						if (data != null) {
							log.info("------------- 开始消费 --------------");
							send(data);
							Thread.sleep(1000);
						} else {
							Thread.sleep(2000);
						}
					} catch (Exception e) {
						log.error("消费失败", e);
					}
				}
			}
		});
	}
	
	public void send(NoticeDto dto) {
		AlarmVO alarmVO = dto.getAlarm();
		TaskVO task = dto.getTask();
		addExt(task);
		
		AlarmTypeEnum alarmType = alarmVO.getType();
		AlarmNoticeTypeEnum noticeTypeEnum = alarmVO.getNoticeType();

		if (alarmVO.getUserGroupId() == null) {
			return;
		}
		// 通过组id获取联系人信息
		Map<AlarmNoticeTypeEnum, List<String>> alarmNoticeMap = userInfoService.getNoticeMapByGroupId(alarmVO.getUserGroupId());
		List<String> addresses = alarmNoticeMap.get(noticeTypeEnum);
		if (CollectionUtils.isEmpty(addresses)) {
			return;
		}
		
		INoticeService iNoticeService = super.getNoticeService(noticeTypeEnum);
		if (iNoticeService == null) {
			return;
		}
		
		if (AlarmTypeEnum.FAILED == alarmType) {
			if (TaskStatusEnum.FAILED == task.getStatus()) {
				iNoticeService.sendErrorMsg(addresses, task);
			} else if (TaskStatusEnum.DATA_FAILED == task.getStatus()) {
				iNoticeService.sendDataErrorMsg(addresses, task);
			}
		} else if (AlarmTypeEnum.RETRY == alarmType) {
			iNoticeService.sendRetryMsg(addresses, task);
		} else if (AlarmTypeEnum.SUCCESS == alarmType) {
			iNoticeService.sendSuccessMsg(addresses, task);
		} else if (AlarmTypeEnum.DELAY == alarmType) {
			iNoticeService.sendDelayMsg(addresses, alarmVO.getDelayDur(), task);
		}
	}

	private void addExt(TaskVO task) {
		if (WEB_HOST == null) {
			WEB_HOST = SysConfigUtil.getByKey(Constant.WEB_HOST, ConfigType.WEB);
		}
		task.setLogUrl(WEB_HOST+taskLoggerUri+task.getId());
		task.setUsername(userService.getUserName(task.getCreateUser()));
	}
}
