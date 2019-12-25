package com.qmkj.niaogebiji.common.utils;

import com.blankj.utilcode.util.TimeUtils;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-10-15
 * 描述:(时间戳转换几天前，几分钟前，刚刚等)
 */
public class GetTimeAgoUtil {


    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;

    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    /**
     * 按照毫秒来存储
     *
     * @param time
     * @return
     */
    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return "未知时间";
        }

        //时间戳相减
        final long diff = now - time;
        //小于1分钟
        if (diff < MINUTE_MILLIS) {
            return "刚刚";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1分钟前";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "分钟前";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1小时前";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + "小时前";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "昨天";
        } else {
            return diff / DAY_MILLIS + "天前";
        }
    }


    /** 项目中运用的逻辑 */
    public static String getTimeAgoByApp(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return "未知时间";
        }

        //时间戳相减
        final long diff = now - time;
        //小于1分钟
        if (diff < MINUTE_MILLIS) {
            return "刚刚";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1分钟前";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "分钟前";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + "小时前";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "昨天";
        } else if (diff < 72 * HOUR_MILLIS) {
            return (diff / DAY_MILLIS) + "天前";
        } else {
            String data =  TimeUtils.millis2String(time,"yyyy/MM/dd");
            return data;
        }
    }
}
