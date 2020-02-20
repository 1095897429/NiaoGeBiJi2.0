package com.qmkj.niaogebiji.module.fragment;

import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.module.adapter.TopicSelectAdapter;
import com.qmkj.niaogebiji.module.bean.TopicBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-13
 * 描述:话题选择Fragment
 */
public class TopicSelectFragment extends BaseLazyFragment {

    @BindView(R.id.tex)
    TextView tex;

    List<TopicBean> list =  new ArrayList<>();
    TopicSelectAdapter mTopicSelectAdapter;
    String typename;

    public static TopicSelectFragment getInstance(String chainName) {
        TopicSelectFragment newsItemFragment = new TopicSelectFragment();
        Bundle args = new Bundle();
        args.putString("typename", chainName);
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_topic;
    }

    @Override
    protected void initView() {
        typename = getArguments().getString("typename");
        tex.setText(typename);
        initLayout();

    }


    @Override
    protected void lazyLoadData() {
        getData();
    }

    private void getData() {

        TopicBean bean;
        for (int i = 0; i < 10; i++) {
            bean = new TopicBean();
            bean.setName(typename);
            list.add(bean);
        }

        mTopicSelectAdapter.setNewData(list);
    }



    /** --------------------------------- 通用的配置  ---------------------------------*/
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mTopicSelectAdapter = new TopicSelectAdapter(list);
        mRecyclerView.setAdapter(mTopicSelectAdapter);
        //禁用change动画
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
    }
}
