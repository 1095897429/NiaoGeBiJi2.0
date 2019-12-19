package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-20
 * 描述:
 */
public class WebViewActivityTest extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.ll_webview)
    LinearLayout mLayout;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;


    @BindView(R.id.webview)
    WebView webview;



    private String link;
    private String mTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview_1;
    }



    private void initSetting() {
        WebSettings webSettings = webview.getSettings();
        if(null == webSettings){
            return;
        }


        //开启 js交互功能
        webSettings.setJavaScriptEnabled(true);

//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webSettings.setDefaultTextEncodingName("utf-8");
//        webSettings.setAllowContentAccess(true);
//        webSettings.setAllowFileAccess(true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            MyWebView.setWebContentsDebuggingEnabled(true);
//        }
//        //不因手机修改字体变化
//        webSettings.setTextZoom(100);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//
//        //开启 database storage API 功能
//        webSettings.setDatabaseEnabled(true);
//
//        //开启DomStorage缓存
//        webSettings.setDomStorageEnabled(true);
//        //开启 H5缓存 功能
//        webSettings.setAppCacheEnabled(true);
//        webSettings.setGeolocationEnabled(true);
//        //兼容所有的手机界面,使网页始终按照webview宽度设定(如果设置为true,此项功能为失效,导致部分手机网页如淘宝显示为PC样式,但能完整显示PC网页)
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);
//
//        //加快内容加载速度
//        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
//        //阻止图片网络加载
//        webSettings.setBlockNetworkImage(false);
//        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        //势焦点
//        webview.requestFocusFromTouch();
//        //视频播放需要
//        webSettings.setPluginState(WebSettings.PluginState.ON);
//
//        //在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        if (Build.VERSION.SDK_INT >= 19) {
//            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        } else {
//            webview.setLayerType(View.LAYER_TYPE_NONE, null);
//        }
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

        initSetting();

        webview.loadUrl(link);

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
                webview.loadUrl(result);
            } else {
                String value = "传递字符串";
                String result = "javascript:" + "xyApplication.getAppToken(\"" + value + "\")";
                KLog.d("tag",result);
                webview.evaluateJavascript(result, null);
            }
        }


        @JavascriptInterface
        public void copyText(String text) {
            // 从API11开始android推荐使用android.content.ClipboardManager
            // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
            ClipboardManager cm = (ClipboardManager) WebViewActivityTest.this.getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            cm.setText(text);
            Toast.makeText(WebViewActivityTest.this, text + "复制成功", Toast.LENGTH_SHORT).show();
        }

        //跳转文章详情
        @JavascriptInterface
        public void toActicleDetail(String articleId) {
            if(!TextUtils.isEmpty(articleId)){
                try {
                    JSONObject b= new JSONObject(articleId);
                    String result = b.optString("id");
                    UIHelper.toNewsDetailActivity(WebViewActivityTest.this,result);
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
                        UIHelper.toNewsDetailActivity(WebViewActivityTest.this,article);
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
            webview.post(() -> {
                //小于4.4
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    String result = "javascript:" + "localStorage.setItem('accessToken',\"" + token + "\")";
                    KLog.d("tag",result + "");
                    if (webview != null){
                        //传递参数
                        webview.loadUrl(result);
                    }
                } else {//4.4以上 包括4.4
                    String result =  "javascript:" + "localStorage.setItem('accessToken',\"" + token + "\")";
                    KLog.d("tag",result);
                    if (webview != null){
                        webview.evaluateJavascript(result, value -> {});
                    }
                }
            });
        }
    }



}
