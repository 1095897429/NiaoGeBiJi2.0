package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.socks.library.KLog;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-20
 * 描述:
 */
public class WebViewActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.ll_webview)
    LinearLayout mLayout;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    MyWebView mMyWebView;
    //apk是否已经在下载了，针对链接是apk的行为
    public static HashMap<String, Boolean> isDownOk = new HashMap<>();

    //文件上传
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessage5;
    public static final int FILECHOOSER_RESULTCODE = 5173;
    public static final int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 5174;


    private String link;
    private String mTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initView() {


        link = getIntent().getStringExtra("link");
        mTitle = getIntent().getStringExtra("title");
        KLog.d("tag","link " +link);
        if(TextUtils.isEmpty(link)){
            return;
        }


        //创建WebView
        mMyWebView = new MyWebView(getApplicationContext());

        //设置apk下载
        mMyWebView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
//            if(!TextUtils.isEmpty(url)){
//                KLog.d("tag","自定义下载逻辑");
//                start_service(url);
//            }

            //利用系统的下载
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        //js交互 -- 给js调用app的方法，xnNative是协调的对象
//        mMyWebView.addJavascriptInterface(new AndroidtoJs(), "xnNative");

        mMyWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                KLog.d("tag","---- " + url);
                return false;
            }

//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                //deepLink
//                String url = request.getUrl().toString();
//
//                if(null != url && url.startsWith("weixin://")){
//                    Uri uri = Uri.parse(url);
//                    Intent intent = new Intent();
//                    intent.setData(uri);
//                    startActivity(intent);
//                    //表示webviewClient自己处理
//                    return true;
//                }
//
//                if (url.startsWith("pinduoduo://")) {
//                    if (AppUtils.isAppInstalled("com.xunmeng.pinduoduo")) {
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                        startActivity(intent);
//                        return true;
//                    } else {
//                        ToastUtils.showLong("未安装拼多多");
//                        return true;
//                    }
//                }
//
//                if (url.startsWith("weixin://wap/pay?")) {
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(url));
//                    startActivity(intent);
//                    return true;
//                }
//
//                if (url.startsWith("alipays://platformapi/startApp?")) {
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(url));
//                    startActivity(intent);
//                    return true;
//                }
//
//
//                if (url != null && url.startsWith("taobao://")) {
//                    Uri uri = Uri.parse(url);
//                    Intent intent = new Intent();
//                    intent.setData(uri);
//                    startActivity(intent);
//                    return true;
//                }
//                if (url != null && url.startsWith("openapp.jdmobile://")) {
//
//                    if (AppUtils.isAppInstalled("com.jingdong.app.mall")) {
//                        Uri uri = Uri.parse(url);
//                        Intent intent = new Intent();
//                        intent.setData(uri);
//                        startActivity(intent);
//                        return true;
//                    } else {
//                        ToastUtils.showLong("未安装京东");
//                        return true;
//                    }
//                }
//
//                //苏宁易购
//                if (url != null && url.startsWith("suning://")) {
//                    if (AppUtils.isAppInstalled("com.suning.mobile.ebuy")) {
//                        Uri uri = Uri.parse(url);
//                        Intent intent = new Intent();
//                        intent.setData(uri);
//                        startActivity(intent);
//                        return true;
//                    } else {
//                        ToastUtils.showLong("未安装苏宁易购");
//                        return true;
//                    }
//                }
//
////                view.loadUrl(url);
//                return false;
//
////                if(url.startsWith("http")){
////                    return false;
////                }
////
////                //网页在webView中打开  安卓5.0的加载方法
////                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
////                    view.loadUrl(request.toString());
////                } else {//5.0以上的加载方法
////                    view.loadUrl(request.getUrl().toString());
////                }
////
////                return super.shouldOverrideUrlLoading(view, request);
//            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // 接受所有网站的证书
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                KLog.d("tag","---- " + url);
            }
        });

        mMyWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(100 == newProgress || 1 >= newProgress){
                    mProgressBar.setVisibility(View.GONE);
                }else{
                    if (mProgressBar.getVisibility() == View.GONE) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
            }


            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if(!TextUtils.isEmpty(mTitle)){
                    tv_title.setText(mTitle);
                }else{
                    tv_title.setText(title);
                }

            }

            //图片
            //文件上传
            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                this.openFileChooser(uploadMsg, "*/*");
            }

            // For Android >= 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType) {
                this.openFileChooser(uploadMsg, acceptType, null);
            }

            // For Android >= 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            // For Lollipop 5.0+ Devices
            @Override
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                if (mUploadMessage5 != null) {
                    mUploadMessage5.onReceiveValue(null);
                    mUploadMessage5 = null;
                }
                mUploadMessage5 = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent,
                            FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
                } catch (ActivityNotFoundException e) {
                    mUploadMessage5 = null;
                    return false;
                }
                return true;
            }


        });



        mMyWebView.loadUrl(link);
        mLayout.addView(mMyWebView);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁WebView
        if(null != mMyWebView){
            mMyWebView.removeAllViews();
            mMyWebView.onDestroy();
            mMyWebView = null;
        }
    }


    @SuppressLint("JavascriptInterface")
    public class AndroidtoJs extends Object {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @JavascriptInterface
        public void getToken() {
            KLog.d("heihei", "JS调用了Android的我的方法告诉我 它需要token,重新发起初始化界面，给与token");
            // 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
            if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                String value = "传递字符串";
                String result = "javascript:" + "xyApplication.getAppToken(\"" + value + "\")";
                mMyWebView.loadUrl(result);
            } else {
                String value = "传递字符串";
                String result = "javascript:" + "xyApplication.getAppToken(\"" + value + "\")";
                mMyWebView.evaluateJavascript(result, null);
            }
        }


        @JavascriptInterface
        public void copyText(String text) {
            // 从API11开始android推荐使用android.content.ClipboardManager
            // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
            ClipboardManager cm = (ClipboardManager) WebViewActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            cm.setText(text);
            Toast.makeText(WebViewActivity.this, text + "复制成功", Toast.LENGTH_SHORT).show();
        }

    }



    @OnClick({R.id.iv_back})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }

    /** --------------------------------- 上传图片  ---------------------------------*/

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) {
                return;
            }
            Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessage5) {
                return;
            }
            mUploadMessage5.onReceiveValue(WebChromeClient.FileChooserParams
                    .parseResult(resultCode, intent));
            mUploadMessage5 = null;
        }
    }



}
