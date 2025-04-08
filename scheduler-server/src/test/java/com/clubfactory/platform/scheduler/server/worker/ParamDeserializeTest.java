package com.clubfactory.platform.scheduler.server.worker;

import com.clubfactory.platform.scheduler.core.utils.JSONUtils;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.ShellParameters;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.SparkParameters;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ParamDeserializeTest {

    @Test
    public void shellParamDeserializeTest(){

        Map<String,Object> taskParams = new HashMap<String, Object>(){{
            put("param1","value1");
            put("param2",2);
        }};
        Map<String,Object> originParam = new HashMap<String, Object>(){{
            put("rawScript","echo '111111'");
            put("taskParams",taskParams);
            put("startFile","run.sh");
        }};

        String json = JSONUtils.toJsonString(originParam);

        ShellParameters shellParameters = JSONUtils.parseObject(json,ShellParameters.class);

        System.out.println(shellParameters.getRawScript());
        System.out.println(shellParameters.getStartFile());
    }


    @Test
    public void sparkParamDeserializeTest(){
        Map<String,Object> originParam = new HashMap<String, Object>(){{
            put("languageType","JAVA");
//            put("languageType","java");
            put("executorCores",1);
        }};
        String json = JSONUtils.toJsonString(originParam);

        SparkParameters sparkParameters = JSONUtils.parseObject(json,SparkParameters.class);
        System.out.println(sparkParameters.getLanguageType());
    }
}
