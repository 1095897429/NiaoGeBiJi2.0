package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.fastjson.JSON;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.MessageCooperationBean;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-24
 * 描述:
 * 1.截取了web中的弹框显示
 */
public class WebViewWithLayoutOnlyActivity extends WebViewAllActivity {


    @BindView(R.id.loading_dialog)
    LinearLayout loading_dialog;

    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottieAnimationView;

    public void showWaitingDialog() {
        loading_dialog.setVisibility(View.VISIBLE);
        lottieAnimationView.setImageAssetsFolder("images");
        lottieAnimationView.setAnimation("images/loading.json");
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();
    }

    /**
     * 隐藏等待提示框
     */
    public void hideWaitingDialog() {
        if(null != lottieAnimationView){
            loading_dialog.setVisibility(View.GONE);
            lottieAnimationView.cancelAnimation();
        }
    }



    @BindView(R.id.rl_common_title)
    RelativeLayout rl_common_title;


    @BindView(R.id.webview)
    WebView webview;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.iv_back)
    ImageView iv_back;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview_only_layout;
    }


    private String link;

    @Override
    public void initFirstData() {

        showWaitingDialog();

        rl_common_title.setVisibility(View.VISIBLE);

        iv_back.setOnClickListener(v -> {
            if ( webview.canGoBack()) {
                webview.goBack();// 返回前一个页面
            }else{
                finish();
            }
        });


        link = getIntent().getStringExtra("link");

        if(TextUtils.isEmpty(link)){
            return;
        }

        initSetting();

        webview.setWebChromeClient(new WebChromeClient(){

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {

                KLog.e("aaa", "onJsConfirm" + "," + "url: " + url);
                AlertDialog.Builder builder = new AlertDialog.Builder(WebViewWithLayoutOnlyActivity.this);
                builder.setMessage(message)
                        .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("确定", (dialog, which) -> {
                            dialog.dismiss();
                            new Handler().postDelayed(() -> finish(),1000);
                        }).show();
                result.cancel();
                return true;
            }
        });

        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideWaitingDialog();
            }
        });

        webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                tv_title.setText(title);
            }
        });

        webview.loadUrl(link);

        //js交互 -- 给js调用app的方法，xnNative是协调的对象
        webview.addJavascriptInterface(new AndroidtoJs(), "ngbjNative");

    }


    @SuppressLint("JavascriptInterface")
    public class AndroidtoJs extends Object {
        //通用方法
        @JavascriptInterface
        public void sendMessage(String param) {
            KLog.d("tag","param " + param);
            if(!TextUtils.isEmpty(param)){
                try {
                    JSONObject b= new JSONObject(param);
                    String result = b.optString("type");
                    if("toSubmitInfo".equals(result)){
                        //去编辑界面
                        UIHelper.toUserInfoModifyActivity(WebViewWithLayoutOnlyActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    @Override
    protected void initView() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁WebView
        if(null != webview){
            webview.removeAllViews();
            webview = null;
        }
    }



    private void initSetting() {
        WebSettings webSettings = webview.getSettings();
        if(null == webSettings){
            return;
        }
        //开启 js交互功能
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MyWebView.setWebContentsDebuggingEnabled(true);
        }
        //不因手机修改字体变化
        webSettings.setTextZoom(100);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);

        //开启DomStorage缓存
        webSettings.setDomStorageEnabled(true);
        //开启 H5缓存 功能
        webSettings.setAppCacheEnabled(true);
        webSettings.setGeolocationEnabled(true);
        //兼容所有的手机界面,使网页始终按照webview宽度设定(如果设置为true,此项功能为失效,导致部分手机网页如淘宝显示为PC样式,但能完整显示PC网页)
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        //加快内容加载速度
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //阻止图片网络加载
        webSettings.setBlockNetworkImage(false);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //势焦点
        webview.requestFocusFromTouch();
        //视频播放需要
        webSettings.setPluginState(WebSettings.PluginState.ON);

        //在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webview.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
