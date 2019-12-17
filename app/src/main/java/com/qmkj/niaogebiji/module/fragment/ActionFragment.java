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
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.ActionAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.ActionBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MoringNewsBean;
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
 * 创建时间 2019-11-13
 * 描述:活动fragment
 */
public class ActionFragment extends BaseLazyFragment {

    private int page = 1;
    //活动类型：0-全部，1-线上，2-线下
    private String type = "0";
    private List<ActionBean.Act_list> mAct_lists = new ArrayList<>();

    public static ActionFragment getInstance(String chainId, String chainName) {
        ActionFragment actionItemFragment = new ActionFragment();
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
        return R.layout.first_action;
    }

    @Override
    protected void initView() {
        initSamrtLayout();
        initLayout();
    }

    @Override
    protected void initData() {
    }


    @Override
    protected void lazyLoadData() {
        activitiesList();
    }

    private void activitiesList() {
        Map<String,String> map = new HashMap<>();
        map.put("page",page +"");
        map.put("type",type + "");
        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().activitiesList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<ActionBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<ActionBean> response) {

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }

                        ActionBean mActionBean = response.getReturn_data();
                        if(null != mActionBean){
                            mAct_lists = mActionBean.getAct_list();
                            if(page == 1){
                                mActionAdapter.setNewData(mAct_lists);
                            }else{
                                if(mAct_lists != null && !mAct_lists.isEmpty()){
                                    mActionAdapter.addData(mAct_lists);
                                    mActionAdapter.loadMoreComplete();
                                }else{
                                    //已为加载更多无更多数据
                                    mActionAdapter.loadMoreEnd();
                                }
                            }
                        }
                    }

                    @Override
                    public void onNetFail(String msg) {
                        super.onNetFail(msg);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                    }

                    @Override
                    public void onHintError(String errorMes) {
                        super.onHintError(errorMes);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                    }
                });
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

    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAct_lists.clear();
            page = 1;
            activitiesList();
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
        mActionAdapter = new ActionAdapter(mAct_lists);
        mRecyclerView.setAdapter(mActionAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {

        mActionAdapter.setOnLoadMoreListener(() -> {
            ++page;
            activitiesList();
        },mRecyclerView);

        //相关资料
        mActionAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            String aid = mActionAdapter.getData().get(position).getId();
            if(!TextUtils.isEmpty(aid)){
                UIHelper.toDataInfoActivity(getActivity(),aid);
            }

        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshBus(toRefreshEvent event){
        if(getUserVisibleHint()){
            KLog.d("tag","我是活动界面，我刷新了");
            mRecyclerView.scrollToPosition(0);
            smartRefreshLayout.autoRefresh();
        }
    }
}
