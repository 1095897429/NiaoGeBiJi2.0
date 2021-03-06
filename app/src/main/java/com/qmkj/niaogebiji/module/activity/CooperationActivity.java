package com.qmkj.niaogebiji.module.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.Size;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.widget.PopupWindowCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.dialog.ToolDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew;
import com.qmkj.niaogebiji.module.adapter.CooperateToolAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolItemAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolRecommentItemAdapter;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.MessageAllH5Bean;
import com.qmkj.niaogebiji.module.bean.MessageCooperationBean;
import com.qmkj.niaogebiji.module.bean.MessageLinkBean;
import com.qmkj.niaogebiji.module.bean.MessageUserBean;
import com.qmkj.niaogebiji.module.bean.MessageVipBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.bean.ToollndexBean;
import com.qmkj.niaogebiji.module.widget.AndroidBug5497Workaround;
import com.qmkj.niaogebiji.module.widget.MyWebChromeClientByCamera;
import com.qmkj.niaogebiji.module.widget.MyWebChromeClientJieTu;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.qmkj.niaogebiji.module.widget.RecyclerViewNoBugLinearLayoutManager;
import com.socks.library.KLog;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
 * 创建时间 2020-02-26
 * 描述:招合作
 *
 * 分享字段：toShareCooperate
 */
public class CooperationActivity extends BaseActivity {


    @BindView(R.id.webview)
    MyWebView mMyWebView;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.ll_tool)
    LinearLayout ll_tool;

    @BindView(R.id.rl_title)
    RelativeLayout rl_title;

    @BindView(R.id.rl_all)
    RelativeLayout rl_all;



    @BindView(R.id.space_view)
    TextView space_view;


    String url;


    private MyWebChromeClientByCamera mMyWebChromeClient;


    //用于webview图片选择权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    //用于webview图片选择的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mMyWebChromeClient != null) {
            mMyWebChromeClient.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void initSetting() {
        WebSettings webSettings = mMyWebView.getSettings();
        if(null == webSettings){
            return;
        }
        //开启 js交互功能
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        //跨域
        webSettings.setAllowUniversalAccessFromFileURLs(true);

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
        mMyWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //势焦点
        mMyWebView.requestFocusFromTouch();
        //视频播放需要
        webSettings.setPluginState(WebSettings.PluginState.ON);

        //在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            mMyWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mMyWebView.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }




    @Override
    public void initFirstData() {


        AndroidBug5497Workaround.assistActivity(this);

        toollist();

        initLayout();

        url  = getIntent().getStringExtra("url");
        KLog.d("tag","加载的url " + url);
        showWaitingDialog();
//        String  url = "http://apph5.xy860.com/cooperatehome";

        if(TextUtils.isEmpty(url)){
            return;
        }

        //TODO 3.18 换下域名测试下
//        url = "http://preapph5.niaogebiji.com/cooperatehome";


        initSetting();


        //加了webview可图片上传功能
        mMyWebChromeClient = new MyWebChromeClientByCamera(this,tv_title, () -> hideWaitingDialog());
        mMyWebView.setWebChromeClient(mMyWebChromeClient);

        //https支持
        mMyWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            }
        });

        //跨域操作
        try {
            if (Build.VERSION.SDK_INT >= 16) {
                Class<?> clazz = mMyWebView.getSettings().getClass();
                Method method = clazz.getMethod(
                        "setAllowUniversalAccessFromFileURLs", boolean.class);//利用反射机制去修改设置对象
                if (method != null) {
                    method.invoke(mMyWebView.getSettings(), true);//修改设置
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        mMyWebView.loadUrl(url);



        //js交互 -- 给js调用app的方法，xnNative是协调的对象
        mMyWebView.addJavascriptInterface(new AndroidtoJs(), "ngbjNative");

        mMyWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                KLog.d("tag","---- " + url);
                Uri uri = Uri.parse(url);
                KLog.e("打印Scheme", uri.getScheme() + "==" + url);
                if (url == null) {
                    return false;
                }
                return false;
            }

        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cooperation;
    }

    @Override
    protected void initView() {

    }


    public void showToolDialog(List<ToolBean> lists) {
        final ToolDialog iosAlertDialog = new ToolDialog(this).builder();
        iosAlertDialog.setList(lists);
        iosAlertDialog.show();
    }


    @OnClick({
            R.id.iv_back, R.id.icon_cooperate_close,
            R.id.icon_tool,
            R.id.icon_cooperate_tool_delete,R.id.space_view

    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.space_view:
//                showShareCooperateDialog();
                space_view.setVisibility(View.GONE);
            case R.id.icon_cooperate_tool_delete:
                ll_tool.setVisibility(View.GONE);
                space_view.setVisibility(View.GONE);
            break;
            case R.id.icon_tool:

                MobclickAgentUtils.onEvent(UmengEvent.tools_toollist_2_2_0);


                //初始化列表布局

                ll_tool.setVisibility(View.VISIBLE);


                ObjectAnimator translationY =  ObjectAnimator.ofFloat(ll_tool,"translationY",
                        -SizeUtils.dp2px(height + 44),0 );
                AnimatorSet animatorSet = new AnimatorSet();  //组合动画
                animatorSet.playTogether(translationY); //设置动画
                animatorSet.setInterpolator(new LinearInterpolator());
                animatorSet.setDuration(150);  //设置动画时间
                animatorSet.start(); //启动
                space_view.setVisibility(View.VISIBLE);

                //动画的监听
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        KLog.d("tag","动画开始");
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        KLog.d("tag","动画结束");
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        KLog.d("tag","动画取消");
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                        KLog.d("tag","动画重复");
                    }
                });

                break;
            case R.id.icon_cooperate_close:

                MobclickAgentUtils.onEvent(UmengEvent.tools_close_2_0_0);


                finish();
                //参数一：Activity1进入动画，参数二：Activity2退出动画
                overridePendingTransition(R.anim.activity_alpha_enter, R.anim.activity_exit_bottom);
                break;
            case R.id.iv_back:
                if(mMyWebView.canGoBack()){
                    mMyWebView.goBack();// 返回前一个页面
                }else{
                    finish();
                    //参数一：Activity1进入动画，参数二：Activity2退出动画
                    overridePendingTransition(R.anim.activity_alpha_enter, R.anim.activity_exit_bottom);
                }
                break;
            default:
        }
    }




    /**- ------------------------------- 浮层  --------------------------------- */

    @BindView(R.id.rl_tools)
    RecyclerView rl_tools;

    //适配器
    private CooperateToolAdapter mCooperateToolAdapter;
    //组合集合
    private List<ToolBean> mLists = new ArrayList<>();
    //布局管理器
    GridLayoutManager mGridLayoutManager;

    private void initLayout() {
        mGridLayoutManager = new GridLayoutManager(this,4);
        //设置布局管理器
        rl_tools.setLayoutManager(mGridLayoutManager);
        //设置适配器
        mCooperateToolAdapter = new CooperateToolAdapter(mLists);
        rl_tools.setAdapter(mCooperateToolAdapter);
        ((SimpleItemAnimator)rl_tools.getItemAnimator()).setSupportsChangeAnimations(false);
        //解决数据加载不完
        rl_tools.setNestedScrollingEnabled(true);
        rl_tools.setHasFixedSize(true);

        mCooperateToolAdapter.setOnItemClickListener((adapter, view, position) -> {

            ll_tool.setVisibility(View.GONE);
            space_view.setVisibility(View.GONE);

            ToolBean temp = mLists.get(position);
            if("0".equals(temp.getType())){
                //外链
                String link = temp.getUrl();
                if(!TextUtils.isEmpty(link)){
                    UIHelper.toCooperationActivity(CooperationActivity.this,temp.getUrl());
                }
            }else if("1".equals(temp.getType())){
                //小程序
                String appid = temp.getUrl();
                if(!TextUtils.isEmpty(appid)){
                    toJumpWX(appid);
                }
            }

            if(position <= 4){
                MobclickAgentUtils.onEvent("tools_toollist_tool"+ (position  + 1) +"_2_2_0");
            }



        });

    }

    private void toJumpWX(String appId) {
        //小程序跳转
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constant.WXAPPKEY);
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = appId; // 填小程序原始id -- 后台传递的
        req.path = ""; //拉起小程序页面的可带参路径，不填默认拉起小程序首页 /pages/media是固定的
        req.miniprogramType = Integer.valueOf("0");// 可选打开 开发版，体验版 和 正式版0
        api.sendReq(req);

    }

    private void toollist() {
        Map<String,String> map = new HashMap<>();
        map.put("cate","0");//0表示全部
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().toollist(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<ToolBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<ToolBean>> response) {
                        KLog.d("tag","response " + response.getReturn_code());
                        mLists = response.getReturn_data();
                        setData();
                    }
                });
    }


    int height;
    private void setData() {
        if(!mLists.isEmpty()){
//            mLists = mLists.subList(0,9);

            mCooperateToolAdapter.setNewData(mLists);
            //动态测量下布局的高度 大于12个意味着有第四行(3行 * 高度 + 半行)
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) rl_tools.getLayoutParams();

            if(mLists.size() > 12){
                height = 3 * SizeUtils.dp2px(72 + 6) + SizeUtils.dp2px(72 + 6) / 2;
            }else{
                int per = mLists.size() / 4;
                int per11 = mLists.size() % 4;
                if(per11 > 0){
                    height = (per + 1) * SizeUtils.dp2px(72 + 6);
                }else{
                    height = per * SizeUtils.dp2px(72 + 6);
                }
            }

            lp.height = height;
            rl_tools.setLayoutParams(lp);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK ) {
            clicks(findViewById(R.id.iv_back));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

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


    @SuppressLint("JavascriptInterface")
    public class AndroidtoJs extends Object {

        //TODO 3.17 晚测试发现忘记加了
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

        //通用方法
        @JavascriptInterface
        public void sendMessage(String param) {
            KLog.d("tag","param " + param);
            if(!TextUtils.isEmpty(param)){
                try {
                    JSONObject b= new JSONObject(param);
                    String result = b.optString("type");
                    if("shareVip".equals(result)){
                        //VIP 个人分享
                    }else if("toVipMember".equals(result)){
                        //去vip界面
                        String link = StringUtil.getLink("vipmember");
                        UIHelper.toWebViewAllActivity(CooperationActivity.this,link,"vipmember");
                    }else if("toShareCooperate".equals(result)){

                        //TODO 3.4 找合作分享数据
                        MessageCooperationBean javaBean = JSON.parseObject(param, MessageCooperationBean.class);
                        MessageCooperationBean.CooperationBean bean = javaBean.getParams();

                        if(bean != null){
                            showShareCooperateDialog(bean);
                        }
                    }else if("tolink".equals(result)){
                        //工具中的跳转 2.2.0 忘记加了 跳转链接
                        MessageLinkBean javaBean = JSON.parseObject(param, MessageLinkBean.class);
                        MessageLinkBean.MessageLink bean = javaBean.getParams();
                        String link =  bean.getLink();
                        UIHelper.toNewWebView(CooperationActivity.this,link);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



        }
    }

    // 当前合作信息详情页H5，从后端获取
    private void showShareCooperateDialog(MessageCooperationBean.CooperationBean bean) {

        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(mContext).builder();
        alertDialog.setSharelinkView().setTitleGone();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            ShareBean bean1;
            switch (position) {
                case 0:
                    bean1 = new ShareBean();
                    bean1.setShareType("circle_link");
                    bean1.setLink(bean.getLink());
                    bean1.setImg(bean.getImg());
                    bean1.setTitle(bean.getTitle());
                    bean1.setContent(bean.getSubTitle());
                    StringUtil.shareWxByWeb((Activity) mContext,bean1);
                    break;
                case 1:
                    KLog.d("tag","朋友 是链接");

                    bean1 = new ShareBean();
                    bean1.setShareType("weixin_link");
                    bean1.setLink(bean.getLink());
                    bean1.setImg(bean.getImg());

                    bean1.setTitle( bean.getTitle());
                    bean1.setContent( bean.getSubTitle());

                    StringUtil.shareWxByWeb((Activity) mContext,bean1);
                    break;
                case 2:
                    String result = bean.getTitle() + "\n" + bean.getLink();
                    StringUtil.copyLink(result);
                    break;
                default:
            }
        });
        alertDialog.show();
    }


}
