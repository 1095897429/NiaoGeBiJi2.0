package com.qmkj.niaogebiji.module.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.SPUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.LaunchPermissDialog;
import com.qmkj.niaogebiji.common.dialog.PermissForbidDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-08
 * 描述:权限管理
 */
public class SplashActivity extends BaseActivity {

    private LaunchPermissDialog mLaunchPermissDialog;
    private PermissForbidDialog mPermissForbidDialog;
    private static final int INIT_PERMISSIONS = 100;
    //第二个弹框出现，就不需要onResume时检查权限
    boolean continuerequest = true;
    //跳转到设置界面，然后返回时onResume检查权限
    boolean isJumpSet = false;

    String permissions[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isJumpSet || continuerequest){
            isJumpSet = false;
            continuerequest = true;
            checkAppPermission();
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
        mLaunchPermissDialog.setPositionButton("立即开启", v -> {

            // 声明一个集合，在后面的代码中用来存储用户拒绝授权的权
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
        if (mPermissForbidDialog != null && mPermissForbidDialog.isShowing()) {
            mPermissForbidDialog.dismiss();
        }
        mPermissForbidDialog = new PermissForbidDialog(this).builder();
        mPermissForbidDialog.setCallBack(new PermissForbidDialog.CallBack() {
            @Override
            public void forward() {

                //引导用户至设置页手动授权
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                isJumpSet = true;

                mPermissForbidDialog.dismiss();
            }


            @Override
            public void exit() {
                SplashActivity.this.finish();

            }
        });
        mPermissForbidDialog.show();
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
                    showPermissForbidDialog();
                    return;
                }

            }
        }
    }






    //检查权限
    public static boolean hasPermissions(@NonNull Context context,
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

            boolean firstCome = SPUtils.getInstance().getBoolean("isFirstCome",true);
            if(firstCome){
                UIHelper.toHomeActivity(SplashActivity.this,0);
                finish();
            }else{
                UIHelper.toLoginActivity(SplashActivity.this);
                finish();
            }

        },1200);
    }
}
