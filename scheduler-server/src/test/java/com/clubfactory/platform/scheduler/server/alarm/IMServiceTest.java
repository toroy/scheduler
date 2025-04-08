package com.clubfactory.platform.scheduler.server.alarm;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.clubfactory.platform.scheduler.core.utils.OkHttp3Utils;
import com.clubfactory.platform.scheduler.server.BaseTest;
import com.clubfactory.platform.scheduler.server.alarm.dto.IMDto;
import com.clubfactory.platform.scheduler.server.alarm.dto.IMDto.Text;
import com.clubfactory.platform.scheduler.server.alarm.enums.MsgTypeEnum;


public class IMServiceTest extends BaseTest {
	
	@Value("${im.token}")
	String token;
	
	@Test
	public void sendTest() throws InterruptedException {
		String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=%s", token);
		
		IMDto dto = new IMDto();
		dto.setMsgType(MsgTypeEnum.TEXT);
		dto.setTouser("@all");
		
		Text text = new Text();
		text.setContent("测试content");
		dto.setText(text);
		
		String data = OkHttp3Utils.post(url, JSON.toJSONString(dto).toLowerCase());
		System.out.println(data);
		
		Thread.sleep(20000);
		
	}
	

	
}
