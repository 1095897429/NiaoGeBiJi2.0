package com.qmkj.niaogebiji.common.utils;

import com.socks.library.KLog;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-03
 * 描述:
 */
public class FormatUtil {

    public static double pers = 1048576; //1024*1024



    //long==> 616.19KB,3.73M
//    public static String sizeFormatNum2String(long size) {
//        String s ;
//        if(size >= 1024*1024){
//            s= String.format("%.2f", (double)size/pers)+"M";
//        } else if(size >= 1024){
//            s= String.format("%.2f", (double)size/(1024))+"KB";
//        }else{
//            s = String.format("%.2f", (double)size)+"B";
//        }
//
//        return s;
//    }

    //long==> 616.19KB,3.73M
    public static String sizeFormatNum2String(long size) {
        KLog.d("tag","缓存图片文件的大小是 " + size + " 单位是 byte");
        String s = "";
        if(size>1024*1024)
            s=String.format("%.2f", (double)size/pers)+"M";
        else
            s=String.format("%.2f", (double)size/(1024))+"KB";
        return s;
    }




    //616.19KB,3.73M ==> long
    public static long sizeFormatString2Num(String str){
        long size = 0;
        if(str!=null){
            if(str.endsWith("KB"))
                size = (long)(Double.parseDouble(str.substring(0,str.length()-2))*1024);
            else if(str.endsWith("M"))
                size = (long)(Double.parseDouble(str.substring(0,str.length()-1))*pers);
        }
        return size;

    }
}
