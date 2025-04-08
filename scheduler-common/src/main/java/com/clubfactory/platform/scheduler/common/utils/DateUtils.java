package com.clubfactory.platform.scheduler.common.utils;

import com.clubfactory.platform.scheduler.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * date utils
 */
public class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    /**
     * <code>java.util.Date</code> to <code>java.time.LocalDateTime</code>
     * use default zone
     * @param date
     * @return
     */
    private static LocalDateTime date2LocalDateTime(Date date) {
        return date2LocalDateTime(date, ZoneId.systemDefault());
    }


    /**
     * 指定时区生成LocalDateTime
     * @param date
     * @param zoneId
     * @return
     */
    private static LocalDateTime date2LocalDateTime(Date date, ZoneId zoneId) {
        return LocalDateTime.ofInstant(date.toInstant(), zoneId);
    }

    /**
     * <code>java.time.LocalDateTime</code> to <code>java.util.Date</code>
     * use default zone
     * @param localDateTime
     * @return
     */
    private static Date localDateTime2Date(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * @return get the formatted date string for the current time
     */
    public static String getCurrentTime() {
        return getCurrentTime(Constants.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * @param format
     * @return get the date string in the specified format of the current time
     */
    public static String getCurrentTime(String format) {
//        return new SimpleDateFormat(format).format(new Date());
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * @param date
     * @param format e.g. yyyy-MM-dd HH:mm:ss
     * @return get the formatted date string
     */
    public static String format(Date date, String format) {
        return format(date2LocalDateTime(date), format);
    }

    /**
     * 按时区转换Date为String
     * @param date
     * @param format
     * @param timeZone
     * @return
     */
    public static String format(Date date, String format, String timeZone) {
        ZoneId zoneId =  ZoneId.systemDefault();
        if (StringUtils.isNotBlank(timeZone)) {
            zoneId = TimeZone.getTimeZone(timeZone).toZoneId();
        }
        return format(date2LocalDateTime(date, zoneId), format);
    }

    /**
     * @param localDateTime
     * @param format        e.g. yyyy-MM-dd HH:mm:ss
     * @return get the formatted date string
     */
    public static String format(LocalDateTime localDateTime, String format) {
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * @param date
     * @return convert time to yyyy-MM-dd HH:mm:ss format
     */
    public static String dateToString(Date date) {
        return format(date, Constants.YYYY_MM_DD_HH_MM_SS);
    }


    /**
     * @param date
     * @return convert string to date and time
     */
    public static Date parse(String date, String format) {
        try {
            //     return new SimpleDateFormat(format).parse(date);
            LocalDateTime ldt = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format));
            return localDateTime2Date(ldt);
        } catch (Exception e) {
            logger.error("error while parse date:" + date, e);
        }
        return null;
    }

    /**
     * convert date str to yyyy-MM-dd HH:mm:ss format
     *
     * @param str
     * @return
     */
    public static Date stringToDate(String str) {
        return parse(str, Constants.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * get seconds between two dates
     *
     * @param d1
     * @param d2
     * @return
     */
    public static long differSec(Date d1, Date d2) {
        return (long) Math.ceil(differMs(d1, d2) / 1000.0);
    }

    /**
     * get ms between two dates
     *
     * @param d1
     * @param d2
     * @return
     */
    public static long differMs(Date d1, Date d2) {
        return Math.abs(d1.getTime() - d2.getTime());
    }


    /**
     * get hours between two dates
     *
     * @param d1
     * @param d2
     * @return
     */
    public static long diffHours(Date d1, Date d2) {
        return (long) Math.ceil(diffMin(d1, d2) / 60.0);
    }

    /**
     * get minutes between two dates
     *
     * @param d1
     * @param d2
     * @return
     */
    public static long diffMin(Date d1, Date d2) {
        return (long) Math.ceil(differSec(d1, d2) / 60.0);
    }


    /**
     * get the date of the specified date in the days before and after
     *
     * @param date
     * @param day
     * @return
     */
    public static Date getSomeDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }

    /**
     * compare two dates
     *
     * @param future
     * @param old
     * @return
     */
    public static boolean compare(Date future, Date old) {
        return future.getTime() > old.getTime();
    }

    /**
     * convert schedule string to date
     *
     * @param schedule
     * @return
     */
    public static Date getScheduleDate(String schedule) {
        return stringToDate(schedule);
    }

    /**
     * format time to readable
     *
     * @param ms
     * @return
     */
    public static String format2Readable(long ms) {

        long days = ms / (1000 * 60 * 60 * 24);
        long hours = (ms % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (ms % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (ms % (1000 * 60)) / 1000;

        return String.format("%02d %02d:%02d:%02d", days, hours, minutes, seconds);

    }

    /**
     * get monday
     * <p>
     * note: Set the first day of the week to Monday, the default is Sunday
     */
    public static Date getMonday(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        return cal.getTime();
    }

    /**
     * get sunday
     * <p>
     * note: Set the first day of the week to Monday, the default is Sunday
     */
    public static Date getSunday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        return cal.getTime();
    }

    /**
     * get first day of month
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        return cal.getTime();
    }

    /**
     * get first day of month
     */
    public static Date getSomeHourOfDay(Date date, int hours) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - hours);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return cal.getTime();
    }

    /**
     * get last day of month
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        return cal.getTime();
    }

    /**
     * return YYYY-MM-DD 00:00:00
     *
     * @param inputDay
     * @return
     */
    public static Date getStartOfDay(Date inputDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(inputDay);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * return YYYY-MM-DD 23:59:59
     *
     * @param inputDay
     * @return
     */
    public static Date getEndOfDay(Date inputDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(inputDay);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * return YYYY-MM-DD 00:00:00
     *
     * @param inputDay
     * @return
     */
    public static Date getStartOfHour(Date inputDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(inputDay);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * return YYYY-MM-DD 23:59:59
     *
     * @param inputDay
     * @return
     */
    public static Date getEndOfHour(Date inputDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(inputDay);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * 判断date是否在参考值dateRef之后
     * @param date
     * @param dateRef
     * @return
     */
    public static boolean after(Date date, Date dateRef){
        if (date == null || dateRef == null){
            return false;
        }
        return date.after(dateRef);
    }


    /**
     * @param date
     * @param minutes
     * @return
     */
    public static Date addMinutes(Date date, int minutes) {
        return org.apache.commons.lang3.time.DateUtils.addMinutes(date, minutes);
    }

    /**
     * @param date
     * @param months
     * @return
     */
    public static Date addMonths(Date date, int months) {
        return org.apache.commons.lang3.time.DateUtils.addMonths(date, months);
    }

    /**
     * @param date
     * @param days
     * @return
     */
    public static Date addDays(Date date, int days) {
        return org.apache.commons.lang3.time.DateUtils.addDays(date, days);
    }


}
