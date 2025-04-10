package com.clubfactory.platform.scheduler.common.util;


import com.clubfactory.platform.scheduler.common.exception.BizException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;

public class DateUtil {
    public DateUtil() {
    }

    public static Long getDateLongTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(date);
        return StringUtils.isNumeric(dateString) ? Long.parseLong(dateString) : null;
    }

    public static String format(Long stamp, String pattern) {
        Date date = new Date(stamp);
        return format(date, pattern);
    }

    public static String format(Date d) {
        return format((Date)d, (String)null);
    }

    public static String format(Date d, String pattern) {
        return format(d, pattern, (TimeZone)null);
    }

    public static String format(Date d, String pattern, TimeZone timeZone) {
        if (d == null) {
            return "";
        } else {
            pattern = StringUtils.isBlank(pattern) ? "yyyy-MM-dd HH:mm:ss.SSSZ" : pattern;
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            if (timeZone != null) {
                format.setTimeZone(timeZone);
            }

            return format.format(d);
        }
    }

    public static Date parse(String str) {
        return parse(str, (String)null);
    }

    public static Date parse(String source, String pattern) {
        try {
            return parse(source, pattern, (TimeZone)null);
        } catch (ParseException var3) {
            throw new BizException(String.format("格式化错误，source: %s, pattern: %s", source, pattern));
        }
    }

    public static String formatUnixStamp(Long unixStamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(unixStamp);
    }

    public static Date parse(String source, String pattern, TimeZone timeZone) throws ParseException {
        if (source == null) {
            return null;
        } else {
            pattern = StringUtils.isBlank(pattern) ? "yyyy-MM-dd HH:mm:ss.SSSZ" : pattern;
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            if (timeZone != null) {
                format.setTimeZone(timeZone);
            }

            return format.parse(source);
        }
    }

    public static Date getDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        } else {
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = localDateTime.atZone(zoneId);
            return Date.from(zdt.toInstant());
        }
    }

    public static LocalDate getLocalDate(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime getLocalDateTime(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date getDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        } else {
            ZonedDateTime zdt = localDate.atStartOfDay(ZoneId.systemDefault());
            return Date.from(zdt.toInstant());
        }
    }

    public static double getDatePoor(Date startDate, Date endDate, double nd) {
        return (double)(endDate.getTime() - startDate.getTime()) / nd;
    }

    public static String getAfterDay(String dateString, String format, int days) {
        if (StringUtils.isBlank(dateString)) {
            return null;
        } else {
            Date date = parse(dateString, format);
            Date targetDate = getAfterDay(date, days);
            return format(targetDate, format);
        }
    }

    public static Date getAfterDay(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(5, days);
        return calendar.getTime();
    }

    public static Date getBeforeDay(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(5, -days);
        return calendar.getTime();
    }

    public static Date getMaxOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getMinOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String format(LocalDateTime localDateTime, String format) {
        if (localDateTime == null) {
            return null;
        } else {
            if (StringUtils.isEmpty(format)) {
                format = "yyyy-MM-dd HH:mm:ss";
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
            return dtf.format(localDateTime);
        }
    }

    public static String getDurStr(Date start, Date end) {
        return getDurStr(getLocalDateTime(start), getLocalDateTime(end));
    }

    public static String getDurStr(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            Duration duration = Duration.between(start, end);
            StringBuilder sb = new StringBuilder();
            if (duration.toDays() != 0L) {
                sb.append(duration.toDays());
                sb.append("d");
            }

            if (duration.toHours() != 0L) {
                sb.append(duration.toHours() % 24L);
                sb.append("h");
            }

            if (duration.toMinutes() != 0L) {
                sb.append(duration.toMinutes() % 60L);
                sb.append("m");
            }

            if (duration.getSeconds() != 0L) {
                sb.append(duration.getSeconds() % 60L);
                sb.append("s");
            } else if (duration.getNano() > 0) {
                sb.append("<1s");
            }

            return sb.toString();
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(format(localDateTime, "yyyy-MM-dd HH:mm:ss"));
    }
}
