package com.qmkj.niaogebiji.module.widget;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.SDCardUtils;
import com.blankj.utilcode.util.Utils;
import com.qmkj.niaogebiji.common.dialog.HeadAlertDialog;
import com.socks.library.KLog;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-26
 * 描述: 测试拍照 + 相册 (在原有的基础上新增了拍照)
 */
public class MyWebChromeClientByCamera extends WebChromeClient {

    private TextView mTitle;
    private Activity mActivity;

    private ProgressBar mProgressBar;

    //新增的
    public MyWebChromeClientByCamera(@NonNull Activity mActivity, TextView tv_title, MyListener myListener) {
        this.mActivity = mActivity;
        this.mMyListener = myListener;
        mTitle = tv_title;
    }


    public MyWebChromeClientByCamera(@NonNull Activity mActivity, ProgressBar progressBar, TextView tv_title) {
        this.mActivity = mActivity;
        mProgressBar = progressBar;
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
        this.uploadMessage = uploadMsg;
        openFileChooseProcess();
    }

    // 2
    public void openFileChooser(ValueCallback<Uri> uploadMsgs) {
        this.uploadMessage = uploadMsgs;
        openFileChooseProcess();
    }

    // 3
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        this.uploadMessage = uploadMessage;
        openFileChooseProcess();
    }

    // 5.0之后就又不一样了
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView,
                                     ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        this.uploadMessageAboveL = filePathCallback;
        openFileChooseProcess();
        return true;
    }






    private void openFileChooseProcess() {
        showHeadDialog();
    }


    //5.0以下使用
    private ValueCallback<Uri> uploadMessage;
    // 5.0及以上使用
    private ValueCallback<Uri[]> uploadMessageAboveL;


    //图片
    private final static int FILE_CHOOSER_RESULT_CODE = 128;
    //拍照
    private final static int FILE_CAMERA_RESULT_CODE = 129;
    //拍照图片路径
    private String cameraFielPath;


    private void showHeadDialog(){
        HeadAlertDialog dialog = new HeadAlertDialog(mActivity).builder();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setOnDialogItemClickListener(position1 -> {
            if(0 == position1){
                if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, 1);

                    if (uploadMessageAboveL != null) {
                        uploadMessageAboveL.onReceiveValue(null);
                        uploadMessageAboveL = null;
                    }
                    if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(null);
                        uploadMessage = null;
                    }
                } else {
                    takeCamera();
                }
            }else if(1 == position1){
                if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

                    if (uploadMessageAboveL != null) {
                        uploadMessageAboveL.onReceiveValue(null);
                        uploadMessageAboveL = null;
                    }
                    if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(null);
                        uploadMessage = null;
                    }
                } else {
                    takePhoto();
                }
            }else{
                if (uploadMessageAboveL != null) {
                    uploadMessageAboveL.onReceiveValue(null);
                    uploadMessageAboveL = null;
                }
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
            }
        });
        dialog.show();


    }


    //选择图片
    private void takePhoto() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        mActivity.startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    Uri cameraUri;
    //拍照
    private void takeCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (SDCardUtils.isSDCardEnableByEnvironment()) {
            //这里可能需要检查文件夹是否存在
//            File file = new File(Environment.getExternalStorageDirectory() + "/APPNAME/");
//            if (!file.exists()) {
//              file.mkdirs();
//            }
//            cameraFielPath = Environment.getExternalStorageDirectory() +  "/" + "upload.jpg";


            cameraFielPath = mActivity.getExternalCacheDir() +  "/" + "upload.jpg";
            File outputImage = new File(cameraFielPath);

            if (Build.VERSION.SDK_INT >= 24){
                String authority = Utils.getApp().getPackageName() + ".fileprovider";
                //content://包名.provider/xml中的name/output_image.jpg
                cameraUri = FileProvider.getUriForFile(mActivity, authority,outputImage);
                Log.i("tag","cameraUri：" + cameraUri);
            }else {
                //file:///storage/sdcard/Android/data/包名/cache/output_image.jpg
                cameraUri =  Uri.fromFile(outputImage);
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            mActivity.startActivityForResult(intent, FILE_CAMERA_RESULT_CODE);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == uploadMessage && null == uploadMessageAboveL) {
            return;
        }
        //同上所说需要回调onReceiveValue方法防止下次无法响应js方法
        if (resultCode != RESULT_OK) {
            if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(null);
                uploadMessageAboveL = null;
            }
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }
            return;
        }
        Uri result = null;
        if (requestCode == FILE_CAMERA_RESULT_CODE) {
            if (null != data && null != data.getData()) {
                result = data.getData();
            }
            KLog.d("tag","cameraFielPath " + cameraFielPath);
            if (result == null && hasFile(cameraFielPath)) {
                result = Uri.fromFile(new File(cameraFielPath));
            }
            KLog.d("tag","result " + result);
            if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(new Uri[]{result});
                uploadMessageAboveL = null;
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        } else if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (data != null) {
                result = data.getData();
            }
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    /**
     * 判断文件是否存在
     */
    public static boolean hasFile(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(Intent intent) {
        Uri[] results = null;
        if (intent != null) {
            String dataString = intent.getDataString();
            ClipData clipData = intent.getClipData();
            if (clipData != null) {
                results = new Uri[clipData.getItemCount()];
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    results[i] = item.getUri();
                }
            }
            if (dataString != null){
                results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }




}
