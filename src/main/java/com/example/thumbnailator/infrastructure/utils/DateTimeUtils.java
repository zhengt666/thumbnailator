package com.example.thumbnailator.infrastructure.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

/**
 * 时间工具类
 *
 * @author 机考（企业版）项目组
 * @date 2019/9/12
 */
public class DateTimeUtils {
    
    private DateTimeUtils() {
    }
    
    /**
     * Hours per day.
     */
    public static final int HOURS_PER_DAY = 24;
    
    /**
     * Minutes per hour.
     */
    public static final int MINUTES_PER_HOUR = 60;
    
    /**
     * Minutes per day.
     */
    public static final int MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY;
    
    /**
     * Milliseconds per second.
     */
    public static final int MILLISECONDS_PER_SECOND = 1000;
    
    /**
     * Seconds per minute.
     */
    public static final int SECONDS_PER_MINUTE = 60;
    
    /**
     * Milliseconds per minute.
     */
    public static final int MILLISECONDS_PER_MINUTE = SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
    
    /**
     * Seconds per hour.
     */
    public static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    
    /**
     * Seconds per day.
     */
    public static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;
    
    private static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    
    private static final String FORMAT_DATE_TIME_EXCLUDE_SECONDS = "yyyy-MM-dd HH:mm";
    
    private static final String FORMAT_TIME = "HH:mm";
    
    private static final String FORMAT_DATE_TIME_EXCLUDE_HMS = "yyyy-MM-dd";
    
    private static final String DATE_HMS_STR = " 00:00:00";
    
    private static final String DATE_MAX_HMS_STR = " 23:59:59";
    
    private static final String FORMAT_DATE_TIME_MS = "yyyy-MM-dd HH:mm:ss.SSS";
    
    private static final String PURE_FORMAT_DATE_TIME = "yyyyMMddHHmmss";
    
    private static final String JUST_NOW = "刚刚";
    
    private static final String MINUTES_AGO = "分钟前";
    
    private static final String HOURS_AGO = "小时前";
    
    private static final String LAST_DAY = "昨天";
    
    
    // 匹配 yyyy-MM-dd HH:mm:ss 的正则
    private static final String REGEX_DATE_TIME = "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$";
    
    // 匹配 yyyy-MM-dd 的正则
    private static final String REGEX_DATE_TIME_EXCLUDE_HMS = "^\\d{4}-\\d{1,2}-\\d{1,2}";
    
    // 匹配 yyyy-MM 的正则
    private static final String REGEX_DATE_TIME_EXCLUDE_DHMS = "^\\d{4}-\\d{1,2}";
    
    /**
     * 返回localDateTime与当前时间的时间间隔数字（单位为timeUnit）
     *
     * @param localDateTime 待比较的时间
     * @param timeUnit      时间单位
     * @return 时间间隔数字
     */
    public static long getDuration(LocalDateTime localDateTime, TimeUnit timeUnit) {
        long seconds = Duration.between(localDateTime, LocalDateTime.now()).abs().getSeconds();
        switch (timeUnit) {
            case SECONDS:
                return seconds;
            case MINUTES:
                return seconds / SECONDS_PER_MINUTE;
            case HOURS:
                return seconds / SECONDS_PER_HOUR;
            case DAYS:
                return seconds / SECONDS_PER_DAY;
            default:
                throw new DateTimeException("Unsupported TimeUnit: " + timeUnit.name());
        }
    }
    
    /**
     * 返回可读性高的localDateTime与当前时间的时间间隔（x天x小时x分x秒），
     *
     * @param localDateTime 待比较的时间
     * @return 持续时间字符串
     */
    public static String getReadableDuration(LocalDateTime localDateTime) {
        Duration duration = Duration.between(localDateTime, LocalDateTime.now());
        return getReadableDuration(duration);
    }
    
    /**
     * 将长整型时长转为可读性高的时间间隔（x天x小时x分x秒），
     *
     * @param amount 持续时间
     * @param unit   时间单位
     * @return 持续时间字符串
     */
    public static String getReadableDuration(Long amount, TemporalUnit unit) {
        Duration duration = Duration.of(amount == null ? 0L : amount, unit);
        return getReadableDuration(duration);
    }
    
    /**
     * 将长整型时长转为可读性高的时间间隔（xh/xm/xs），
     *
     * @param amount 持续时间
     * @param unit   时间单位
     * @return 持续时间字符串
     */
    public static String getReadableDurationOnHour(Long amount, TemporalUnit unit) {
        Duration duration = Duration.of(amount == null ? 0L : amount, unit);
        return getReadableDurationOnHour(duration);
    }
    
    /**
     * 返回可读性高的时间间隔（xh/xm/xs），
     *
     * @param duration Duration时长
     * @return 持续时间字符串
     */
    public static String getReadableDurationOnHour(Duration duration) {
        StringBuilder stringBuilder = new StringBuilder();
        long seconds = duration.getSeconds();
        if (seconds == 0) {
            return "0s";
        }
        long hours = seconds / SECONDS_PER_HOUR;
        long minutes = seconds / SECONDS_PER_MINUTE;
        if (hours > 0) {
            Double doubleHours = BigDecimal.valueOf(seconds)
                    .divide(BigDecimal.valueOf(SECONDS_PER_HOUR), 1, RoundingMode.HALF_UP).doubleValue();
            stringBuilder.append(doubleHours).append("h");
        } else if (minutes > 0) {
            stringBuilder.append(minutes).append("m");
        } else {
            stringBuilder.append(seconds).append("s");
        }
        return stringBuilder.toString();
    }
    
    /**
     * 返回可读性高的时间间隔（x天x小时x分x秒）
     *
     * @param duration Duration时长
     * @return 持续时间字符串
     */
    public static String getReadableDuration(Duration duration) {
        StringBuilder stringBuilder = new StringBuilder();
        long seconds = duration.getSeconds();
        if (seconds == 0) {
            return "-";
        }
        long days = seconds / SECONDS_PER_DAY;
        if (days > 0) {
            stringBuilder.append(days).append("天");
            seconds = seconds - days * SECONDS_PER_DAY;
        }
        long hours = seconds / SECONDS_PER_HOUR;
        if (hours > 0 || days > 0) {
            stringBuilder.append(hours).append("小时");
            seconds = seconds - hours * SECONDS_PER_HOUR;
        }
        long minutes = seconds / SECONDS_PER_MINUTE;
        if (minutes > 0 || hours > 0 || days > 0) {
            stringBuilder.append(minutes).append("分");
            seconds = seconds - minutes * SECONDS_PER_MINUTE;
        }
        stringBuilder.append(seconds).append("秒");
        return stringBuilder.toString();
    }
    
    /**
     * 返回可读性高的时间间隔（x天x小时x分） todo 优化根据时间类型控制时间间隔
     *
     * @param duration Duration时长
     * @return 持续时间字符串
     */
    public static String getReadableDurationWithoutSecond(Duration duration) {
        StringBuilder stringBuilder = new StringBuilder();
        long seconds = duration.getSeconds();
        if (seconds == 0) {
            return "-";
        }
        long days = seconds / SECONDS_PER_DAY;
        if (days > 0) {
            stringBuilder.append(days).append("天");
            seconds = seconds - days * SECONDS_PER_DAY;
        }
        long hours = seconds / SECONDS_PER_HOUR;
        if (hours > 0 || days > 0) {
            stringBuilder.append(hours).append("时");
            seconds = seconds - hours * SECONDS_PER_HOUR;
        }
        long minutes = seconds / SECONDS_PER_MINUTE;
        if (minutes > 0 || hours > 0 || days > 0) {
            stringBuilder.append(minutes).append("分");
        }
        return stringBuilder.toString();
    }
    
    /**
     * LocalDateTime转String（yyyy-MM-dd HH:mm:ss）
     *
     * @param localDateTime 时间
     * @return 格式化时间字符串
     */
    public static String getFormatTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(FORMAT_DATE_TIME));
    }
    
    public static String getFormatTime(LocalDateTime localDateTime, String formatTime) {
        if (localDateTime == null) {
            return "";
        }
        if (StringUtils.isEmpty(formatTime)) {
            return getFormatTime(localDateTime);
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(formatTime));
    }
    
    /**
     * LocalDateTime转String（yyyy-MM-dd HH:mm）
     *
     * @param localDateTime 时间
     * @return 格式化时间字符串
     */
    public static String getFormatTimeExcludeSeconds(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(FORMAT_DATE_TIME_EXCLUDE_SECONDS));
    }
    
    /**
     * LocalDateTime转String（yyyy-MM-dd）
     *
     * @param localDateTime 时间
     * @return 格式化时间字符串
     */
    public static String getFormatDateTimeExcludeHms(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(FORMAT_DATE_TIME_EXCLUDE_HMS));
    }
    
    /**
     * 获取当前时间的格式化时间字符串（yyyy-MM-dd HH:mm:ss）
     *
     * @return 格式化时间字符串
     */
    public static String getFormatDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(FORMAT_DATE_TIME));
    }
    
    
    /**
     * String（HH:mm）转LocalTime
     *
     * @param formatTime 时间
     * @return LocalTime时间
     */
    public static LocalTime getLocalTime(String formatTime) {
        return LocalTime.parse(formatTime, DateTimeFormatter.ofPattern(FORMAT_TIME));
    }
    
    /**
     * LocalTime 转String（HH:mm）
     *
     * @param localTime 时间
     * @return String 时间
     */
    public static String getFormatLocalTime(LocalTime localTime) {
        if (Objects.isNull(localTime)) {
            return "";
        }
        return localTime.format(DateTimeFormatter.ofPattern(FORMAT_TIME));
    }
    
    /**
     * String（yyyy-MM-dd HH:mm）转LocalDateTime
     *
     * @param formatTime 格式化时间字符串
     * @return LocalDateTime时间
     */
    public static LocalDateTime getLocalDateTimeExcludeSeconds(String formatTime) {
        return LocalDateTime.parse(formatTime, DateTimeFormatter.ofPattern(FORMAT_DATE_TIME_EXCLUDE_SECONDS));
    }
    
    /**
     * String（yyyy-MM-dd）转LocalDateTime
     *
     * @param formatTime 格式化时间字符串
     * @return LocalDateTime时间
     */
    public static LocalDateTime getLocalDateTimeExcludeHms(String formatTime) {
        return LocalDateTime.parse(formatTime + DATE_HMS_STR, DateTimeFormatter.ofPattern(FORMAT_DATE_TIME));
    }
    
    /**
     * String（yyyy-MM-dd）转LocalDateTime
     *
     * @param formatTime 格式化时间字符串
     * @return LocalDateTime时间
     */
    public static LocalDateTime getLocalDateTimeExcludeHmsForMaxHms(String formatTime) {
        return LocalDateTime.parse(formatTime + DATE_MAX_HMS_STR, DateTimeFormatter.ofPattern(FORMAT_DATE_TIME));
    }
    
    
    /**
     * String（yyyy-MM-dd HH:mm:ss.SSS）转LocalDateTime
     *
     * @param formatTime 格式化时间字符串
     * @return LocalDateTime时间
     */
    public static LocalDateTime getLocalDateTimeWithMs(String formatTime) {
        return LocalDateTime.parse(formatTime, DateTimeFormatter.ofPattern(FORMAT_DATE_TIME_MS));
    }
    
    /**
     * 获取当前时间的格式化时间字符串（yyyyMMddHHmmss）
     *
     * @return 格式化时间字符串
     */
    public static String getPureFormatDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(PURE_FORMAT_DATE_TIME));
    }
    
    
    /**
     * 将时间戳转成localdatetime
     *
     * @param timestampLong 时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(long timestampLong) {
        return Instant.ofEpochMilli(timestampLong).atZone(ZoneOffset.ofHours(8))
                .toLocalDateTime();
    }
    
    /**
     * LocalDateTime转指定时刻的cron表达式
     *
     * @param localDateTime 时间
     * @return cron表达式
     */
    public static String getCronExpression(LocalDateTime localDateTime) {
        // 秒，必选，可选值：0-59 , - * /
        int seconds = localDateTime.getSecond();
        // 分，必选，可选值：0-59 , - * /
        int minutes = localDateTime.getMinute();
        // 小时，必选，可选值：0-23 , - * /
        int hours = localDateTime.getHour();
        // 日期，必选，可选值：1-31 , - * ? / L W C
        int dayOfMonth = localDateTime.getDayOfMonth();
        // 月份，必选，可选值：1-12 或者 JAN-DEC , - * /
        int month = localDateTime.getMonth().getValue();
        // 星期，必选，可选值：1-7 或者 SUN-SAT , - * ? / L C #，localDateTime.getDayOfWeek().getValue()
        String dayOfWeek = "?";
        // 年，可选，可选值：留空, 1970-2099 , - * /
        int year = localDateTime.getYear();
        return String
                .format("%1$s %2$s %3$s %4$s %5$s %6$s %7$s", seconds, minutes, hours, dayOfMonth, month, dayOfWeek,
                        year);
    }
    
    /**
     * 返回可读性高的时间间隔，用于app端 例：（1分钟以内 --- 刚刚；1小时以内 --- 以分钟计；超过1小时 --- 以小时计；昨天 --> 显示昨天；昨天以前的 --- 显示日期）
     *
     * @param startTime 开始日期
     * @param endTime   结束日期
     * @return 可读性字符串
     */
    public static String getReadableTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (null == startTime || endTime == startTime) {
            return null;
        }
        final int ZERO = 0;
        final int ONE = 1;
        Duration duration = Duration.between(startTime, endTime);
        String result;
        //获取开始时间的日期
        LocalDate startDate = startTime.toLocalDate();
        //获取结束时间的日期
        LocalDate endDate = endTime.toLocalDate();
        //获取总间隔（单位：秒）
        long seconds = duration.getSeconds();
        //获取天数
        long days = seconds / SECONDS_PER_DAY;
        if (days > ZERO) {
            seconds = seconds - days * SECONDS_PER_DAY;
        }
        //获取小时数
        long hours = seconds / SECONDS_PER_HOUR;
        if (hours > ZERO || days > ZERO) {
            seconds = seconds - hours * SECONDS_PER_HOUR;
        }
        //获取分钟数
        long minutes = seconds / SECONDS_PER_MINUTE;
        if (minutes > ZERO || hours > ZERO || days > ZERO) {
            //获取秒数
            seconds = seconds - minutes * SECONDS_PER_MINUTE;
        }
        //最终时间表达式确认
        if (days == ZERO && hours == ZERO && minutes == ZERO) {
            //刚刚
            result = JUST_NOW;
        } else if (days == ZERO && (hours < ONE || (hours == ONE && minutes == ZERO && seconds == ZERO))) {
            //几分钟前
            result = minutes + MINUTES_AGO;
        } else if (days == ZERO && hours < HOURS_PER_DAY) {
            //几小时前
            result = hours + HOURS_AGO;
        } else if (startDate.plusDays(ONE).equals(endDate)) {
            //昨天
            result = LAST_DAY;
        } else {
            //具体日期
            result = startTime.toLocalDate().toString();
        }
        return result;
    }
    
    /**
     * 查询两个时间差（秒）
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return long 时间差（秒）
     */
    public static long betweenTime(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        return duration.getSeconds();
    }
    
    /**
     * 检查字符串是否为 yyyy-MM-dd HH:mm:ss 格式
     */
    public static boolean isDateTimeFormat(String str) {
        return str.matches(REGEX_DATE_TIME);
    }
    
    /**
     * 检查字符串是否为 yyyy-MM-dd 格式
     */
    public static boolean isDateFormat(String str) {
        return str.matches(REGEX_DATE_TIME_EXCLUDE_HMS);
    }
    
    /**
     * 检查字符串是否为 yyyy-MM 格式
     */
    public static boolean isDateMonthFormat(String str) {
        return str.matches(REGEX_DATE_TIME_EXCLUDE_DHMS);
    }
    
    /**
     * (秒数)转换为时分秒格式
     *
     * @param time
     * @return
     */
    public static String secToTime(Long time) {
        String timeStr = null;
        Long hour = 0L;
        Long minute = 0L;
        Long second = 0L;
        if (time <= 0) {
            return "00:00:00";
        } else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99) {
                    return "99:59:59";
                }
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }
    
    public static String unitFormat(Long i) {
        String retStr = null;
        if (i >= 0L && i < 10L) {
            retStr = "0" + Long.toString(i);
        } else {
            retStr = "" + i;
        }
        return retStr;
    }
    
    /**
     * 获取当月第一天时间，例如：输入：2021-05-20 xx:xx:xx 输出：2021-05-01 00:00:00
     */
    public static LocalDateTime getMonthStart(LocalDateTime date) {
        return date.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
    }
    
    /**
     * 获取当月最后一刻，例如：输入：2021-05-20 xx:xx:xx 输出：2021-05-31 23:59:59
     */
    public static LocalDateTime getMonthEnd(LocalDateTime date) {
        return date.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
    }
}
