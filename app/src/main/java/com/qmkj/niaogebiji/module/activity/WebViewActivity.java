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

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-20
 * 描述:
 * 1.android 调用 js  参数 --  编辑徽章完成调用方法名 finish
 */
public class WebViewActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.ll_webview)
    LinearLayout mLayout;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    MyWebView mMyWebView;


    private String link;
    private String mTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

        //js交互 -- 给js调用app的方法，xnNative是协调的对象
        mMyWebView.addJavascriptInterface(new AndroidtoJs(), "ngbjNative");

        mMyWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                KLog.d("tag","---- " + url);
                return false;
            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // 接受所有网站的证书
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                KLog.d("tag","---- " + url);
//                toGiveToken();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String title = view.getTitle();
                KLog.d("tag","title: " + title);
                //js交互
//                toGiveToken();
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
                KLog.d("tag",result);
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

        //跳转文章详情
        @JavascriptInterface
        public void toActicleDetail(String articleId) {
            if(!TextUtils.isEmpty(articleId)){
                try {
                    JSONObject b= new JSONObject(articleId);
                    String result = b.optString("id");
                    UIHelper.toNewsDetailActivity(WebViewActivity.this,result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            KLog.d("tag","articleId " + articleId);
        }


        //通用方法
        @JavascriptInterface
        public void sendMessage(String param) {
            KLog.d("tag","param " + param);
            if(!TextUtils.isEmpty(param)){
                try {
                    JSONObject b= new JSONObject(param);
                    String result = b.optString("type");
                    //去文章详情
                    if("toArticleDetail".equals(result)){
                        JSONObject object  = b.getJSONObject("params");
                        String article = object.optString("id");
                        UIHelper.toNewsDetailActivity(WebViewActivity.this,article);
                    }else if("toHome".equals(result)){
                        //去文章首页
//                        UIHelper.toHomeActivity(WebViewActivity.this,0);

                    }else if("toKnow".equals(result)){
                        //去更懂你
//                        UIHelper.toHomeActivity(WebViewActivity.this,0);
                    }else if("toConfirmOk".equals(result)){
                        // id 1 职业认证成功  id 2  审核认证成功
//                        UIHelper.toHomeActivity(WebViewActivity.this,0);
                    }else if("toTestList".equals(result)){
                        //徽章 尚未获得，立即前往获取
//                        UIHelper.toHomeActivity(WebViewActivity.this,0);
                    }else if("toEditBadge".equals(result)){
                        //去编辑徽章页面
//                        UIHelper.toHomeActivity(WebViewActivity.this,0);
                    }else if("toUserDetail".equals(result)){
                        //关注列表去跳转
//                        UIHelper.toHomeActivity(WebViewActivity.this,0);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

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


    private String token;
    public void toGiveToken() {

        RegisterLoginBean.UserInfo userInfo = StringUtil.getUserInfoBean();
        if(null != userInfo){
            token = userInfo.getAccess_token();
        }

        if (!TextUtils.isEmpty(token)) {
            mMyWebView.post(() -> {
                //小于4.4
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    String result = "javascript:" + "localStorage.setItem('accessToken',\"" + token + "\")";
                    KLog.d("tag",result + "");
                    if (mMyWebView != null){
                        //传递参数
                        mMyWebView.loadUrl(result);
                    }
                } else {//4.4以上 包括4.4
                    String result =  "javascript:" + "localStorage.setItem('accessToken',\"" + token + "\")";
                    KLog.d("tag",result);
                    if (mMyWebView != null){
                        mMyWebView.evaluateJavascript(result, value -> {});
                    }
                }
            });
        }
    }



}
