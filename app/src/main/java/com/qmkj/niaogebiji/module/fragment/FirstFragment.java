package com.qmkj.niaogebiji.module.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringToolKit;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

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
 * 创建时间 2019-11-11
 * 描述：首页Fragment
 */
public class FirstFragment extends BaseLazyFragment {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    //Fragment 集合
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitls = new ArrayList<>();
    //存储频道集合
    private List<ChannelBean> mChannelBeanList;
    //适配器
    private FirstFragmentAdapter mFirstFragmentAdapter;



    public static FirstFragment getInstance() {
        return new FirstFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_first;
    }


    @Override
    protected void initView() {

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
    public void initData() {
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
            if(i == 2){
                FlashFragment flashFragment = FlashFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(flashFragment);
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
        mViewPager.setCurrentItem(2);


        //设置事件
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                KLog.d(TAG, "选中的位置 ：" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @OnClick({R.id.search_part})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.search_part:
                UIHelper.toSearchActivity(getActivity());
                break;


            default:
        }
    }


}
