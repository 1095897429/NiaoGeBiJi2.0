package com.qmkj.niaogebiji.module.fragment;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.adapter.HotNewsAdapter;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;

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


    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
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
        getData();

        initLayout();
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
    }



}
