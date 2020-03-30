package com.qmkj.niaogebiji.module.activity;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.CleanUtils;
import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.SDCardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.ZipUtils;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.GetPhoneInfoListener;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.huawei.android.hms.agent.HMSAgent;
import com.qmkj.niaogebiji.BuildConfig;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.LaunchPermissDialog;
import com.qmkj.niaogebiji.common.dialog.PermissForbidPhoneDialog;
import com.qmkj.niaogebiji.common.dialog.PermissForbidStorageDialog;
import com.qmkj.niaogebiji.common.dialog.SecretAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.ChannelUtil;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.module.bean.JPushBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.socks.library.KLog;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import butterknife.BindView;
import cn.jiguang.jmlinksdk.api.JMLinkAPI;
import cn.jiguang.jmlinksdk.api.JMLinkCallback;
import cn.magicwindow.MWConfiguration;
import cn.magicwindow.MagicWindowSDK;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述:权限管理 -- 弹框，然后弹出所有权限
 */
public class SplashActivity extends BaseActivity {


    @BindView(R.id.image)
    ImageView animationIV;

    private LaunchPermissDialog mLaunchPermissDialog;
    private PermissForbidStorageDialog mPermissForbidStorageDialog;
    private PermissForbidPhoneDialog mPermissForbidPhoneDialog;
    private static final int INIT_PERMISSIONS = 100;
    //第二个弹框出现，就不需要onResume时检查权限
    boolean continuerequest = true;
    //跳转到设置界面，然后返回时onResume检查权限
    boolean isJumpSet = false;
    String permissions[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    String permissions1[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    String permissions2[] = new String[]{
            Manifest.permission.READ_PHONE_STATE};

    private AnimationDrawable animationDrawable;


    private JPushBean mJPushBean;


    @Override
    public void initFirstData() {


        getStartInfo();

        String s = "i love china";
        char[] arrys = s.toCharArray();
        KLog.d("tag",arrys[0]);
        String s2 = "我是个好人";
        char[] arrys2 = s2.toCharArray();
        KLog.d("tag",arrys2[0]);

        KLog.d("tag",md5(s));
        KLog.d("tag",md5(s2));

        TreeSet<String> set = new TreeSet();
        set.add("sss");
        set.add("222");
        for (String sss : set) {
            KLog.d("tag","ssss " + sss);
        }



//        Gson gson = new Gson();
//        String jsonString = "{\"name\":\"sunny\",\"age\":24}";
//        String jsonString2 = "{\"name\": \"zhangsan\", \"age\": 15,\"grade\": [ 95, 98] }";
//        RegisterLoginBean user = fromJson(jsonString2, RegisterLoginBean.class);



//        JMLinkAPI.getInstance().register("open_article", new JMLinkCallback() {
//            @Override
//            public void execute(Map<String, String> map, Uri uri) {
//                KLog.e("tag","数据 map " + map);
//            }
//        });
    }




    public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        Object object = fromJson(json, (Type) classOfT);
        return Primitives.wrap(classOfT).cast(object);
    }


    public <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        if (json == null) {
            return null;
        }
        StringReader reader = new StringReader(json);
        T target = (T) fromJson(reader, typeOfT);
        return target;
    }


    public <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        JsonReader jsonReader = newJsonReader(json);
        T object = (T) fromJson(jsonReader, typeOfT);
        return object;
    }


    public <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        boolean isEmpty = true;
        boolean oldLenient = reader.isLenient();
        reader.setLenient(true);
        try {
            reader.peek();
            isEmpty = false;
            //typeOfT class com.qmkj.niaogebiji.module.bean.RegisterLoginBean
            TypeToken<T> typeToken = (TypeToken<T>) TypeToken.get(typeOfT);
            KLog.e("tag","typeOfT " + typeToken.getType());
            KLog.e("tag","rawType " + typeToken.getRawType());
            TypeAdapter<T> typeAdapter = getAdapter(typeToken);
            T object = typeAdapter.read(reader);
            return object;
        } catch (EOFException e) {
            /*
             * For compatibility with JSON 1.5 and earlier, we return null for empty
             * documents instead of throwing.
             */
            if (isEmpty) {
                return null;
            }
            throw new JsonSyntaxException(e);
        } catch (IllegalStateException e) {
            throw new JsonSyntaxException(e);
        } catch (IOException e) {
            // TODO(inder): Figure out whether it is indeed right to rethrow this as JsonSyntaxException
            throw new JsonSyntaxException(e);
        } finally {
            reader.setLenient(oldLenient);
        }
    }


    public <T> TypeAdapter<T> getAdapter(TypeToken<T> type) {

        return null;
    }



    public JsonReader newJsonReader(Reader reader) {
        JsonReader jsonReader = new JsonReader(reader);
        jsonReader.setLenient(false);
        return jsonReader;
    }



    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
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

    private void getStartInfo() {



        KLog.d("tag","应用的Id  " + AppUtils.getAppUid());
        KLog.d("tag","应用的签名MD5  " + AppUtils.getAppSignatureMD5());
        KLog.d("tag","应用的签名SHA1 [方法获取||cd .android + keytool -list -v -keystore xxx.keystore获取] " + AppUtils.getAppSignatureSHA1());
        KLog.d("tag","应用的签名SHA256  " + AppUtils.getAppSignatureSHA256());
        KLog.d("tag","状态栏的高度  " + BarUtils.getStatusBarHeight());
        KLog.d("tag","内部缓存路径  " + Utils.getApp().getCacheDir().getAbsolutePath());

        String string = "中文";
        KLog.d("tag","字节是  " + string.getBytes());
        ConvertUtils.bytes2Bits(string.getBytes());



        KLog.d("tag","当前设备是否root过，通过是否有/system/su文件判断  " + DeviceUtils.isDeviceRooted());
        KLog.d("tag","应用ADB是否可用 [设置中关闭调试即可] " +  DeviceUtils.isAdbEnabled());
        KLog.d("tag","设备的Android系统版本  " +  DeviceUtils.getSDKVersionName());
        KLog.d("tag","设备的Android系统API  " +  DeviceUtils.getSDKVersionCode());
        KLog.d("tag","设备当前的AndroidID [此值改变情况有：恢复出厂设置/刷机/root等] " + DeviceUtils.getAndroidID());
        KLog.d("tag","应用的MAC地址  " +  DeviceUtils.getMacAddress());
        KLog.d("tag","手机的厂商  " +  DeviceUtils.getManufacturer());
        KLog.d("tag","手机的型号  " +  DeviceUtils.getModel());
        KLog.d("tag","是否是平板  " +  DeviceUtils.isTablet());
        KLog.d("tag","是否是模拟器【他的方法不能用，主要是有个unkwon,去掉即可】  " +  DeviceUtils.isEmulator());

        String url = "http://www.baidu.com?wd=";
        KLog.d("tag","编码url [You don't encode the entire URL]" +  EncodeUtils.urlEncode("中文"));
        KLog.d("tag","解码url [NoWrap 略去所有的换行符  NoPadding 略去编码字符串最后的“=”]" +  EncodeUtils.urlDecode(EncodeUtils.urlEncode("中文")));





        KLog.d("tag","网络是否连接 " +  NetworkUtils.isConnected());
        NetworkUtils.isAvailableAsync(data -> KLog.d("tag","网络是否可用 " + data ));
        KLog.d("tag","移动数据是否打开 " +  NetworkUtils.getMobileDataEnabled());
        KLog.d("tag","wifi数据是否(可用)打开 " +  NetworkUtils.getWifiEnabled());
        KLog.d("tag","wifi数据是否链接 " +  NetworkUtils.isWifiConnected());
        KLog.d("tag","使用的是否是 移动数据 " +  NetworkUtils.isMobileData());
        KLog.d("tag","移动数据是否打开 " +  NetworkUtils.getMobileDataEnabled());
        KLog.d("tag","移动数据的运营商 " +  NetworkUtils.getNetworkOperatorName());

        KLog.d("tag","网络的类型 " +  NetworkUtils.getNetworkType());
        KLog.d("tag","ip地址(不使用 ipV4) " +  NetworkUtils.getIPAddress(false));
        KLog.d("tag","ip地址(使用 ipV4) " +  NetworkUtils.getIPAddress(true));

        KLog.d("tag","ip地址(wifi) " +  NetworkUtils.getIpAddressByWifi());
        KLog.d("tag","广播ip地址() " +  NetworkUtils.getBroadcastIpAddress());

        KLog.d("tag","设置是否是手机 " +  PhoneUtils.isPhone());
//        KLog.d("tag","IMIE " +  PhoneUtils.getIMEI());
//        KLog.d("tag","IMSI " +  PhoneUtils.getIMSI());
        KLog.d("tag","SIM卡是否准备好 " +  PhoneUtils.isSimCardReady());
        KLog.d("tag","SIM卡运营商 " +  PhoneUtils.getSimOperatorName());
//        KLog.d("tag","手机状态信息 " +  PhoneUtils.getPhoneStatus());

        KLog.d("tag","获取当前线程包名 " +  ProcessUtils.getForegroundProcessName());

        KLog.d("tag","屏幕的宽度 " +  ScreenUtils.getScreenWidth());
        KLog.d("tag","屏幕的高度 " +  ScreenUtils.getScreenHeight());
        KLog.d("tag","当前屏幕方向是否是竖屏 " +  ScreenUtils.isPortrait());

        Bitmap bitmap = ScreenUtils.screenShot(this);
        Bitmap bitmap1 = ScreenUtils.screenShot(this,true);
        KLog.d("tag","屏幕的截屏对象是否为null " + bitmap  + " bitmap1 " + bitmap1);
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/laopai/";
        String filePath = baseDir + "zl_jieping.jpg";
        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File f = new File(filePath);
        try {
            f.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        boolean isSave = ImageUtils.save(bitmap,f, Bitmap.CompressFormat.JPEG);
        KLog.d("tag","屏幕的截屏是否成功 " + isSave );

        KLog.d("tag","是否锁屏 " + ScreenUtils.isScreenLock() );


        KLog.d("tag","SD卡是否可用 " + SDCardUtils.isSDCardEnableByEnvironment());
        KLog.d("tag","SD卡的存储目录 " + SDCardUtils.getSDCardPathByEnvironment());


        KLog.d("tag","dp2px 1dp =  " + SizeUtils.dp2px(1)  + "px" );


        String is = "";
        String is2 = " ";
        KLog.d("tag","字符串是否为null 或者 长度为0 " + StringUtils.isEmpty(is) );
        KLog.d("tag","字符串是否为null 或者 为空格 " + StringUtils.isTrimEmpty(is2) );
        String is3 = "ab";
        String is4 = "AB";
        KLog.d("tag","AB ab 字符串忽略大小写 " + StringUtils.equalsIgnoreCase(is3,is4) );
        String is5 = "我爱中国";
        KLog.d("tag","字符串反转 一个正序 一个倒叙 " + StringUtils.reverse(is5) );
        KLog.d("tag","用半角字符显示 " + StringUtils.toDBC(is5) );
        KLog.d("tag","用全角字符显示 " + StringUtils.toSBC(is5) );

        long time = TimeUtils.getNowMills();
        String sTime = " 2020-03-12 21:48:14";
        KLog.d("tag","系统的毫秒数 " + time);
        KLog.d("tag","当前时间戳的字符串格式 " + TimeUtils.getNowString());
        KLog.d("tag","当前日期 " + TimeUtils.getNowDate());
        KLog.d("tag","时间戳转字符串 【利用时间戳 创建 Date 对象，在利用DateFormat 去 format 日期 】 " + TimeUtils.millis2String(time));

        KLog.d("tag","字符串转 时间戳 " + TimeUtils.string2Millis(sTime));
        KLog.d("tag","字符串转 Date类型 " + TimeUtils.string2Date(sTime));

        KLog.d("tag","是不是今天 【00:00 -- 00:00 + 86400000 判断时间戳是否在这个范围内】 " +  TimeUtils.isToday("1584022716018"));

        KLog.d("tag","是否是闰年 【能被4整除并且不能被100整除 或者 能被400整除】" + TimeUtils.isLeapYear(time));

        KLog.d("tag","时间戳对应的星期 " + TimeUtils.getChineseWeek(time));

        KLog.d("tag","生肖  " + TimeUtils.getChineseZodiac(time));


        KLog.d("tag","CUP的数量  " + Runtime.getRuntime().availableProcessors());

        KLog.d("tag","线程池 ExecutorSercvice  " + "的创建方式有两种 1.一种是工厂方法 Executors.newFixedThreadPool  2.一种是直接创建 new ThreadPoolExecutor" );

    }


    //运用递归的方式
    public boolean deleteFile(File file){

        if(file == null){
            return false;
        }

        if(!file.exists()){
            return false;
        }

        if(!file.isDirectory()){
            return false;
        }


        File[] files = file.listFiles();
        if(files != null && file.length() != 0){
            for(File file1 : files){
                if(file1.isFile()){
                    file.delete();
                }else if(file1.isDirectory()){
                    deleteFile(file1);
                }
            }
        }


        return true;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //当前Activity是否为根Activity，即应用启动的第一个Activity
        if (!this.isTaskRoot() && getIntent() != null) {
            String action = getIntent().getAction();
            if (getIntent().hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
                return;
            }
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        //帧动画
        animationIV.setImageResource(R.drawable.splash_animation1);
        animationDrawable = (AnimationDrawable) animationIV.getDrawable();
        animationDrawable.start();


        //TODO 2020.1.10 应用宝渠道首个界面显示隐私弹框
        boolean isAgree = SPUtils.getInstance().getBoolean("isAgree");
        if (!isAgree) {
            showSecretDialog(this);
        }


        if(getIntent().getExtras() != null){
            mJPushBean = (JPushBean) getIntent().getExtras().getSerializable("jpushbean");
        }
    }


    @Override
    public void initData() {

//        KLog.e("tag","SplashActivity ----  initData");


        if(isHuaWei()){
            /** SDK连接HMS -- 打开后的首个界面 */
            HMSAgent.connect(this, rst -> KLog.e("tag","HMS connect end:" + rst));
            getToken();
        }
    }

    //推送走这里了
    @Override
    protected void onNewIntent(Intent intent) {
        KLog.e("tag","SplashActivity ----  onNewIntent");
        super.onNewIntent(intent);
        if(intent.getExtras() != null){
            mJPushBean = (JPushBean) intent.getExtras().getSerializable("jpushbean");
        }
    }

    //0867229032433051300004997800CN01 -- 测试机器1
    //AAXKNSLVvqqvtmuIxbiAI2-syr7aRanHEFA9XO0qtA_WDJQGGPQM6yM6srOB6NLQPkf_yFc6skaTqkkMKD7aOJzXDj1Zjuv7asdTGDtqvo2rHHciRvaiZBqnAx5aj5d2WQ -- 测试机器2
    public static boolean isHuaWei() {
        String manufacturer = Build.MANUFACTURER;
        //这个字符串可以自己定义,例如判断华为就填写huawei,魅族就填写meizu
        if ("huawei".equalsIgnoreCase(manufacturer)) {
            return true;
        }
        return false;
    }



    /**
     * 获取token
     */
    private void getToken() {
//        KLog.d("tag","get token: begin");
        HMSAgent.Push.getToken(rtnCode -> KLog.d("tag","get token: end" + rtnCode));
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isAgree = SPUtils.getInstance().getBoolean("isAgree");
        if (isAgree) {
            if(isJumpSet || continuerequest){
                isJumpSet = false;
                continuerequest = true;
                checkAppPermission();
            }
        }

    }

    private void checkAppPermission() {
        //发请求之前判断权限
        if (hasPermissions(this, permissions)) {
            toNext();
        }else{

            showPermissDialog();
        }
    }



    private void showPermissDialog() {
        if (isFinishing()) {
            return;
        }

        if (mLaunchPermissDialog != null && mLaunchPermissDialog.isShowing()) {
            mLaunchPermissDialog.dismiss();
        }

        mLaunchPermissDialog = new LaunchPermissDialog(this).builder();

        //如果有权限了，设置对勾状态
        if(hasPermissions(this,permissions1)){
            if(null != mLaunchPermissDialog){
                mLaunchPermissDialog.setSdcardOK();
            }
        }

        if(hasPermissions(this,permissions2)){
            if(null != mLaunchPermissDialog){
                mLaunchPermissDialog.setPhoneOK();
            }
        }

        mLaunchPermissDialog.setPositionButton("立即开启", v -> {

            // 声明一个集合，在后面的代码中用来存储用户拒绝授权的权限
            List<String> mPermissionList = new ArrayList<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //没有同意的话，加入到临时集合中
                if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[0]);
                }
                if (checkSelfPermission(permissions[1]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[1]);
                }

                //如果不为空，则申请权限
                if (!mPermissionList.isEmpty()) {
                    //将List转为数组
                    String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                    ActivityCompat.requestPermissions(SplashActivity.this, permissions, INIT_PERMISSIONS);
                }
            }
            mLaunchPermissDialog.dismiss();

        }).setCanceledOnTouchOutside(false);
        mLaunchPermissDialog.show();
    }



    private void showPermissForbidDialog() {
        if (isFinishing()) {
            return;
        }
        if (mPermissForbidStorageDialog != null && mPermissForbidStorageDialog.isShowing()) {
            mPermissForbidStorageDialog.dismiss();
        }
        mPermissForbidStorageDialog = new PermissForbidStorageDialog(this).builder();
        mPermissForbidStorageDialog.setCallBack(new PermissForbidStorageDialog.CallBack() {
            @Override
            public void forward() {
                //引导用户至设置页手动授权
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                isJumpSet = true;
                mPermissForbidStorageDialog.dismiss();
            }


            @Override
            public void exit() {
                SplashActivity.this.finish();

            }
        });
        mPermissForbidStorageDialog.show();
    }


    private void showPermissForbidPhoneDialog() {
        if (isFinishing()) {
            return;
        }
        if (mPermissForbidPhoneDialog != null && mPermissForbidPhoneDialog.isShowing()) {
            mPermissForbidPhoneDialog.dismiss();
        }
        mPermissForbidPhoneDialog = new PermissForbidPhoneDialog(this).builder();
        mPermissForbidPhoneDialog.setCallBack(new PermissForbidPhoneDialog.CallBack() {
            @Override
            public void forward() {
                //引导用户至设置页手动授权
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                isJumpSet = true;
                mPermissForbidPhoneDialog.dismiss();
            }


            @Override
            public void exit() {
                finish();

            }
        });
        mPermissForbidPhoneDialog.show();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; ++i) {

            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                //用户勾选了不再提示，函数返回false
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    //解释原因，并且引导用户至设置页手动授权
                    continuerequest = false;

                    if(permissions[0].equals("android.permission.WRITE_EXTERNAL_STORAGE")){
                        showPermissForbidDialog();
                    }else if(permissions[0].equals("android.permission.READ_PHONE_STATE")){
                        showPermissForbidPhoneDialog();
                    }
                    return;
                }

            }
        }

        //根据选择结果显示对勾
        if(hasPermissions(this,permissions1)){
            if(null != mLaunchPermissDialog){
                mLaunchPermissDialog.setSdcardOK();
            }
        }

        if(hasPermissions(this,permissions2)){
            if(null != mLaunchPermissDialog){
                mLaunchPermissDialog.setPhoneOK();
            }
        }

    }






    //检查权限
    @Override
    protected   boolean hasPermissions(@NonNull Context context,
                                       @Size(min = 1) @NonNull String... perms) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w("tag", "hasPermissions: API version < M, returning true by default");
            return true;
        }

        if (context == null) {
            throw new IllegalArgumentException("Can't check permissions for null context");
        }

        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }



    public void toNext(){
        new Handler().postDelayed(() -> {
            boolean firstCome = SPUtils.getInstance().getBoolean("isFirstCome",false);
            boolean isLogin  = SPUtils.getInstance().getBoolean(Constant.IS_LOGIN,false);
            if(firstCome){
                if(isLogin){
                    KLog.e("tag"," 闪屏去主界面 ");

                    Intent intent = new Intent(this, HomeActivityV2.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",0);
                    bundle.putSerializable("jpushbean",mJPushBean);
                    intent.putExtras(bundle);

                    startActivity(intent);

                }else{
                    UIHelper.toLoginActivity(SplashActivity.this);
                }
                finish();
            }else{
                UIHelper.toWelcomeActivity(SplashActivity.this);
                finish();
            }

        },1800);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != animationDrawable){
            animationDrawable.stop();
        }
    }



    private void showSecretDialog(Context ctx) {
        final SecretAlertDialog iosAlertDialog = new SecretAlertDialog(ctx).builder();
        iosAlertDialog.setMsg(ctx.getResources().getString(R.string.secret_hint))
                .setPositiveButton("同意", v -> {
                    SPUtils.getInstance().put("isAgree", true);
                    KLog.d("tag", "同意");
                    MobclickAgentUtils.onEvent(UmengEvent.agreement_agree_2_0_0);
                    //同意  检查权限
                    if(isJumpSet || continuerequest){
                        isJumpSet = false;
                        continuerequest = true;
                        checkAppPermission();
                    }
                })
                .setNegativeButton("不同意", v -> {
                    MobclickAgentUtils.onEvent(UmengEvent.agreement_disagree_2_0_0);
                    KLog.d("tag","弹框内部做了二次弹框的操作");
                }).setCanceledOnTouchOutside(false);
        iosAlertDialog.setCancelable(false);
        iosAlertDialog.show();

    }


}
