package com.qmkj.niaogebiji.module.fragment;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringToolKit;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.widget.ViewPagerTitle;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class CircleFragment extends BaseLazyFragment {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.pager_title)
    ViewPagerTitle pager_title;


    //Fragment 集合
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitls = new ArrayList<>();
    //存储频道集合
    private List<ChannelBean> mChannelBeanList;
    //适配器
    private FirstFragmentAdapter mFirstFragmentAdapter;


    public static CircleFragment getInstance() {
        return new CircleFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_circle;
    }


    @Override
    protected void initView() {
        String [] titile = new String[]{"关注","推荐"};

        pager_title.initData(titile,mViewPager,0);


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
        bean = new ChannelBean("1","推荐");
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
                CircleFocusFragment focusFragment = CircleFocusFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(focusFragment);
            }else if(i == 1){
                CircleRecommendFragment actionFragment = CircleRecommendFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(actionFragment);
            }
            mTitls.add(StringToolKit.dealNullOrEmpty(mChannelBeanList.get(i).getChaname()));
        }

        //设置适配器
        mFirstFragmentAdapter = new FirstFragmentAdapter(getActivity(),getChildFragmentManager(), mFragmentList, mTitls);
        mViewPager.setAdapter(mFirstFragmentAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentList.size());
        //设置当前显示标签页为第二页
        mViewPager.setCurrentItem(1);

        initEvent();
    }

    private void initEvent() {
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



    @OnClick({R.id.icon_send_msg})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.icon_send_msg:
                UIHelper.toCircleMakeActivity(getActivity());
                //参数一：目标Activity1进入动画，参数二：之前Activity2退出动画
                getActivity().overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                break;

            default:
        }
    }

}
