package com.qmkj.niaogebiji.module.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
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
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述:权限管理 -- 弹框，然后弹出所有权限 --
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

    private boolean isSdcardOk;
    private boolean isPhoneok;

    private AnimationDrawable animationDrawable;


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

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }


        //TODO 2020.1.10 应用宝渠道首个界面显示隐私弹框
        boolean isAgree = SPUtils.getInstance().getBoolean("isAgree");
        if (!isAgree) {
            showSecretDialog(this);
        }


    }


    @Override
    public void initData() {
        if(isHuaWei()){
            /** SDK连接HMS -- 打开后的首个界面 */
            HMSAgent.connect(this, rst -> KLog.e("tag","HMS connect end:" + rst));
            getToken();
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
        KLog.d("tag","get token: begin");
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
            isSdcardOk = true;
            if(null != mLaunchPermissDialog){
                mLaunchPermissDialog.setSdcardOK();
            }
        }

        if(hasPermissions(this,permissions2)){
            isPhoneok = true;
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
            isSdcardOk = true;
            if(null != mLaunchPermissDialog){
                mLaunchPermissDialog.setSdcardOK();
            }
        }

        if(hasPermissions(this,permissions2)){
            isPhoneok = true;
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
                    UIHelper.toHomeActivity(SplashActivity.this,0);
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
