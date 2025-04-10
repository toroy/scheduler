package com.clubfactory.platform.scheduler.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class ValidateUtils {
    static Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    static Pattern IM_PATTERN = Pattern.compile("https://qyapi.weixin.qq.com/cgi-bin/webhook/send\\?key=.*");
    static Pattern PHONE_PATTERN = Pattern.compile("^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$");

    public ValidateUtils() {
    }

    public static boolean isValidPhoneNo(String phoneNo) {
        if (!StringUtils.isBlank(phoneNo) && !StringUtils.isBlank(phoneNo.trim())) {
            Matcher matcher = PHONE_PATTERN.matcher(phoneNo.trim());
            return matcher.matches();
        } else {
            return false;
        }
    }

    public static boolean isValidEmail(String email) {
        if (!StringUtils.isBlank(email) && !StringUtils.isBlank(email.trim())) {
            Matcher matcher = EMAIL_PATTERN.matcher(email.trim());
            return matcher.find();
        } else {
            return false;
        }
    }

    public static boolean isValidImRobotUrl(String imRobotUrl) {
        if (!StringUtils.isBlank(imRobotUrl) && !StringUtils.isBlank(imRobotUrl.trim())) {
            Matcher matcher = IM_PATTERN.matcher(imRobotUrl.trim());
            return matcher.matches();
        } else {
            return false;
        }
    }
}

