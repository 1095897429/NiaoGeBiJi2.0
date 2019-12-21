package com.qmkj.niaogebiji.module.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.SearchEvent;
import android.view.View;

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
import com.qmkj.niaogebiji.module.adapter.FocusAdapter;
import com.qmkj.niaogebiji.module.adapter.SearchAllAdapter;
import com.qmkj.niaogebiji.module.bean.ActiclePeopleBean;
import com.qmkj.niaogebiji.module.bean.ActionBean;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
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
import com.qmkj.niaogebiji.module.event.PeopleFocusEvent;
import com.qmkj.niaogebiji.module.event.SearchWordEvent;
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
 * 创建时间 2019-11-18
 * 描述:搜索结果第一个界面
 *  *  1.搜索文章 + 人脉
 *  *  2.搜索动态
 *
 *  1.这里将chainName作为关键字对象传入
 */
public class SearchAllFragment extends BaseLazyFragment {

    private String myKeyword = "";

    //人脉 + 文章
    private List<RecommendBean.Article_list> mArticle_lists = new ArrayList<>();
    private List<RegisterLoginBean.UserInfo> mPeopleList  = new ArrayList<>();

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
        myKeyword = getArguments().getString("chainName");
        initSamrtLayout();
        initLayout();
    }


    @Override
    protected void initData() {
        searchArticlePeople();
    }

    @Override
    protected void lazyLoadData() {

    }
    //人脉(没有) + 文章(直接拿取)
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

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }

                        temp = response.getReturn_data();
                        if(null != temp){
                            mArticle_lists = temp.getArticle_list();
                            mPeopleList = temp.getRenmai_list();
                            setData1(mArticle_lists);
                            setData11(mPeopleList);
                        }

                        if(mPeopleList.isEmpty()){
                            searchPeople();
                        }else{
                            searchBlog();
                        }

                    }
                });
    }


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
                            setData8(temp.getList());
                        }
                        searchBlog();
                    }
                });
    }



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
                            setData4(temp.getList());
                        }
                        searchWiki();
                    }

                    @Override
                    public void onNetFail(String msg) {
                        KLog.d("tag","解析错误继续发送请求");
                        if("解析错误".equals(msg)){
                            searchWiki();
                        }
                    }
                });
    }

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
                            setData5( temp.getList());
                        }
                        searchMaterial();
                    }
                });
    }


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
                            setData6(temp.getList());
                        }
                        searchAuthor();
                    }
                });
    }


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

                        SearchAllAuthorBean temp  = response.getReturn_data();
                        if(null != temp){
                            setData7(temp.getList());
                        }
                    }
                });
    }




    private void setData7(List<AuthorBean.Author> authors) {

        List<AuthorBean.Author> mT = new ArrayList<>();
        mT.clear();
        //可能字段不一样，以前是name,现在给的是author
        AuthorBean.Author author;
        for (int i = 0; i < authors.size(); i++) {
            author = new AuthorBean.Author();
            author.setId(authors.get(i).getAid());
            author.setName(authors.get(i).getAuthor());
            author.setImg(authors.get(i).getPic());
            author.setIs_follow(authors.get(i).getIs_follow());
            mT.add(author);
        }

        tempMul.clear();
        if(null != authors &&  !authors.isEmpty()) {
            MultSearchBean bean1 ;
            bean1 = new MultSearchBean();
            bean1.setAuthorBeanList(mT);
            bean1.setItemType(SearchAllAdapter.AUTHOR);
            tempMul.add(bean1);
            mSearchAllAdapter.addData(tempMul);
        }
    }



    private void setData8(List<RegisterLoginBean.UserInfo> list) {
        tempMul.clear();
        if(null != list &&  !list.isEmpty()) {
            MultSearchBean bean1 ;
            bean1 = new MultSearchBean();
            bean1.setUserInfos(list);
            bean1.setItemType(SearchAllAdapter.PEOPLE);
            tempMul.add(bean1);
            mSearchAllAdapter.addData(tempMul);
        }
    }

    private void setData6(List<RecommendBean.Article_list> things) {
        tempMul.clear();
        if(null != things &&  !things.isEmpty()) {
            MultSearchBean bean1 ;
            bean1 = new MultSearchBean();
            bean1.setThings(things);
            bean1.setItemType(4);
            tempMul.add(bean1);
            mSearchAllAdapter.addData(tempMul);
        }
    }

    private void setData5(List<SearchAllBaiduBean.Wiki> wikis) {
        tempMul.clear();
        if(null != wikis &&  !wikis.isEmpty()) {
            MultSearchBean bean1 ;
            bean1 = new MultSearchBean();
            bean1.setWikis(wikis);
            bean1.setItemType(SearchAllAdapter.BAIDU);
            tempMul.add(bean1);
            mSearchAllAdapter.addData(tempMul);
        }
    }

    private void setData4(List<CircleBean> circleBeanList) {
        tempMul.clear();
        if(null != circleBeanList &&  !circleBeanList.isEmpty()) {
            MultSearchBean bean1 ;
            bean1 = new MultSearchBean();
            bean1.setCircleBeanList(circleBeanList);
            bean1.setItemType(SearchAllAdapter.DYNAMIC);
            tempMul.add(bean1);
            mSearchAllAdapter.addData(tempMul);
        }
    }


    private void setData11(List<RegisterLoginBean.UserInfo> peopleList) {
        tempMul.clear();
        if(null != peopleList &&  !peopleList.isEmpty()) {
            MultSearchBean bean1 ;
            bean1 = new MultSearchBean();
            bean1.setUserInfos(peopleList);
            bean1.setItemType(SearchAllAdapter.SEACHER_PEOPLE);
            tempMul.add(bean1);
            mSearchAllAdapter.addData(tempMul);
        }
    }

    private void setData1(List<RecommendBean.Article_list> article_lists){
        tempMul.clear();
        if(null != article_lists &&  !article_lists.isEmpty()) {
            MultSearchBean bean1 ;
            bean1 = new MultSearchBean();
            bean1.setNewsItemBeanList(article_lists);
            bean1.setItemType(SearchAllAdapter.SEACHER_GANHUO);
            tempMul.add(bean1);
            mSearchAllAdapter.addData(tempMul);
        }
    }


    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    SearchAllAdapter mSearchAllAdapter;
    //组合集合
    List<MultSearchBean> mAllList = new ArrayList<>();
    //临时数据
    List<MultSearchBean> tempMul = new ArrayList<>();
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

        mSearchAllAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            int  type = mSearchAllAdapter.getData().get(position).getItemType();
            switch (type){
                case SearchAllAdapter.GANHUO:
                    EventBus.getDefault().post(new LookMoreEvent(1));
                    break;
                case SearchAllAdapter.PEOPLE:
                    EventBus.getDefault().post(new LookMoreEvent(2));
                    break;
                case SearchAllAdapter.DYNAMIC:
                    EventBus.getDefault().post(new LookMoreEvent(3));
                    break;
                case SearchAllAdapter.BAIDU:
                    EventBus.getDefault().post(new LookMoreEvent(4));
                    break;
                case SearchAllAdapter.DATA:
                    EventBus.getDefault().post(new LookMoreEvent(5));
                    break;
                case SearchAllAdapter.AUTHOR:
                    EventBus.getDefault().post(new LookMoreEvent(6));
                    break;
                default:
            }
        });

    }


    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            searchArticlePeople();
        });
    }


    //第一次不会进入，等第二次就会走这个方法了
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchWordEvent(SearchWordEvent event) {
        myKeyword = event.getWord();
    }



    //点击全部里的查看更多事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPeopleFocusEvent(PeopleFocusEvent event) {
        KLog.d("tag","用户的uid是 "  + event.getUid() +  " 状态  "  + event.getStatus());

        int  position  = -1 ;
        int  firstPosition = -1;
        for (int i = 0; i < mAllList.size(); i++) {
            if(null != mAllList.get(i).getUserInfos()){
                int size = mAllList.get(i).getUserInfos().size();
                for (int j = 0; j < size; j++) {
                    if(event.getUid().equals(mAllList.get(i).getUserInfos().get(j).getUid())){
                        position = j;
                        firstPosition = i;
                        break;
                    }
                }
            }
        }

        if(position != -1){
            // 0未关注 1已关注 2我屏蔽了别人 3别人屏蔽了我
            mSearchAllAdapter.getData().get(firstPosition).getUserInfos().get(position).setFollow_status(event.getStatus());
            mSearchAllAdapter.notifyItemChanged(firstPosition);
        }


    }



}
