package com.qmkj.niaogebiji.module.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.adapter.FocusAdapter;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.FouBBBB;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
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
 * 描述:
 */
public class FocusFragment extends BaseLazyFragment {

    public static FocusFragment getInstance(String chainId, String chainName) {
        FocusFragment newsItemFragment = new FocusFragment();
        Bundle args = new Bundle();
        args.putString("chainId", chainId);
        args.putString("chainName", chainName);
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.firtst_focus_item;
    }

    @Override
    protected void initView() {
        initSamrtLayout();
        getIndexArticle();
        initLayout();
    }

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    FocusAdapter mFocusAdapter;
    //组合集合
    List<MultiNewsBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;
    private int page = 1;
    //作者列表
    private List<IndexFocusBean.Auther_list> mAuther_lists;
    //文章列表
    private List<IndexFocusBean.Article_list> mArticle_lists;

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mFocusAdapter = new FocusAdapter(mAllList);
        mRecyclerView.setAdapter(mFocusAdapter);

        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);

        initEvent();

    }

    private void initEvent() {
        mFocusAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            UIHelper.toLoginActivity(getActivity());
        });
        mFocusAdapter.setOnItemClickListener((adapter, view, position) -> {
            int type = adapter.getItemViewType(position);
            switch (type) {
                case FocusAdapter.RIGHT_IMG_TYPE:
                    break;
                default:
            }
        });
    }


    private void initSamrtLayout() {
        smartRefreshLayout.setEnableLoadMore(false);
        //下拉刷新
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            getIndexArticle();
        });

    }


    //from =1 代表来自主界面推荐关注里的关注  from = 0 下拉刷新的请求
    private void getIndexArticle() {
        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getIndexArticle(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<IndexFocusBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<IndexFocusBean> response) {
                        IndexFocusBean mIndexFocusBean = response.getReturn_data();
                        if(null != mIndexFocusBean){
                            if(null != smartRefreshLayout){
                                smartRefreshLayout.finishRefresh();
                            }
                            mAuther_lists =  mIndexFocusBean.getAuther_list();
                            mArticle_lists = mIndexFocusBean.getArticle_list();
                            listCommonLogic();
                        }
                    }

                    @Override
                    public void onHintError(String errorMes) {
                        super.onHintError(errorMes);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onNetFail(String msg) {
                        super.onNetFail(msg);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                    }
                });
    }

    private void listCommonLogic() {

        for (int i = 0; i < 2; i++) {
            MultiNewsBean bean = new MultiNewsBean();
            bean.setItemType(2);
            bean.setArticleList(mArticle_lists.get(i));
            mAllList.add(bean);
        }

        MultiNewsBean bean1 = new MultiNewsBean();
        FouBBBB bbbb = new FouBBBB();
        bbbb.setDjj(mAuther_lists);
        bean1.setFouBBBB(bbbb);
        bean1.setItemType(1);
        mAllList.add(bean1);

        MultiNewsBean bean2 = new MultiNewsBean();
        bean2.setItemType(3);
        mAllList.add(bean2);

        for (int i = 0; i < mArticle_lists.size(); i++) {
            MultiNewsBean bean = new MultiNewsBean();
            bean.setItemType(2);
            bean.setArticleList(mArticle_lists.get(i));
            mAllList.add(bean);
        }



        if(1 == page){
            mFocusAdapter.setNewData(mAllList);
        }else{

        }

    }




}
