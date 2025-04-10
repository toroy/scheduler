package com.clubfactory.platform.scheduler.server.alarm;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.clubfactory.platform.scheduler.common.util.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.markdown4j.Markdown4jProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.clubfactory.platform.scheduler.core.vo.JobOnlineVO;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.server.alarm.dto.InvalidEmailDto;
import com.clubfactory.platform.scheduler.server.utils.BizUtils;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailNoticeService extends AbastractNoticeService implements INoticeService  {
	
	@Value("${spring.mail.username}")
	String FROM_MAIL;
	@Value("${spring.mail.default-encoding}")
	String DEFAULT_ENCODING;
	@Value("${spring.mail.host}")
	String HOST;
	@Value("${spring.mail.port}")
	Integer PORT;
	@Value("${spring.mail.protocol}")
	String PROTOCOL;
	@Value("${spring.mail.properties.mail.smtp.auth}")
	String SMTP_AUTH;
	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	String STARTTLS_ENABLE;
	@Value("#{${email.system}}")
	private Map<String, String> EMAIL_GROUP;
	@Value("#{'${email.manager}'.split(',')}")
	private List<String> MANAGER_GROUP;
	
	private long CHECK_DUR = 30 * 60 * 1_000;
	
	private List<InvalidEmailDto> INVALID_EMAILS = Lists.newArrayList();
	
	@Autowired
	JavaMailSender mailSender;
	
	
	@Scheduled(cron="0 * * * * ?")
	public void schedulerCheck() {
		if (CollectionUtils.isEmpty(INVALID_EMAILS)) {
			return;
		}
		Iterator<InvalidEmailDto> iterator = INVALID_EMAILS.iterator();
		Date date = new Date();
		while(iterator.hasNext()) {
			InvalidEmailDto invalidEmailDto = iterator.next();
			if (invalidEmailDto.getDate().getTime() < date.getTime() - CHECK_DUR) {
				iterator.remove();
			}
		}
		
	}

	@Override
	public void sendSuccessMsg(List<String> addresses, TaskVO task) {
		Assert.notNull(task);
		Assert.collectionNotEmpty(addresses, "地址列表");
		
		String subject = task.getName() + " 成功提醒";
		String text = super.getSuccessMsg(task);
		sendJavaMail(addresses, subject, text);
	}

	@Override
	public void sendRetryMsg(List<String> addresses, TaskVO task) {
		Assert.notNull(task);
		Assert.collectionNotEmpty(addresses, "地址列表");
		
		String subject = task.getName() + " 重试提醒";
		String text = super.getRetryMsg(task);
		sendJavaMail(addresses, subject, text);
	}

	@Override
	public void sendErrorMsg(List<String> addresses, TaskVO task) {
		Assert.notNull(task);
		Assert.collectionNotEmpty(addresses, "地址列表");
		
		String subject = task.getName() + " 失败提醒";
		String text = super.getFailedMsg(task);
		sendJavaMail(addresses, subject, text);
	}
	
	private void sendJavaMail(List<String> addresses, String subject, String text) {
		// 选出可用邮箱
		Set<String> userNames = EMAIL_GROUP.keySet();
		List<String> invalidUserNames = INVALID_EMAILS.stream().map(InvalidEmailDto::getUserName).collect(Collectors.toList());
		List<String> emails = userNames.stream().filter(userName -> !invalidUserNames.contains(userName)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(emails)) {
			String userName = BizUtils.getRand(emails);
			String password = EMAIL_GROUP.get(userName);
			// 转html
			String html;
			try {
				html = new Markdown4jProcessor().process(text);
			} catch (IOException e) {
				html = text;
				e.printStackTrace();
			}
			// 发送用户邮件
			Boolean isSuccess = send(addresses, subject, html, userName, password);
			if (isSuccess.equals(false)) {
				// 通知给管理员
				String manageSubject = String.format("系统通知：%s 邮箱不可用", userName);
				sendManageMsg(MANAGER_GROUP, manageSubject, manageSubject);
				// 发送用户邮件
				sendJavaMail(addresses, subject, text);
			}
		} else {
			// 通知给管理员
			String manageSubject = String.format("系统通知：所有邮箱不可用，请紧急处理");
			sendManageMsg(MANAGER_GROUP, manageSubject, manageSubject);
		}
	}
	
	private Boolean send(List<String> addresses, String subject, String text, String userName, String password) {
		JavaMailSenderImpl jms = new JavaMailSenderImpl();
		jms.setHost(HOST);
		jms.setPort(PORT);
		jms.setUsername(userName);
		jms.setPassword(password);
		jms.setDefaultEncoding(DEFAULT_ENCODING);
		jms.setProtocol(PROTOCOL);
		Properties p = new Properties();
		p.setProperty("mail.smtp.auth", SMTP_AUTH);
		p.setProperty("mail.smtp.starttls.enable", STARTTLS_ENABLE);
		jms.setJavaMailProperties(p);
		
		MimeMessage mimeMessage = jms.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(userName);
			helper.setTo(addresses.toArray(new String[addresses.size()]));
			helper.setSubject(subject);
			helper.setText(text, true);
			jms.send(mimeMessage);  
			return true;
		} catch (MessagingException e) {
			log.error("userName : {}, text : {}, error: {}", userName, text, e);
			InvalidEmailDto dto = new InvalidEmailDto();
			dto.setUserName(userName);
			dto.setDate(new Date());
			INVALID_EMAILS.add(dto);
			return false;
		}
	}
	
	private void sendManageMsg(List<String> addresses, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(FROM_MAIL);
		message.setTo(addresses.toArray(new String[addresses.size()]));
		message.setSubject(subject);
		message.setText(text);
		try {
			mailSender.send(message);
		} catch (Exception e) {
			log.error("text : {}, error: {}", text, e);
		}
		
	}

	@Override
	public void sendDelayMsg(List<String> addresses, Integer delayDur, TaskVO task) {
		Assert.notNull(task);
		Assert.notNull(delayDur);
		Assert.collectionNotEmpty(addresses, "地址列表");
		
		String subject = task.getName() + " 延迟告警";
		String text = super.getDelayMsg(task, delayDur);
		sendJavaMail(addresses, subject, text);
	}

	@Override
	public void sendPauseMsg(List<String> addresses, JobOnlineVO jobOnline) {
		Assert.notNull(jobOnline);
		Assert.collectionNotEmpty(addresses, "地址列表");
		
		String subject = jobOnline.getName() + " 暂停调度提醒";
		String text = super.getPauseMsg(jobOnline);
		sendJavaMail(addresses, subject, text);
		
	}

	@Override
	public void sendDataErrorMsg(List<String> addresses, TaskVO task) {
		Assert.notNull(task);
		Assert.collectionNotEmpty(addresses, "地址列表");
		
		String subject = task.getName() + " 数据校验失败提醒";
		String text = super.getDataErrorMsg(task);
		sendJavaMail(addresses, subject, text);
		
	}

}
