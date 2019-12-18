package com.qmkj.niaogebiji.module.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.CircleRecommendAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.event.toActionEvent;
import com.qmkj.niaogebiji.module.widget.header.XnClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-14
 * 描述:圈子关注
 */
public class CircleFocusFragment extends BaseLazyFragment {

    @BindView(R.id.backtop)
    ImageView backtop;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    private int page = 1;

    private String chanelName;

    //适配器
    CircleRecommendAdapter mCircleRecommendAdapter;
    //组合集合
    List<MultiCircleNewsBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;


    //后台直接返回一个集合
    private List<CircleBean> serverData = new ArrayList<>();



    public static CircleFocusFragment getInstance(String chainId, String chainName) {
        CircleFocusFragment newsItemFragment = new CircleFocusFragment();
        Bundle args = new Bundle();
        args.putString("catid", chainId);
        args.putString("chainName", chainName);
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_circle_focus;
    }

    @Override
    protected void initView() {
        chanelName = getArguments().getString("chainName");
        KLog.d("tag","当前展示的是 " + chanelName);
        initSamrtLayout();
        initLayout();
//        getData();
    }



    @Override
    protected void lazyLoadData() {
//        followBlogList();
    }

    private void followBlogList() {
        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().followBlogList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<CircleBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<CircleBean>> response) {
                        KLog.d("tag","response " + response.getReturn_code());
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }

                        serverData = response.getReturn_data();
                        if(null != serverData){
                            if(1 == page){
                                setData2(serverData);
                                mCircleRecommendAdapter.setNewData(mAllList);
                                //如果第一次返回的数据不满10条，则显示无更多数据
                                if(serverData.size() < Constant.SEERVER_NUM){
                                    mCircleRecommendAdapter.loadMoreEnd();
                                }
                            }else{
                                //已为加载更多有数据
                                if(serverData != null && serverData.size() > 0){
                                    mCircleRecommendAdapter.loadMoreComplete();
                                    mCircleRecommendAdapter.addData(mAllList);
                                }else{
                                    //已为加载更多无更多数据
                                    mCircleRecommendAdapter.loadMoreEnd();
                                }
                            }
                        }
                    }

                    //{"return_code":"200","return_msg":"success","return_data":{}} -- 后台空集合返回{}，那么会出现解析异常，在这里所判断
                    @Override
                    public void onNetFail(String msg) {
                        if("解析错误".equals(msg) || "未知错误".equals(msg)){
                            if(page == 1){
                                setData2(serverData);
                            }else{
                                mCircleRecommendAdapter.loadMoreComplete();
                                mCircleRecommendAdapter.loadMoreEnd();
                            }
                        }
                    }

                });
    }


    private void setData2(List<CircleBean> list) {

        if(!list.isEmpty()){

            CircleBean temp;
            String type;
            String link;
            List<String> imgs;
            MultiCircleNewsBean mulBean;
            for (int i = 0; i < list.size(); i++) {
                mulBean = new MultiCircleNewsBean();
                temp = list.get(i);
                type = temp.getType();
                link = temp.getLink();
                imgs =  temp.getImages();
                //1 是 转发
                if(!TextUtils.isEmpty(type) && "1".equals(type)){
                    //内部图片
                    List<String> imgsss = temp.getP_blog().getImages();
                    if(imgsss != null &&  !imgsss.isEmpty()){
                        mulBean.setItemType(2);
                    }
                    //link
                    String linked = temp.getP_blog().getLink();
                    if(!TextUtils.isEmpty(linked)){
                        mulBean.setItemType(3);
                    }

                    if((imgsss.isEmpty()) && TextUtils.isEmpty(link)){
                        mulBean.setItemType(5);
                    }
                }else{
                    //原创图片
                    if(imgs != null &&  !imgs.isEmpty()){
                        mulBean.setItemType(1);
                    }

                    //原创link
                    if(!TextUtils.isEmpty(link)){
                        mulBean.setItemType(4);
                    }

                    if((imgs.isEmpty()) && TextUtils.isEmpty(link)){
                        mulBean.setItemType(5);
                    }

                }

                mulBean.setCircleBean(temp);
                mAllList.add(mulBean);
            }
        }else{
            //第一次加载无数据
            ll_empty.setVisibility(View.VISIBLE);
            smartRefreshLayout.setVisibility(View.GONE);
        }
    }


    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mCircleRecommendAdapter = new CircleRecommendAdapter(mAllList);
        mRecyclerView.setAdapter(mCircleRecommendAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            followBlogList();
        });
    }


    private void getData() {

//        NewsItemBean itemBean;
//        FirstItemBean firstItemBean;
//        MultiCircleNewsBean bean1 ;
//        for (int i = 0; i < 10; i++) {
//            if(i == 2){
//                firstItemBean = new FirstItemBean();
//                bean1 = new MultiCircleNewsBean();
//                bean1.setItemType(4);
//                bean1.setFirstItemBean(firstItemBean);
//            }else {
//                itemBean = new NewsItemBean();
//                bean1 = new MultiCircleNewsBean();
//                if(i == 4){
//                    bean1.setItemType(2);
//                }else if(i == 5){
//                    bean1.setItemType(3);
//                }else{
//                    bean1.setItemType(1);
//                }
//
//                bean1.setNewsItemBean(itemBean);
//            }
//            mAllList.add(bean1);
//        }

        mCircleRecommendAdapter.setNewData(mAllList);

    }


    private void initEvent() {
        mCircleRecommendAdapter.setOnLoadMoreListener(() -> {
            ++page;
            followBlogList();
        },mRecyclerView);

        mRecyclerView.addOnScrollListener(new RvScrollListener());

        mCircleRecommendAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.toMoreActivity:
                    EventBus.getDefault().post(new toActionEvent("去活动界面"));
                    break;
                case R.id.toMoreFlash:
                    EventBus.getDefault().post(new toActionEvent("去快讯信息流"));
                    break;

                default:
            }
        });


        mCircleRecommendAdapter.setOnItemClickListener((adapter, view, position) -> {

            RxView.clicks(view)
                    //每1秒中只处理第一个元素
                    .throttleFirst(1000, TimeUnit.MILLISECONDS)
                    .subscribe(object -> {
                        int type = adapter.getItemViewType(position);
                        switch (type) {
                            case FirstItemNewAdapter.RIGHT_IMG_TYPE:
                                String aid ;
                                aid = "24689";
                                if(!TextUtils.isEmpty(aid)){
                                    UIHelper.toNewsDetailActivity(getActivity(),aid);
                                }
                                break;
                            default:
                        }
                    });
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
                    backtop.setVisibility(View.VISIBLE);
                }else{
                    backtop.setVisibility(View.GONE);
                }
            }

        }
    }




}
