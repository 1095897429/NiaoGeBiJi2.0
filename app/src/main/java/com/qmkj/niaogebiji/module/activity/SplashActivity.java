package com.qmkj.niaogebiji.module.activity;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
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
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.GetPhoneInfoListener;
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
import com.socks.library.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

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
        KLog.d("tag","应用的Id  " + AppUtils.getAppUid());
        KLog.d("tag","应用的签名MD5  " + AppUtils.getAppSignatureMD5());
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



        String s = "ABC";
        KLog.d("tag","截取的字符串是【利用fastSubstring】 " +  s.substring(1));

        LogUtils.json("tag","LogUtils的json " + " 我是LogUtils的json");
        LogUtils.eTag("tag","LogUtils的json " + " 我是LogUtils的json");


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
