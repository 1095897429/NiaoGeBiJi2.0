package com.qmkj.niaogebiji.module.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jakewharton.rxbinding2.view.RxView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.WelcomeActivity;
import com.qmkj.niaogebiji.module.adapter.FirstAuthorAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.ActicleAllBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.FlashBulltinBean;
import com.qmkj.niaogebiji.module.bean.IndexBulltin;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MoringAllBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.ProBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.event.ProDoneEvent;
import com.qmkj.niaogebiji.module.event.toActionEvent;
import com.qmkj.niaogebiji.module.event.toFlashEvent;
import com.qmkj.niaogebiji.module.event.toRefreshEvent;
import com.qmkj.niaogebiji.module.widget.EndlessRecyclerOnScrollListener;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static com.blankj.utilcode.util.Utils.runOnUiThread;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:首页干货界面
 *
 * 1.实时快讯
 * 2.推荐活动
 * 3.文章列表
 */
public class FirstItemFragment extends BaseLazyFragment {

    @BindView(R.id.part3333)
    RelativeLayout part3333;

    @BindView(R.id.backtop)
    ImageView backtop;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    private String catid = "111";
    private int page = 1;
    private int pageSize = 10;
    private List<ActicleAllBean.Article> mArticles;
    //适配器
    FirstItemNewAdapter mFirstItemAdapter;
    //集合
    List<FirstItemBean> mList = new ArrayList<>();
    //组合集合
    List<MultiNewsBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;


    //通过此方式实例化Fragment
    public static FirstItemFragment getInstance(String chainId, String chainName) {
        FirstItemFragment newsItemFragment = new FirstItemFragment();
        Bundle args = new Bundle();
        args.putString("catid", chainId);
        args.putString("chainName", chainName);
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_first_item;
    }


    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected void initView() {
        initSamrtLayout();
        initLayout();

        //明天提醒 + 是否完成 -- ok
        long time = SPUtils.getInstance().getLong("today_time",0);
        boolean isDone = SPUtils.getInstance().getBoolean("is_done",false);
        if(time != 0){
           if(!TimeUtils.isToday(time) && !isDone){
               KLog.d("tag","不是今天且没有做过测试，提示弹框");
               part3333.setVisibility(View.VISIBLE);
           }
        }else{
            part3333.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void lazyLoadData() {
        //实时快讯
        getIndexBulltin();

    }

    private List<RecommendBean.Article_list> mArticle_lists;
    private void recommendlist() {
        Map<String,String> map = new HashMap<>();
        map.put("page_no",page + "");
        map.put("page_size",pageSize + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().recommendlist(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<RecommendBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<RecommendBean> response) {

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }

                        RecommendBean temp = response.getReturn_data();

                        mArticle_lists = temp.getArticle_list();

                        if(null != mArticle_lists){
                            if(1 == page){
                                setActicleData(mArticle_lists);
                                mFirstItemAdapter.setNewData(mAllList);
                                //如果第一次返回的数据不满10条，则显示无更多数据
                                if(mArticle_lists.size() < Constant.SEERVER_NUM){
                                    mFirstItemAdapter.loadMoreEnd();
                                }
                            }else{
                                //已为加载更多有数据
                                if(mArticle_lists != null && mArticle_lists.size() > 0){
                                    setActicleData(mArticle_lists);
                                    mFirstItemAdapter.loadMoreComplete();
                                    mFirstItemAdapter.addData(tempList);
                                }else{
                                    //已为加载更多无更多数据
                                    mFirstItemAdapter.loadMoreEnd();
                                }
                            }
                        }

                    }
                });
    }

    //后台返回临时数据
    List<MultiNewsBean> tempList = new ArrayList<>();

    private void setActicleData(List<RecommendBean.Article_list> article_lists) {

        //顶置 2篇
        //实时快讯
        //活动推荐
        //5条 文章列表
        //文章列表


        tempList.clear();

        RecommendBean.Article_list itemBean;
        IndexBulltin indexBulltin1;
        MultiNewsBean bean1 ;

        if(1 == page){
            int size ;
            //取头两条数据
            if(!article_lists.isEmpty()){
                size = article_lists.size();
                if(size > 2){
                    for (int i = 0; i < 2; i++) {
                        itemBean = article_lists.get(i);
                        bean1 = new MultiNewsBean();
                        bean1.setItemType(1);
                        bean1.setNewsActicleList(itemBean);
                        mAllList.add(bean1);
                    }
                }
            }

        if(null != indexBulltin){
            indexBulltin1 = indexBulltin;
            bean1 = new MultiNewsBean();
            bean1.setItemType(4);
            bean1.setIndexBulltin(indexBulltin1);
            mAllList.add(bean1);
        }


            String pic_type;
            for (int i = 0 + 2; i < article_lists.size(); i++) {
                itemBean = article_lists.get(i);
                bean1 = new MultiNewsBean();
                pic_type = article_lists.get(i).getPic_type();
                if("1".equals(pic_type)){
                    bean1.setItemType(1);
                }else if("2".equals(pic_type)){
                    bean1.setItemType(3);
                }else if("3".equals(pic_type)){
                    bean1.setItemType(2);
                }
                bean1.setNewsActicleList(itemBean);
                mAllList.add(bean1);
            }

            mFirstItemAdapter.setNewData(mAllList);
        }else{

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
                }
                bean1.setNewsActicleList(itemBean);
                tempList.add(bean1);
            }
        }



    }


    /** --------------------------------- 实时快讯  ---------------------------------*/

    private List<IndexBulltin.Bulletn_list> mBulletn_lists = new ArrayList<>();
    //后台对象
    private  IndexBulltin indexBulltin ;
    private void getIndexBulltin() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getIndexBulltin(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<IndexBulltin>>() {
                    @Override
                    public void onSuccess(HttpResponse<IndexBulltin> response) {
                        indexBulltin = response.getReturn_data();
                        if(null != indexBulltin){
                            mBulletn_lists.clear();
                            mBulletn_lists.addAll(indexBulltin.getBulletn_list());
                        }
                        setIndexBulltinData();
                    }
                });
    }

    private void setIndexBulltinData() {
        //文章推荐
        recommendlist();
    }


    private void initAnimate() {
//        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(flash_title,"translationY",0f,200f);
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.play(objectAnimatorY);
//        animatorSet.setDuration(3000);
//        animatorSet.start();
    }

    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            recommendlist();
        });
    }


    private void getData() {

        NewsItemBean itemBean;
        FirstItemBean firstItemBean;
        MultiNewsBean bean1 ;
        for (int i = 0; i < 10; i++) {
           if(i == 2){
              firstItemBean = new FirstItemBean();
              bean1 = new MultiNewsBean();
              bean1.setItemType(4);
              bean1.setFirstItemBean(firstItemBean);
          }else {
              itemBean = new NewsItemBean();
              bean1 = new MultiNewsBean();
              if(i == 4){
                  bean1.setItemType(2);
              }else if(i == 5){
                  bean1.setItemType(3);
              }else if(i == 7){
                  bean1.setItemType(5);
              }else{
                  bean1.setItemType(1);
              }

              bean1.setNewsItemBean(itemBean);
          }
            mAllList.add(bean1);
        }

        mFirstItemAdapter.setNewData(mAllList);

        initAnimate();
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



    //初始化布局管理器
    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mFirstItemAdapter = new FirstItemNewAdapter(mAllList);
        mRecyclerView.setAdapter(mFirstItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    @SuppressLint("CheckResult")
    private void initEvent() {
        mFirstItemAdapter.setOnLoadMoreListener(() -> {
            ++page;
            recommendlist();
        },mRecyclerView);

        mRecyclerView.addOnScrollListener(new RvScrollListener());

        mFirstItemAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.toMoreActivity:
                    EventBus.getDefault().post(new toActionEvent("去活动界面"));
                    break;
                default:
            }
        });


        mFirstItemAdapter.setOnItemClickListener((adapter, view, position) -> {
            if(StringUtil.isFastClick()){
                return;
            }

            int type = mFirstItemAdapter.getData().get(position).getItemType();
            //快讯
            if(type == 4){
                EventBus.getDefault().post(new toFlashEvent("去快讯信息流"));
            }else if(type == 1){
                String aid = mFirstItemAdapter.getData().get(position).getNewsActicleList().getAid();
                if (!TextUtils.isEmpty(aid)) {
                    UIHelper.toNewsDetailActivity(getActivity(), aid);
                }
            }

        });

    }


    private class RvScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                //获取最后一个可见view的位置
                int lastItemPosition = linearManager.findLastVisibleItemPosition();
                if(lastItemPosition > 6){
                   backtop.setVisibility(View.GONE);
                }else{
                    backtop.setVisibility(View.GONE);
                }
            }

        }
    }


    @SuppressLint("CheckResult")
    @Override
    public void initData() {

        RxView.clicks(backtop)
                //每1秒中只处理第一个元素
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(object -> {
                    mRecyclerView.scrollToPosition(0);
                    smartRefreshLayout.autoRefresh();
                });
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshBus(toRefreshEvent event){
        if(getUserVisibleHint()){
            KLog.d("tag","我是干货界面，我刷新了");
            mRecyclerView.scrollToPosition(0);
            smartRefreshLayout.autoRefresh();
        }
    }



    @OnClick({R.id.to_tomorow, R.id.toMoreLoveYou})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.to_tomorow:
                //明天提示
                SPUtils.getInstance().put("today_time",System.currentTimeMillis());
                part3333.setVisibility(View.GONE);
                break;
            case R.id.toMoreLoveYou:
                getProfession();
                break;
            default:
        }
    }


    //

    private ArrayList<ProBean> temp;
    private void getProfession() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getProfession(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<ProBean>>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<List<ProBean>> response) {
                        temp = (ArrayList<ProBean>) response.getReturn_data();
                        if(temp != null && !temp.isEmpty()){
                            UIHelper.toMoreKnowYouActivity(getActivity(),temp);
                        }
                    }
                });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toProDoneEvent(ProDoneEvent event){
        KLog.d("tag","关闭视图");
        part3333.setVisibility(View.GONE);
    }


}
