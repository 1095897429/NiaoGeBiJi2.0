package com.qmkj.niaogebiji.module.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.SizeUtils;
import com.qmkj.niaogebiji.BuildConfig;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.tab.TabLayout;
import com.qmkj.niaogebiji.common.utils.StringToolKit;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.NewsFragmentAdapter;
import com.qmkj.niaogebiji.module.bean.NewsChannelBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.event.FeatherEvent;
import com.qmkj.niaogebiji.module.event.UserFeatherEvent;
import com.qmkj.niaogebiji.module.fragment.FeatherItemFragment1;
import com.qmkj.niaogebiji.module.fragment.FeatherItemFragment2;
import com.qmkj.niaogebiji.module.fragment.FeatherItemFragment3;
import com.qmkj.niaogebiji.module.widget.CustomScrollViewPager;
import com.qmkj.niaogebiji.module.widget.MyNestedScrollView;
import com.qmkj.niaogebiji.module.widget.ViewPagerTitleFeather;
import com.qmkj.niaogebiji.module.widget.header.XnClassicsHeader;
import com.qmkj.niaogebiji.module.widget.tab1.ViewPagerTitle;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:羽毛任务
 */
public class FeatherActivity  extends BaseActivity {

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.my_feather)
    TextView my_feather;

    @BindView(R.id.ll)
    LinearLayout ll;

    @BindView(R.id.scrollView)
    MyNestedScrollView scrollView;


    @BindView(R.id.viewpager)
    CustomScrollViewPager mViewPager;


    @BindView(R.id.pager_title)
    ViewPagerTitleFeather pager_title;


    @BindView(R.id.ll_center)
    LinearLayout ll_center;


    @BindView(R.id.rl_top)
    LinearLayout rl_top;

    int mHeight = 0;

    int textHeight;

    @BindView(R.id.titleLayout)
    RelativeLayout titleLayout;


    @BindView(R.id.pager_title2)
    ViewPagerTitleFeather pager_title2;

    @BindView(R.id.tv_title)
    TextView tv_title;


    RegisterLoginBean.UserInfo userInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feather_new_3;
    }





    //设置背景透明度
    private void setBgAlphaChange(int oldy, float totalHight) {
        /**
         * 渐变取值范围0--255
         *                               高度（向上或向下移动的高度）
         * 渐变百分比： percent =--------------------------------------------
         *                           总高度（也就是head图片高度-标题栏高度）
         */
        float percent = (float) Math.abs(oldy) / Math.abs(totalHight);
        int alpha = (int) (percent * 255);
        titleLayout.setAlpha(alpha);
        tv_title.setAlpha(alpha);
        //设置文字颜色，黑色，加透明度
        tv_title.setTextColor(Color.argb(alpha, 0, 0, 0));
        rl_top.setAlpha(alpha);
        rl_top.setBackgroundColor(Color.argb(alpha, 255, 255, 255));

    }


    boolean isLock ;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView() {

        initSamrtLayout();

        String [] titile = new String[]{"羽毛任务","羽毛记录","排行榜"};

        pager_title.initData(titile,mViewPager,0);


        pager_title2.initData(titile,mViewPager,0);

        pager_title2.setOnTextViewClickListener((textView, index) -> {
            scrollView.smoothScrollTo(0,textHeight);
        });

        ll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int solid_title_height = ll.getHeight();
                KLog.d("tag ", "固定标题高度为 " + solid_title_height);
                scrollView.setMyScrollHeight(solid_title_height);
                ll.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });



        rl_top.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int solid_title_height = rl_top.getHeight();
                KLog.d("tag ", "固定标题高度为 " + solid_title_height);

                rl_top.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        pager_title2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int text = pager_title2.getHeight();
                KLog.d("tag ", "固定标题中下方tablayout高度为 " + text);
                pager_title2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        pager_title.post(() -> {
            int[] position = new int[2];
            pager_title.getLocationOnScreen(position);
            mHeight = position[1];
            KLog.d("tag","getLocationOnScreen:" + position[0] + "," + position[1]);
            textHeight = mHeight - SizeUtils.dp2px(50 + 25);
//            scrollView.setMyScrollHeight(300);
            KLog.d("tag","移动距离可隐藏第一次加载的tablayout  " + textHeight);

        });

//        mTabLayout.post(() -> {
//            int[] position = new int[2];
//            mTabLayout.getLocationOnScreen(position);
//            mHeight = position[1];
//            KLog.d("tag","getLocationOnScreen:" + position[0] + "," + position[1]);
//            textHeight = mHeight - SizeUtils.dp2px(50 + 25);
//            KLog.d("tag","移动距离可隐藏第一次加载的tablayout  " + textHeight);
//
//        });



        scrollView.setScrollViewListener(t -> {
            if (t <= textHeight) {
                //根据滑动设置渐变透明度
                setBgAlphaChange(t, textHeight);
            } else if (t > textHeight) {
                //防止快速滑动导致透明度问题  向上
                //快速滑动就直接设置不透明
                setBgAlphaChange(textHeight, textHeight);
            }


            if(t >= textHeight){
                pager_title2.setVisibility(View.VISIBLE);
                scrollView.setNeedScroll(false);
            }else{
                pager_title2.setVisibility(View.GONE);
                scrollView.setNeedScroll(true);
            }
        });




//        scrollView.setOnObservableScrollViewScrollChanged(new HoldTabScrollView.OnHoldTabScrollViewScrollChanged() {
//            @Override
//            public void onObservableScrollViewScrollChanged(int l, int t, int oldl, int oldt) {
//
//                KLog.d("tag","t " + t);
//                if (t <= textHeight) {
//                    //根据滑动设置渐变透明度
//                    setBgAlphaChange(t, textHeight);
//                } else if (t > textHeight) {
//                    //防止快速滑动导致透明度问题  向上
//                    //快速滑动就直接设置不透明
//                    setBgAlphaChange(textHeight, textHeight);
//                }
//
//
//                if(t >= textHeight){
//                    pager_title2.setVisibility(View.VISIBLE);
//                }else{
//                    pager_title2.setVisibility(View.GONE);
//                }
//            }
//        });






        userInfo = StringUtil.getUserInfoBean();
        if(null != userInfo && !TextUtils.isEmpty(userInfo.getPoint())){
            //自定义ttf
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/DIN-Black.otf");
            my_feather.setTypeface(typeface);
            //羽毛数
            my_feather.setText(userInfo.getPoint());
        }


        //类型：1-增加积分，2-减少积分
        mChannelListBeanList = new ArrayList<>();
        NewsChannelBean.AllChannelBean bean ;
        bean = new NewsChannelBean.AllChannelBean("1","羽毛任务");
        mChannelListBeanList.add(bean);
        bean = new NewsChannelBean.AllChannelBean("0","羽毛记录");
        mChannelListBeanList.add(bean);
        bean = new NewsChannelBean.AllChannelBean("2","排行榜");
        mChannelListBeanList.add(bean);

        if(null != mChannelListBeanList){
            setUpAdater();
        }
    }

    /**  ============================== tablayout部分 ======================================== */

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;


    //集合
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitls = new ArrayList<>();
    //存储频道集合
    private List<NewsChannelBean.AllChannelBean> mChannelListBeanList;
    //适配器
    private NewsFragmentAdapter mNewsFragmentAdapter;


    private void setUpAdater() {

        mFragmentList.clear();
        mTitls.clear();
        for (int i = 0; i < mChannelListBeanList.size(); i++) {
            if(0 == i){
                FeatherItemFragment1 temp1 = FeatherItemFragment1.getInstance(mChannelListBeanList.get(i).getChaid(),
                        mChannelListBeanList.get(i).getChaname());
                mFragmentList.add(temp1);
            }else if(1 == i){
                FeatherItemFragment2 temp2 = FeatherItemFragment2.getInstance(mChannelListBeanList.get(i).getChaid(),
                        mChannelListBeanList.get(i).getChaname());
                mFragmentList.add(temp2);
            }else{
                FeatherItemFragment3 temp3 = FeatherItemFragment3.getInstance(mChannelListBeanList.get(i).getChaid(),
                        mChannelListBeanList.get(i).getChaname());
                mFragmentList.add(temp3);
            }


            mTitls.add(StringToolKit.dealNullOrEmpty(mChannelListBeanList.get(i).getChaname()));
        }

        //设置适配器
        mNewsFragmentAdapter = new NewsFragmentAdapter(FeatherActivity.this,getSupportFragmentManager(), mFragmentList, mTitls);
        mViewPager.setAdapter(mNewsFragmentAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentList.size());
        //设置当前显示标签页为第二页
        mViewPager.setCurrentItem(0);
//        //设置指示器颜色
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.tab_select));
        //设置可滑动模式
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        //设置选中状态
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view=tab.getCustomView();
                if(null != view){
                    TextView textView=view.findViewById(R.id.tv_header);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                    textView.setTypeface(Typeface.DEFAULT_BOLD);
                    textView.setTextColor(getResources().getColor(R.color.text_news_title_color));
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view=tab.getCustomView();
                if(null != view){
                    TextView textView=view.findViewById(R.id.tv_header);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                    textView.setTypeface(Typeface.DEFAULT);
                    textView.setTextColor(getResources().getColor(R.color.text_news_tag_color));
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //绑定
        mTabLayout.setupWithViewPager(mViewPager);

        //设置自定义tab,这个需要在setupWithViewPager方法后
        for (int i = 0; i < mTabLayout.getTabCount(); i++){
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(mNewsFragmentAdapter.getTabView(i));
            }
        }
        //设置第二个为选中状态时的tab文字颜色
        View view = mTabLayout.getTabAt(0).getCustomView();
        TextView textView = view.findViewById(R.id.tv_header);
        textView.setTextColor(getResources().getColor(R.color.text_news_title_color));
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        textView.setSelected(true);



//        mTabLayout.post(() -> setIndicator(mTabLayout,20,20));

        //设置事件
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                KLog.d("tag", "选中的位置 ：" + position);
//                mViewPager.resetHeight(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//        mViewPager.resetHeight(0);
    }



    @OnClick({R.id.rule_feather,R.id.iv_back})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.rule_feather:
                String link ;
                if(BuildConfig.DEBUG){
                    link = "http://share.xy860.com/#/featherrule";
                }else{
                    link = "http://share.niaogebiji.com/#/featherrule";
                }

                Intent intent = new Intent(FeatherActivity.this, WebViewActivity.class);
                intent.putExtra("link",link);
                intent.putExtra("title","羽毛规则");
                startActivity(intent);
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }


    @Override
    protected boolean regEvent() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(FeatherEvent event){
        my_feather.setText(userInfo.getPoint() + event.num);
    }

    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(this);
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setDisableContentWhenRefresh(true);
        smartRefreshLayout.setDisableContentWhenLoading(true);
        smartRefreshLayout.setEnableLoadMore(false);


        //下拉刷新
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            EventBus.getDefault().post(new UserFeatherEvent());
            new Handler().postDelayed(() -> smartRefreshLayout.finishRefresh(),500);
        });

    }
}