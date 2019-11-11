package com.qmkj.niaogebiji.common.net.helper;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-07-03
 * 描述:okhttp帮助类
 */
public class OkHttpHelper {
    //读取时间
    private static final long DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000;
    //写入时间
    private static final long DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000;
    //超时时间
    private static final long DEFAULT_CONNECT_TIMEOUT_MILLIS = 20 * 1000;
    //单例
    private static OkHttpHelper sInstance;
    //组合
    private OkHttpClient mOkHttpClient;


    private OkHttpHelper(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        mOkHttpClient = builder
                .readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                // 失败重发
                .retryOnConnectionFailure(true)
                //请求log
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }

    public static OkHttpHelper getInstance() {
        if (null == sInstance) {
            synchronized (OkHttpHelper.class) {
                if (sInstance == null) {
                    sInstance = new OkHttpHelper();
                }
            }
        }
        return sInstance;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}
