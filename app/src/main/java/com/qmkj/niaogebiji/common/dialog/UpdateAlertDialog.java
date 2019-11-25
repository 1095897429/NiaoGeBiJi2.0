package com.qmkj.niaogebiji.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.qmkj.niaogebiji.R;
import com.socks.library.KLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-22
 * 描述:更新弹出框
 */
public class UpdateAlertDialog {

    private Context mContext;
    //
    private Display mDisplay;
    //组合Dialog
    private Dialog mDialog;
    //标题
    private TextView txt_title;
    //内容
    private TextView txt_msg;
    //是否显示标题
    private boolean showTitle = false;
    //是否显示内容
    private boolean showMsg = false;
    //是否显示右按钮
    private boolean showPosBtn = false;
    //是否显示左按钮
    private boolean showNegBtn = false;
    //加载进度
    private RelativeLayout part2222;
    //不强制更新
    private LinearLayout part1111;
    private TextView btn_left;
    private TextView btn_right;
    private TextView progressBar_text;
    //强制更新
    private TextView btn_force;

    private boolean isForce;


    public UpdateAlertDialog(Context context){
        this.mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();
    }

    //内部添加样式
    public UpdateAlertDialog builder(){
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_update, null);
        // 获取自定义Dialog布局中的控件
        txt_title =  view.findViewById(R.id.title);
        txt_msg =  view.findViewById(R.id.txt_msg);
        txt_msg.setVisibility(View.GONE);
        btn_force =  view.findViewById(R.id.btn_force);
        btn_force.setVisibility(View.GONE);
        part1111 = view.findViewById(R.id.part1111);
        btn_left = view.findViewById(R.id.btn_left);
        btn_right = view.findViewById(R.id.btn_right);
        part2222 = view.findViewById(R.id.part2222);
        mProgress = view.findViewById(R.id.progressBar);
        progressBar_text  = view.findViewById(R.id.progressBar_text);

        // 定义Dialog布局和参数
        mDialog = new Dialog(mContext,R.style.MyDialog);
        mDialog.setContentView(view);

        // 调整dialog背景大小
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (mDisplay.getWidth() * 0.8);

        return this;
    }


    //设置标题
    public UpdateAlertDialog setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            txt_title.setText("标题");
        } else {
            txt_title.setText(title);
        }
        return this;
    }

    //设置内容
    public UpdateAlertDialog setMsg(String msg) {
        showMsg = true;
        if ("".equals(msg)) {
            txt_msg.setText("内容");
        } else {
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");
            txt_msg.setTypeface(typeface);
            txt_msg.setText(msg);
        }
        return this;
    }

    //false 点击物理返回键不消失
    public UpdateAlertDialog setCancelable(boolean cancel) {
        mDialog.setCancelable(cancel);
        return this;
    }

    //false 点击外部不消失
    public UpdateAlertDialog setCanceledOnTouchOutside(boolean b) {
        mDialog.setCanceledOnTouchOutside(b);
        return this;
    }




    //设置填充内容
    private void setLayout() {
        if (!showTitle && !showMsg) {
            txt_title.setText("下载链接");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }


        if (!showPosBtn && showNegBtn) {
            btn_force.setVisibility(View.VISIBLE);
        }

        setEvent();
    }



    private void setEvent() {

        btn_left.setOnClickListener(view -> dismiss());
        btn_right.setOnClickListener(view -> {
            part2222.setVisibility(View.VISIBLE);
            part1111.setVisibility(View.GONE);
            btn_force.setVisibility(View.GONE);
            //下载apk安装包
            downloadApk();
        });
        btn_force.setOnClickListener(view -> {

            part2222.setVisibility(View.VISIBLE);
            btn_force.setVisibility(View.GONE);
            downloadApk();

            //判断是否已存在xx.apk -- 这个如果有问题，就会一直报错误
//                String apkName = appUrl.substring(appUrl.lastIndexOf("/") + 1);
//                File apkFile = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),apkName);
//                if(!apkFile.exists()){
//                    part2222.setVisibility(View.VISIBLE);
//                    btn_force.setVisibility(View.GONE);
//                    //下载apk安装包
//                    downloadApk();
//                }else{
//                    //存存在直接安装
//                    AppUtils.installApp(apkFile);
//                }


        });
    }


    public void show() {
        setLayout();
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }


    //设置事件

    public OnButtonClickLisenter mOnButtonClickLisenter;

    public void setOnButtonClickLisenter(OnButtonClickLisenter onButtonClickLisenter) {
        mOnButtonClickLisenter = onButtonClickLisenter;
    }

    public interface OnButtonClickLisenter{
        void fun(int position);
    }



    //下载链接
    private String appUrl;
    //下载文件的长度
    private int contentLength;
    //下载控件
    private ProgressBar mProgress;

    //强更设置
    public void setForce(boolean force) {
        isForce = force;
        if(isForce){
            part1111.setVisibility(View.GONE);
            part2222.setVisibility(View.GONE);
            btn_force.setVisibility(View.VISIBLE);
        }else{
            part1111.setVisibility(View.VISIBLE);
            part2222.setVisibility(View.GONE);
            btn_force.setVisibility(View.GONE);
        }
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    private void downloadApk() {
        //利用AsyncTask单线程队列下载
        new ApkDownAsyncTask().execute(appUrl);
    }

    class  ApkDownAsyncTask extends AsyncTask<String, Integer, String> {

        //apk的名字
        private String apkName;
        //存储apk的文件
        private File apkFile;
        //当前进度
        private int progress;
        //之前的进度
        private int oldProgress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            KLog.d("开始下载");
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(appUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //设置超时时间
                connection.setConnectTimeout(10 * 1000);
                connection.setReadTimeout(10 * 10000);
                //GET请求
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                //设置其他属性
                connection.setRequestProperty("Connection","Keep-Alive");
                connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                connection.setRequestProperty("Charset","UTF-8");
                //打开连接
                connection.connect();
                //获取文件长度
                contentLength = connection.getContentLength();
                //apk名字
                apkName = appUrl.substring(appUrl.lastIndexOf("/") + 1);
                //下载地址 /mnt/sdcard/Android/data/包名/files/Download/ -- 测试可用此路径
                //下载地址 /mnt/sdcard/Android/data/包名/files/xxx.apk
                apkFile = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),apkName);
                if(!apkFile.exists()){
                    apkFile.createNewFile();
                }else{
                    apkFile.delete();
                }
                //将字节流转缓冲流 缓存流 效率高
                BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(apkFile));

                int len;
                int count = 0;
                byte [] bytes = new byte[1024];
                while ((len = bis.read(bytes)) != -1){
                    bos.write(bytes,0,len);
                    count += len;
                    progress = (int) (count * 100L / contentLength);
                    //如果进度与之前进度相等，这不更新，如果更新太频繁，造成界面卡顿
                    if(oldProgress != progress){
                        publishProgress(progress);
                    }
                    oldProgress = progress;
                }

                bis.close();
                bos.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "写入完成";
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int position = values[0];
            if (null != mProgress){
                mProgress.setProgress(position);
                progressBar_text.setText("下载中 " + position + "%");
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            KLog.d("下载结束,安装app");

            part2222.setVisibility(View.GONE);
            btn_force.setVisibility(View.VISIBLE);

            AppUtils.installApp(apkFile);
        }
    }



}
