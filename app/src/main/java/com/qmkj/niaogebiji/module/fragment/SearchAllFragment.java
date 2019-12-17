package com.qmkj.niaogebiji.module.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.SearchEvent;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.FocusAdapter;
import com.qmkj.niaogebiji.module.adapter.SearchAllAdapter;
import com.qmkj.niaogebiji.module.bean.ActiclePeopleBean;
import com.qmkj.niaogebiji.module.bean.ActionBean;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MultSearchBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.SearchAllActicleBean;
import com.qmkj.niaogebiji.module.bean.SearchAllAuthorBean;
import com.qmkj.niaogebiji.module.bean.SearchAllBaiduBean;
import com.qmkj.niaogebiji.module.bean.SearchAllCircleBean;
import com.qmkj.niaogebiji.module.bean.SearchAllPeopleBean;
import com.qmkj.niaogebiji.module.bean.SearchResultBean;
import com.qmkj.niaogebiji.module.event.LookMoreEvent;
import com.qmkj.niaogebiji.module.event.SearchWordEvent;
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
 * 创建时间 2019-11-18
 * 描述:搜索结果第一个界面
 */
public class SearchAllFragment extends BaseLazyFragment {

    //搜索类型：1-文章。2-作者
    private String mType = "1";
    private String myKeyword = "抖音";

    private SearchResultBean mSearchResultBean;
    //人脉 + 文章
    private List<RecommendBean.Article_list> mArticle_lists;
    private List<RegisterLoginBean.UserInfo> mUserInfos;


    public static SearchAllFragment getInstance(String chainId, String chainName) {
        SearchAllFragment newsItemFragment = new SearchAllFragment();
        Bundle args = new Bundle();
        args.putString("chainId", chainId);
        args.putString("chainName", chainName);
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_all;
    }


    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected void initView() {
        initSamrtLayout();
        initLayout();
    }




    @Override
    protected void lazyLoadData() {
//        searchArticlePeople();
//        searchArticle();
//        searchPeople();
        searchAuthor();
    }

    ActiclePeopleBean temp;
    private void searchArticlePeople() {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",myKeyword);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchArticlePeople(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<ActiclePeopleBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<ActiclePeopleBean> response) {

                        temp = response.getReturn_data();
                        if(null != temp){
                            mArticle_lists = temp.getArticle_list();
                            mUserInfos = temp.getRenmai_list();
                            setData1();
                        }
                    }
                });
    }


    private List<RecommendBean.Article_list> mArticleListList;
    private void searchArticle() {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",myKeyword);
        map.put("page",page + "");
        map.put("page_size",page_size + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchArticle(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SearchAllActicleBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<SearchAllActicleBean> response) {
                        SearchAllActicleBean temp  = response.getReturn_data();
                        if(null != temp){
                            mArticleListList = temp.getList();
                            setData2();
                        }

                    }
                });
    }


    private List<RegisterLoginBean.UserInfo> mUserInfoList;
    private void searchPeople() {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",myKeyword);
        map.put("page",page + "");
        map.put("page_size",page_size + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchPeople(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SearchAllPeopleBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<SearchAllPeopleBean> response) {
                        SearchAllPeopleBean temp  = response.getReturn_data();
                        if(null != temp){
                            mUserInfoList = temp.getList();
                            setData3();
                        }

                    }
                });
    }

    private List<CircleBean> mCircleBeanList;
    private void searchBlog() {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",myKeyword);
        map.put("page",page + "");
        map.put("page_size",page_size + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SearchAllCircleBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<SearchAllCircleBean> response) {
                        SearchAllCircleBean temp  = response.getReturn_data();
                        if(null != temp){
                            mCircleBeanList = temp.getList();
                            setData4();
                        }

                    }
                });
    }

    private List<SearchAllBaiduBean.Wiki> mWikis;
    private void searchWiki() {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",myKeyword);
        map.put("page",page + "");
        map.put("page_size",page_size + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchWiki(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SearchAllBaiduBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<SearchAllBaiduBean> response) {
                        SearchAllBaiduBean temp  = response.getReturn_data();
                        if(null != temp){
                            mWikis = temp.getList();
                            setData5();
                        }

                    }
                });
    }


    private List<RecommendBean.Article_list> mThings;
    private void searchMaterial() {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",myKeyword);
        map.put("page",page + "");
        map.put("page_size",page_size + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchMaterial(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SearchAllActicleBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<SearchAllActicleBean> response) {
                        SearchAllActicleBean temp  = response.getReturn_data();
                        if(null != temp){
                            mThings = temp.getList();
                            setData6();
                        }

                    }
                });
    }


    private List<SearchAllAuthorBean.SearchAuthor> mSearchAuthors;
    private void searchAuthor() {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",myKeyword);
        map.put("page",page + "");
        map.put("page_size",page_size + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchAuthor(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SearchAllAuthorBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<SearchAllAuthorBean> response) {
                        SearchAllAuthorBean temp = response.getReturn_data();
                        if(null != temp){
                            mSearchAuthors = temp.getList();
                            setData7();
                        }
                    }
                });
    }

    private void setData7() {

    }

    private void setData6() {

    }

    private void setData5() {

    }

    private void setData4() {

    }


    private void setData3() {

    }

    private void setData2() {

    }

    private void setData1() {
        if(null != mArticle_lists &&  !mArticle_lists.isEmpty()){

        }
    }


    private void getData() {

        List<NewsItemBean>  list = new ArrayList<>();
        NewsItemBean newsItemBean;
        MultSearchBean bean1 ;

        //模拟第1条干货
        bean1 = new MultSearchBean();
        bean1.setItemType(1);
        for (int j = 0; j < 3; j++) {
            newsItemBean = new NewsItemBean();
            list.add(newsItemBean);
        }
        bean1.setNewsItemBeanList(list);
        mAllList.add(bean1);


        //模拟第2条活动
        MultSearchBean bean2 ;
        ActionBean.Act_list actionBean;
        List<ActionBean.Act_list>  list2 = new ArrayList<>();
        bean2 = new MultSearchBean();
        bean2.setItemType(2);
        for (int j = 0; j < 2; j++) {
            actionBean = new ActionBean.Act_list();
            list2.add(actionBean);
        }
        bean2.setActionBeanList(list2);
        mAllList.add(bean2);

        //模拟第3条快讯
        MultSearchBean bean3 ;
        FlashBulltinBean.BuilltinBean flashBulltinBean;
        List<FlashBulltinBean.BuilltinBean>  list3 = new ArrayList<>();
        bean3 = new MultSearchBean();
        bean3.setItemType(3);
        for (int j = 0; j < 2; j++) {
            flashBulltinBean = new FlashBulltinBean.BuilltinBean();
            list3.add(flashBulltinBean);
        }
        bean3.setFlashBulltinBeanList(list3);
        mAllList.add(bean3);


        //模拟第4条快讯
        MultSearchBean bean4 ;
        bean4 = new MultSearchBean();
        bean4.setItemType(4);
        mAllList.add(bean4);

        //模拟第4条快讯
        MultSearchBean bea54 ;
        bea54 = new MultSearchBean();
        bea54.setItemType(5);
        mAllList.add(bea54);


        //模拟第4条快讯
        MultSearchBean bean6 ;
        bean6 = new MultSearchBean();
        bean6.setItemType(6);
        mAllList.add(bean6);


        //模拟第4条快讯
        MultSearchBean bean7 ;
        bean7 = new MultSearchBean();
        bean7.setItemType(7);
        mAllList.add(bean7);


        mSearchAllAdapter.setNewData(mAllList);
    }


    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    SearchAllAdapter mSearchAllAdapter;
    //组合集合
    List<MultSearchBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;
    private int page = 1;
    private int page_size = 10;

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mSearchAllAdapter = new SearchAllAdapter(mAllList);
        mRecyclerView.setAdapter(mSearchAllAdapter);

        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);

        initEvent();

    }

    private void initEvent() {

        mSearchAllAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.more_author:
                    UIHelper.toAuthorListActivity(getActivity());
                    break;
                default:
            }
        });

        mSearchAllAdapter.setToMoreListenerListener((partPosition, des) -> {
            if(partPosition  == SearchAllAdapter.GANHUO){
                KLog.d("tag","点击的干货查看更多 ");
                EventBus.getDefault().post(new LookMoreEvent(1));
            }else if(partPosition  == SearchAllAdapter.ACTION){
                KLog.d("tag","点击的活动查看更多 ");
                EventBus.getDefault().post(new LookMoreEvent(2));
            }else if(partPosition  == SearchAllAdapter.FLASH){
                KLog.d("tag","点击的快讯查看更多 ");
                EventBus.getDefault().post(new LookMoreEvent(3));
            }
        });
    }


    private void initSamrtLayout() {
        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setEnableLoadMore(false);
    }


    //点击全部里的查看更多事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchWordEvent(SearchWordEvent event) {
        myKeyword = event.getWord();
        KLog.d("tag","myKeyword = " + myKeyword);
    }



}
