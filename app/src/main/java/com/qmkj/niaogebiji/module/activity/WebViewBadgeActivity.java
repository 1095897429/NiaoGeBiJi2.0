package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.dialog.TalkCircleAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CommentSecondAdapter;
import com.qmkj.niaogebiji.module.bean.ActicleCommentHeadBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentCircleBean;
import com.qmkj.niaogebiji.module.bean.CommentOkBean;
import com.qmkj.niaogebiji.module.bean.MessageAllH5Bean;
import com.qmkj.niaogebiji.module.bean.MessageUserBean;
import com.qmkj.niaogebiji.module.bean.MessageVipBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.ProBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.bean.VipBean;
import com.qmkj.niaogebiji.module.event.EditChangeEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.MyWebChromeClientJieTu;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-30
 * 描述:
 */
public class WebViewBadgeActivity  extends BaseActivity {


    @Override
    protected boolean regEvent() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEditChangeEvent(EditChangeEvent event){
        KLog.d("tag","onEditChangeEvent");
        webview.reload();
    }


    @Override
    protected void onResume() {
        KLog.d("tag","WebViewBadgeActivity onResume");
        webview.reload();
        super.onResume();

    }

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.ll_webview)
    LinearLayout mLayout;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.webview)
    WebView webview;


    @BindView(R.id.iv_back)
    ImageView iv_back;


    @BindView(R.id.all_part)
    RelativeLayout allpart;


    private String link;

    private String fromWhere;

    private MyWebChromeClientJieTu mMyWebChromeClient;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview_1;
    }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initView() {
        link = getIntent().getStringExtra("link");
        fromWhere = getIntent().getStringExtra("fromWhere");
        KLog.d("tag","link " + link);

        allpart.setBackgroundColor(Color.parseColor("#2C2C2E"));
        tv_title.setText("我的徽章");
        tv_title.setTextColor(getResources().getColor(R.color.white));
        iv_back.setVisibility(View.VISIBLE);
        iv_back.setColorFilter(Color.WHITE);


        if(TextUtils.isEmpty(link)){
            return;
        }

        initSetting();


        mMyWebChromeClient = new MyWebChromeClientJieTu(this,mProgressBar,tv_title);


        //js交互 -- 给js调用app的方法，xnNative是协调的对象
        webview.addJavascriptInterface(new AndroidtoJs(), "ngbjNative");

        webview.setWebViewClient(new WebViewClient(){
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
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        webview.setWebChromeClient(mMyWebChromeClient);
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





    @OnClick({R.id.iv_back,
    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mMyWebChromeClient != null) {
            mMyWebChromeClient.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);

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



    //3个操作
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
                    if("toEditBadge".equals(result)){
                        // - ok
                        String link = StringUtil.getLink("editbadge");
                        UIHelper.toWebViewAllActivity(WebViewBadgeActivity.this,link,"editbadge");
                    }else if("toVipMember".equals(result)){
                        String link = StringUtil.getLink("vipmember");
                        UIHelper.toWebViewAllActivity(WebViewBadgeActivity.this,StringUtil.getLink("vipmember"),"vipmember");

//                        UIHelper.toWebViewActivityWithOnLayout(WebViewBadgeActivity.this,link,"vipmember");
                    }else if("toTestList".equals(result)){
                        // - ok
                        Constant.isReLoad = true;
                        UIHelper.toTestListActivity(WebViewBadgeActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }




}
