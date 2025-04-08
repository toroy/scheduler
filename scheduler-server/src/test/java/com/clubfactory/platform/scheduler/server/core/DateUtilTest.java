package com.clubfactory.platform.scheduler.server.core;

import com.clubfactory.platform.scheduler.common.utils.DateUtils;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;

public class DateUtilTest {

    @Test
    public void formatTest(){
        Date date = new Date();
        System.out.println(DateUtils.format(date,"yyyy-MM-dd HH:mm:ss"));
        
        
    }
    
    public static void main(String[] args) {
    	System.out.println(Instant.now().getNano());
    	System.out.println(new Date().toString());
	}
}
