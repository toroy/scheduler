package com.clubfactory.platform.scheduler.server.core;

import com.clubfactory.platform.scheduler.common.Constants;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppIdExtractTest {

    @Test
    public void findAppId() {
        String line  = "19/12/19 07:11:55 INFO Client: Application report for application_1575727693777_66796 (state: ACCEPTED)";
        Pattern APPLICATION_REGEX = Pattern.compile(Constants.APPLICATION_REGEX);
        Matcher matcher = APPLICATION_REGEX.matcher(line);

        if (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
