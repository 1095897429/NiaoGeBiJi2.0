package com.qmkj.niaogebiji.module.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringToolKit;
import com.qmkj.niaogebiji.module.activity.HomeActivity;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.event.AudioEvent;
import com.qmkj.niaogebiji.module.event.MyEvent;
import com.qmkj.niaogebiji.module.event.toActionEvent;
import com.qmkj.niaogebiji.module.event.toFlashEvent;
import com.qmkj.niaogebiji.module.widget.DynamicLine;
import com.qmkj.niaogebiji.module.widget.MyLinearLayout;
import com.qmkj.niaogebiji.module.widget.ViewPagerTitle;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述：首页Fragment
 * 准备资源 -- 播放视频(进度条会前进)   暂停视频
 */
public class FirstFragment extends BaseLazyFragment {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.pager_title)
    ViewPagerTitle pager_title;

    @BindView(R.id.first_11)
    TextView first_11;

    @BindView(R.id.first_22)
    TextView first_22;

    @BindView(R.id.myll)
    MyLinearLayout myll;



    //Fragment 集合
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitls = new ArrayList<>();
    //存储频道集合
    private List<ChannelBean> mChannelBeanList;
    //适配器
    private FirstFragmentAdapter mFirstFragmentAdapter;


    @Override
    protected boolean regEvent() {
        return true;
    }

    public static FirstFragment getInstance() {
        return new FirstFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_first;
    }


    @SuppressLint("CheckResult")
    @Override
    protected void initView() {
        String [] titile = new String[]{"关注","干货","活动","快讯","热榜"};

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        first_11.setTypeface(typeface);
        first_22.setTypeface(typeface);

        pager_title.initData(titile,mViewPager,0);

        myll.initData(titile,mViewPager,0);

        initEvent();
    }

    DynamicLine dynamicLine;
    private void createDynamicLine() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dynamicLine = new DynamicLine(getContext());
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        dynamicLine.setLayoutParams(params);
    }


    @BindView(R.id.llll)
    RelativeLayout llll;

    private void initEvent() {
        createDynamicLine();

        first_11.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= 16) {
                            first_11.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        }
                        else {
                            first_11.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }
                        first_11.getWidth(); // 获取宽度
                        first_11.getHeight(); // 获取高度

                        int wi =  first_11.getWidth();
                        KLog.d("tag",wi + "");

                        dynamicLine.updateView(0,wi);
                        llll.addView(dynamicLine,0);
                    }
                });

    }


    //点击切换fragement会调用
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            //pause

        }else{
            //resume
        }
    }


    @Override
    public void initData()  {
        mChannelBeanList = new ArrayList<>();
        ChannelBean bean ;
        bean = new ChannelBean("0","关注");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("1","干货");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("2","活动");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("3","快讯");
        mChannelBeanList.add(bean);
        bean = new ChannelBean("4","热榜");
        mChannelBeanList.add(bean);


        if(null != mChannelBeanList){
            setUpAdater();
        }


    }



    private void setUpAdater() {

        mFragmentList.clear();
        mTitls.clear();
        for (int i = 0; i < mChannelBeanList.size(); i++) {
            if(i == 0){
                FocusFragment focusFragment = FocusFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(focusFragment);
            }else if(i == 2){
                ActionFragment actionFragment = ActionFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(actionFragment);
            }else if(i == 3){
                FlashFragment flashFragment = FlashFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(flashFragment);
            }else if(i == 4){
                HotNewsFragment hotNewsFragment = HotNewsFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(hotNewsFragment);
            }else{
                FirstItemFragment newsItemFragment = FirstItemFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(newsItemFragment);
            }




            mTitls.add(StringToolKit.dealNullOrEmpty(mChannelBeanList.get(i).getChaname()));
        }

        //设置适配器
        mFirstFragmentAdapter = new FirstFragmentAdapter(getActivity(),getChildFragmentManager(), mFragmentList, mTitls);
        mViewPager.setAdapter(mFirstFragmentAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentList.size());
        //设置当前显示标签页为第二页
        mViewPager.setCurrentItem(0);


        //设置事件
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                first_11.setTextSize(16);
                first_22.setTextSize(22);
                first_11.setTextColor(getResources().getColor(R.color.text_second_color));
                first_22.setTextColor(getResources().getColor(R.color.text_first_color));

                KLog.d(TAG, "选中的位置 ：" + position);
                first_22.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {
                                if (Build.VERSION.SDK_INT >= 16) {
                                    first_22.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                }
                                else {
                                    first_22.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }

                                int wi =  first_22.getWidth();
                                KLog.d("tag",wi + " " + " getLeft " + first_22.getLeft());

                                dynamicLine.updateView(first_22.getLeft(),wi+first_22.getLeft());
                            }
                        });


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }





    @OnClick({R.id.search_part,R.id.toMoreMoring,R.id.icon_catogory,R.id.listenMoring,R.id.moring_content,R.id.rl_sign})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.rl_sign:
                KLog.d("tag","去签到界面");
                break;
            case R.id.search_part:
                UIHelper.toSearchActivity(getActivity());
                break;
            case R.id.moring_content:
                KLog.d("tag","去明细页");
                break;
            case R.id.icon_catogory:
                UIHelper.toCategoryActivity(getActivity());
                //参数一：Activity2进入动画  参数二：Activity1退出动画
                getActivity().overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                break;
            case R.id.listenMoring:

                EventBus.getDefault().post(new AudioEvent(0));

                break;
            case R.id.toMoreMoring:
                UIHelper.toMoringActivity(getActivity());
                break;

            default:
        }
    }



    /** --------------------------------- EventBus 事件  ---------------------------------*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActionThread(toActionEvent event){
        mViewPager.setCurrentItem(2);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFlashThread(toFlashEvent event){
        mViewPager.setCurrentItem(3);
    }








}
