package com.qmkj.niaogebiji.module.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.ActicleAllBean;
import com.qmkj.niaogebiji.module.bean.AuthorArticleBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.FristActionBean;
import com.qmkj.niaogebiji.module.bean.IndexBulltin;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.ProBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.event.ProDoneEvent;
import com.qmkj.niaogebiji.module.event.toActionEvent;
import com.qmkj.niaogebiji.module.event.toFlashEvent;
import com.qmkj.niaogebiji.module.event.toRefreshEvent;
import com.qmkj.niaogebiji.module.event.toRefreshMoringEvent;
import com.qmkj.niaogebiji.module.widget.RecyclerViewNoBugLinearLayoutManager;
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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:用户文章界面
 *
 */
public class UserArticleFragment extends BaseLazyFragment {


    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    private int page = 1;
    LinearLayoutManager mLinearLayoutManager;
    FirstItemNewAdapter mFirstItemNewAdapter;
    List<MultiNewsBean> mAllList = new ArrayList<>();


    //通过此方式实例化Fragment
    public static UserArticleFragment getInstance(String chainId, String chainName) {
        UserArticleFragment newsItemFragment = new UserArticleFragment();
        Bundle args = new Bundle();
        args.putString("catid", chainId);
        args.putString("chainName", chainName);
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_article;
    }


    @Override
    protected boolean regEvent() {
        return true;
    }


    //传递uid
    private String  aid;
    @Override
    protected void initView() {
        aid = getArguments().getString("catid");
        initSamrtLayout();
        initLayout();
    }


    @Override
    protected void lazyLoadData() {
        getAuthorArticle();
    }

    //authorInfo中的id字段
    private void getAuthorArticle() {
        KLog.e("tag","page " + page);
        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");
        map.put("author_id",aid);
        map.put("page_size",10 + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getAuthorArticle(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<AuthorArticleBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<AuthorArticleBean> response) {

                        if(null != smartRefreshLayout){
                            mIsRefreshing = false;
                            smartRefreshLayout.finishRefresh();
                        }

                        AuthorArticleBean bean = response.getReturn_data();
                        if(bean != null){
                            List<RecommendBean.Article_list> mList= bean.getList();
                            setArticleData(mList);
                        }
                    }
                });
    }


    private void setArticleData(List<RecommendBean.Article_list> articles) {
        if(1 == page){
            if(null != articles && !articles.isEmpty()){
                setActicleData(articles);
                mFirstItemNewAdapter.setNewData(mAllList);
                //如果第一次返回的数据不满10条，则显示无更多数据
                if(articles.size() < Constant.SEERVER_NUM){
                    mFirstItemNewAdapter.loadMoreComplete();
                    mFirstItemNewAdapter.loadMoreEnd();
                }
            }else{
                //第一次加载无数据
                ll_empty.setVisibility(View.VISIBLE);
                ((TextView)ll_empty.findViewById(R.id.tv_empty)).setText("暂无内容");
                ((ImageView)ll_empty.findViewById(R.id.iv_empty)).setImageResource(R.mipmap.icon_empty_article);
                mRecyclerView.setVisibility(View.GONE);
            }
        }else{
            //已为加载更多有数据
            if(articles != null && articles.size() > 0){
                setActicleData(articles);
                mFirstItemNewAdapter.addData(tempList);
                mFirstItemNewAdapter.loadMoreComplete();
            }else{
                //已为加载更多无更多数据
                mFirstItemNewAdapter.loadMoreEnd();
            }
        }
    }


    List<MultiNewsBean> tempList = new ArrayList<>();
    private void setActicleData(List<RecommendBean.Article_list> article_lists) {

        tempList.clear();
        RecommendBean.Article_list itemBean;
        MultiNewsBean bean1 ;

        String pic_type;
        for (int i = 0; i < article_lists.size(); i++) {
            itemBean = article_lists.get(i);
            bean1 = new MultiNewsBean();
            pic_type = article_lists.get(i).getPic_type();
            if("1".equals(pic_type)){
                bean1.setItemType(1);
            }else if("2".equals(pic_type)){
                bean1.setItemType(3);
            }else if("3".equals(pic_type)){
                bean1.setItemType(2);
            }else{
                bean1.setItemType(1);
            }
            bean1.setNewsActicleList(itemBean);
            tempList.add(bean1);
        }

        if(page == 1){
            mAllList.addAll(tempList);
        }
    }


    private boolean mIsRefreshing;

    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
        });

        mRecyclerView.setOnTouchListener(
                (v, event) -> {
                    if (mIsRefreshing) {
                        return true;
                    } else {
                        return false;
                    }
                }
        );
    }


    //初始化布局管理器
    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mFirstItemNewAdapter = new FirstItemNewAdapter(mAllList);
        mRecyclerView.setAdapter(mFirstItemNewAdapter);
        //false 这个方法主要是设置RecyclerView不处理滚动事件(主要用于嵌套中)
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);

        mFirstItemNewAdapter.setOnLoadMoreListener(() -> {
            ++page;
            getAuthorArticle();
        },mRecyclerView);


        //点击事件
        mFirstItemNewAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","点击的是 position " + position );

            if(StringUtil.isFastClick()){
                return;
            }
            String aid = mFirstItemNewAdapter.getData().get(position).getNewsActicleList().getAid();
            if (!TextUtils.isEmpty(aid)) {
                UIHelper.toNewsDetailActivity(getActivity(), aid);
            }

        });


//        headView = LayoutInflater.from(this).inflate(R.layout.item_head_author_detail,null);
//
//        mFirstItemAdapter.setHeaderView(headView);
    }




}
