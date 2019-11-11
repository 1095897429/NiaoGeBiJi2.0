package com.qmkj.niaogebiji.module.fragment;


import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;
import com.qmkj.niaogebiji.module.widget.MyLoadMoreView;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

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
 * 创建时间 2019-11-11
 * 描述：快讯的Fragment
 */
public class FlashFragment extends BaseLazyFragment  {



    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLinearLayoutManager;


    /** --------------------------------- 快讯列表  ---------------------------------*/
    private int page = 1;
    private FlashItemAdapter mFlashItemAdapter;
    private List<FlashBulltinBean.BuilltinBean> mBuilltinBeans = new ArrayList<>();
    //单体Bean
    private FlashBulltinBean.BuilltinBean mTempBuilltinBean;

    //大的Bean
    private FlashBulltinBean mFlashBulltinBean;

    //记录当前点击的索引
    private int myPosition;


    public static FlashFragment getInstance(String chainId, String chainName) {
        FlashFragment newsItemFragment = new FlashFragment();
        Bundle args = new Bundle();
        args.putString("catid", chainId);
        args.putString("chainName", chainName);
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_flash;
    }

    @Override
    protected void initView() {

    }


    @Override
    protected void initData() {
        getBulletinList();
    }

    private void initLayout() {

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        //设置适配器
        mFlashItemAdapter = new FlashItemAdapter(mBuilltinBeans);
        //禁用change动画
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //设置Adapter
        mRecyclerView.setAdapter(mFlashItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);


        mFlashItemAdapter.setLoadMoreView(new MyLoadMoreView());

        mFlashItemAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                ++page;
                getBulletinList();
            }
        },mRecyclerView);

    }

    private void getBulletinList() {

        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");

        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().getBulletinList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<FlashBulltinBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<FlashBulltinBean> response) {

                        mFlashBulltinBean = response.getReturn_data();
                        if(null != mFlashBulltinBean){

                            mBuilltinBeans = mFlashBulltinBean.getList();
                            //已为第一次进入
                            if(mFlashItemAdapter == null){
                                initLayout();
                            }else{
                                //已为加载更多有数据
                                if(mBuilltinBeans != null && mBuilltinBeans.size() > 0){
                                    mFlashItemAdapter.loadMoreComplete();
                                    mFlashItemAdapter.addData(mBuilltinBeans);
                                }else{
                                    //已为加载更多无更多数据
                                    mFlashItemAdapter.loadMoreEnd();
                                }

                            }

                        }
                    }

                });
    }


}
