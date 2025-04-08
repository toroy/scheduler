package com.clubfactory.platform.scheduler.server.alarm.dto;

import java.io.Serializable;

import com.clubfactory.platform.scheduler.server.alarm.enums.MsgTypeEnum;

import lombok.Data;

@Data
public class IMDto implements Serializable {

	private static final long serialVersionUID = -609849669886685983L;

	/**
	 * 指定接收消息的成员, 当touser为”@all”时忽略本参数
	 */
	private String touser;
	
	/**
	 * 消息类型
	 */
	private MsgTypeEnum msgType;
	
	/**
	 * 正文内容
	 */
	private Text text;
	
	/**
	 * markdown格式
	 */
	private Markdown markdown;
	
	@Data
	public static class Text {
		
		/**
		 * 消息内容
		 */
		private String content;
		
	}
	
	@Data
	public static class Markdown {
		
		/**
		 * 消息内容
		 */
		private String content;
		
	}
}
