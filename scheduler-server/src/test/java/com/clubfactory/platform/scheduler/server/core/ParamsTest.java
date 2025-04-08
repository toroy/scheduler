package com.clubfactory.platform.scheduler.server.core;


import com.clubfactory.platform.scheduler.common.utils.DateUtils;
import com.clubfactory.platform.scheduler.common.utils.placeholder.MacroVarConvertUtils;
import com.clubfactory.platform.scheduler.core.utils.PropertyUtils;
import com.clubfactory.platform.scheduler.common.utils.ParamUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class ParamsTest{


    @Test
    public void curingGlobalParamsTest(){

        Map<String,String> originGlobalParams = new HashMap<>();
        originGlobalParams.put("test_date","${system.biz.date}");
        originGlobalParams.put("test_param1","value1");
        originGlobalParams.put("test_date2","${system.biz.curdate}");
        originGlobalParams.put("test_param3","${test_param1}");

        MacroVarConvertUtils.curingGlobalParams(originGlobalParams, new Date(), "GMT");

        originGlobalParams.forEach((key, value) -> log.info("{} == {}", key, value));
    }


    @Test
    public void timeZoneTest() {
        Date date = new Date();
        Map<String,String> originGlobalParams = new HashMap<>();
        originGlobalParams.put("UTC_TIME","${task.instance.date.cn}");
        MacroVarConvertUtils.curingGlobalParams(originGlobalParams, date, "GMT");

        originGlobalParams.put("BEIJING_TIME","${task.instance.date.cn}");
        MacroVarConvertUtils.curingGlobalParams(originGlobalParams, date, "GMT+8");
        originGlobalParams.forEach((key, value) -> log.info("TimeZone: {} == {}", key, value));
    }

    @Test
    public void timeZoneTest2() {
        Date date = new Date();
        Map<String,String> originGlobalParams = new HashMap<>();
        originGlobalParams.put("UTC_TIME","${task.instance.date.cn}");
        originGlobalParams = ParamUtils.convert(originGlobalParams, date, "GMT");

        originGlobalParams.put("BEIJING_TIME","${task.instance.date.cn}");
        originGlobalParams.put("BEIJING_TIME_1","${TIME_NH_1}");
        originGlobalParams = ParamUtils.convert(originGlobalParams, date, "GMT+8");
        originGlobalParams.forEach((key, value) -> log.info("TimeZone: {} == {}", key, value));
    }

    @Test
    public void timeZoneTest3() {
        Date date = new Date();
        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("UTC_TIME","${task.instance.date.cn}");
        paramsMap = ParamUtils.convert(paramsMap, date, "GMT");

        paramsMap.put("BEIJING_TIME","${task.instance.date.cn}");
        paramsMap.put("BEIJING_TIME_1","${TIME_NH_1}");
        paramsMap.put("UTC_TIME_2","$[yyyy-MM-dd HH:mm:ss+2/24#UTC]");
        paramsMap = ParamUtils.convert(paramsMap, date, "GMT+8");

        String taskScript = "select '${UTC_TIME}' as UTC_TIME, '${BEIJING_TIME}' as BEIJING_TIME, '${BEIJING_TIME_1}' as BEIJING_TIME_1" +
                "\n  '${UTC_TIME_2}' as UTC_TIME2" +
                "\n  '$[yyyy-MM-dd HH:mm:ss-1/24#UTC]' as UTC_TIME3";
        taskScript = MacroVarConvertUtils.convertParameterPlaceholders(taskScript, paramsMap, "GMT+8");
        System.out.println(taskScript);
    }


    @Test
    public void paramConvertTest(){
        Map<String,String> globalParams = new HashMap<>();
        globalParams.put("global_date","${system.biz.date}");
        globalParams.put("global_param1","value1");
        globalParams.put("global_date2","${system.biz.curdate}");
        globalParams.put("global_param3","${global_param1}");

        Map<String,String> localParams = new HashMap<>();
        localParams.put("local_date","${system.biz.date}");
        localParams.put("local_param1","${global_param1}"); // value1
        localParams.put("local_date2","${global_date2}");
        localParams.put("local_param3","${global_param3}"); // value1

        globalParams.putAll(localParams);


        Map<String,String> newMap = ParamUtils.convert(globalParams, new Date(), "GMT");

        newMap.forEach((key, value) -> log.info("{} == {}", key, value));
    }


    @Test
    public void convertParameterPlaceholdersTest(){
        PropertyUtils.init(new Properties());
        String paramString = "select resource_type,updated_at \n" +
                "from wechat_msg_content \n" +
                "where created_at < '${TIME_ND_1}'+1 \n" +
                "and WEEK = '${TIME_WEEK_2}'+1 \n" +
                "and time = '${TIME}'+1 \n" +
                "and NF = '${TIME_NF_33}'+1 \n" +
                "and RNF = '${TIME_RNF_10}'+1 \n" +
                "and NH = '${TIME_NH_2}'+1 \n" +
                "and RNH = '${TIME_RNH_2}'+1 \n" +
                "and ND = '${TIME_ND_2}'+1 \n" +
                "and RND = '${TIME_RND_2}'+1 \n" +
                "and NW = '${TIME_NW_1}'+1 \n" +
                "and NM = '${TIME_NM_3}'+1 \n" +
                "and RNM = '${TIME_RNM_3}'+1 \n" +
                "and DT = '${DT}'+1 \n" +
                "and key1 = '${user_name}'+1 \n" +
                "and cur_date = '${biz_cur_date}'+1 \n" +
                "and task_time = '${task_time}'+1 \n" +
                "and test_time = '${test_time}'+1 \n" +
                "and TIME_NYMD_1 = '${TIME_NYMD_1}'+1\n" +
                "and TIME_NYMD_1 = '$[yyyy-MM-dd+1]'+1 \n" +
                "and TIME_NYMD_1 = '$[yyyy-MM-dd+1]'+1 \n" +
                "and TIME_RNYMD_1 = '${TIME_RNYMD_1}'+1 \n" +
                "and TIME_RNYMD_1 = '$[yyyy-MM-dd-1]'+1 \n" +
                "and TIME_RNYMD_1 = '$[yyyy-MM-dd-1]'+1 \n" +
                "and MONTH_BEGIN = '${MONTH_BEGIN}'\n" +
                "and MONTH_END = '${MONTH_END}'\n" +
                "and DATA_BEGIN_TIME = '${DATA_BEGIN_TIME}'\n" +
                "and DATA_END_TIME = '${DATA_END_TIME}'\n" +
                "and user_date = '${user_date}'+1";
        System.out.println(MacroVarConvertUtils.convertParameterPlaceholders(paramString,new HashMap<>()));

        System.out.println("\n");

        Map<String,String> allParams = new HashMap<>();
        allParams.put("user_name","张三");
        allParams.put("user_date","${system.biz.date}");
        allParams.put("biz_cur_date","${system.biz.curdate}");
        allParams.put("task_time","${task.instance.date.cn}");
        allParams.put("test_time","$[yyyy-MM-dd+1]");
        Date date = DateUtils.parse("20200201000000","yyyyMMddHHmmss");
//        date = new Date();
        Map<String,String> params = ParamUtils.convert(allParams,date);
        System.out.println(MacroVarConvertUtils.convertParameterPlaceholders(paramString,params));
    }


    @Test
    public void timeConstTest(){
        PropertyUtils.init(new Properties());
        String paramString = "select '${TIME_ND_1}'+1 \n" +
                "and WEEK = '${TIME_WEEK_2}'+1 \n" +
                "and time = '${TIME}'+1 \n" +
                "and NF = '${TIME_NF_33}'+1 \n" +
                "and RNF = '${TIME_RNF_10}'+1 \n" +
                "and NH = '${TIME_NH_2}'+1 \n" +
                "and RNH = '${TIME_RNH_2}'+1 \n" +
                "and ND = '${TIME_ND_2}'+1 \n" +
                "and RND = '${TIME_RND_2}'+1 \n" +
                "and NW = '${TIME_NW_1}'+1 \n" +
                "and NM = '${TIME_NM_3}'+1 \n" +
                "and RNM = '${TIME_RNM_3}'+1 \n" +
                "and DT = '${DT}'+1 \n" +
                "and TIME_NYMD_1 = '${TIME_NYMD_1}'+1\n" +
                "and TIME_NYMD_1 = '$[yyyy-MM-dd+1]'+1 \n" +
                "and TIME_NYMD_1 = '$[yyyy-MM-dd+1]'+1 \n" +
                "and TIME_RNYMD_1 = '${TIME_RNYMD_1}'+1 \n" +
                "and TIME_RNYMD_1 = '$[yyyy-MM-dd-1]'+1 \n" +
                "and TIME_RNYMD_1 = '$[yyyy-MM-dd-1]'+1 \n" +
                "and MONTH_BEGIN = '${MONTH_BEGIN}'\n" +
                "and MONTH_END = '${MONTH_END}'\n" +
                "and DATA_BEGIN_TIME = '${DATA_BEGIN_TIME}'\n" +
                "and DATA_END_TIME = '${DATA_END_TIME}'\n";

        Map<String,String> allParams = new HashMap<>();
        Date date = new Date();
        Map<String,String> params = ParamUtils.convert(allParams,date);
        System.out.println(MacroVarConvertUtils.convertParameterPlaceholders(paramString,params));

        System.out.println("\n");
        paramString = "select '${TIME_ND_1#UTC}'+1 \n" +
                "and WEEK = '${TIME_WEEK_2#UTC}'+1 \n" +
                "and WEEK1 = '${TIME_WEEK_2#GMT+7}'+1 \n" +
                "and time = '${TIME#Utc}'+1 \n" +
                "and NF = '${TIME_NF_33#UTC}'+1 \n" +
                "and RNF = '${TIME_RNF_10#UTC}'+1 \n" +
                "and RNF1 = '${TIME_RNF_10#GMT-1}'+1 \n" +
                "and NH = '${TIME_NH_2#UTC}'+1 \n" +
                "and RNH = '${TIME_RNH_2#UTC}'+1 \n" +
                "and ND = '${TIME_ND_2#UTC}'+1 \n" +
                "and RND = '${TIME_RND_2#UTC}'+1 \n" +
                "and NW = '${TIME_NW_1#UTC}'+1 \n" +
                "and NM = '${TIME_NM_3#UTC}'+1 \n" +
                "and RNM = '${TIME_RNM_3#UTC}'+1 \n" +
                "and DT = '${DT#UTC}'+1 \n" +
                "and TIME_NYMD_1 = '${TIME_NYMD_1#UTC}'+1\n" +
                "and TIME_NYMD_1 = '$[yyyy-MM-dd+1#UTC]'+1 \n" +
                "and TIME_NYMD_1 = '$[yyyy-MM-dd+1#utc]'+1 \n" +
                "and TIME_RNYMD_1 = '${TIME_RNYMD_1#UTC}'+1 \n" +
                "and TIME_RNYMD_1 = '$[yyyy-MM-dd-1#UTC]'+1 \n" +
                "and TIME_RNYMD_1 = '$[yyyy-MM-dd-1#utc]'+1 \n" +
                "and MONTH_BEGIN = '${MONTH_BEGIN#UTC}'\n" +
                "and MONTH_END = '${MONTH_END#UTC}'\n" +
                "and DATA_BEGIN_TIME = '${DATA_BEGIN_TIME#UTC}'\n" +
                "and DATA_END_TIME = '${DATA_END_TIME#UTC}'\n";

        System.out.println(MacroVarConvertUtils.convertParameterPlaceholders(paramString,params));
    }


    @Test
    public void testTimeZone() {
        Map<String,String> params = ParamUtils.convert(new HashMap<>(),new Date());
        System.out.println(MacroVarConvertUtils.convertParameterPlaceholders("WEEK = '${TIME#GMT+9}'+1", params));
        System.out.println(MacroVarConvertUtils.convertParameterPlaceholders("WEEK = '${TIME#UTC}'+1", params));
    }

    @Test
    public void testTimeZone2() {
        Map<String,String> paramsMap = ParamUtils.convert(new HashMap<>(), DateUtils.stringToDate("2020-11-30 00:00:00"), "GMT+9");
        System.out.println(MacroVarConvertUtils.convertParameterPlaceholders("'${TIME}' '$[yyyy-MM-dd HH:mm:ss]'", paramsMap, "GMT+9"));
        System.out.println(MacroVarConvertUtils.convertParameterPlaceholders("'${TIME}' \n '${TIME#UTC}' \n '${TIME#GMT}'\n" +
                " '$[yyyy-MM-dd HH:mm:ss#UTC]' \n" +
                " '$[yyyy-MM-dd HH:mm:ss#GMT]' \n " +
                " '${TIME#GMT-7}' \n " +
                " '${TIME#GMT+8}' \n " +
                "'${task.instance.date}' ", paramsMap, "GMT+9"));

//        System.out.println(StringUtils.replaceEach("+ - $ [ ] { } [ ] { } $ + -",
//                new String[] {"[", "]", "{", "}", "$", "+", "-"}, new String[] {"\\[", "\\]", "\\{", "\\}", "\\$", "\\\\+", "\\\\-"}));
    }

    @Test
    public void testTimeZone3() throws InterruptedException {
        Date date = new Date();
        Thread t1 = new Thread(() -> {
            this.test("GMT", date);
        });

        Thread t2 = new Thread(() -> {
            this.test("GMT+8", date);
        });
        t1.setName("UTC-Thread");
        t2.setName("BeiJing-Thread");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    private void test(String timeZone, Date date) {
        Map<String,String> paramsMap = ParamUtils.convert(new HashMap<>(), date, timeZone);
        for (int i = 0; i <= 20; i++) {
            System.out.println(Thread.currentThread().getName() + ": " + MacroVarConvertUtils.convertParameterPlaceholders("${TIME}", paramsMap, timeZone));
        }
    }

    @Test
    public void sysTimeTest() {
        String timeZone = "GMT";
        Map<String,String> paramsMap = ParamUtils.convert(new HashMap<>(), DateUtils.stringToDate("2020-01-22 12:34:55"), timeZone);
        System.out.println(MacroVarConvertUtils.convertParameterPlaceholders("==>${system.current.date}\n" +
                "==>${system.current.time}\n" +
                "==>${TIME}\n" +
                "==>${task.instance.date.cn}\n" +
                "==>${CURRENT_DATE}\n" +
                "==>${CURRENT_TIME}\n" +
                "==>$[month_begin(yyyy-MM-dd, 1)]\n", paramsMap, timeZone));
    }

    @Test
    public void timeParamCovertTest() {
        String timeZone = "GMT";
        Date baseTime = DateUtils.stringToDate("2020-01-22 12:34:55");
        String paramString = "==>${system.current.date}\n" +
                "==>${system.current.time}\n" +
                "==>${TIME}\n" +
                "==>${task.instance.date.cn}\n" +
                "==>${CURRENT_DATE}\n" +
                "==>${CURRENT_TIME}\n" +
                "==>$[yyyy-MM-dd-1]\n" +
                "==>$[month_begin(yyyy-MM-dd, 0)]\n" +
                "==>$[month_begin(yyyy-MM-dd)]\n" +
                "==>$[month_begin(yyyy-MM-dd, 1)]\n" +
                "==>$[month_begin(yyyy-MM-dd, 2)]\n" +
                "==>$[month_end(yyyy-MM-dd, 0)]\n" +
                "==>$[month_end(yyyy-MM-dd)]\n" +
                "==>$[add_months(yyyy-MM-dd HH:mm:ss, 1)#UTC]\n" +
                "==>$[timestamp(month_begin(yyyyMMddHHmmss, 1))]\n" +
                "==>$[timestamp(month_begin(yyyyMMdd000000, 1))]\n"
                ;
        System.out.println(ParamUtils.convertVariable(paramString, baseTime));
        System.out.println();
        System.out.println();
        System.out.println(ParamUtils.convertVariable(paramString, baseTime, timeZone));
    }


}
