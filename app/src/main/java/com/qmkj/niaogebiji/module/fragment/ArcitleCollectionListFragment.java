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

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.adapter.NewsCollectItemAdapter;
import com.qmkj.niaogebiji.module.bean.CollectArticleBean;
import com.qmkj.niaogebiji.module.event.CollectionEvent;
import com.qmkj.niaogebiji.module.event.toActionEvent;
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
 * 创建时间 2019-11-25
 * 描述:文章收藏
 */
public class ArcitleCollectionListFragment extends BaseLazyFragment {

    //收藏对象类型：1-文章
    private String tartget_id = "1";
    private int page = 1;
    private CollectArticleBean mCollectArticleBean;
    private List<CollectArticleBean.Collect_list> mCollectLists = new ArrayList<>();
    private List<CollectArticleBean.Collect_list> mAllList = new ArrayList<>();
    private NewsCollectItemAdapter mNewsCollectItemAdapter;

    private CollectArticleBean.Collect_list mCollectList;


    public static ArcitleCollectionListFragment getInstance(String chainId, String chainName) {
        ArcitleCollectionListFragment actionItemFragment = new ArcitleCollectionListFragment();
        Bundle args = new Bundle();
        args.putString("chainId", chainId);
        args.putString("chainName", chainName);
        actionItemFragment.setArguments(args);
        return actionItemFragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_acticle_collection;
    }

    @Override
    protected void initView() {
        initLayout();
        initSamrtLayout();
    }

    @Override
    protected void initData() {
        favoriteList();
    }

    private void favoriteList() {
        Map<String,String> map = new HashMap<>();
        map.put("target_type",tartget_id);
        map.put("page_no",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().favoriteList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<CollectArticleBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<CollectArticleBean> response) {

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }

                        mCollectArticleBean = response.getReturn_data();
                        if(null != mCollectArticleBean){
                            mCollectLists = mCollectArticleBean.getList();

                            if(1 == page){
                                setData(mCollectLists);
                                mNewsCollectItemAdapter.setNewData(mAllList);
                                //如果第一次返回的数据不满10条，则显示无更多数据
                                if(mNewsCollectItemAdapter.getData().size() < Constant.SEERVER_NUM){
                                    mNewsCollectItemAdapter.loadMoreEnd();
                                }
                            }else{
                                //已为加载更多有数据
                                if(mCollectLists != null && mCollectLists.size() > 0){
                                    setData(mCollectLists);
                                    mNewsCollectItemAdapter.loadMoreComplete();
                                    mNewsCollectItemAdapter.addData(templist);
                                }else{
                                    //已为加载更多无更多数据
                                    mNewsCollectItemAdapter.loadMoreEnd();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                    }

                    @Override
                    public void onNetFail(String msg) {
                        super.onNetFail(msg);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                    }
                });

    }

    private List<CollectArticleBean.Collect_list> templist = new ArrayList<>();
    private void setData(List<CollectArticleBean.Collect_list> collectLists) {
        templist.clear();
        templist.addAll(collectLists);
        if(page == 1){
            mAllList.addAll(templist);
        }
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
        mNewsCollectItemAdapter = new NewsCollectItemAdapter(mAllList);
        mRecyclerView.setAdapter(mNewsCollectItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {

        mNewsCollectItemAdapter.setOnLoadMoreListener(() -> {
            ++page;
            favoriteList();
        },mRecyclerView);

        //不需要可以配置加载更多
        mNewsCollectItemAdapter.disableLoadMoreIfNotFullPage();
        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_empty,null);
        mNewsCollectItemAdapter.setEmptyView(emptyView);
        ((TextView)emptyView.findViewById(R.id.tv_empty)).setText("您还没有关注的文章哦～");

        mNewsCollectItemAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            KLog.d("点击图片，请求取消接口，刷新界面");
            if(null != mAllList && !mAllList.isEmpty()){
                mCollectList = mAllList.get(position);
                myPosition = position;
                showCancelFocusDialog();
            }
        });

        //长按事件
        mNewsCollectItemAdapter.setOnItemLongClickListener((adapter, view, position) -> {

            KLog.d("点击图片，请求取消接口，刷新界面");
            if(null != mCollectLists && !mCollectLists.isEmpty()){
                mCollectList = mAllList.get(position);
                myPosition = position;
                showCancelFocusDialog();
            }

            return false;
        });


        mNewsCollectItemAdapter.setOnItemClickListener((adapter, view, position) -> {
            if(StringUtil.isFastClick()){
                return;
            }
            String aid = mNewsCollectItemAdapter.getData().get(position).getAid();
            if (!TextUtils.isEmpty(aid)) {
                UIHelper.toNewsDetailActivity(getActivity(), aid);
            }
        });


    }


    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            favoriteList();
        });
    }



    /** --------------------------------- 取消收藏  ---------------------------------*/

    private int myPosition ;

    private void unfavorite() {
        Map<String,String> map = new HashMap<>();
        if(!TextUtils.isEmpty(mCollectList.getAid())){
            map.put("target_id",mCollectList.getAid());
        }

        map.put("target_type",tartget_id);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().acticleUnfavorite(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse response) {
                        mNewsCollectItemAdapter.getData().remove(myPosition);
                        mNewsCollectItemAdapter.updateData(myPosition);

                        KLog.d("tag","移除之后列表数据条数 " + mNewsCollectItemAdapter.getData().size());
                    }

                });
    }


    @Override
    protected boolean regEvent() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(CollectionEvent event){
        page = 1;
        mCollectLists.clear();
        favoriteList();
    }



    public void showCancelFocusDialog(){

        final FocusAlertDialog iosAlertDialog = new FocusAlertDialog(getActivity()).builder();
        iosAlertDialog.setPositiveButton("删除", v -> {
            unfavorite();
        }).setNegativeButton("再想想", v -> {}).setMsg("删除这篇收藏？").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }


}
