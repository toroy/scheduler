package com.clubfactory.platform.scheduler.server.alarm;

import com.clubfactory.boot.autoconfigure.vms.VmsProxy;
import com.clubfactory.platform.common.util.Assert;
import com.clubfactory.platform.scheduler.core.proxy.LettuceProxy;
//import com.clubfactory.platform.scheduler.core.proxy.VoiceProxy;
import com.clubfactory.platform.scheduler.core.vo.JobOnlineVO;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class PhoneNoticeService extends AbastractNoticeService implements INoticeService {
//    private static final String PHONE_NOTICE_ADDRESS = "http://dayu-notice.huoli101.com/msg/send";

    private static final String WHOLEE_COMMON_VOICE_TEMPLATE_ID = "1479026";

    // 超时单位5分钟
    private static final Integer TIME_OUT = 5;
    private static final Integer CALL_TIME_OUT = 10;

    private static String active;
    private static String appName;

    @Autowired
    Environment environment;

//    @Autowired
//    VoiceProxy voiceClient;

    @Autowired
    private VmsProxy proxy;
    @Autowired
    LettuceProxy lettuceProxy;
    @PostConstruct
    public void init() {
        active = environment.getActiveProfiles()[0];
        appName = environment.getProperty("spring.application.name");
    }
    @Override
    public void sendSuccessMsg(List<String> addresses, TaskVO task) {
        Assert.notNull(task);
        Assert.collectionNotEmpty(addresses, "地址列表");

        String content = super.getSuccessMsgShort(task);
        sendMsg(addresses, content);
    }

    @Override
    public void sendRetryMsg(List<String> addresses, TaskVO task) {
        Assert.notNull(task);
        Assert.collectionNotEmpty(addresses, "地址列表");

        String content = super.getRetryMsgShort(task);
        sendMsg(addresses, content);

    }

    @Override
    public void sendDelayMsg(List<String> addresses, Integer delayDur, TaskVO task) {
        Assert.notNull(task);
        Assert.notNull(delayDur);
        Assert.collectionNotEmpty(addresses, "地址列表");

        String content = super.getDelayMsgShort(task);
        sendMsg(addresses, content);
    }

    @Override
    public void sendPauseMsg(List<String> addresses, JobOnlineVO jobOnline) {
        Assert.notNull(jobOnline);
        Assert.collectionNotEmpty(addresses, "地址列表");

        String content = super.getPauseMsgShort(jobOnline);
        sendMsg(addresses, content);

    }

    @Override
    public void sendErrorMsg(List<String> addresses, TaskVO task) {
        Assert.notNull(task);
        Assert.collectionNotEmpty(addresses, "地址列表");

        String content = super.getFailedMsgShort(task);
        sendMsg(addresses, content);

    }

    @Override
    public void sendDataErrorMsg(List<String> addresses, TaskVO task) {
        Assert.collectionNotEmpty(addresses, "地址列表");

        String content = super.getDataErrorMsgShort(task);
        sendMsg(addresses, content);
    }

    private void sendMsg(List<String> addresses, String title) {
        callPhone(Sets.newHashSet(addresses),title);
    }
//    public void callPhone(Set<String> mobiles, String text) {
//        Assert.collectionNonEmpty(mobiles, "电话列表");
//        Assert.notBlank(text, "文本");
//        // 有一人成功，就置为成功
//        List<String> errors = Lists.newArrayList();
//        boolean isSuccess = false;
//        for (String mobile : mobiles) {
//            if (lettuceProxy.setIfAbsent(getRedisKey(mobile), "1", TIME_OUT)) {
//                try {
//                    voiceClient.doSend(voiceClient.genRequest(text, mobile, WHOLEE_COMMON_VOICE_TEMPLATE_ID));
//                    isSuccess = true;
//                } catch (Exception e) {
//                    errors.add(String.format("mobile %s, error: %s", mobile, e.getMessage()));
//                }
//            } else {
//                String errorMsg = String.format("mobile %s, is reject", mobile);
//                log.warn(errorMsg);
//                errors.add(errorMsg);
//            }
//            lettuceProxy.setIfAbsent(getContentRedisKey(mobile), text, CALL_TIME_OUT);
//        }
//        log.info("callNumber: name:{}, mobiles: {}, errorMsg: {}",text, StringUtils.join(mobiles),StringUtils.join(errors));
//    }

    public void callPhone(Set<String> mobiles, String text) {
        Assert.collectionNonEmpty(mobiles, "电话列表");
        Assert.notBlank(text, "文本");
        boolean isSuccess = false;
        String errmsg = proxy.callPhone(mobiles, text);
        log.info("callNumber: name:{}, mobiles: {}, errorMsg: {}",text, StringUtils.join(mobiles),errmsg);
    }

    private String getError(List<String> errors) {
        String errorMsg = null;
        if (CollectionUtils.isNotEmpty(errors)) {
            errorMsg = StringUtils.join(errors, ";");
        }
        return errorMsg;
    }

    public String getRedisKey(String data) {
        return String.format("%s_%s_%s", appName, active, data);
    }

    public String getContentRedisKey(String data) {
        return String.format("content_%s_%s_%s", appName, active, data);
    }

}
