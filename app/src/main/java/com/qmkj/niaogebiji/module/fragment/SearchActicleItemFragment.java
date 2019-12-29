package com.qmkj.niaogebiji.module.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.ActicleAllBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.ProBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.bean.SearchAllActicleBean;
import com.qmkj.niaogebiji.module.event.SearchWordEvent;
import com.qmkj.niaogebiji.module.event.toActionEvent;
import com.qmkj.niaogebiji.module.event.toFlashEvent;
import com.qmkj.niaogebiji.module.event.toRefreshEvent;
import com.qmkj.niaogebiji.module.widget.header.XnClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 *  搜索干货界面
 *  1.首页区别是：没有置顶
 *  2.没有更懂你
 */
public class SearchActicleItemFragment extends BaseLazyFragment {

    @BindView(R.id.backtop)
    ImageView backtop;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    private String catid = "111";
    private int page = 1;
    private int pageSize = 10;
    private List<ActicleAllBean.Article> mArticles;
    //适配器
    FirstItemNewAdapter mFirstItemAdapter;
    //集合
    List<FirstItemBean> mList = new ArrayList<>();
    //组合集合
    List<MultiNewsBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    private String myKeyword;


    //通过此方式实例化Fragment
    public static SearchActicleItemFragment getInstance(String chainId, String chainName) {
        SearchActicleItemFragment newsItemFragment = new SearchActicleItemFragment();
        Bundle args = new Bundle();
        args.putString("catid", chainId);
        args.putString("chainName", chainName);
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_acticle_item;
    }


    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected void initView() {
        initSamrtLayout();
        initLayout();
    }


    @Override
    protected void lazyLoadData() {
        searchArticle();
    }


    private void searchArticle() {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",myKeyword);
        map.put("page",page + "");
        map.put("page_size",pageSize + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchArticle(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SearchAllActicleBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<SearchAllActicleBean> response) {
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }

                        SearchAllActicleBean temp  = response.getReturn_data();

                        mArticle_lists = temp.getList();

                        if(null != mArticle_lists){
                            if(1 == page){
                                setActicleData(mArticle_lists);
                                mFirstItemAdapter.setNewData(mAllList);
                                //如果第一次返回的数据不满10条，则显示无更多数据
                                if(mArticle_lists.size() < Constant.SEERVER_NUM){
                                    mFirstItemAdapter.loadMoreEnd();
                                }
                            }else{
                                //已为加载更多有数据
                                if(mArticle_lists != null && mArticle_lists.size() > 0){
                                    setActicleData(mArticle_lists);
                                    mFirstItemAdapter.loadMoreComplete();
                                    mFirstItemAdapter.addData(tempList);
                                }else{
                                    //已为加载更多无更多数据
                                    mFirstItemAdapter.loadMoreEnd();
                                }
                            }
                        }
                    }
                });
    }


    private List<RecommendBean.Article_list> mArticle_lists;

    //后台返回临时数据
    List<MultiNewsBean> tempList = new ArrayList<>();

    private void setActicleData(List<RecommendBean.Article_list> article_lists) {

        tempList.clear();

        RecommendBean.Article_list itemBean;
        MultiNewsBean bean1 ;

        if(1 == page){
            String pic_type;
            for (int i = 0; i < article_lists.size(); i++) {
                itemBean = article_lists.get(i);
                bean1 = new MultiNewsBean();
                pic_type = article_lists.get(i).getPic_type();
                if("1".equals(pic_type)){
                    bean1.setItemType(1);
                }else if("2".equals(pic_type)){
                    bean1.setItemType(3);
                }else if("3".equals(pic_type)){
                    bean1.setItemType(2);
                }else{
                    bean1.setItemType(1);
                }
                bean1.setNewsActicleList(itemBean);
                mAllList.add(bean1);
            }

            mFirstItemAdapter.setNewData(mAllList);
        }else{
            String pic_type;
            for (int i = 0; i < article_lists.size(); i++) {
                itemBean = article_lists.get(i);
                bean1 = new MultiNewsBean();
                pic_type = article_lists.get(i).getPic_type();
                if("1".equals(pic_type)){
                    bean1.setItemType(1);
                }else if("2".equals(pic_type)){
                    bean1.setItemType(3);
                }else if("3".equals(pic_type)){
                    bean1.setItemType(2);
                }else{
                    bean1.setItemType(1);
                }
                bean1.setNewsActicleList(itemBean);
                tempList.add(bean1);
            }
        }
    }


    /** --------------------------------- 实时快讯  ---------------------------------*/

    private void initAnimate() {
//        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(flash_title,"translationY",0f,200f);
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.play(objectAnimatorY);
//        animatorSet.setDuration(3000);
//        animatorSet.start();
    }

    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            searchArticle();
        });
    }


    private void getData() {

        NewsItemBean itemBean;
        FirstItemBean firstItemBean;
        MultiNewsBean bean1 ;
        for (int i = 0; i < 10; i++) {
           if(i == 2){
              firstItemBean = new FirstItemBean();
              bean1 = new MultiNewsBean();
              bean1.setItemType(4);
              bean1.setFirstItemBean(firstItemBean);
          }else {
              itemBean = new NewsItemBean();
              bean1 = new MultiNewsBean();
              if(i == 4){
                  bean1.setItemType(2);
              }else if(i == 5){
                  bean1.setItemType(3);
              }else if(i == 7){
                  bean1.setItemType(5);
              }else{
                  bean1.setItemType(1);
              }

              bean1.setNewsItemBean(itemBean);
          }
            mAllList.add(bean1);
        }

        mFirstItemAdapter.setNewData(mAllList);

        initAnimate();
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



    //初始化布局管理器
    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mFirstItemAdapter = new FirstItemNewAdapter(mAllList);
        mRecyclerView.setAdapter(mFirstItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    @SuppressLint("CheckResult")
    private void initEvent() {
        mFirstItemAdapter.setOnLoadMoreListener(() -> {
            ++page;
            searchArticle();
        },mRecyclerView);

        mRecyclerView.addOnScrollListener(new RvScrollListener());

        mFirstItemAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.toMoreActivity:
                    EventBus.getDefault().post(new toActionEvent("去活动界面"));
                    break;
                default:
            }
        });


        mFirstItemAdapter.setOnItemClickListener((adapter, view, position) -> {
            if(StringUtil.isFastClick()){
                return;
            }

            if(position <= 2) {
                MobclickAgentUtils.onEvent("index_search_index_" + (position + 1) + "_2_0_0");
            }


            String aid = mFirstItemAdapter.getData().get(position).getNewsActicleList().getAid();
            if (!TextUtils.isEmpty(aid)) {
                UIHelper.toNewsDetailActivity(getActivity(), aid);
            }

//
//            int type = mFirstItemAdapter.getData().get(position).getItemType();
//            //快讯
//            if(type == 4){
//                EventBus.getDefault().post(new toFlashEvent("去快讯信息流"));
//            }else if(type == 1){
//
//            }

        });

    }


    private class RvScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                //获取最后一个可见view的位置
                int lastItemPosition = linearManager.findLastVisibleItemPosition();
                if(lastItemPosition > 6){
                   backtop.setVisibility(View.GONE);
                }else{
                    backtop.setVisibility(View.GONE);
                }
            }

        }
    }


    @SuppressLint("CheckResult")
    @Override
    public void initData() {

        RxView.clicks(backtop)
                //每1秒中只处理第一个元素
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(object -> {
                    mRecyclerView.scrollToPosition(0);
                    smartRefreshLayout.autoRefresh();
                });
    }


    //点击全部里的查看更多事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchWordEvent(SearchWordEvent event) {
        myKeyword = event.getWord();
//        KLog.d("tag","myKeyword = " + myKeyword);
    }





}
