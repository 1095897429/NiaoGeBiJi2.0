package com.qmkj.niaogebiji.module.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.PicPreviewActivity;
import com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew;
import com.qmkj.niaogebiji.module.adapter.CircleSearchAdapter;
import com.qmkj.niaogebiji.module.adapter.CircleSearchAdapterNew;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.SearchAllAuthorBean;
import com.qmkj.niaogebiji.module.bean.SearchAllCircleBean;
import com.qmkj.niaogebiji.module.event.SearchWordEvent;
import com.qmkj.niaogebiji.module.event.SendOkCircleEvent;
import com.qmkj.niaogebiji.module.event.toActionEvent;
import com.qmkj.niaogebiji.module.widget.header.XnClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-18
 * 描述: 搜索动态
 *
 * 1.原先布局  -- 只显示 原创内容
 */
public class SearchCircleFragment extends BaseLazyFragment {

    @BindView(R.id.allpart)
    LinearLayout allpart;
    
    @BindView(R.id.backtop)
    ImageView backtop;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    //页数
    private int page = 1;
    //适配器
    CircleSearchAdapterNew mCircleSearchAdapterNew;
    //组合集合
    List<CircleBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;


    private String blog_id;
    private int pageSize = 10;
    private String myKeyword;


    public static SearchCircleFragment getInstance(String chainId, String chainName) {
        SearchCircleFragment newsItemFragment = new SearchCircleFragment();
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
        return R.layout.fragment_circle_recommend;
    }

    @Override
    protected void initView() {
        initSamrtLayout();
        initLayout();
    }

    @Override
    protected void lazyLoadData() {
        searchBlog();
    }


    private void searchBlog() {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",myKeyword);
        map.put("page",page + "");
        map.put("page_size",pageSize + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SearchAllCircleBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<SearchAllCircleBean> response) {

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                        SearchAllCircleBean temp  = response.getReturn_data();
                        if(null != temp){
                            List<CircleBean> tempList = temp.getList();
                            if(page == 1){
                                if(!tempList.isEmpty()){
                                    setData2(tempList);
                                    mCircleSearchAdapterNew.setNewData(mAllList);
                                    //如果第一次返回的数据不满10条，则显示无更多数据
                                    if(tempList.size() < Constant.SEERVER_NUM){
                                        mCircleSearchAdapterNew.loadMoreEnd();
                                    }
                                }else{
                                    KLog.d("tag","显示空布局");
                                    setEmpty(mCircleSearchAdapterNew);
                                }
                            }else{
                                //已为加载更多有数据
                                if(tempList != null && tempList.size() > 0){
                                    setData2(tempList);
                                    mCircleSearchAdapterNew.loadMoreComplete();
                                    mCircleSearchAdapterNew.addData(teList);
                                }else{
                                    //已为加载更多无更多数据
                                    mCircleSearchAdapterNew.loadMoreComplete();
                                    mCircleSearchAdapterNew.loadMoreEnd();
                                }
                            }
                        }

                    }

                    //{"return_code":"200","return_msg":"success","return_data":{}} -- 后台空集合返回{}，那么会出现解析异常，在这里所判断
                    @Override
                    public void onNetFail(String msg) {

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                        if("解析错误".equals(msg)){
                            if(page == 1){
                                setEmpty(mCircleSearchAdapterNew);
                            }else{
                                mCircleSearchAdapterNew.loadMoreComplete();
                                mCircleSearchAdapterNew.loadMoreEnd();
                            }
                        }
                    }

                });
    }


    //先判断原创 再判断图片 再判断link，最后只剩下全文本
    List<CircleBean> teList = new ArrayList<>();
    private void setData2(List<CircleBean> list) {
        teList.clear();
        if(list != null){
            int type ;
            CircleBean temp;
            for (int i = 0; i < list.size(); i++) {
                temp  = list.get(i);
                type = StringUtil.getCircleType(temp);


                if(temp != null && !TextUtils.isEmpty(temp.getBlog())){
                    temp = StringUtil.addLinksData(temp);
                }

                //如果判断有空数据，则遍历下一个数据
                if(100 == type){
                    continue;
                }
                temp.setCircleType(type);
                teList.add(temp);
            }
            if(page == 1){
                mAllList.addAll(teList);
            }
        }
    }



    protected void setEmpty(BaseQuickAdapter adapter){
        //不需要可以配置加载更多
        adapter.disableLoadMoreIfNotFullPage();
        //TODO 预加载，当列表滑动到倒数第N个Item的时候(默认是1)回调onLoadMoreRequested方法
        adapter.setPreLoadNumber(2);
        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_empty_no_search,null);
        adapter.setEmptyView(emptyView);
    }


    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mCircleSearchAdapterNew = new CircleSearchAdapterNew(mAllList);
        mRecyclerView.setAdapter(mCircleSearchAdapterNew);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        //添加动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        initEvent();
    }



    private void initSamrtLayout() {

        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            searchBlog();
        });
    }



    @SuppressLint("CheckResult")
    private void initEvent() {
        mCircleSearchAdapterNew.setOnLoadMoreListener(() -> {
            ++page;
            searchBlog();
        }, mRecyclerView);


        //item点击事件
        mCircleSearchAdapterNew.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag", "评论去圈子详情");

            blog_id = mAllList.get(position).getId();

            if(position <= 2) {
                MobclickAgentUtils.onEvent("index_search_weibo_" + (position + 1) + "_2_0_0");
            }



        });


        mCircleSearchAdapterNew.setOnItemClickListener((adapter, view, position) -> {
            if (StringUtil.isFastClick()) {
                return;
            }
            int type = adapter.getItemViewType(position);
            switch (type) {
                case FirstItemNewAdapter.RIGHT_IMG_TYPE:
//                    UIHelper.toCommentDetailActivity(getActivity(),"5","1",position);
                    break;
                default:
            }

        });
    }




    //点击全部里的查看更多事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchWordEvent(SearchWordEvent event) {
        myKeyword = event.getWord();
        KLog.d("tag","myKeyword = " + myKeyword);
        if(event.getPosition() == 3){
            mAllList.clear();
            page =  1;
            searchBlog();
        }
    }


}