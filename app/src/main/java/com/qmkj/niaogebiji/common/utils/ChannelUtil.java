package com.qmkj.niaogebiji.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-07-05
 * 描述:一些渠道有关
 */
public class ChannelUtil {

    //内存变量
    private static String mChannel;
    //常量
    private static final String CHANNEL_KEY = "channel";

    /** 返回渠道号 如果获取失败返回 "Q闪" */
    public static String getChannel(Context context){

        if(null == context){
            return "ngbj";
        }

        //内存中获取
        if(!TextUtils.isEmpty(mChannel)){
            return mChannel;
        }
        //apk中获取
        mChannel = getChannelFromApk(context,CHANNEL_KEY);
        if(!TextUtils.isEmpty(mChannel)){
            //保存到sp备用
            saveChannelBySP(context,mChannel);
            return mChannel;
        }

        //sp中获取
        mChannel = getChannelBySP(context);
        if(!TextUtils.isEmpty(mChannel)){
            return mChannel;
        }
        //全部获取失败
        return "ngbj";
    }

    /** 从apk中获取版本信息 */
    public static String getChannelFromApk(Context context, String channelKey) {
        //从apk包中获取
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        //默认放在meta-inf/里， 所以需要再拼接一下
        String key = "META-INF/" + channelKey;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] split = ret.split("_");
        String channel = "";
        if (split.length >= 2) {
            channel = ret.substring(split[0].length() + 1);
        }
        return channel;
    }

    /** 本地保存channel */
    private static void saveChannelBySP(Context context, String channel){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CHANNEL_KEY,channel);
        editor.apply();
    }

    /** 本地获取channel */
    private static String getChannelBySP(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(CHANNEL_KEY,"");
    }

}
