package com.linghong.fkdp.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间转换
 *
 * @author luck_nhb
 */
public class DateUtil {
    /**
     * 将Date日期格式转换成"yyyy-MM-dd HH:mm:ss"
     *
     * @param date
     * @return
     */
    public static String date2SimpleDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(date);
        return format;
    }

    /**
     * 将Date日期格式转换成"yyyy-MM-dd"
     *
     * @param date
     * @return
     */
    public static String date2SimpleDay(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String format = simpleDateFormat.format(date);
        return format;
    }

    /**
     * 将Date日期格式转换成"yyyy-MM-dd"
     *
     * @param date
     * @return
     */
    public static String date2SimpleDay(Long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String format = simpleDateFormat.format(date);
        return format;
    }

    /**
     * 将Long型转换成日期格式转换成"yyyy-MM-dd HH:mm:ss"
     *
     * @param date
     * @return
     */
    public static String long2SimpleDate(Long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(date.longValue()));
    }


    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     */
    public static String getDistanceTime(Date startTime, Date endTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;

        long time1 = startTime.getTime();
        long time2 = endTime.getTime();
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        return day + ":" + hour + ":" + min + ":" + sec;
    }


    /**
     * 两个时间相差距离多少小时多少分多少秒
     */
    public static String getDistanceHourse(Long startTime, Long endTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;

        long time1 = startTime;
        long time2 = endTime;
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        return (hour + day * 24) + ":" + min + ":" + sec;
    }

}
