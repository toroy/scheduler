package com.clubfactory.platform.scheduler.server.core;

import com.clubfactory.platform.scheduler.common.utils.OSUtils;
import org.junit.Test;

public class OSUtilTest {

    @Test
    public void localIpTest(){
        System.out.println(OSUtils.getHost());
    }
}
