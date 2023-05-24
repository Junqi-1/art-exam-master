package com.youkeda.application.art.member.util;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

/**
 * ID 工具类
 */
public class IDUtils {
    private static String[] WORDS =
        {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
            "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8"};

    /**
     * 获取唯一ID
     */
    public static String getId() {
        return ObjectId.get().toString();
    }

    /**
     * 根据时间生成ID，不保证唯一
     *
     * @return
     */
    public static String generateId() {
        LocalDateTime localDateTime = LocalDateTime.now();
        StringBuilder sb = new StringBuilder();
        int year = localDateTime.getYear() - 2019;
        sb.append(WORDS[year]);
        int month = localDateTime.getMonth().getValue();
        sb.append(WORDS[month]);
        int day = localDateTime.getDayOfMonth();
        sb.append(WORDS[day]);
        int hour = localDateTime.getHour();
        sb.append(WORDS[hour]);
        int minute = localDateTime.getMinute();
        sb.append(WORDS[minute]);
        int second = localDateTime.getSecond();
        sb.append(WORDS[second]);
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(getId());
    }

}
