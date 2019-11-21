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
import com.qmkj.niaogebiji.module.bean.ActionBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MultSearchBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.SearchResultBean;
import com.qmkj.niaogebiji.module.event.LookMoreEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;

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
    private List<SearchResultBean.Article_list> mArticle_lists;

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
        searchContent();
    }

    private void searchContent() {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",myKeyword);
        map.put("type",mType + "");
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchContent(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SearchResultBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<SearchResultBean> response) {

                        mSearchResultBean = response.getReturn_data();
                        if(null != mSearchResultBean){
                            mArticle_lists = mSearchResultBean.getList();
                            //得到全部的数据，去解析
                            getData();

                        }

                    }

                });
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

}
