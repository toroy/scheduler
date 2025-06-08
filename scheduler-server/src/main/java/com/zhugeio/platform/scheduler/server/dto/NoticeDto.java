package com.zhugeio.platform.scheduler.server.dto;

import java.io.Serializable;

import com.zhugeio.platform.scheduler.core.vo.AlarmVO;
import com.zhugeio.platform.scheduler.core.vo.TaskVO;

import lombok.Data;

@Data
public class NoticeDto implements Comparable<NoticeDto>, Serializable {

	private static final long serialVersionUID = -100967403685902262L;

	private AlarmVO alarm;
	
	private TaskVO task;



	@Override
	public int compareTo(NoticeDto no) {
		if (no == null || no.getAlarm() == null || no.getAlarm().getType() == null) {
			return 0;
		}
		return this.getAlarm().getType().compareTo(no.getAlarm().getType());
	}
}
