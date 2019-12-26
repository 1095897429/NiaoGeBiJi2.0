package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.MessageAllH5Bean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.event.ShowRedPointEvent;
import com.qmkj.niaogebiji.module.widget.MyWebChromeClientJieTu;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-20
 * 描述:在布局中添加了webview
 *
 * 1.消息界面
 * 2.徽章界面 编辑徽章
 * 3.个人信息认证
 *
 */
public class WebViewActivityWithLayout extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.tv_canccle)
    TextView tv_cancle;


    @BindView(R.id.tv_done)
    TextView tv_done;

    @BindView(R.id.ll_webview)
    LinearLayout mLayout;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.webview)
    WebView webview;

    @BindView(R.id.tv_right)
    TextView tv_right;

    @BindView(R.id.all_part)
    RelativeLayout allpart;


    @BindView(R.id.iv_back)
    ImageView iv_back;


    private String link;

    private String fromWhere;

    private MyWebChromeClientJieTu mMyWebChromeClient;



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



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initView() {
        link = getIntent().getStringExtra("link");
        fromWhere = getIntent().getStringExtra("fromWhere");
        KLog.d("tag","link " + link);

        if(fromWhere.equals("显示一键已读消息")){
            tv_right.setVisibility(View.VISIBLE);
        }else if(fromWhere.equals("mybadge")){
            allpart.setBackgroundColor(getResources().getColor(R.color.badge_color));
            iv_back.setImageResource(R.mipmap.icon_back_white);
            tv_title.setVisibility(View.GONE);
        }else if(fromWhere.equals("webview_badges")) {
            allpart.setBackgroundColor(Color.parseColor("#3D3D3F"));
            iv_back.setVisibility(View.GONE);
            tv_title.setText("编辑展示徽章");
            tv_done.setVisibility(View.VISIBLE);
            tv_cancle.setVisibility(View.VISIBLE);
        }


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
                    //消息中心
                    if("toMessage".equals(result)){
                        MessageAllH5Bean javaBean = JSON.parseObject(param, MessageAllH5Bean.class);
                        MessageAllH5Bean.MessageH5Bean bean = javaBean.getParams();
                        String toType = bean.getType();
                       if("1".equals(toType)){
                           UIHelper.toMsgDetailActivity(WebViewActivityWithLayout.this,bean);
                        }else if("14".equals(toType) || "15".equals(toType) || "16".equals(toType) || "17".equals(toType)){
                           //文章二级评论
                           UIHelper.toNewsDetailActivity(WebViewActivityWithLayout.this,bean.getRelatedid());
                       }else if("24".equals(toType) || "25".equals(toType) || "26".equals(toType) || "27".equals(toType)){
                           //圈子二级评论
                           UIHelper.toCommentDetailActivity(WebViewActivityWithLayout.this,bean.getRelatedid());
                       }else if("21".equals(toType) || "22".equals(toType) || "23".equals(toType)){
                           //圈子详情
                           UIHelper.toCommentDetailActivity(WebViewActivityWithLayout.this,bean.getRelatedid());
                       }else if("31".equals(toType) || "32".equals(toType)){
                           //用户信息
                           UIHelper.toUserInfoActivity(WebViewActivityWithLayout.this,bean.getAuthorid());
                       }
                    }else if("toEditBadge".equals(result)){
                        // - ok
                        String link = StringUtil.getLink("editbadge");
                        UIHelper.toWebViewActivityWithOnLayout(WebViewActivityWithLayout.this,link,"webview_badges");

                    }else if("toTestList".equals(result)){
                        // - ok
                        UIHelper.toTestListActivity(WebViewActivityWithLayout.this);
                    }else if("toConfirmOk".equals(result)){
                        //职业认证成功页面 - ok
                        finish();
                    }else if("finish".equals(result)){
                        // -
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }


    }


    @OnClick({R.id.iv_back,
            R.id.tv_right,
            R.id.tv_done,
            R.id.tv_canccle
    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.tv_done:

                break;
            case R.id.tv_right:
                readInformation();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }

    // 1-单个已读，2-所有已读
    private String mesId = "";
    private String mesType = "2";
    private void readInformation() {
        Map<String,String> map = new HashMap<>();
        map.put("type",mesType);
        map.put("id",mesId);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().readInformation(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        //请求成功 -- 如何操作
                        //1-有新消息，0-无新消息
                        webview.reload();
                    }
                });
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mMyWebChromeClient != null) {
            mMyWebChromeClient.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }




}
