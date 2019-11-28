package com.qmkj.niaogebiji.module.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.activity.AuthorListActivity;
import com.qmkj.niaogebiji.module.adapter.AuthorAdapter;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.event.UpdateHomeListEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-25
 * 描述:关注作者
 */
public class FocusAuthorListFragment extends BaseLazyFragment {

    public static FocusAuthorListFragment getInstance(String chainId, String chainName) {
        FocusAuthorListFragment actionItemFragment = new FocusAuthorListFragment();
        Bundle args = new Bundle();
        args.putString("chainId", chainId);
        args.putString("chainName", chainName);
        actionItemFragment.setArguments(args);
        return actionItemFragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_focus_authors;
    }

    @Override
    protected void initView() {
        initLayout();
        initSamrtLayout();

    }


    @Override
    protected void lazyLoadData() {
        myFollowList();
    }

    private void myFollowList() {

        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().myFollowList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<AuthorBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<AuthorBean> response) {

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }

                        AuthorBean mAuthorBean = response.getReturn_data();
                        if(null != mAuthorBean){
                            mAuthors =  mAuthorBean.getList();


                            if(1 == page){
                                mAuthorAdapter.setNewData(mAuthors);
                                //如果第一次返回的数据不满10条，则显示无更多数据
                                if(mAuthorAdapter.getData().size() < Constant.SEERVER_NUM){
                                    mAuthorAdapter.loadMoreEnd();
                                }
                            }else{
                                //已为加载更多有数据
                                if(mAuthors != null && mAuthors.size() > 0){
                                    mAuthorAdapter.loadMoreComplete();
                                    mAuthorAdapter.addData(mAuthors);
                                }else{
                                    //已为加载更多无更多数据
                                    mAuthorAdapter.loadMoreEnd();
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


    /** --------------------------------- 通用的配置  ---------------------------------*/
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    AuthorAdapter mAuthorAdapter;
    //组合集合
    private List<AuthorBean.Author> mAuthors;
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;
    private int page = 1;

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mAuthorAdapter = new AuthorAdapter(mAuthors);
        mRecyclerView.setAdapter(mAuthorAdapter);

        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);

        initEvent();

    }

    private void initEvent() {

        mAuthorAdapter.setOnLoadMoreListener(() -> {
               ++page;
               myFollowList();
               KLog.d("tag","加载更多");
        },mRecyclerView);

        //不需要可以配置加载更多
        mAuthorAdapter.disableLoadMoreIfNotFullPage();
        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_empty,null);
        mAuthorAdapter.setEmptyView(emptyView);
        ((TextView)emptyView.findViewById(R.id.tv_empty)).setText("您还没有关注的作者哦，看看推荐关注～");

        //点击事件
        mAuthorAdapter.setOnItemClickListener((adapter, view, position) -> {
            AuthorBean.Author mAuthor = mAuthorAdapter.getData().get(position);
            KLog.d("tag","点击的是 position " + position );
        });

        //关注事件
        mAuthorAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            showCancelFocusDialog(position);
        });
    }


    private void initSamrtLayout() {
        smartRefreshLayout.setEnableLoadMore(false);
        //下拉刷新
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAuthors.clear();
            page = 1;
            myFollowList();
        });
    }



    /** --------------------------------- 关注 取关  ---------------------------------*/
    //1-去关注，0-取消关注
    private String focus_type = "1";
    private String author_id;
    private AuthorBean.Author mAuthor;

    public void showCancelFocusDialog(int position){
        String name ;
        String author ;
        mAuthor = mAuthorAdapter.getData().get(position);
        author_id = mAuthor.getId();
        author = mAuthor.getName();
        if(mAuthor.getIs_follow() == 1){
            name = "取消";
            focus_type = "0";
            final FocusAlertDialog iosAlertDialog = new FocusAlertDialog(getActivity()).builder();
            iosAlertDialog.setPositiveButton("确定", v -> {
                followAuthor(position);
            }).setNegativeButton("取消", v -> {}).setMsg("确定要 " + name +"关注「" + author  +"」").setCanceledOnTouchOutside(false);
            iosAlertDialog.show();
        }else{
            focus_type = "1";
            followAuthor(position);
        }
    }



    private void followAuthor(int position) {
        Map<String,String> map = new HashMap<>();
        map.put("type",focus_type);
        map.put("id",author_id);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().followAuthor(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<IndexFocusBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<IndexFocusBean> response) {
                        if(1 == mAuthor.getIs_follow()){
                            mAuthor.setIs_follow(0);
                        }else{
                            mAuthor.setIs_follow(1);
                        }
                        mAuthorAdapter.getData().remove(position);
                        mAuthorAdapter.notifyDataSetChanged();

                        //TODO 11.14 统一发送事件，更新主界面
                        EventBus.getDefault().post(new UpdateHomeListEvent());
                    }

                });
    }


}
