package com.qmkj.niaogebiji.module.fragment;

import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.HotAuthorAdapter;
import com.qmkj.niaogebiji.module.adapter.HotNewsAdapter;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
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
 * 创建时间 2020-2-12
 * 描述:作者热榜
 * 参考 收藏作者列表
 */
public class HotAuthorFragment extends BaseLazyFragment {

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    @BindView(R.id.iv_empty)
    ImageView iv_empty;

    @BindView(R.id.tv_empty)
    TextView tv_empty;



    private int page = 1;
    //适配器 -- 需手动排序
    HotAuthorAdapter mHotAuthorAdapter;
    //组合集合
    List<MultiNewsBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;



    public static HotAuthorFragment getInstance(String chainId, String chainName) {
        HotAuthorFragment newsItemFragment = new HotAuthorFragment();
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


    private List<AuthorBean.Author> mAuthors;
    private void hotauthor() {
        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().hotauthor(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<AuthorBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<AuthorBean> response) {

                        AuthorBean mAuthorBean = response.getReturn_data();
                        if(null != mAuthorBean){
                            //得到集合
                            mAuthors =  mAuthorBean.getList();
                            if(1 == page){
                                if(mAuthors != null && !mAuthors.isEmpty()){


                                    //加载原有数据
                                    setData2(mAuthors);
                                    mHotAuthorAdapter.setNewData(mAllList);

                                    //如果第一次返回的数据不满10条，则显示无更多数据
                                    if(mAuthors.size() < Constant.SEERVER_NUM){
                                        mHotAuthorAdapter.loadMoreEnd();
                                    }

                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    ll_empty.setVisibility(View.GONE);



                                }else{
                                    mRecyclerView.setVisibility(View.GONE);
                                    ll_empty.setVisibility(View.VISIBLE);
                                }

                            }else{
                                //已为加载更多有数据
                                if(mAuthors != null && mAuthors.size() > 0){
                                    mHotAuthorAdapter.loadMoreComplete();
                                    mHotAuthorAdapter.addData(mAuthors);
                                }else{
                                    //已为加载更多无更多数据
                                    mHotAuthorAdapter.loadMoreEnd();
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
                    public void onHintError(String return_code, String errorMes) {
                        super.onHintError(return_code, errorMes);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                    }
                });
    }

    List<CircleBean> teList = new ArrayList<>();
    private void setData2(List<AuthorBean.Author> authors) {
        teList.clear();
        if(page == 1){
            mAllList.addAll(teList);
        }
    }


    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected void lazyLoadData() {
//        getData();

        hotauthor();
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
        mHotAuthorAdapter.setNewData(mAllList);

//        mRecyclerView.setVisibility(View.GONE);
//        ll_empty.setVisibility(View.VISIBLE);
//        iv_empty.setImageResource(R.mipmap.icon_empty_article);
//        tv_empty.setText("空态");

    }


    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
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
        mHotAuthorAdapter = new HotAuthorAdapter(mAllList);
        mRecyclerView.setAdapter(mHotAuthorAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {
        mHotAuthorAdapter.setOnLoadMoreListener(() -> {
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
