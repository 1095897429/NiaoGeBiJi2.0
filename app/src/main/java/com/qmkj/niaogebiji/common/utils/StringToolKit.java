package com.qmkj.niaogebiji.common.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.qmkj.niaogebiji.BuildConfig;
import com.qmkj.niaogebiji.common.BaseApp;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class StringToolKit {


    /** 判读字符串是否为空 */
    public static String dealNullOrEmpty(String s){
        if(null == s || s.length() == 0){
            return "";
        }else{
            return s;
        }
    }


    /** 加入一些必须参数,获取sign  -- 小鱼*/
    public static Map<String, Object> mapSecretByXiaoYu(Map<String, Object> map){
        //版本号
        map.put("app_version", BuildConfig.VERSION_NAME + "");
        //包名
        map.put("package_name", AppUtils.getAppPackageName());
        //渠道名
        map.put("channel_code",ChannelUtil.getChannel(BaseApp.getApplication()));
        //设备信息
        if(ActivityCompat.checkSelfPermission(BaseApp.getApplication(),
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            map.put("device_udid", PhoneUtils.getIMEI());
            map.put("device_manufact", DeviceUtils.getManufacturer());
            map.put("device_model", DeviceUtils.getModel());
            map.put("bssid","");
        }
        TreeMap<String, Object> treeMap = (TreeMap<String, Object>) sortMapByKey(map);
        String mapStr = xYZMap2String(treeMap).replaceAll("\\\\","");
        String sign = md5(mapStr + BuildConfig.SecretKey);
        treeMap.put("_sign",sign);

        return treeMap;
    }



    /** 加入一些必须参数,获取sign  -- 小鸟*/
    public static Map<String, Object> mapSecret(Map<String, Object> map){
        //版本号
        map.put("version", BuildConfig.VERSION_CODE + "");
        //设备类型
        map.put("device_type","3");
        //版本名称
        map.put("app_version_name",BuildConfig.VERSION_NAME);
        //友盟
        map.put("upush","");
        //包名
        map.put("package_name", AppUtils.getAppPackageName());
        //渠道名
        map.put("channel_code",ChannelUtil.getChannel(BaseApp.getApplication()));
        //设备信息
        if(ActivityCompat.checkSelfPermission(BaseApp.getApplication(),
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            map.put("device_serial", PhoneUtils.getIMEI());
            //厂商
            map.put("phone_type", Build.BRAND);
            map.put("device_name", DeviceUtils.getModel());
            map.put("device_manufact", DeviceUtils.getManufacturer());
        }
        TreeMap<String, Object> treeMap = (TreeMap<String, Object>) sortMapByKey(map);
        String mapStr = map2String(treeMap).replaceAll("\\\\","");
        String sign = md5(mapStr + BuildConfig.SecretKey);
        treeMap.put("_sign",sign);

        return treeMap;
    }

    /** 使用Map 按key 进行排序 */
    public static Map<String, Object> sortMapByKey(Map<String, Object> map){
        if(null == map || map.isEmpty()){
            return null;
        }
        Map<String, Object> sortMap = new TreeMap<>(new MapKeyComparator());
        sortMap.putAll(map);

        return sortMap;
    }
    /** 比较器  */
    static class MapKeyComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o2.compareTo(o1);
        }
    }


    /** Map 转化为 String key1value1key2value2*/
    public static String map2String(Map<String, Object> map){
        if(null == map || map.isEmpty()){
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()){
            sb.append(entry.getKey()).append(entry.getValue());
        }

        return sb.toString();
    }


    /** Map 转化为 String key1=value1key2=value2*/
    public static String xYMap2String(Map<String, Object> map){
        if(null == map || map.isEmpty()){
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()){

            if(null == entry || null == entry.getKey()){
                continue;
            }
            if(null == entry.getValue()){
                sb.append(entry.getKey()).append("=").append("");
            }else{
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }

        return sb.toString();
    }

    /** Map 转化为 String key1=value1&key2=value2*/
    public static String xYZMap2String(Map<String, Object> map){
        if(null == map || map.isEmpty()){
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()){

            if(null == entry || null == entry.getKey()){
                continue;
            }
            if(null == entry.getValue()){
                sb.append(entry.getKey()).append("=").append("").append("&");
            }else{
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }

        //去掉最后一个&
        String result  = sb.toString();
        if(!TextUtils.isEmpty(result)){
            result = result.substring(0,result.length() - 1);
            return result;
        }

        return result;
    }

    /** Map 转化为 RequestBody */
    public static RequestBody map2RequestBody(Map<String, Object> params){
        JSONObject json = new JSONObject(params);
        String result = json.toString();
        return RequestBody.create(MediaType.parse("application/json"),result);
    }

    /** md5 加密 */
    public static String md5(String string){
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 ;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }



}