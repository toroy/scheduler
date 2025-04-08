package com.clubfactory.platform.scheduler.server.core;

import com.clubfactory.platform.scheduler.common.enums.SystemDateType;
import com.clubfactory.platform.scheduler.common.utils.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.clubfactory.platform.scheduler.common.utils.placeholder.TimePlaceholderUtils.*;


public class TimePlaceholderUtilsTest {

    Date date = null;

    @Before
    public void init(){
//        date = DateUtils.parse("20170101010101","yyyyMMddHHmmss");
        date = DateUtils.parse("2020-03-13 13:14:15","yyyy-MM-dd HH:mm:ss");
//        date = new Date();
    }

    @Test
    public void timestampTest(){
        date = new Date();
        System.out.println(replacePlaceholders("$[timestamp(yyyyMMddHHmmss)]",date,true, "GMT"));
        System.out.println(replacePlaceholders("$[timestamp(yyyyMMddHHmmss)]",date,false, "GMT+8"));
        System.out.println(replacePlaceholders("$[yyyy-MM-dd HH:mm:ss]",date,true, "GMT"));
        System.out.println(replacePlaceholders("$[yyyy-MM-dd HH:mm:ss]",date,false, "GMT+8"));

        System.out.println(replacePlaceholders("$[yyyy-MM-dd HH:mm:ss+1/24#BJ]",date,false));
        System.out.println(replacePlaceholders("$[yyyy-MM-dd HH:mm:ss#UTC]",date,false));
        System.out.println(replacePlaceholders("$[add_months(yyyy-MM-dd HH:mm:ss, 1)#UTC]",date,false));
        System.out.println(replacePlaceholders("$[add_months(yyyy-MM-dd HH:mm:ss, 1) #UTC]",date,false) + "==");
    }

    @Test
    public void replacePlaceholdersT() {
        Assert.assertEquals("2017test12017:***2016-12-31,20170102,20170130,20161227,20161231", replacePlaceholders("$[yyyy]test1$[yyyy:***]$[yyyy-MM-dd-1],$[month_begin(yyyyMMdd, 1)],$[month_end(yyyyMMdd, -1)],$[week_begin(yyyyMMdd, 1)],$[week_end(yyyyMMdd, -1)]",
                date, true));

        Assert.assertEquals("1483200061,1483290061,1485709261,1482771661,1483113600,1483203661", replacePlaceholders("$[timestamp(yyyyMMdd00mmss)],"
                        + "$[timestamp(month_begin(yyyyMMddHHmmss, 1))],"
                        + "$[timestamp(month_end(yyyyMMddHHmmss, -1))],"
                        + "$[timestamp(week_begin(yyyyMMddHHmmss, 1))],"
                        + "$[timestamp(week_end(yyyyMMdd000000, -1))],"
                        + "$[timestamp(yyyyMMddHHmmss)]",
                date, true));
        System.out.println(
                replacePlaceholders(
                        "$[yyyy-MM-dd HH:mm:ss],\n" +
                                "$[yyyy],\n" +
                                "test1,\n" +
                                "$[yyyy:***],\n" +
                                "$[yyyy-MM-dd-1],\n" +
                                "$[yyyy-MM-dd HH:mm:ss+1*7],\n" + //1周后的时间
                                "$[yyyy-MM-dd HH:mm:ss+1/24],\n" + //+N/24必须连在一起，不能有空格，以date为基准，取N小时后的时间
                                "$[yyyy-MM-dd HH:mm:ss+23/24],\n" + //+N/24 :N/24必须连在一起，不能有空格，以date为基准，取N小时后的时间
                                "$[yyyy-MM-dd HH:mm:ss+55/24/60],\n" + //+N/24 :N/24/60必须连在一起，不能有空格，以date为基准，取N分钟后的时间
                                "$[month_begin(yyyyMMdd, 0)],\n" +
                                "$[month_begin(yyyyMMdd, 1)],\n" +
                                "$[month_end(yyyyMMdd, -1)],\n" +
                                "$[week_begin(yyyyMMdd, 0)],\n" +
                                "$[week_begin(yyyyMMdd, 1)],\n" +
                                "$[week_begin(yyyy-MM-dd HH:mm:ss, 1)],\n" +
                                "$[week_end(yyyyMMdd, -1)]",
                        date
                        , true)
        );

        System.out.println(
                replacePlaceholders("$[timestamp(yyyyMMdd00mmss)],"
                                + "$[timestamp(month_begin(yyyyMMddHHmmss, 1))],"
                                + "$[timestamp(month_end(yyyyMMddHHmmss, -1))],"
                                + "$[timestamp(week_begin(yyyyMMddHHmmss, 1))],"
                                + "$[timestamp(week_end(yyyyMMdd000000, -1))],"
                                + "$[timestamp(yyyyMMddHHmmss)]",
                        date, true)
        );
    }



    @Test
    public void calcMinutesT() {
        Assert.assertEquals("Sun Jan 01 01:01:01 CST 2017=yyyy", calcMinutes("yyyy", date).toString());
        Assert.assertEquals("Sun Jan 08 01:01:01 CST 2017=yyyyMMdd", calcMinutes("yyyyMMdd+7*1", date).toString());
        Assert.assertEquals("Sun Dec 25 01:01:01 CST 2016=yyyyMMdd", calcMinutes("yyyyMMdd-7*1", date).toString());
        Assert.assertEquals("Mon Jan 02 01:01:01 CST 2017=yyyyMMdd", calcMinutes("yyyyMMdd+1", date).toString());
        Assert.assertEquals("Sat Dec 31 01:01:01 CST 2016=yyyyMMdd", calcMinutes("yyyyMMdd-1", date).toString());
        Assert.assertEquals("Sun Jan 01 02:01:01 CST 2017=yyyyMMddHH", calcMinutes("yyyyMMddHH+1/24", date).toString());
        Assert.assertEquals("Sun Jan 01 00:01:01 CST 2017=yyyyMMddHH", calcMinutes("yyyyMMddHH-1/24", date).toString());
    }

    @Test
    public void calcMonthsT() {
        Assert.assertEquals("Mon Jan 01 01:01:01 CST 2018=yyyyMMdd", calcMonths("add_months(yyyyMMdd,12*1)", date).toString());
        Assert.assertEquals("Fri Jan 01 01:01:01 CST 2016=yyyyMMdd", calcMonths("add_months(yyyyMMdd,-12*1)", date).toString());
    }

    @Test
    public void test(){
        System.out.println(SystemDateType.keyOf("NN"));
        System.out.println(SystemDateType.keyOf("NF"));
        Pattern p2 = Pattern.compile("\\$\\{TIME_(N|WEEK)\\w+\\}");
        String str = "xawdaw ${TIME_NF_2}wdawdaw ${TIME_WEEK_3}";
        Matcher matcher4 = p2.matcher(str);
        while (matcher4.find()){
            System.out.println(matcher4.group());
            System.out.println(matcher4.group(1));
        }
    }

}