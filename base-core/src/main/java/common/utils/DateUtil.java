package common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间工具类
 */
public class DateUtil {

    // ================== 常用格式 ==================
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_NO_LINE = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter DATE_CN = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    public static final DateTimeFormatter TIME_MINUTE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_COMPACT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static final DateTimeFormatter SHORT_TIME = DateTimeFormatter.ofPattern("HH:mm");

    // ================== 时间常量 ==================
    private static final long DAY_IN_MILLIS = 24 * 3600 * 1000;
    private static final long HOUR_IN_MILLIS = 3600 * 1000;
    private static final long MINUTE_IN_MILLIS = 60 * 1000;
    private static final long SECOND_IN_MILLIS = 1000;

    /**
     * 获取当前日期时间
     */
    public static Date getDate() {
        return new Date();
    }

    /**
     * 获取当前日期（LocalDate，不含时分秒）
     */
    public static LocalDate getLocalDate() {
        return LocalDate.now();
    }

    /**
     * 获取当前日期时间（LocalDateTime）
     */
    public static LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }

    /**
     * Date 转 LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * LocalDateTime 转 Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 格式化日期时间
     */
    public static String format(LocalDateTime localDateTime, DateTimeFormatter formatter) {
        return localDateTime.format(formatter);
    }

    /**
     * 格式化日期
     */
    public static String format(LocalDate localDate, DateTimeFormatter formatter) {
        return localDate.format(formatter);
    }

    /**
     * 解析字符串为 LocalDateTime
     */
    public static LocalDateTime parseDateTime(String text, DateTimeFormatter formatter) {
        return LocalDateTime.parse(text, formatter);
    }

    /**
     * 解析字符串为 LocalDate
     */
    public static LocalDate parseDate(String text, DateTimeFormatter formatter) {
        return LocalDate.parse(text, formatter);
    }
}


