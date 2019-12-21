package com.qmkj.niaogebiji.module.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.AuthorAdapter;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.SearchAllAuthorBean;
import com.qmkj.niaogebiji.module.event.SearchWordEvent;
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
 * 创建时间 2019-11-11
 *  搜索作者
 */
public class SearchAuthorItemFragment extends BaseLazyFragment {


    private int page = 1;
    private int pageSize = 10;
    private String myKeyword;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    //适配器
    AuthorAdapter mAuthorAdapter;
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    private List<AuthorBean.Author> mList = new ArrayList<>();

    //通过此方式实例化Fragment
    public static SearchAuthorItemFragment getInstance(String chainId, String chainName) {
        SearchAuthorItemFragment newsItemFragment = new SearchAuthorItemFragment();
        Bundle args = new Bundle();
        args.putString("catid", chainId);
        args.putString("chainName", chainName);
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }


    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.common_refresh;
    }


    @Override
    protected void initView() {
        initSamrtLayout();
        initLayout();
    }

    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mList.clear();
            page = 1;
            searchAuthor();
        });
    }

    @Override
    protected void lazyLoadData() {
        searchAuthor();
    }

    private void searchAuthor() {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",myKeyword);
        map.put("page",page + "");
        map.put("page_size",pageSize + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchAuthor(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SearchAllAuthorBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<SearchAllAuthorBean> response) {

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }

                        SearchAllAuthorBean temp  = response.getReturn_data();
                        if(null != temp){
                            List<AuthorBean.Author> tempList = temp.getList();
                            if(page == 1){
                                if(!tempList.isEmpty()){
                                    setData2(tempList);
                                    mAuthorAdapter.setNewData(mList);
                                    //如果第一次返回的数据不满10条，则显示无更多数据
                                    if(tempList.size() < Constant.SEERVER_NUM){
                                        mAuthorAdapter.loadMoreEnd();
                                    }
                                }else{
                                    KLog.d("tag","显示空布局");
                                    setEmpty(mAuthorAdapter);
                                }
                            }else{
                                //已为加载更多有数据
                                if(tempList != null && tempList.size() > 0){
                                    setData2(tempList);
                                    mAuthorAdapter.loadMoreComplete();
                                    mAuthorAdapter.addData(tempList);
                                }else{
                                    //已为加载更多无更多数据
                                    mAuthorAdapter.loadMoreComplete();
                                    mAuthorAdapter.loadMoreEnd();
                                }
                            }
                        }
                    }
                });
    }


    protected void setEmpty(BaseQuickAdapter adapter){
        //不需要可以配置加载更多
        adapter.disableLoadMoreIfNotFullPage();
        //TODO 预加载，当列表滑动到倒数第N个Item的时候(默认是1)回调onLoadMoreRequested方法
        adapter.setPreLoadNumber(2);
        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_empty,null);
        adapter.setEmptyView(emptyView);
        ((TextView)emptyView.findViewById(R.id.tv_empty)).setText("没有数据");
    }


    List<AuthorBean.Author> teList = new ArrayList<>();
    private void setData2(List<AuthorBean.Author> list) {
        teList.clear();
        //可能字段不一样，以前是name,现在给的是author
        AuthorBean.Author author;
        for (int i = 0; i < list.size(); i++) {
            author = new AuthorBean.Author();
            author.setId(list.get(i).getAid());
            author.setName(list.get(i).getAuthor());
            author.setImg(list.get(i).getPic());
            author.setIs_follow(list.get(i).getIs_follow());
            teList.add(author);
        }

        if(page == 1){
            mList.addAll(teList);
        }
    }


    //点击切换fragement会调用
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            //pause

        }else{
            //resume
        }
    }




    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mAuthorAdapter = new AuthorAdapter(mList);
        mRecyclerView.setAdapter(mAuthorAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {

        mAuthorAdapter.bindToRecyclerView(mRecyclerView);

    }


    @Override
    public void initData() {

    }


    //点击全部里的查看更多事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchWordEvent(SearchWordEvent event) {
        myKeyword = event.getWord();
        KLog.d("tag","myKeyword = " + myKeyword);
    }


}