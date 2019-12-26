package com.qmkj.niaogebiji.common.utils;

import com.socks.library.KLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        KLog.d("tag","分 " + nMin + "秒 " + nSec);
        if(nSec == 0){
            return String.format("%02d分钟", nMin);
        }else{
            return String.format("%02d分钟%02d秒", nMin, nSec);
        }

    }






    /**
     * 获取前n天日期、后n天日期
     *
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @return
     */
    public static String getOldDate(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy年MM月dd日");
        //当前日期
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        KLog.d("前7天==" + dft.format(endDate));
        return dft.format(endDate);
    }


    //两个时间间隔判断
    public static int stringDaysBetween(String smdate, String bdate){

//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(sdf.parse(smdate));
//            long time1 = cal.getTimeInMillis();
//            cal.setTime(sdf.parse(bdate));
//            long time2 = cal.getTimeInMillis();
//            long between_days = (time2 - time1) / (1000 * 3600 * 24);
//            return Integer.parseInt(String.valueOf(between_days));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return 0;
    }
}
