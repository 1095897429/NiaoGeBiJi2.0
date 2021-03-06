package com.qmkj.niaogebiji.common.net.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.qmkj.niaogebiji.BuildConfig;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.net.api.ApiEncryptService;
import com.qmkj.niaogebiji.common.utils.AESCipherUtils;
import com.qmkj.niaogebiji.common.utils.ChannelUtil;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.socks.library.KLog;

import java.lang.reflect.Method;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.READ_PHONE_STATE;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-07-03
 * 描述:网络请求管理类
 */
public class RetrofitHelper {

    //组合Retrofit
    private Retrofit mRetrofit;
    //单例
    private static RetrofitHelper instance;
    //接口地址
    private static ApiEncryptService mApiService;
    //端口地址
    public static String BASEURL = "http://mitao.birdbrowser.ifo/";
    private static final String BASE_URL_USER = "http://www.111.com/";
    private static final String BASE_URL_PAY = "http://www.222.com/";
    //加密参数
    private static final String AES_KEY = "werojcllljowerl3905uLDZ90t20jsdf";
    private static final String AES_IV = "0123456789ABCDEF";


    private RetrofitHelper(){}

    public static RetrofitHelper getInstance(){
        if(null == instance){
            synchronized (RetrofitHelper.class){
                if(null == instance){
                    instance = new RetrofitHelper();
                }
            }
        }
        return instance;
    }





    //获取Retrofit
    public Retrofit getRetrofit(){
        if(null == mRetrofit){
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(BuildConfig.URL)
                    .client(OkHttpHelper.getInstance().getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
            mRetrofit = builder.build();
        }
        return mRetrofit;
    }


    //获取service的代理对象
    public static ApiEncryptService getApiService(){
        if(null == mApiService){
            mApiService = getInstance().getRetrofit().create(ApiEncryptService.class);
        }

        return mApiService;
    }


    //配置公共参数 -- 加密
    public static String commonParam(Map<String, String> map) {
        //1-iOS, 2-Android
        map.put("device_type","2");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String idfa;
            if (!TextUtils.isEmpty(BaseApp.oaid)) {
                idfa = BaseApp.oaid;
                map.put("device_serial", idfa);
            }else{
                if(ActivityCompat.checkSelfPermission(BaseApp.getApplication(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
                    String result = StringUtil.getIMEI();
                    if(!TextUtils.isEmpty(result)){
                        map.put("device_serial",StringUtil.getIMEI());
                    }else{
                        String uniqueId = StringUtil.getAndroidId(BaseApp.getApplication());
                        map.put("device_serial", uniqueId);
                    }
                }
            }

        } else {
            if(ActivityCompat.checkSelfPermission(BaseApp.getApplication(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
                String result = StringUtil.getIMEI();
                if(!TextUtils.isEmpty(result)){
                    map.put("device_serial",StringUtil.getIMEI());
                }else{
                    String uniqueId = StringUtil.getAndroidId(BaseApp.getApplication());
                    map.put("device_serial", uniqueId);
                }
            }
        }


        map.put("version_no", AppUtils.getAppVersionName());
        map.put("version_code", AppUtils.getAppVersionCode() + "");
        map.put("timestamp","");

        //注册测试服
        RegisterLoginBean.UserInfo userInfo = StringUtil.getUserInfoBean();
        String token ;
        if(null != userInfo){
            token = userInfo.getAccess_token();
        }else{
            token = "";
        }
        map.put("access_token",token);

        //TODO 2020.1.16号新增
//        KLog.e("tag","jpush_id " + JPushInterface.getRegistrationID(BaseApp.getApplication()));
        map.put("jpush_id", JPushInterface.getRegistrationID(BaseApp.getApplication()));

        //渠道号
        map.put("app_channel", ChannelUtil.getChannel(BaseApp.getApplication()));

        //第一步拼接
        Gson gson = new Gson();
        String json = gson.toJson(map);
        //第二步加密
        String key = AES_KEY;
        String iv = AES_IV;

        byte[] result_byte = AESCipherUtils.AES_cbc_encrypt(json.getBytes(),key.getBytes(),iv.getBytes());
        String result ;
        result = Base64.encodeToString(result_byte,Base64.NO_WRAP);
        return result;

    }


}
