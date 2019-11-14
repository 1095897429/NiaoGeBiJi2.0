package com.qmkj.niaogebiji.module.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.base.RefreshFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.adapter.ActionAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.ActionBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MoringNewsBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:活动fragment
 */
public class ActionFragment extends BaseLazyFragment {



    public static ActionFragment getInstance(String chainId, String chainName) {
        ActionFragment actionItemFragment = new ActionFragment();
        Bundle args = new Bundle();
        args.putString("chainId", chainId);
        args.putString("chainName", chainName);
        actionItemFragment.setArguments(args);
        return actionItemFragment;
    }



    @Override
    protected int getLayoutId() {
        return R.layout.first_action;
    }

    @Override
    protected void initView() {
        initLayout();
        initSamrtLayout();
    }

    @Override
    protected void initData() {
        for (int i = 0; i < 10; i++) {
            mList.add(new ActionBean());
        }
    }


    //列表控件
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //下拉刷新控件
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;


    //适配器
    ActionAdapter mActionAdapter;
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;
    //集合
    List<ActionBean> mList = new ArrayList<>();

    private void initSamrtLayout() {

        smartRefreshLayout.setEnableLoadMore(false);

        //下拉刷新
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            //接口回调
            KLog.d("tag","接口回调");
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
        mActionAdapter = new ActionAdapter(mList);
        mRecyclerView.setAdapter(mActionAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {
        //相关资料
        mActionAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            UIHelper.toDataInfoActivity(getActivity());
        });
    }

}
