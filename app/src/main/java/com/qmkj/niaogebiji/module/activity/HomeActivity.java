package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.event.toActicleEvent;
import com.qmkj.niaogebiji.module.event.toRefreshEvent;
import com.qmkj.niaogebiji.module.fragment.CircleFragment;
import com.qmkj.niaogebiji.module.fragment.FirstFragment;
import com.qmkj.niaogebiji.module.fragment.FlashFragment;
import com.qmkj.niaogebiji.module.fragment.MyFragment;
import com.qmkj.niaogebiji.module.fragment.SchoolFragment;
import com.qmkj.niaogebiji.module.fragment.ToolFragment;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.VISIBLE;

public class HomeActivity extends BaseActivity {


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


    String [] string = new String[]{"这里是学院。我们根据知识脉络精心梳理了营销知识树WIKI，欢迎大家查阅\n" +
            "此外，专业认证测试与课程帮助你更好的提生自己~",
    "这里是工具箱。我们为你推荐了常用的营销人工作必备工具",
    "这里是圈子。行业大V都在用的营销人朋友圈，快看看大家都在说什么吧\n" +
            "好了，介绍完成了，尽情体验全新鸟哥笔记APP吧~"};

    LinearLayout.LayoutParams lp;

    //点击引导图的次数
    int count = 1;

    FirstFragment mFirstFragment;
    FlashFragment mFlashFragment;
    SchoolFragment mSchoolFragment;
//    ToolFragment mToolFragment;
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
                KLog.d("tag",result);
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
        mMyWebView.setWebViewClient(new WebViewClient(){
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


        boolean firstGuide = SPUtils.getInstance().getBoolean("isFirstHomeGuide",false);
        if(false){
            oldguide.setVisibility(View.VISIBLE);
            guideUser();
        }

        loginHW();
    }


    private void loginHW() {
//        if (DeviceUtils.getManufacturer().equals("HUAWEI")){
//            HMSAgent.Push.getToken(new GetTokenHandler() {
//                @Override
//                public void onResult(int rtnCode) {
//                    KLog.d("tag","code  " + rtnCode);
//                }
//            });
//        }
    }



    @Override
    public void initData() {
        initFragment();
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


    @OnClick({R.id.index_first,R.id.index_flash,R.id.index_tool,R.id.index_circle,R.id.index_my

    })
    public void bottomClick(android.view.View layout){
        switch (layout.getId()){

            case R.id.index_first:

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
                hideImageStatus();
                if(null == mFlashFragment){
                    mFlashFragment = FlashFragment.getInstance();
                }
                index_flash_icon.setImageResource(R.mipmap.icon_index_11);
                index_flash_text.setTextColor(Color.parseColor("#333333"));
                switchFragment(mFlashFragment);
                break;
            case R.id.index_tool:

                hideImageStatus();
                if(null == mSchoolFragment){
                    mSchoolFragment = SchoolFragment.getInstance();
                }
                index_school_icon.setImageResource(R.mipmap.icon_index_01);
                index_school_text.setTextColor(Color.parseColor("#333333"));
                switchFragment(mSchoolFragment);
            break;
            case R.id.index_circle:
                hideImageStatus();
                if(null == mCircleFragment){
                    mCircleFragment = CircleFragment.getInstance();
                }
                index_activity_icon.setImageResource(R.mipmap.icon_index_31);
                index_activity_text.setTextColor(Color.parseColor("#333333"));
                switchFragment(mCircleFragment);
                break;
            case R.id.index_my:
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
            Toast.makeText(HomeActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }



    private int fromType;
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getExtras() != null){
            fromType =  intent.getExtras().getInt("type");
            bottomClick(findViewById(R.id.index_first));
            EventBus.getDefault().post(new toActicleEvent("去干货"));
        }
    }
}
