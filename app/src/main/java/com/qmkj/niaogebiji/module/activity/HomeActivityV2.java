package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.push.JPushReceiver;
import com.qmkj.niaogebiji.common.service.MediaService;
import com.qmkj.niaogebiji.common.utils.AppUpdateUtilNew;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.JPushBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.VersionBean;
import com.qmkj.niaogebiji.module.event.LoginGoodEvent;
import com.qmkj.niaogebiji.module.event.toActicleEvent;
import com.qmkj.niaogebiji.module.event.toRefreshEvent;
import com.qmkj.niaogebiji.module.fragment.CircleFragment;
import com.qmkj.niaogebiji.module.fragment.FirstFragment;
import com.qmkj.niaogebiji.module.fragment.FlashFragmentV2;
import com.qmkj.niaogebiji.module.fragment.MyFragment;
import com.qmkj.niaogebiji.module.fragment.SchoolFragment;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class HomeActivityV2 extends BaseActivity {

    public static final int FLASH = 1;
    public static final int H5_TO_ACTICLE = 5;
    public static final int JPUSH_TO_FLASH = 10;
    public static final int JPUSH_TO_My = 11;

    @BindView(R.id.index_first_icon)
    ImageView index_first_icon;
    @BindView(R.id.index_first_text)
    TextView index_first_text;

    @BindView(R.id.index_school_icon)
    ImageView index_school_icon;
    @BindView(R.id.index_school_text)
    TextView index_school_text;

    @BindView(R.id.index_flash_icon)
    ImageView index_flash_icon;
    @BindView(R.id.index_flash_text)
    TextView index_flash_text;

    @BindView(R.id.index_activity_icon)
    ImageView index_activity_icon;
    @BindView(R.id.index_activity_text)
    TextView index_activity_text;

    @BindView(R.id.index_my_icon)
    ImageView index_my_icon;
    @BindView(R.id.index_my_text)
    TextView index_my_text;

    @BindView(R.id.index_tool)
    LinearLayout index_tool;

    @BindView(R.id.index_first)
    LinearLayout index_first;

    @BindView(R.id.index_circle)
    LinearLayout index_circle;

    @BindView(R.id.index_my)
    LinearLayout index_my;

    @BindView(R.id.oldguide)
    LinearLayout oldguide;


    @BindView(R.id.guide_img)
    ImageView guide_img;

    @BindView(R.id.guide_text)
    TextView guide_text;


    @BindView(R.id.skip)
    TextView skip;

    @BindView(R.id.guide_num)
    TextView guide_num;

    @BindView(R.id.red_point)
    FrameLayout red_point;


    String [] string = new String[]{"这里是学院。我们根据知识脉络精心梳理了营销知识树WIKI，欢迎大家查阅\n" +
            "此外，专业认证测试与课程帮助你更好的提生自己~",
    "这里是工具箱。我们为你推荐了常用的营销人工作必备工具",
    "这里是圈子。行业大V都在用的营销人朋友圈，快看看大家都在说什么吧\n" +
            "好了，介绍完成了，尽情体验全新鸟哥笔记APP吧~"};

    LinearLayout.LayoutParams lp;

    //点击引导图的次数
    int count = 1;

    FirstFragment mFirstFragment;
    FlashFragmentV2 mFlashFragment;
    SchoolFragment mSchoolFragment;
    CircleFragment mCircleFragment;
    MyFragment mMyFragment;
    Fragment mCurrentFragment;
    //每个cell的大小
    int perWidth;


    MyWebView mMyWebView;

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }


    @Override
    public void initFirstData() {
        //伪代码
        //        请求数据{
        //            1.有显示 index_dynamic.setVisiable
        //            2.不显示
        //        }

        initFragment();

    }




    private JPushBean mJPushBean;


    //下面是app从关闭杀死到启动首页 - 走这里）
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        KLog.e("tag","HomeActivity  onCreate");
        super.onCreate(savedInstanceState);
        if(null != getIntent().getExtras()){
            mJPushBean = (JPushBean) getIntent().getExtras().getSerializable("jpushbean");
        }

        initService();
    }


    //“绑定”服务的intent
    Intent MediaServiceIntent;
    public static MediaService.MyBinder mMyBinder;

    public static MediaService mMediaService;

    private void initService() {
        MediaServiceIntent = new Intent(this, MediaService.class);
        //绑定播放音乐的服务
        bindService(MediaServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }


    //TODO 2019.12.17 接入极光 ，华为sdk 时 就会报service连接不上
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMyBinder = (MediaService.MyBinder) service;
            Log.d("tag", "Service与Activity已连接");
            mMediaService = ((MediaService.MyBinder) service).getInstance();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private void toDiffer(JPushBean javaBean) {
        String jump_type = javaBean.getJump_type();
        String jump_info = javaBean.getJump_info();
        try{
            if("1".equals(jump_type)){
                bottomClick(findViewById(R.id.index_my));
            }else if("20".equals(jump_type)){
                KLog.d("tag","文章");
                UIHelper.toNewsDetailActivity(this,jump_info);
            }else if("31".equals(jump_type)){
                bottomClick(findViewById(R.id.index_flash));
            }else if("50".equals(jump_type)){
                UIHelper.toCommentDetailActivity(this,jump_info);
            }else if("60".equals(jump_type)){
                //wiki
                String link = StringUtil.getLink("wikidetail/" + jump_info);
                UIHelper.toWebViewActivityWithOnStep(this,link);
            }else if("70".equals(jump_type)){
                String link = jump_info;
                UIHelper.toWebViewActivityWithOnLayout(this,link,"");
            }else{

            }

        }catch (Throwable throwable){

        }
    }




    private String token;
    public void toGiveToken() {

        RegisterLoginBean.UserInfo userInfo = StringUtil.getUserInfoBean();
        if(null != userInfo){
            token = userInfo.getAccess_token();
        }

        if (!TextUtils.isEmpty(token)) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                String result = "javascript:" +"localStorage.setItem('accessToken',\"" + token + "\")";
                String re11 = "android";
                String sss = "javascript:" +"localStorage.setItem('client',\"" + re11 + "\")";;
                KLog.d("tag",result);
                KLog.d("tag",result + "");
                if (mMyWebView != null){
                    //传递参数
                    mMyWebView.loadUrl(result);
                    mMyWebView.loadUrl(sss);
                }
            } else {//4.4以上 包括4.4
                String result = "javascript:" +"localStorage.setItem('accessToken',\"" + token + "\")";
                String re11 = "android";
                String sss = "javascript:" +"localStorage.setItem('client',\"" + re11 + "\")";;
//                KLog.d("tag",result);
                if (mMyWebView != null){
                    mMyWebView.evaluateJavascript(result, value -> {});
                    mMyWebView.evaluateJavascript(sss, value -> {});
                }
            }
        }
    }



    @Override
    protected void initView() {

        mMyWebView = new MyWebView(getApplicationContext());
        mMyWebView.loadUrl(StringUtil.getLink("myactivity"));
        toGiveToken();
        mMyWebView.setWebViewClient(new WebViewClient() {
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
                toGiveToken();
            }
        });

        int screenWidth = ScreenUtils.getScreenWidth();
        perWidth = screenWidth / 5;
        lp = (LinearLayout.LayoutParams) guide_img.getLayoutParams();
        lp.width = perWidth;
        guide_img.setLayoutParams(lp);


        boolean firstGuide = SPUtils.getInstance().getBoolean("isFirstHomeGuide", false);
        if (false) {
            oldguide.setVisibility(View.VISIBLE);
            guideUser();
        }

    }



    @Override
    public void initData() {

//        registerMessageReceiver();  // used for receive msg

        checkupd();

//        if(JPUSH_TO_FLASH == fromType){
//            bottomClick(findViewById(R.id.index_flash));
//        }else if(JPUSH_TO_My == fromType){
//            bottomClick(findViewById(R.id.index_my));
//        }


        if(mJPushBean !=  null){
            KLog.e("tag","mJPushBean 类型是 " + mJPushBean.getJump_type()
                    + "  参数 " + mJPushBean.getJump_info());
            toDiffer(mJPushBean);
        }
    }

    /** 默认的Fragment */
    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(null == mCurrentFragment){
            mFirstFragment = FirstFragment.getInstance();
        }
        mCurrentFragment = mFirstFragment;
        transaction.add(R.id.fragmentLayout,mCurrentFragment);
        transaction.commit();
    }



    @SuppressLint("CheckResult")
    private void guideUser() {
        RxView.clicks(oldguide)
                //每1秒中只处理第一个元素
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(object -> {
                    ++count;
                    KLog.d("tag","count = " + count);
                    if(count == 2){
                        guide_num.setText(count + "/4");
                        guide_text.setText(string[0]);
                        lp.setMargins(perWidth,0,0,0);
                        guide_img.setLayoutParams(lp);
                        guide_img.setImageResource(R.mipmap.bg_oldguide_2);
                    }else if(count == 3){
                        guide_num.setText(count + "/4");
                        guide_text.setText(string[1]);
                        lp.setMargins(perWidth * 2,0,0,0);
                        guide_img.setLayoutParams(lp);
                        guide_img.setImageResource(R.mipmap.bg_oldguide_3);
                    }else if(count == 4){
                        //这里对应最后一页
                        skip.setText("立即体验");
                        skip.setTextColor(getResources().getColor(R.color.text_first_color));
                        skip.setBackgroundResource(R.drawable.bg_corners_22_yellow);
                        guide_text.setText(string[2]);
                        guide_num.setVisibility(View.GONE);
                        lp.setMargins(perWidth * 3,0,0,0);
                        guide_img.setLayoutParams(lp);
                        guide_img.setImageResource(R.mipmap.bg_oldguide_4);
                    }else{
                        oldguide.setVisibility(View.GONE);
                        SPUtils.getInstance().put("isFirstHomeGuide",true);
                    }
                });

        RxView.clicks(skip)
                //每1秒中只处理第一个元素
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(object -> {
                    SPUtils.getInstance().put("isFirstHomeGuide",true);
                    oldguide.setVisibility(View.GONE);
                });

    }


    @OnClick({R.id.index_first,//干货
            R.id.index_flash,//动态获取
            R.id.index_tool,//学院
            R.id.index_circle,//圈子
            R.id.index_my

    })
    public void bottomClick(View layout){
        switch (layout.getId()){

            case R.id.index_first:
                MobclickAgentUtils.onEvent(UmengEvent.index_tab_2_0_0);
                hideImageStatus();
                if(null == mFirstFragment){
                    mFirstFragment = FirstFragment.getInstance();
                }
                index_first_icon.setImageResource(R.mipmap.icon_index_001);
                index_first_text.setTextColor(Color.parseColor("#333333"));
                switchFragment(mFirstFragment);


                //TODO 11.14 在搜索栏不见的情况下，点击可刷新界面
                LinearLayout part =  mFirstFragment.getView().findViewById(R.id.search_part);
                Rect localRect = new Rect();
                boolean isVisible = part.getLocalVisibleRect(localRect);
                KLog.d("tag","搜索栏的状态 " + isVisible );
                if(!isVisible){
                    EventBus.getDefault().post(new toRefreshEvent("去刷新呈现的界面"));
                }

                break;
            case R.id.index_flash:
//                MobclickAgentUtils.onEvent(UmengEvent.news_tab_2_0_0);
//                hideImageStatus();
//
//                if(null == mFlashFragment){
//                    mFlashFragment = FlashFragmentV2.getInstance();
//                }
//                index_flash_icon.setImageResource(R.mipmap.icon_index_01);
//                index_flash_text.setTextColor(Color.parseColor("#333333"));
//                switchFragment(mFlashFragment);

                //底部弹出Activity

                UIHelper.toCooperationActivity(this,"");


                break;
            case R.id.index_tool:
                MobclickAgentUtils.onEvent(UmengEvent.academy_tab_2_0_0);
                hideImageStatus();
                if(null == mSchoolFragment){
                    mSchoolFragment = SchoolFragment.getInstance();
                }
                index_school_icon.setImageResource(R.mipmap.icon_index_11);
                index_school_text.setTextColor(Color.parseColor("#333333"));
                switchFragment(mSchoolFragment);
            break;
            case R.id.index_circle:
                MobclickAgentUtils.onEvent(UmengEvent.quanzi_tab_2_0_0);
                hideImageStatus();
                if(null == mCircleFragment){
                    mCircleFragment = CircleFragment.getInstance();
                }
                index_activity_icon.setImageResource(R.mipmap.icon_index_31);
                index_activity_text.setTextColor(Color.parseColor("#333333"));
                switchFragment(mCircleFragment);
                break;
            case R.id.index_my:
                MobclickAgentUtils.onEvent(UmengEvent.i_tab_2_0_0);
                hideImageStatus();
                if(null == mMyFragment){
                    mMyFragment = MyFragment.getInstance();
                }
                index_my_icon.setImageResource(R.mipmap.icon_index_41);
                index_my_text.setTextColor(Color.parseColor("#333333"));
                switchFragment(mMyFragment);
                break;
            default:
                break;
        }
    }


    /** 切换 Fragment */
    private void switchFragment(Fragment fragment) {
        if(mCurrentFragment != fragment){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(mCurrentFragment);
            mCurrentFragment = fragment;
            if(!fragment.isAdded()){
                transaction.add(R.id.fragmentLayout,fragment);
            }else{
                transaction.show(fragment);
            }
            transaction.commit();
        }
    }


    /** 隐藏状态 */
    @SuppressLint("ResourceAsColor")
    private void hideImageStatus(){
        index_first_icon.setImageResource(R.mipmap.icon_index_000);
        index_flash_icon.setImageResource(R.mipmap.icon_index_00);
        index_school_icon.setImageResource(R.mipmap.icon_index_10);
        index_activity_icon.setImageResource(R.mipmap.icon_index_30);
        index_my_icon.setImageResource(R.mipmap.icon_index_40);
        index_first_text.setTextColor(Color.parseColor("#C0C0C0"));
        index_flash_text.setTextColor(Color.parseColor("#C0C0C0"));
        index_activity_text.setTextColor(Color.parseColor("#C0C0C0"));
        index_school_text.setTextColor(Color.parseColor("#C0C0C0"));
        index_my_text.setTextColor(Color.parseColor("#C0C0C0"));
    }


    /** --------------------------------- 退出两次提醒  ---------------------------------*/
    // 第一次按下返回键的事件
    private long firstPressedTime;

    // System.currentTimeMillis() 当前系统的时间
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(HomeActivityV2.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }



    //下面是app在前台（已在首页）
    private int fromType;
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mJPushBean = (JPushBean)intent.getExtras().getSerializable("jpushbean");
        if(mJPushBean !=  null){
            KLog.e("tag","mJPushBean 类型是 " + mJPushBean.getJump_type()
                    + "  参数 " + mJPushBean.getJump_info());
            toDiffer(mJPushBean);
        }

        if(intent.getExtras() != null){
            fromType =  intent.getExtras().getInt("type");
            KLog.e("tag","HomeActivity  fromType " + fromType);


            if(H5_TO_ACTICLE == fromType){
                bottomClick(findViewById(R.id.index_first));
                EventBus.getDefault().post(new toActicleEvent("去干货"));
            }

//            else if(JPUSH_TO_FLASH == fromType){
//                bottomClick(findViewById(R.id.index_flash));
//            }else if(JPUSH_TO_My == fromType){
//                bottomClick(findViewById(R.id.index_my));
//            }
        }
    }



    private void getUserInfo() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getUserInfo(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<RegisterLoginBean.UserInfo>>() {
                    @Override
                    public void onSuccess(HttpResponse<RegisterLoginBean.UserInfo> response) {
                        StringUtil.setUserInfoBean(response.getReturn_data());
                    }

                    @Override
                    public void onNetFail(String msg) {

                    }

                    @Override
                    public void onHintError(String return_code, String errorMes) {

                    }
                });

    }



    /** --------------------------------- 推送  ---------------------------------*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static boolean isForeground = false;
    private JPushReceiver mMessageReceiver;
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";


    public void registerMessageReceiver() {
        mMessageReceiver = new JPushReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }


    /** --------------------------------- 版本更新  ---------------------------------*/
    //是否强更：1-是,0-否
    private VersionBean mVersionBean;

    private String serverVersionCode;

    private String apkUrl;

    private String note;

    private String forceUpdat;

    private void checkupd() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().checkupd(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<VersionBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<VersionBean> response) {
                        mVersionBean = response.getReturn_data();
                        if(null != mVersionBean){
                            if(null != mVersionBean.getList() && !mVersionBean.getList().isEmpty() && null != mVersionBean.getList().get(0)){
                                serverVersionCode = mVersionBean.getList().get(0).getVersion_code();
                                apkUrl = mVersionBean.getList().get(0).getUrl();
                                note = mVersionBean.getList().get(0).getUpdate_desc();
                                forceUpdat = mVersionBean.getList().get(0).getForce_update_flag();
                                KLog.d("tag","手机的VersionCode " + AppUtils.getAppVersionCode() + "");

                                //当前的版本小于 后台返回的版本提示更新
                                if(!TextUtils.isEmpty(serverVersionCode)){
                                    if(AppUtils.getAppVersionCode() < Integer.parseInt(serverVersionCode)){
                                        boolean isForce = forceUpdat.equals("1") ? true : false;
                                        showUpdateDialog(isForce);
                                    }else{
                                    }
                                }

                            }

                        }

                    }

                });
    }


    private void showUpdateDialog(boolean isForce) {
        AppUpdateUtilNew updateUtil = new AppUpdateUtilNew(HomeActivityV2.this,apkUrl);
        updateUtil.showUpdateDialog(note,isForce);

    }


    //用户token失效会再次给与token
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginGoodEvent(LoginGoodEvent event) {
        if (this != null) {
            toGiveToken();
        }
    }





}




