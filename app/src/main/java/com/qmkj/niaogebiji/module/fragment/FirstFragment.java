package com.qmkj.niaogebiji.module.fragment;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.SizeUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.service.MediaService;
import com.qmkj.niaogebiji.common.tab.TabLayoutComplex;
import com.qmkj.niaogebiji.common.utils.StringToolKit;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.MoringIndexBean;
import com.qmkj.niaogebiji.module.bean.ToollndexBean;
import com.qmkj.niaogebiji.module.event.AudioEvent;
import com.qmkj.niaogebiji.module.event.toActionEvent;
import com.qmkj.niaogebiji.module.event.toFlashEvent;
import com.qmkj.niaogebiji.module.widget.DynamicLine;
import com.qmkj.niaogebiji.module.widget.MyLinearLayout;
import com.qmkj.niaogebiji.module.widget.tab1.ViewPagerTitle;
import com.qmkj.niaogebiji.module.widget.tab2.ViewPagerTitleSlide;
import com.qmkj.niaogebiji.module.widget.tab3.ViewPagerTitleSlide3;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
 * 准备资源 -- 播放视频(进度条会前进)   暂停视频
 */
public class FirstFragment extends BaseLazyFragment {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.pager_title)
    ViewPagerTitleSlide3 pager_title;

    @BindView(R.id.ll_moring)
    LinearLayout ll_moring;

    @BindView(R.id.moring_content)
    TextView moring_content;




    //Fragment 集合
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitls = new ArrayList<>();
    //存储频道集合
    private List<ChannelBean> mChannelBeanList;
    //适配器
    private FirstFragmentAdapter mFirstFragmentAdapter;

    private int page = 1;
    private int pageSize = 10;
    private MoringIndexBean.MoringBean mMoringBean;

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
        String [] titile = new String[]{"关注","干货","活动","快讯"};


        pager_title.initData(titile,mViewPager,1);


        initEvent();

        getTopPost();
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
//        bean = new ChannelBean("4","热榜");
//        mChannelBeanList.add(bean);


        if(null != mChannelBeanList){
            setUpAdater();
        }

    }

    // {"return_code":"200","return_msg":"success","return_data":{"morning_article":{}}}
    private void getTopPost() {
        Map<String,String> map = new HashMap<>();
        map.put("page_no",page + "");
        map.put("page_size",pageSize + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getTopPost(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<MoringIndexBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<MoringIndexBean> response) {
                        KLog.d("tag","response " + response.getReturn_code());
                        MoringIndexBean temp = response.getReturn_data();
                        mMoringBean = temp.getMorning_article();
                        setData();
                    }
                });
    }

    private void setData() {
        if(!TextUtils.isEmpty(mMoringBean.getTitle()) &&
                !TextUtils.isEmpty(mMoringBean.getVideo())){
            moring_content.setText(mMoringBean.getTitle());
            ll_moring.setVisibility(View.VISIBLE);
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
            }else if(i == 1){
                FirstItemFragment newsItemFragment = FirstItemFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(newsItemFragment);
            } else if(i == 2){
                ActionFragment actionFragment = ActionFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(actionFragment);
            }else if(i == 3){
                FlashFragment flashFragment = FlashFragment.getInstance(mChannelBeanList.get(i).getChaid(),
                        mChannelBeanList.get(i).getChaname());
                mFragmentList.add(flashFragment);
            }

//            else if(i == 4){
//                HotNewsFragment hotNewsFragment = HotNewsFragment.getInstance(mChannelBeanList.get(i).getChaid(),
//                        mChannelBeanList.get(i).getChaname());
//                mFragmentList.add(hotNewsFragment);
//            }

            mTitls.add(StringToolKit.dealNullOrEmpty(mChannelBeanList.get(i).getChaname()));
        }

        //设置适配器
        mFirstFragmentAdapter = new FirstFragmentAdapter(getActivity(),getChildFragmentManager(), mFragmentList, mTitls);
        mViewPager.setAdapter(mFirstFragmentAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentList.size());
        //设置当前显示标签页为第二页
        mViewPager.setCurrentItem(1);


        //设置事件
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

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

                UIHelper.toWelcomeActivity(getActivity());
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


                    EventBus.getDefault().post(new AudioEvent(MediaService.musicPath[0]));


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
