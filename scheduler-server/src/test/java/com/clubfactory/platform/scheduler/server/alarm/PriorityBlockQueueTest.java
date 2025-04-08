package com.clubfactory.platform.scheduler.server.alarm;

import java.util.concurrent.PriorityBlockingQueue;

import com.clubfactory.platform.scheduler.core.vo.AlarmVO;
import com.clubfactory.platform.scheduler.dal.enums.AlarmTypeEnum;
import com.clubfactory.platform.scheduler.server.dto.NoticeDto;

public class PriorityBlockQueueTest {

	static PriorityBlockingQueue<NoticeDto> NOTICE_QUEUE = new PriorityBlockingQueue<NoticeDto>(5000);
	
	public static void main(String[] args) throws InterruptedException {
		NOTICE_QUEUE.add(genDto(AlarmTypeEnum.SUCCESS));
		NOTICE_QUEUE.add(genDto(AlarmTypeEnum.FAILED));
		NOTICE_QUEUE.add(genDto(AlarmTypeEnum.RETRY));
		NOTICE_QUEUE.add(genDto(AlarmTypeEnum.RETRY));
		NOTICE_QUEUE.add(genDto(AlarmTypeEnum.DELAY));
		
		while (true) {
			NoticeDto dto = NOTICE_QUEUE.poll();
			if (dto != null) {
				System.out.println(dto.getAlarm().getType());
			}
			Thread.sleep(1000L);
		}
		
	}

	private static NoticeDto genDto(AlarmTypeEnum alarmType) {
		NoticeDto dto = new NoticeDto();
		AlarmVO alarm = new AlarmVO();
		alarm.setType(alarmType);
		dto.setAlarm(alarm);
		return dto;
	}
}
