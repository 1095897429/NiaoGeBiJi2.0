package com.qmkj.niaogebiji.module.fragment;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.adapter.HotNewsAdapter;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.event.toRefreshEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:
 */
public class HotNewsFragment extends BaseLazyFragment {

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

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
    }


    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected void lazyLoadData() {
        getData();
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
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            getData();
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
        mHotNewsAdapter.setOnLoadMoreListener(() -> {
           page ++;
           getData();
        }, mRecyclerView);
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
