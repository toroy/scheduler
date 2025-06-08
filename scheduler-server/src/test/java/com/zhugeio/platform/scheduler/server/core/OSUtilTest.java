package com.zhugeio.platform.scheduler.server.core;

import com.zhugeio.platform.scheduler.common.utils.OSUtils;
import org.junit.Test;

public class OSUtilTest {

    @Test
    public void localIpTest(){
        System.out.println(OSUtils.getHost());
    }
}
