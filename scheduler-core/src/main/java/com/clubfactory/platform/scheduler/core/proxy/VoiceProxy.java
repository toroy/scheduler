//package com.clubfactory.platform.scheduler.core.proxy;
//
//import com.alibaba.fastjson.JSON;
//import com.clubfactory.platform.common.exception.BizException;
//import com.tencentcloudapi.common.Credential;
//import com.tencentcloudapi.common.profile.ClientProfile;
//import com.tencentcloudapi.common.profile.HttpProfile;
//import com.tencentcloudapi.vms.v20200902.VmsClient;
//import com.tencentcloudapi.vms.v20200902.models.SendTtsVoiceRequest;
//import com.tencentcloudapi.vms.v20200902.models.SendTtsVoiceResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//
///**
// * TODO
// *
// * @author zhoulijiang
// * @date 2022/6/20 3:02 下午
// **/
//@Service
//@Slf4j
//public class VoiceProxy {
//
//    @Value("${voice.secret.id}")
//    String voiceSecretId;
//
//    @Value("${voice.secret.key}")
//    String voiceSecretKey;
//
//    @Value("${voice.app.id}")
//    String voiceSdkAppid;
//
//    static VmsClient client;
//
//    @PostConstruct
//    public void init() throws Exception {
//        Credential cred = new Credential(voiceSecretId, voiceSecretKey);
//        // 实例化一个http选项，可选，没有特殊需求可以跳过
//        HttpProfile httpProfile = new HttpProfile();
//        httpProfile.setReqMethod("POST");
//        /* SDK有默认的超时时间，非必要请不要进行调整
//         * 如有需要请在代码中查阅以获取最新的默认值 */
//        httpProfile.setConnTimeout(60);
//        httpProfile.setEndpoint("vms.tencentcloudapi.com");
//        /* 非必要步骤:
//         * 实例化一个客户端配置对象，可以指定超时时间等配置 */
//        ClientProfile clientProfile = new ClientProfile();
//        /* SDK默认用TC3-HMAC-SHA256进行签名
//         * 非必要请不要修改这个字段 */
//        clientProfile.setSignMethod("TC3-HMAC-SHA256");
//        clientProfile.setHttpProfile(httpProfile);
//        client = new VmsClient(cred, "ap-guangzhou", clientProfile);
//    }
//
//    public void doSend(SendTtsVoiceRequest request) {
//        try {
//            SendTtsVoiceResponse response = client.SendTtsVoice(request);
//            log.info("[VoiceAlarm] query params = {}, response data = {}", JSON.toJSONString(request), JSON.toJSONString(response));
//        } catch (Exception e) {
//            throw new BizException(e.getMessage());
//        }
//
//    }
//
//    public SendTtsVoiceRequest genRequest(String text, String mobile, String templateId) {
//        SendTtsVoiceRequest request = new SendTtsVoiceRequest();
//        if(!mobile.startsWith("+")) {
//            mobile = "+86" + mobile;
//        }
//        request.setCalledNumber(mobile);
//        request.setTemplateParamSet(new String[] {text});
//        request.setVoiceSdkAppid(voiceSdkAppid);
//        request.setTemplateId(templateId);
//        return request;
//    }
//
//
//}
