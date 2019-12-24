package com.qmkj.niaogebiji.module.fragment;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.NewsCollectItemAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolRecommentItemAdapter;
import com.qmkj.niaogebiji.module.bean.CollectArticleBean;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.bean.ToolNewBean;
import com.qmkj.niaogebiji.module.event.CollectionEvent;
import com.qmkj.niaogebiji.module.event.ToolChangeEvent;
import com.qmkj.niaogebiji.module.event.ToolHomeChangeEvent;
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

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-28
 * 描述:推荐工具
 */
public class ToolRecommentListFragment extends BaseLazyFragment {

    private String cate = "0";
    private String chainId = "0";
    private int page = 1;
    private ToolBean mToolBean;
    private List<ToolBean> mLists = new ArrayList<>();

    private ToolRecommentItemAdapter mToolRecommentItemAdapter;


    public static ToolRecommentListFragment getInstance(String chainId, String chainName) {
        ToolRecommentListFragment actionItemFragment = new ToolRecommentListFragment();
        Bundle args = new Bundle();
        args.putString("chainId", chainId);
        args.putString("chainName", chainName);
        actionItemFragment.setArguments(args);
        return actionItemFragment;
    }


    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tool_recommend_list;
    }

    @Override
    protected void initView() {
        chainId = getArguments().getString("chainId");
        initLayout();
        initSamrtLayout();
    }


    @Override
    protected void initData() {
        toollist();
    }

    private void toollist() {
        Map<String,String> map = new HashMap<>();
        map.put("cate",cate);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().toollist(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<ToolBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<ToolBean>> response) {
                        KLog.d("tag","response " + response.getReturn_code());

                        if(smartRefreshLayout != null){
                            smartRefreshLayout.finishRefresh();
                        }

                        mLists = response.getReturn_data();
                        setData();
                    }
                });
    }


    private void setData() {
        if(!mLists.isEmpty()){
            mToolRecommentItemAdapter.setNewData(mLists);
        }else{
            View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_empty,null);
            mToolRecommentItemAdapter.setEmptyView(emptyView);
            ((TextView)emptyView.findViewById(R.id.tv_empty)).setText("没有推荐工具哦～");
        }
    }

    private void favoriteList() {

        ToolBean bean;
        for (int i = 0; i < 4; i++) {
            bean = new ToolBean();
           if(0 == i){
               bean.setMes("我是第一个");
           }else{
               bean.setMes("10月31日，格力电器公告拟修订公司章程，其中，经营范围新增了「研发、制造、销售新能源发电产品、储能系统及充电桩」的内容");
           }
            mLists.add(bean);
        }


        mToolRecommentItemAdapter.setNewData(mLists);

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
        mToolRecommentItemAdapter = new ToolRecommentItemAdapter(mLists);
        mRecyclerView.setAdapter(mToolRecommentItemAdapter);
        //禁用change动画
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {

//        mToolRecommentItemAdapter.setOnLoadMoreListener(() -> {
//            ++page;
//            favoriteList();
//        },mRecyclerView);


        mToolRecommentItemAdapter.bindToRecyclerView(mRecyclerView);

    }


    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mLists.clear();
            page = 1;
            toollist();
        });
    }


    //点击上方标签 更改下面数据源
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToolChangeEvent(ToolChangeEvent event) {

        KLog.d("tag","发送请求的cateid 是 " + event.getName() + " ");

        cate = event.getName();
        toollist();
    }


    }
