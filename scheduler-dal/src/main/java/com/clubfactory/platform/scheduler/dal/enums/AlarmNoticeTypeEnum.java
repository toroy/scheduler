package com.clubfactory.platform.scheduler.dal.enums;

import com.clubfactory.platform.common.util.ValidateUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AlarmNoticeTypeEnum implements IEnum {

	EMAIL("邮件") {
		@Override
		public Boolean isValid(String data) {
			return ValidateUtils.isValidEmail(data);
		}
		
	},
	PHONE_NO("手机号") {
		@Override
		public Boolean isValid(String data) {
			return ValidateUtils.isValidPhoneNo(data);
		}
	},
	IM("IM") {
		@Override
		public Boolean isValid(String data) {
			return ValidateUtils.isValidImRobotUrl(data);
		}
	};
	
	private String desc;
	
	public abstract Boolean isValid(String data);
	
	public static void main(String[] args) {
		System.out.println(IM.isValid("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=132af695-8172-460b-8291-ee758dbb647f"));
		System.out.println(PHONE_NO.isValid("12345672312"));
		System.out.println(PHONE_NO.isValid("13978292828"));
	}
}
