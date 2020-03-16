package com.qmkj.niaogebiji.module.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.ProfessionAutherDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.AuthorListActivity;
import com.qmkj.niaogebiji.module.activity.HelloMakeActivity;
import com.qmkj.niaogebiji.module.adapter.AuthorAdapter;
import com.qmkj.niaogebiji.module.adapter.AuthorSearchAdapter;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
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
    AuthorSearchAdapter mAuthorAdapter;
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
                                    mAuthorAdapter.addData(teList);
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
        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_empty_no_search,null);
        adapter.setEmptyView(emptyView);
    }


    List<AuthorBean.Author> teList = new ArrayList<>();
    private void setData2(List<AuthorBean.Author> list) {
        teList.clear();
        //可能字段不一样，以前是name,现在给的是author
        AuthorBean.Author author;
        for (int i = 0; i < list.size(); i++) {
            author = new AuthorBean.Author();
            author.setId(list.get(i).getAid());
            author.setTitle(list.get(i).getTitle());
            author.setName(list.get(i).getAuthor());
            author.setImg(list.get(i).getPic());
            author.setIs_follow(list.get(i).getIs_follow());
            author.setType(list.get(i).getType());
            author.setUid(list.get(i).getUid());
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
        mAuthorAdapter = new AuthorSearchAdapter(mList);
        mRecyclerView.setAdapter(mAuthorAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }


    private int mPosition;
    private String uid;
    private void initEvent() {

        mAuthorAdapter.bindToRecyclerView(mRecyclerView);

        mAuthorAdapter.setOnLoadMoreListener(() -> {
            ++page;
            searchAuthor();
        });

        //关注事件 -- 获取uid
        mAuthorAdapter.setToActivityFocusListener(position -> {

            mPosition = position;
            uid = mAuthorAdapter.getData().get(position).getUid();

            RegisterLoginBean.UserInfo user = StringUtil.getUserInfoBean();
            if(TextUtils.isEmpty(user.getCompany_name()) &&
                    TextUtils.isEmpty(user.getPosition()) ){
                showProfessionAuthenNo();
                return;
            }

            //认证过了直接去打招呼界面
            if("1".equals(user.getAuth_email_status()) || "1".equals(user.getAuth_card_status())){
                UIHelper.toHelloMakeActivity(getActivity());
                (getActivity()).overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
            }else{
                showProfessionAuthen();
            }
        });


    }




    //下方的方法从单独的activity 移动到这里
    public void showProfessionAuthenNo(){
        final ProfessionAutherDialog iosAlertDialog = new ProfessionAutherDialog(getActivity()).builder();
        iosAlertDialog.setTitle("完善信息后，被关注几率将提升100%");
        iosAlertDialog.setPositiveButton("让大佬注意你，立即完善", v -> {
            UIHelper.toUserInfoModifyActivity(getActivity());
        }).setNegativeButton("下次再说", v -> {
            //TODO 这里不能用getActivty调用，不然去Activity 了
            Intent intent = new Intent(getActivity(), HelloMakeActivity.class);
            startActivityForResult(intent,100);
            (getActivity()).overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
        }).setMsg("你还未完善信息！").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }


    public void showProfessionAuthen(){
        final ProfessionAutherDialog iosAlertDialog = new ProfessionAutherDialog(getActivity()).builder();
        iosAlertDialog.setPositiveButton("让大佬注意你，立即认证", v -> {
            //和外面的认证一样

            UIHelper.toWebViewActivityWithOnLayout(getActivity(),StringUtil.getLink("certificatecenter"),"");
        }).setNegativeButton("下次再说", v -> {

            Intent intent = new Intent(getActivity(), HelloMakeActivity.class);
            startActivityForResult(intent,100);

            (getActivity()).overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
        }).setMsg("你还未职业认证！").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(data != null){
                message = data.getExtras().getString("message");
                KLog.d("tqg","接收到的文字是 " + message);
            }
        }

        followUser();
    }

    //打招呼返回的字段
    private String message = "";
    private void followUser() {
        Map<String,String> map = new HashMap<>();
        map.put("follow_uid",uid);
        map.put("message",message + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().followUser(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse response) {
                        //同时刷新下方的文章
                        mList.clear();
                        page = 1;
                        searchAuthor();
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
        KLog.d("tag","myKeyword = " + myKeyword);

        if(event.getPosition() == 6){
            XnClassicsHeader header =  new XnClassicsHeader(getActivity());
            smartRefreshLayout.setRefreshHeader(header);
            mList.clear();
            page =  1;
            searchAuthor();
        }
    }


}
