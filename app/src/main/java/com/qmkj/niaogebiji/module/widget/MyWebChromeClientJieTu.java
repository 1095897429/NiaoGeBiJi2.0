package com.qmkj.niaogebiji.module.widget;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.AppUtils;
import com.qmkj.niaogebiji.common.dialog.HeadAlertDialog;
import com.socks.library.KLog;

import static android.app.Activity.RESULT_OK;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-26
 * 描述:
 */
public class MyWebChromeClientJieTu extends WebChromeClient {
    //定义接受返回值 5.0 上下使用
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;

    private ProgressBar mProgressBar;
    private TextView mTitle;
    private Activity mActivity;
    //图片
    private final static int FILECHOOSER_RESULTCODE = 128;


    public MyWebChromeClientJieTu(@NonNull Activity mActivity, ProgressBar progressBar, TextView tv_title) {
        this.mActivity = mActivity;
        mProgressBar = progressBar;
        mTitle = tv_title;
    }


    //新增的
    public MyWebChromeClientJieTu(@NonNull Activity mActivity, TextView tv_title,MyListener myListener) {
        this.mActivity = mActivity;
        this.mMyListener = myListener;
        mTitle = tv_title;
    }

    public interface MyListener{
        void listener();
    }

    private MyListener mMyListener;


    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress == 100) {
            if(mProgressBar != null){
                mProgressBar.setVisibility(View.GONE);
            }

            if(null != mMyListener){
                mMyListener.listener();
            }
        } else {
            if(mProgressBar != null){
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            }
        }

        super.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        mTitle.setText(title);
    }





    // 5.0以下的话，用的是以下三个方法 1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        this.mUploadMessage = uploadMsg;
        openFileChooseProcess();
    }

    // 2
    public void openFileChooser(ValueCallback<Uri> uploadMsgs) {
        this.mUploadMessage = uploadMsgs;
        openFileChooseProcess();
    }

    // 3
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        this.mUploadMessage = mUploadMessage;
        openFileChooseProcess();
    }

    // 5.0之后就又不一样了
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView,
                                     ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        this.mUploadCallbackAboveL = filePathCallback;
        openFileChooseProcess();
        return true;
    }


    //5.0以下的回调
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) {
                return;
            }
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    //5.0以上的回调
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE
                || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == RESULT_OK) {
            if (data == null) {
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                        KLog.e("tag", "onActivityResultAboveL: " + results[i].getPath());
                    }
                }
                if (dataString != null){
                    results = new Uri[]{Uri.parse(dataString)};
                }
                KLog.e("tag", "onActivityResultAboveL: " + results.length);
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
        return;
    }



    //选择图片 -- 先检查
    private void takePhoto() {

        if(ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            mActivity.startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);

        }else{
            Toast.makeText(mActivity,"请开启存储权限",Toast.LENGTH_SHORT).show();

            if (mUploadCallbackAboveL != null) {
                mUploadCallbackAboveL.onReceiveValue(null);
                mUploadCallbackAboveL = null;
            }
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
            return;
        }

    }




    private void openFileChooseProcess() {
        takePhoto();
    }






}
