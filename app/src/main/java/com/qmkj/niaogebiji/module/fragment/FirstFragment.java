package com.qmkj.niaogebiji.module.fragment;

import android.annotation.SuppressLint;
import android.graphics.Rect;
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
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.google.android.material.appbar.AppBarLayout;
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
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.FirstFragmentAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolItemAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.MoringIndexBean;
import com.qmkj.niaogebiji.module.bean.SearchBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.bean.ToollndexBean;
import com.qmkj.niaogebiji.module.event.AudioEvent;
import com.qmkj.niaogebiji.module.event.toActionEvent;
import com.qmkj.niaogebiji.module.event.toFlashEvent;
import com.qmkj.niaogebiji.module.event.toRefreshEvent;
import com.qmkj.niaogebiji.module.event.toRefreshMoringEvent;
import com.qmkj.niaogebiji.module.widget.DynamicLine;
import com.qmkj.niaogebiji.module.widget.HorizontalSpacesDecoration;
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

    @BindView(R.id.moring_time)
    TextView moring_time;

    @BindView(R.id.first_search)
    TextView first_search;

    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;

    @BindView(R.id.search_part)
    LinearLayout search_part;

    @BindView(R.id.tool_recycler)
    RecyclerView tool_recycler;


    @BindView(R.id.tool_part)
    LinearLayout tool_part;



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


    //适配器
    ToolItemAdapter mToolItemAdapter;
    //组合集合
    List<ToolBean> mAllList = new ArrayList<>();
    //布局管理器
    GridLayoutManager mGridLayoutManager;

    private List<ToollndexBean> mList = new ArrayList<>();

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
        String [] titile = new String[]{"关注","干货","活动"};


        pager_title.initData(titile,mViewPager,1);

        initEvent();

        toolindex();

        initToolLayout();

        getTopPost();

        searchIndex();

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
//        bean = new ChannelBean("3","快讯");
//        mChannelBeanList.add(bean);
//        bean = new ChannelBean("4","热榜");
//        mChannelBeanList.add(bean);


        if(null != mChannelBeanList){
            setUpAdater();
        }

    }


    private Rect firstAndLastRect;
    private  Rect rect ;
    private void initToolLayout() {
        mGridLayoutManager = new GridLayoutManager(getActivity(),4);
        //设置布局管理器
        tool_recycler.setLayoutManager(mGridLayoutManager);

//
//        if(firstAndLastRect == null){
//            int space = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(32f) - SizeUtils.dp2px(64) * 4;
//            int i = space / 4;
//            rect =  new Rect(0, 0, i, 0);
//            int j = SizeUtils.dp2px( 0);
//            firstAndLastRect = new Rect(j, 0, i, 0);
//            HorizontalSpacesDecoration spacesDecoration = new HorizontalSpacesDecoration(rect, firstAndLastRect);
//            tool_recycler.addItemDecoration(spacesDecoration);
//        }

        //设置适配器
        mToolItemAdapter = new ToolItemAdapter(mList);
        tool_recycler.setAdapter(mToolItemAdapter);
        //解决数据加载不完
        tool_recycler.setNestedScrollingEnabled(true);
        tool_recycler.setHasFixedSize(true);
        mToolItemAdapter.setOnItemClickListener((adapter, view, position) -> {
            ToollndexBean temp =  mToolItemAdapter.getData().get(position);
            if("0".equals(temp.getType())){
                Toast.makeText(getContext(),"去 webview",Toast.LENGTH_SHORT).show();
            }else if("1".equals(temp.getType())){
                Toast.makeText(getContext(),"去 小程序",Toast.LENGTH_SHORT).show();
            }else{
                UIHelper.toToolEditActivity(getActivity());
            }
        });
    }



    private void toolindex() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().toolindex(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<ToollndexBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<ToollndexBean>> response) {
                        mList = response.getReturn_data();
                        setToolData();
                    }
                });
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
                        MoringIndexBean temp = response.getReturn_data();
                        mMoringBean = temp.getMorning_article();
                        setData();
                    }
                });
    }


    private void setToolData() {
        int size = mList.size();
        if(size >= 4){
           mList = mList.subList(0,4);
        }

        mToolItemAdapter.setNewData(mList);
        tool_part.setVisibility(View.VISIBLE);
    }


    private void setData() {
        if(!TextUtils.isEmpty(mMoringBean.getTitle()) &&
                !TextUtils.isEmpty(mMoringBean.getVideo())){
            moring_content.setText(mMoringBean.getSummary());
            //早报时间
            StringUtil.setPublishTime(moring_time,mMoringBean.getPublished_at());
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



    @OnClick({R.id.search_part,R.id.toMoreMoring,R.id.icon_catogory,R.id.listenMoring,R.id.moring_content,R.id.rl_sign,
        R.id.ll_moring
    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.moring_content:
            case R.id.ll_moring:
                String aid = mMoringBean.getAid();
                UIHelper.toNewsDetailActivity(getActivity(),aid);
                break;
            case R.id.rl_sign:
                UIHelper.toFeatherctivity(getActivity());
                break;
            case R.id.search_part:
                UIHelper.toSearchActivity(getActivity());
                break;
            case R.id.icon_catogory:
                UIHelper.toCategoryActivity(getActivity());
                //参数一：Activity2进入动画  参数二：Activity1退出动画
                getActivity().overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                break;
            case R.id.listenMoring:
                    String audio =  mMoringBean.getVideo();
                    String title = mMoringBean.getSummary();
                    String newid = mMoringBean.getAid();
                    EventBus.getDefault().post(new AudioEvent(audio,title,newid));

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
        if("去活动信息流".equals(event.getContent())){
            mViewPager.setCurrentItem(2);
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ontoRefreshMoringEvent(toRefreshMoringEvent event){
        if(getUserVisibleHint()){
            KLog.d("tag","我是First界面，请求早报接口");
            getTopPost();
        }

    }




    /** --------------------------------- 热搜  ---------------------------------*/
    private SearchBean mSearchBean;
    private List<SearchBean.Hot_search> mHot_searches;
    private SearchBean.Hot_search mHot_search;

    private void searchIndex() {

        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchIndex(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SearchBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<SearchBean> response) {
                        mSearchBean = response.getReturn_data();
                        if(null != mSearchBean){
                            mHot_searches = mSearchBean.getHot_search();

                            if(null != mHot_searches && !mHot_searches.isEmpty()){
                                String defaultHotKey = mHot_searches.get(0).getSearch_string();
                                first_search.setHint(defaultHotKey);
                            }
                        }
                    }
                });
    }



}
