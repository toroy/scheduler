package com.clubfactory.platform.scheduler.server.leader.runnable;

import java.util.concurrent.BlockingQueue;

import com.clubfactory.platform.scheduler.server.alarm.PhoneNoticeService;
import org.springframework.stereotype.Service;

import com.clubfactory.platform.scheduler.core.utils.SpringBean;
import com.clubfactory.platform.scheduler.dal.enums.AlarmNoticeTypeEnum;
import com.clubfactory.platform.scheduler.server.alarm.EmailNoticeService;
import com.clubfactory.platform.scheduler.server.alarm.IMNoticeService;
import com.clubfactory.platform.scheduler.server.alarm.INoticeService;
import com.clubfactory.platform.scheduler.server.dto.NoticeDto;

@Service
public class AlertBaseService {
	
	protected BlockingQueue<NoticeDto> emailQueue;
	
	protected BlockingQueue<NoticeDto> imQueue;

	protected BlockingQueue<NoticeDto> phoneQueue;
	
	public void setQueue(BlockingQueue<NoticeDto> emailQueue, BlockingQueue<NoticeDto> imQueue, BlockingQueue<NoticeDto> phoneQueue) {
		this.emailQueue = emailQueue;
		this.imQueue = imQueue;
		this.phoneQueue = phoneQueue;
	}
	
	
	protected INoticeService getNoticeService(AlarmNoticeTypeEnum noticeTypeEnum) {
		INoticeService iNoticeService = null;
		switch (noticeTypeEnum) {
			case EMAIL:
				iNoticeService = SpringBean.getBean(EmailNoticeService.class);
				break;
			case IM:
				iNoticeService = SpringBean.getBean(IMNoticeService.class);
				break;
			case PHONE_NO:
				iNoticeService = SpringBean.getBean(PhoneNoticeService.class);
				break;
			default:
				break;
		}
		return iNoticeService;
	}
}
