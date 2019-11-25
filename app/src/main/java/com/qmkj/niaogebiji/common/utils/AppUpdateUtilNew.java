package com.qmkj.niaogebiji.common.utils;

import android.content.Context;
import android.widget.ProgressBar;

import com.qmkj.niaogebiji.common.dialog.UpdateAlertDialog;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-09-10
 */
public class AppUpdateUtilNew {

    private Context mContext;
    //下载控件
    private ProgressBar mProgress;
    //下载链接
    private String appUrl;
    //展示布局Dialog
    private UpdateAlertDialog downloadDialog;

    public AppUpdateUtilNew(Context context, String appUrl) {
        mContext = context;
        this.appUrl = appUrl;
    }


    //显示进度布局
    public void showUpdateDialog(String appNote,boolean cancle) {
        downloadDialog = new UpdateAlertDialog(mContext).builder();
        downloadDialog.setTitle("有版本可以更新");
        downloadDialog.setMsg(appNote);
        downloadDialog.setAppUrl(appUrl);
        downloadDialog.setForce(cancle);
        if(cancle){
            downloadDialog.setCanceledOnTouchOutside(false);
            downloadDialog.setCancelable(false);
        }

        downloadDialog.show();
    }



}
