package com.qmkj.niaogebiji.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-06
 * 描述:
 */
public class TimeAppUtils {

    //时间戳转换日期格式字符串
    public static String timeStamp2Date(long time, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

    //日期格式字符串转换时间戳
    public static String date2TimeStamp(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //秒数转换成时分秒
    public static String convertSecToTimeString(long lSeconds) {
        long nHour = lSeconds / 3600;
        long nMin = lSeconds % 3600;
        long nSec = nMin % 60;
        nMin = nMin / 60;

        return String.format("%02d小时%02d分钟%02d秒", nHour, nMin, nSec);
    }
}
