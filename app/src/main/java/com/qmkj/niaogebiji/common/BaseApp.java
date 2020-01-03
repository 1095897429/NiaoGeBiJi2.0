package com.qmkj.niaogebiji.common;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.huawei.android.hms.agent.HMSAgent;
import com.qmkj.niaogebiji.BuildConfig;
import com.qmkj.niaogebiji.common.base.ActivityManager;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.service.MediaService;
import com.qmkj.niaogebiji.common.utils.ChannelUtil;
import com.socks.library.KLog;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.lang.reflect.Method;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.udesk.UdeskSDKManager;


/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-07-02
 * 描述
 */
public class BaseApp extends Application {


    public static final String TAG = "BaseApp";
    private static BaseApp myApp;
    //注册微信
    private IWXAPI mIWXAPI;
    //Activity管理器
    private ActivityManager mActivityManager;

    public static String oaid;

    {
        PlatformConfig.setWeixin(Constant.WXAPPKEY, Constant.WXAPPSECRET);
        PlatformConfig.setQQZone("1109884279", "Ucwj3qXLehg0oxPK");
    }


//    private MiitHelper.AppIdsUpdater appIdsUpdater = new MiitHelper.AppIdsUpdater() {
//        @Override
//        public void OnIdsAvalid(@NonNull String ids) {
//            Log.e("tag", "++++++ids" +  ids);
//            oaid = ids;
//        }
//    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        KLog.d(TAG,"attachBaseContext");
        myApp = this;
        //MultiDex 分包方法，必须最先初始化
        MultiDex.install(this);
        mActivityManager = new ActivityManager();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //获取OAID等设备标识符
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//            MiitHelper miitHelper = new MiitHelper(appIdsUpdater);
//            miitHelper.getDeviceIds(getApplicationContext());
//        }


        KLog.d(TAG,"onCreate");
        initLogger();
        initDataBase();
        initUMConfig();
        initWX();
        initUDesk();
        initJPush();
    }

    private boolean shouldInit() {
        android.app.ActivityManager am = ((android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<android.app.ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (android.app.ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }




    private void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        String id = JPushInterface.getRegistrationID(this);
        KLog.d("tag","极光推送的id " + id + "");


        // 注册push服务，注册成功后会向DemoMessageReceiver发送广播
        // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
        if (shouldInit()) {
            MiPushClient.registerPush(this, Constant.XiaoMi_APP_ID, Constant.XiaoMi_APP_KEY);
            //打开Log
            LoggerInterface newLogger = new LoggerInterface() {

                @Override
                public void setTag(String tag) {
                    // ignore
                }

                @Override
                public void log(String content, Throwable t) {
                    Log.d(TAG, content, t);
                }

                @Override
                public void log(String content) {
                    Log.d(TAG, content);
                }
            };
            Logger.setLogger(this, newLogger);
        }


        if(canHuaWeiPush()){
            HMSAgent.init(this);
        }


        PushClient.getInstance(getApplicationContext()).initialize();

        PushClient.getInstance(getApplicationContext()).turnOnPush(state -> {
            if (state != 0) {
                KLog.d("tag","vivo 打开push异常[" + state + "]");
            } else {
                KLog.d("tag","vivo 打开push成功");
            }
        });
    }


    /**
     * 判断是否可以使用华为推送
     *
     * @return
     */
    public static Boolean canHuaWeiPush() {
        int emuiApiLevel = 0;
        try {
            Class cls = Class.forName("android.os.SystemProperties");
            Method method = cls.getDeclaredMethod("get", new Class[]{String.class});
            emuiApiLevel = Integer.parseInt((String) method.invoke(cls, new Object[]{"ro.build.hw_emui_api_level"}));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return emuiApiLevel > 5.0;

    }



    //“绑定”服务的intent
//    Intent MediaServiceIntent;
//    public static MediaService.MyBinder mMyBinder;
//
//    public static MediaService mMediaService;



//    private void initService() {
//        MediaServiceIntent = new Intent(this, MediaService.class);
//        //绑定播放音乐的服务
//        bindService(MediaServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//    }


    //TODO 2019.12.17 接入极光 ，华为sdk 时 就会报service连接不上
//    private ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mMyBinder = (MediaService.MyBinder) service;
//            Log.d("tag", "Service与Activity已连接");
//            mMediaService = ((MediaService.MyBinder) service).getInstance();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };


    private void initUDesk() {
        UdeskSDKManager.getInstance().initApiKey(this, Constant.UDESKDOMAIN,Constant.UDESKAPPKEY,Constant.UDESKAPPID);
    }

    private void initWX() {
        mIWXAPI = WXAPIFactory.createWXAPI(this,Constant.WXAPPKEY,true);
        mIWXAPI.registerApp(Constant.WXAPPKEY);
    }

    private void initUMConfig() {
        UMConfigure.init(this, Constant.UMAPPKEY, ChannelUtil.getChannel(this) , UMConfigure.DEVICE_TYPE_PHONE, Constant.UMSECRET);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        //分享集成，统计集成
        UMShareAPI.get(this);
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
        UMConfigure.setEncryptEnabled(BuildConfig.DEBUG);
    }

    /** 初始化数据库框架 */
    private void initDataBase() {
        // ① 初始化数据库
//        mBoxStore = MyObjectBox.builder().androidContext(this).build();
//        // debug模式开启数据库浏览器
//        if (BuildConfig.DEBUG) {
//            new AndroidObjectBrowser(mBoxStore).start(this);
//        }
    }

    /** 获取全局唯一上下文 */
    public static BaseApp getApplication() {
        return myApp;
    }



    /** 初始化日志打印框架 */
    private void initLogger() {
        KLog.init(BuildConfig.LOG_DEBUG);

    }



    /** 返回Activity管理器 */
    public ActivityManager getActivityManage(){
        if(null == mActivityManager){
            mActivityManager = new ActivityManager();
        }
        return mActivityManager;
    }

    /** 退出应用 */
    public void exitApp(){
        mActivityManager.finishAll();
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(0);
    }

    /** 此回调函数只发生在模拟器上，真机不执行,demo中的不可信 */
    @Override
    public void onTerminate() {
        super.onTerminate();
        KLog.d(TAG,"onTerminate");
    }





}
