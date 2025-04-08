package com.clubfactory.platform.scheduler.server.leader.monitor;


import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.common.utils.placeholder.MacroVarConvertUtils;
import com.clubfactory.platform.scheduler.core.utils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class LocalTest {

    @Before
    public void init() {
        PropertyUtils.init(new Properties());
    }

    /**
     * $[HH-1/24] 当前减一小时后，返回小时
     * ${LAST_HOUR_YMD} 等于 $[yyyy-MM-dd-1/24]
     */
    @Test
    public void dateAndTime() {
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put(Constants.PARAMETER_INSTANCE_DATE, "20200422010001");

        String ret = MacroVarConvertUtils.convertParameterPlaceholders("$[yyyy-MM-dd-1/24]", parameterMap);
        System.out.println(ret);
    }

    @Test
    public void test() {
        List<String> args = new ArrayList<>();
        args.add("a");
        args.add("b");
        args.add("c.jar");
        // other parameters
        int size = args.size();
        args.add(size - 1, "--conf spark.yarn.submit.waitAppCompletion=false  \\\n" +
                "--conf spark.memory.storageFraction=0.1  \\\n" +
                "--conf spark.memory.fraction=0.8  \\\n" +
                "--packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.4 \\\n");
        System.out.println();
    }


}
