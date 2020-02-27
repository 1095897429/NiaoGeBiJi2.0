package com.qmkj.niaogebiji.module.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.adapter.HotNewsAdapter;
import com.qmkj.niaogebiji.module.bean.ActicleAllBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.event.toRefreshEvent;
import com.qmkj.niaogebiji.module.widget.header.XnClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:参看分类
 */
public class HotNewsFragment extends BaseLazyFragment {

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    private int page = 1;
    //适配器 -- 需手动排序
    HotNewsAdapter mHotNewsAdapter;
    //组合集合
    List<MultiNewsBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;


    public static HotNewsFragment getInstance(String chainId, String chainName) {
        HotNewsFragment newsItemFragment = new HotNewsFragment();
        Bundle args = new Bundle();
        args.putString("catid", chainId);
        args.putString("chainName", chainName);
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_hotnews;
    }

    @Override
    protected void initView() {
        initSamrtLayout();
        initLayout();
//        getData();

        hotArticle();
    }

    private void hotArticle() {
        KLog.e("tag","当前的页数是 " + page + "");
        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");
        map.put("page_size",30 + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().hotArticle(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<ActicleAllBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<ActicleAllBean> response) {

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }

                        ActicleAllBean temp  = response.getReturn_data();
                        List<RecommendBean.Article_list> articles = temp.getList();

                        if(1 == page){
                            if(null != articles && !articles.isEmpty()){
                                setActicleData(articles);
                                mHotNewsAdapter.setNewData(mAllList);
                                //如果第一次返回的数据不满10条，则显示无更多数据
                                if(articles.size() <= 30){
                                    mHotNewsAdapter.loadMoreComplete();
                                    mHotNewsAdapter.loadMoreEnd();
                                }
                            }else{
                                //第一次加载无数据
                                ll_empty.setVisibility(View.VISIBLE);
                                ((TextView)ll_empty.findViewById(R.id.tv_empty)).setText("暂无内容");
                                ((ImageView)ll_empty.findViewById(R.id.iv_empty)).setImageResource(R.mipmap.icon_empty_article);
                                mRecyclerView.setVisibility(View.GONE);
                            }
                        }else{
                            //已为加载更多有数据
                            if(articles != null && articles.size() > 0){
                                setActicleData(articles);
                                mHotNewsAdapter.addData(tempList);
                                mHotNewsAdapter.loadMoreComplete();
                            }else{
                                //已为加载更多无更多数据
                                mHotNewsAdapter.loadMoreEnd();
                            }
                        }
                    }
                });
    }


    List<MultiNewsBean> tempList = new ArrayList<>();
    private void setActicleData(List<RecommendBean.Article_list> article_lists) {

        tempList.clear();
        RecommendBean.Article_list itemBean;
        MultiNewsBean bean1 ;

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

        if(page == 1){
            mAllList.addAll(tempList);
        }
    }





    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected void lazyLoadData() {

    }

    private void getData() {
        NewsItemBean itemBean;
        MultiNewsBean bean1 ;
        for (int i = 0; i < 10; i++) {
                itemBean = new NewsItemBean();
                itemBean.setRank((i + 1 + ""));
                bean1 = new MultiNewsBean();
                bean1.setItemType(1);
                bean1.setNewsItemBean(itemBean);
            mAllList.add(bean1);
        }
        mHotNewsAdapter.setNewData(mAllList);
    }


    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            hotArticle();
        });

    }


    //初始化布局管理器
    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mHotNewsAdapter = new HotNewsAdapter(mAllList);
        mRecyclerView.setAdapter(mHotNewsAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {
//        mHotNewsAdapter.setOnLoadMoreListener(() -> {
//           page ++;
//           hotArticle();
//        }, mRecyclerView);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshBus(toRefreshEvent event){
        if(getUserVisibleHint()){
            KLog.d("tag","我是热搜界面，我刷新了");
            mRecyclerView.scrollToPosition(0);
            smartRefreshLayout.autoRefresh();
        }
    }


}
