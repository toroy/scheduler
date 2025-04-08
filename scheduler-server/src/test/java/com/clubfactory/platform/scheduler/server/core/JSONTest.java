package com.clubfactory.platform.scheduler.server.core;

import com.clubfactory.platform.scheduler.core.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JSONTest {


    @Test
    public void jsonTest(){
        Map<String,String> params =  new HashMap<String, String>(){
            {
                put("queue","root.hive");
                put("mainClass","com.clubfactory.platform.SparkExample");
                put("executorCores","3");
            }
        };
        String jsonString = JSONUtils.toJson(params);
        log.info(jsonString);

//        Map<String,String> newParams = JSONUtils.parseObject(jsonString,HashMap.class);
        Map<String,String> newParams = JSONUtils.toMap(jsonString);
        Assert.assertNotNull(newParams);
        newParams.forEach((k,v)->log.info("{} == {}",k,v));

    }
}
