package com.clubfactory.platform.scheduler.server.alarm;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.clubfactory.platform.common.constant.DateFormatPattern;
import com.clubfactory.platform.common.util.Assert;
import com.clubfactory.platform.common.util.DateUtil;
import com.clubfactory.platform.scheduler.core.utils.OkHttp3Utils;
import com.clubfactory.platform.scheduler.core.utils.SysConfigUtil;
import com.clubfactory.platform.scheduler.core.vo.JobOnlineVO;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.enums.ConfigType;
import com.clubfactory.platform.scheduler.dal.enums.JobCategoryEnum;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.clubfactory.platform.scheduler.dal.po.Task;
import com.clubfactory.platform.scheduler.server.alarm.dto.IMDto;
import com.clubfactory.platform.scheduler.server.alarm.dto.IMDto.Markdown;
import com.clubfactory.platform.scheduler.server.alarm.enums.MsgTypeEnum;
import com.clubfactory.platform.scheduler.server.constant.Constant;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IMNoticeService extends AbastractNoticeService implements INoticeService {
	
	@Override
	public void sendSuccessMsg(List<String> addresses, TaskVO task) {
		Assert.notNull(task);
		Assert.collectionNotEmpty(addresses, "地址列表");
		
		String content = super.getSuccessMsg(task);
		sendMsg(addresses, content);
	}

	@Override
	public void sendRetryMsg(List<String> addresses, TaskVO task) {
		Assert.notNull(task);
		Assert.collectionNotEmpty(addresses, "地址列表");
		
		String content = super.getRetryMsg(task);
		sendMsg(addresses, content);
		
	}

	@Override
	public void sendErrorMsg(List<String> addresses, TaskVO task) {
		Assert.notNull(task);
		Assert.collectionNotEmpty(addresses, "地址列表");
		
		String content = super.getFailedMsg(task);
		sendMsg(addresses, content);
		
	}
	
	protected void sendMsg(List<String> addresses,  String content) {
		IMDto dto = new IMDto();
		dto.setMsgType(MsgTypeEnum.MARKDOWN);
		Markdown markdown = new Markdown();
		markdown.setContent(content);
		dto.setMarkdown(markdown);
		
		for (String address : addresses) {
			String dtoString = JSON.toJSONString(dto).toLowerCase();
			String data = OkHttp3Utils.post(address, dtoString);
			//log.info("address:{}, dto:{}, response:{}", address, dtoString, data);
			if (!StringUtils.equals(data, "{\"errcode\":0,\"errmsg\":\"ok\"}")) {
				log.error("address:{}, dto:{}, res:{}",address, dtoString, data);
			}
		}
	}

	@Override
	public void sendDelayMsg(List<String> addresses, Integer delayDur, TaskVO task) {
		Assert.notNull(task);
		Assert.notNull(delayDur);
		Assert.collectionNotEmpty(addresses, "地址列表");
		
		String content = super.getDelayMsg(task, delayDur);
		sendMsg(addresses, content);
	}

	@Override
	public void sendPauseMsg(List<String> addresses, JobOnlineVO jobOnline) {
		Assert.notNull(jobOnline);
		Assert.collectionNotEmpty(addresses, "地址列表");
		
		String content = super.getPauseMsg(jobOnline);
		sendMsg(addresses, content);
		
	}

	@Override
	public void sendDataErrorMsg(List<String> addresses, TaskVO task) {
		Assert.collectionNotEmpty(addresses, "地址列表");
		
		String content = super.getDataErrorMsg(task);
		sendMsg(addresses, content);
	}
	
	public void sendTaskNotice(List<Task> tasks) {
		int taskNum = tasks.size();
		Map<JobCategoryEnum, List<Task>> taskMap = tasks.stream().collect(Collectors.groupingBy(Task::getCategory));
		int calNum = Optional.ofNullable(taskMap.get(JobCategoryEnum.CAL)).orElse(Lists.newArrayList()).size();
		int collectNum = Optional.ofNullable(taskMap.get(JobCategoryEnum.COLLECT)).orElse(Lists.newArrayList()).size();
		int refNum = Optional.ofNullable(taskMap.get(JobCategoryEnum.REFLUE)).orElse(Lists.newArrayList()).size();
		Long pauseNum = tasks.stream().filter(task -> task.getStatus() == TaskStatusEnum.PAUSE).collect(Collectors.counting());
		
		String date = DateUtil.format(new Date(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS);
		String msg = String.format(AlarmConstants.TASK_INIT_BODY, date, taskNum, collectNum, calNum, refNum, pauseNum);
		
		String url = SysConfigUtil.getByKey(Constant.ADMIN_IM_URL, ConfigType.WEB);
		this.sendMsg(Lists.newArrayList(url), msg);
		
	}
}
