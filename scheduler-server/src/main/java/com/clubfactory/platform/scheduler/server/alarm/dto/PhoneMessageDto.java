package com.clubfactory.platform.scheduler.server.alarm.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Data
public class PhoneMessageDto implements Serializable {
    private List<String> noticeTypes = Arrays.asList("PHONE");

    private String title;

    private Meta meta;

    @Data
    public static class Meta {

        /**
         * 消息内容
         */
        private List<String> phones;

    }
}
