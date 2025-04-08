package com.clubfactory.platform.scheduler.server.alarm;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.clubfactory.platform.scheduler.core.vo.AlarmVO;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.enums.AlarmNoticeTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.AlarmTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.JobCategoryEnum;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.clubfactory.platform.scheduler.server.BaseTest;
import com.clubfactory.platform.scheduler.server.dto.NoticeDto;
import com.clubfactory.platform.scheduler.server.leader.runnable.AlertConsumerRunnable;

public class EmailServiceTest extends BaseTest {

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private AlertConsumerRunnable alertConsumerRunnable;
	@Autowired
	private EmailNoticeService emailNoticeService;
	
	@Value("#{${email.system}}")
	private Map<String, String> MAP_EMAIL;
	
	@Test
	public void check() {
		emailNoticeService.schedulerCheck();
	}
	
	@Test
	public void sendTest() throws InterruptedException {
		NoticeDto dto = new NoticeDto();
		AlarmVO alarm = new AlarmVO();
		alarm.setType(AlarmTypeEnum.FAILED);
		alarm.setNoticeType(AlarmNoticeTypeEnum.IM);
		alarm.setAddresses("soso24w@hotmail.com");
		alarm.setUserGroupId(332L);
		dto.setAlarm(alarm);
		
		TaskVO task = new TaskVO();
		task.setId(1L);
		task.setJobId(674L);
		task.setRetryMax(3);
		task.setCreateUser(1L);
		task.setRetryCount(1);
		task.setStatus(TaskStatusEnum.FAILED);
		task.setCategory(JobCategoryEnum.CAL);
		task.setName("测试");
		task.setExecTime(new Date());
		task.setEndTime(new Date());
		dto.setTask(task);
		
		alertConsumerRunnable.send(dto);
		
		Thread.sleep(10_000);
	}

	@Test
	public void sendSimpleMail() throws Exception {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("gaia_notice01@wholee.sale");
		message.setTo("soso24w@hotmail.com");
		message.setSubject("主题：简单邮件");
		message.setText("测试邮件内容");
		message.setSubject("测试主体");
		 
		
		for (int i = 0; i < 1; i++) { 
			mailSender.send(message);
		}
	}
	
	@Test
	public void sendJavaMail() throws MessagingException {
		JavaMailSenderImpl jms = new JavaMailSenderImpl();
		jms.setHost("smtp.gmail.com");
		jms.setPort(587);
		jms.setUsername("gaia_notice01@wholee.sale");
		jms.setPassword("rlsvudepbuabopun");
		jms.setDefaultEncoding("Utf-8");
		jms.setProtocol("smtp");
		Properties p = new Properties();
		p.setProperty("mail.smtp.auth", "true");
		p.setProperty("mail.smtp.starttls.enable", "true");
		jms.setJavaMailProperties(p);
		
		MimeMessage mimeMessage = jms.createMimeMessage();
		MimeMessageHelper helper;
		helper = new MimeMessageHelper(mimeMessage, true);
		helper.setFrom("gaia_notice05@wholee.sale");
		helper.setTo("soso24w@hotmail.com");
		helper.setSubject("主题：简单邮件");
		helper.setText("测试邮件内容JMS");
		helper.setSubject("测试主体");
		jms.send(mimeMessage);  
	}
	
	@Test
	public void emailProp() {
		//Map<String, String> mapInfo = emailDto.getInfo();
		for (Entry<String, String> map : MAP_EMAIL.entrySet()) {
			System.out.println(map.getKey() + " " + map.getValue());
		}
	}
}
