package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.event.toRefreshEvent;
import com.qmkj.niaogebiji.module.fragment.CircleFragment;
import com.qmkj.niaogebiji.module.fragment.FirstFragment;
import com.qmkj.niaogebiji.module.fragment.MyFragment;
import com.qmkj.niaogebiji.module.fragment.SchoolFragment;
import com.qmkj.niaogebiji.module.fragment.ToolFragment;
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

    @BindView(R.id.index_news_icon)
    ImageView index_news_icon;
    @BindView(R.id.index_news_text)
    TextView index_news_text;

    @BindView(R.id.index_school_icon)
    ImageView index_school_icon;
    @BindView(R.id.index_school_text)
    TextView index_school_text;

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
    RelativeLayout oldguide;


    @BindView(R.id.guide_img)
    ImageView guide_img;

    @BindView(R.id.skip)
    TextView skip;

    @BindView(R.id.guide_num)
    TextView guide_num;



    //点击引导图的次数
    int count = 1;

    FirstFragment mFirstFragment;
    SchoolFragment mSchoolFragment;
    ToolFragment mToolFragment;
    CircleFragment mCircleFragment;
    MyFragment mMyFragment;
    Fragment mCurrentFragment;
    //每个cell的大小
    int perWidth;

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {

        int screenWidth = ScreenUtils.getScreenWidth();
        perWidth = screenWidth / 5;

//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) guide_img.getLayoutParams();
//        lp.setMargins(perWidth / 2 ,0,0,0);
//        guide_img.setLayoutParams(lp);


        boolean firstGuide = SPUtils.getInstance().getBoolean("isFirstHomeGuide",false);
        if(!firstGuide){
            oldguide.setVisibility(View.VISIBLE);
        }
        guideUser();
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
                        guide_img.setImageResource(R.mipmap.bg_oldguide_2);
                    }else if(count == 3){
                        guide_num.setText(count + "/4");
                        guide_img.setImageResource(R.mipmap.bg_oldguide_3);
                    }else if(count == 4){
                        //这里对应最后一页
                        skip.setText("立即体验");
                        guide_num.setVisibility(View.GONE);
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


    @OnClick({R.id.index_first,R.id.index_school,R.id.index_tool,R.id.index_circle,R.id.index_my,R.id.toMoreLoveYou})
    public void bottomClick(android.view.View layout){
        switch (layout.getId()){
            case R.id.toMoreLoveYou:
                //TODO 11.14
                break;
            case R.id.index_first:

                hideImageStatus();
                if(null == mFirstFragment){
                    mFirstFragment = FirstFragment.getInstance();
                }
                index_first_icon.setImageResource(R.mipmap.icon_index_01);
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
            case R.id.index_school:
                hideImageStatus();
                if(null == mSchoolFragment){
                    mSchoolFragment = SchoolFragment.getInstance();
                }
                index_school_icon.setImageResource(R.mipmap.icon_index_11);
                index_school_text.setTextColor(Color.parseColor("#333333"));
                switchFragment(mSchoolFragment);
                break;
            case R.id.index_tool:
                hideImageStatus();
                if(null == mToolFragment){
                    mToolFragment = ToolFragment.getInstance();
                }
                index_news_icon.setImageResource(R.mipmap.icon_index_21);
                index_news_text.setTextColor(Color.parseColor("#333333"));
                switchFragment(mToolFragment);
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
        index_first_icon.setImageResource(R.mipmap.icon_index_00);
        index_school_icon.setImageResource(R.mipmap.icon_index_10);
        index_news_icon.setImageResource(R.mipmap.icon_index_20);
        index_activity_icon.setImageResource(R.mipmap.icon_index_30);
        index_my_icon.setImageResource(R.mipmap.icon_index_40);
        index_first_text.setTextColor(Color.parseColor("#C0C0C0"));
        index_school_text.setTextColor(Color.parseColor("#C0C0C0"));
        index_activity_text.setTextColor(Color.parseColor("#C0C0C0"));
        index_news_text.setTextColor(Color.parseColor("#C0C0C0"));
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


//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(SPUtils.getInstance().getBoolean("audio_view_show",false)){
//            part_audio.setVisibility(View.GONE);
//        }
//    }
}
