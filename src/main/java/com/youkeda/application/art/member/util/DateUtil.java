package com.youkeda.application.art.member.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author joe
 * @date 2020/4/14
 */
public class DateUtil {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 格式化日期，yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static LocalDateTime parse(String time) {
        try {
            return LocalDateTime.parse(time, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取某年第一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static LocalDateTime getYearFirst(int year) {
        LocalDateTime ldt = LocalDateTime.now();
        ldt = ldt.withYear(year).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        return ldt;
    }
}
