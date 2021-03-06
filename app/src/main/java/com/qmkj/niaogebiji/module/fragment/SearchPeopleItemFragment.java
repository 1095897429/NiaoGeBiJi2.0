package com.qmkj.niaogebiji.module.fragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
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
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.PeopleItemAdapter;
import com.qmkj.niaogebiji.module.adapter.ThingsAdapter;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.SearchAllPeopleBean;
import com.qmkj.niaogebiji.module.event.PeopleFocusEvent;
import com.qmkj.niaogebiji.module.event.SayHiEvent;
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
 * 创建时间 2019-11-11
 *  搜索人脉
 */
public class SearchPeopleItemFragment extends BaseLazyFragment {


    private int page = 1;
    private int pageSize = 10;
    private String myKeyword;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.no_vip_auther)
    LinearLayout no_vip_auther;




    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    //适配器
    PeopleItemAdapter mPeopleItemAdapter;
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    private List<RegisterLoginBean.UserInfo> mList = new ArrayList<>();

    //通过此方式实例化Fragment
    public static SearchPeopleItemFragment getInstance(String chainId, String chainName) {
        SearchPeopleItemFragment newsItemFragment = new SearchPeopleItemFragment();
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
        return R.layout.activity_search_people;
    }


    @Override
    protected void initView() {
        initSamrtLayout();
        initLayout();

        no_vip_auther.setOnClickListener(v ->
                UIHelper.toWebViewAllActivity(getActivity(),StringUtil.getLink("vipmember"),"vipmember"));

//        UIHelper.toWebViewActivityWithOnLayout(getActivity(),StringUtil.getLink("vipmember"),"vipmember"));
    }

    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mList.clear();
            page = 1;
            searchPeople();
        });
    }

    @Override
    protected void lazyLoadData() {
        searchPeople();
    }

    private void searchPeople() {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",myKeyword);
        map.put("page",page + "");
        map.put("page_size",pageSize + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchPeople(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SearchAllPeopleBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<SearchAllPeopleBean> response) {

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }


                        RegisterLoginBean.UserInfo mUserInfo = StringUtil.getUserInfoBean();

                        if(!TextUtils.isEmpty(mUserInfo.getVip_last_time()) && !"0".equals(mUserInfo.getVip_last_time())){
                            no_vip_auther.setVisibility(View.GONE);
                        }else{
                            no_vip_auther.setVisibility(View.VISIBLE);
                        }


                        SearchAllPeopleBean temp  = response.getReturn_data();
                        if(null != temp){
                            List<RegisterLoginBean.UserInfo> tempList = temp.getList();
                            if(page == 1){
                                if(!tempList.isEmpty()){
                                    setData2(tempList);
                                    mPeopleItemAdapter.setNewData(mList);
                                    //如果第一次返回的数据不满10条，则显示无更多数据
                                    if(tempList.size() < Constant.SEERVER_NUM){
                                        mPeopleItemAdapter.loadMoreEnd();
                                    }
                                }else{
                                    KLog.d("tag","显示空布局");
                                    setEmpty(mPeopleItemAdapter);
                                }
                            }else{
                                //已为加载更多有数据
                                if(tempList != null && tempList.size() > 0){
                                    setData2(tempList);
                                    mPeopleItemAdapter.loadMoreComplete();
                                    mPeopleItemAdapter.addData(teList);
                                }else{
                                    //已为加载更多无更多数据
                                    mPeopleItemAdapter.loadMoreComplete();
                                    mPeopleItemAdapter.loadMoreEnd();
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
        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_empty_no_search,null);
        adapter.setEmptyView(emptyView);
    }


    List<RegisterLoginBean.UserInfo> teList = new ArrayList<>();
    private void setData2(List<RegisterLoginBean.UserInfo> list) {
        teList.clear();
        teList.addAll(list);
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
        mPeopleItemAdapter = new PeopleItemAdapter(mList);
        mRecyclerView.setAdapter(mPeopleItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {

        mPeopleItemAdapter.bindToRecyclerView(mRecyclerView);


        mPeopleItemAdapter.setOnLoadMoreListener(() -> {
            ++page;
            searchPeople();
        });

        mPeopleItemAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            myPosition = position;
            RegisterLoginBean.UserInfo temp = mPeopleItemAdapter.getData().get(position);
            switch (view.getId()){
                case R.id.focus:
                    follow_uid = temp.getUid();
                    UIHelper.toHelloMakeActivity((Activity) mContext);
                    ((Activity) mContext).overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);

                    break;
                case R.id.already_focus:
                    unfollowUser();
                    break;
                    default:
            }
        });

    }


    @Override
    public void initData() {

    }


    //点击全部里的查看更多事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchWordEvent(SearchWordEvent event) {
        myKeyword = event.getWord();
        if(event.getPosition() == 2){
            mList.clear();
            page =  1;
            searchPeople();
        }
    }


    //点击全部里的查看更多事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toSayHiEvent(SayHiEvent event) {
        message = event.getContent();
        followUser();
    }


    private int myPosition;
    private String follow_uid;
    private String message;
    private void followUser() {
        Map<String,String> map = new HashMap<>();
        map.put("follow_uid",follow_uid);
        map.put("message",message + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().followUser(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse response) {
                        mPeopleItemAdapter.getData().get(myPosition).setFollow_status(1);
                        mPeopleItemAdapter.notifyItemChanged(myPosition);
                    }
                });
    }


    private void unfollowUser() {
        Map<String,String> map = new HashMap<>();
        map.put("follow_uid",follow_uid);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().unfollowUser(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse response) {
                        mPeopleItemAdapter.getData().get(myPosition).setFollow_status(0);
                        mPeopleItemAdapter.notifyItemChanged(myPosition);
                    }
                });
    }



    //点击全部里的查看更多事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPeopleFocusEvent(PeopleFocusEvent event) {
        KLog.d("tag","用户的uid是 "  + event.getUid() +  " 状态  "  + event.getStatus());
        int  position  = -1 ;
        for (int i = 0; i < mList.size(); i++) {
            if(event.getUid().equals(mList.get(i).getUid())){
                position = i;
                break;
            }
        }

        if(position != -1){
            // 0未关注 1已关注 2我屏蔽了别人 3别人屏蔽了我
            mPeopleItemAdapter.getData().get(position).setFollow_status(event.getStatus());
            mPeopleItemAdapter.notifyItemChanged(position);
        }

    }





}