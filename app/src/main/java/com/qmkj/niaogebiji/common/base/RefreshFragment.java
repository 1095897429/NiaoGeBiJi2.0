package com.qmkj.niaogebiji.common.base;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;


/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:
 */
public abstract  class RefreshFragment extends BaseLazyFragment {

    //列表控件
    protected RecyclerView mRecyclerView;
    //下拉刷新控件
    protected SmartRefreshLayout smartRefreshLayout;


    @Override
    protected void initView() {
        mRecyclerView = getActivity().findViewById(R.id.recycler);
        smartRefreshLayout = getActivity().findViewById(R.id.smartRefreshLayout);
        initSamrtLayout();
    }


    private void initSamrtLayout() {

        smartRefreshLayout.setEnableLoadMore(false);

        //下拉刷新
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            //接口回调
            KLog.d("tag","接口回调");
        });

    }



}
