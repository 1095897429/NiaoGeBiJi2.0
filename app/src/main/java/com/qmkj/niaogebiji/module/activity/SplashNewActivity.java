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
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.SPUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.LaunchPermissDialog;
import com.qmkj.niaogebiji.common.dialog.PermissForbidPhoneDialog;
import com.qmkj.niaogebiji.common.dialog.PermissForbidStorageDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.socks.library.KLog;

import butterknife.BindView;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述:权限管理 -- 弹框，然后验证权限 -- 再验证下个权限 -- 采用！！
 */
public class SplashNewActivity extends BaseActivity {

    @BindView(R.id.image)
    ImageView animationIV;


    private LaunchPermissDialog mLaunchPermissDialog;
    private PermissForbidStorageDialog mPermissForbidStorageDialog;
    private PermissForbidPhoneDialog mPermissForbidPhoneDialog;
    private static final int INIT_PERMISSIONS = 100;
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
        checkAppPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isJumpSet){
            //🍅 点击到设置权限主界面需检查权限
            isJumpSet = false;
            checkAppPermission();
        }
    }

    private void checkAppPermission() {
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

        mLaunchPermissDialog = new LaunchPermissDialog(this,isSdcardOk,isPhoneok).builder();
        mLaunchPermissDialog.setPositionButton("立即开启", v -> {

            //先检查手机存储权限
            if(!isSdcardOk){
                ActivityCompat.requestPermissions(SplashNewActivity.this, permissions1, INIT_PERMISSIONS);
                return;
            }

            //再检查手机状态权限
            if(!isPhoneok){
                ActivityCompat.requestPermissions(SplashNewActivity.this, permissions2, INIT_PERMISSIONS);
                return;
            }

        }).setCanceledOnTouchOutside(false)
          .setCancelable(false);
        mLaunchPermissDialog.show();
    }



    private void showPermissForbidStorageDialog() {
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
                SplashNewActivity.this.finish();

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
                SplashNewActivity.this.finish();

            }
        });
        mPermissForbidPhoneDialog.show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        KLog.d("tag","权限是： " + permissions[0]);
        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
            //用户勾选了不再提示，函数返回false
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                //解释原因，并且引导用户至设置页手动授权
                if(permissions[0].equals("android.permission.WRITE_EXTERNAL_STORAGE")){
                    showPermissForbidStorageDialog();
                }else if(permissions[0].equals("android.permission.READ_PHONE_STATE")){
                    showPermissForbidPhoneDialog();
                }
                return;
            }
        }else{
            if(permissions[0].equals("android.permission.WRITE_EXTERNAL_STORAGE")){
                isSdcardOk = true;
                KLog.d("tag","存储卡已同意");
                mLaunchPermissDialog.setSdcardOK();
            }else if(permissions[0].equals("android.permission.READ_PHONE_STATE")){
                isPhoneok = true;
                mLaunchPermissDialog.setPhoneOK();
                KLog.d("tag","手机状态已同意");
            }

            //权限都有了
            if(isSdcardOk && isPhoneok){
                toNext();
            }
        }
    }





    //动画完成时间是1400
    public void toNext(){
        new Handler().postDelayed(() -> {
            boolean firstCome = SPUtils.getInstance().getBoolean("isFirstCome",false);
            boolean isLogin  = SPUtils.getInstance().getBoolean(Constant.IS_LOGIN,false);
            if(firstCome){
                if(isLogin){
                    UIHelper.toHomeActivity(SplashNewActivity.this,0);
                }else{
                    UIHelper.toLoginActivity(SplashNewActivity.this);
                }
                finish();
            }else{
                UIHelper.toWelcomeActivity(SplashNewActivity.this);
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
}
