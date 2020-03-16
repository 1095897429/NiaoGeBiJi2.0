package com.qmkj.niaogebiji.module.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.airbnb.lottie.LottieAnimationView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.TopicFocusAdapter;
import com.qmkj.niaogebiji.module.adapter.TopicSelectAdapter;
import com.qmkj.niaogebiji.module.bean.TopicBean;
import com.qmkj.niaogebiji.module.event.UpdapteListTopicEvent;
import com.qmkj.niaogebiji.module.event.UpdateRecommendTopicFocusListEvent;
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
 * 创建时间 2020-02-13
 * 描述:话题关注Fragment，区别
 */
public class TopicFocusFragment extends BaseLazyFragment {

    @BindView(R.id.tex)
    TextView tex;

    List<TopicBean> list =  new ArrayList<>();
    TopicFocusAdapter mTopicFocusAdapter;

    String typename;
    String chainId;

    public static TopicFocusFragment getInstance(String chainName,String chainId) {
        TopicFocusFragment newsItemFragment = new TopicFocusFragment();
        Bundle args = new Bundle();
        args.putString("typename", chainName);
        args.putString("chainId", chainId);
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_topic;
    }

    @Override
    protected void initView() {
        typename = getArguments().getString("typename");
        chainId = getArguments().getString("chainId");
        tex.setText(typename);
        initLayout();
    }


    @Override
    protected void lazyLoadData() {
        showWaitingDialog();
        getTopicListByCate();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            if(!TextUtils.isEmpty(typename) && "我的关注".equals(typename)){
                getTopicListByCate();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //有焦点时再请求一次
        if(!TextUtils.isEmpty(typename) && "我的关注".equals(typename)){
            getTopicListByCate();
        }
    }

    private List<TopicBean> mTopicBeanList;
    private void getTopicListByCate() {
        Map<String,String> map = new HashMap<>();
        map.put("cate_id",chainId);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getTopicListByCate(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<TopicBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<TopicBean>> response) {
                        hideWaitingDialog();
                        mTopicBeanList = response.getReturn_data();
                        if(null != mTopicBeanList && !mTopicBeanList.isEmpty()){
                            mTopicFocusAdapter.setNewData(mTopicBeanList);
                        }else{
                            mRecyclerView.setVisibility(View.GONE);
                            ll_empty.setVisibility(View.VISIBLE);
                            ll_empty.findViewById(R.id.iv_empty).setBackgroundResource(R.mipmap.icon_empty_comment);
                            ((TextView) ll_empty.findViewById(R.id.tv_empty)).setText("没有关注话题~");
                        }
                    }

                    @Override
                    public void onNetFail(String msg) {
                        hideWaitingDialog();
                        mRecyclerView.setVisibility(View.GONE);
                        ll_empty.setVisibility(View.VISIBLE);
                        ll_empty.findViewById(R.id.iv_empty).setBackgroundResource(R.mipmap.icon_empty_comment);
                        ((TextView) ll_empty.findViewById(R.id.tv_empty)).setText("没有关注话题~");
                    }
                });
    }

    private void getData() {

        TopicBean bean;
        for (int i = 0; i < 10; i++) {
            bean = new TopicBean();
            bean.setName(typename);
            list.add(bean);
        }

        mTopicFocusAdapter.setNewData(list);
    }



    /** --------------------------------- 通用的配置  ---------------------------------*/

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mTopicFocusAdapter = new TopicFocusAdapter(list);
        mRecyclerView.setAdapter(mTopicFocusAdapter);
        //禁用change动画
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
    }



    @BindView(R.id.loading_dialog)
    LinearLayout loading_dialog;

    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottieAnimationView;

    public void showWaitingDialog() {
        loading_dialog.setVisibility(View.VISIBLE);
        lottieAnimationView.setImageAssetsFolder("images");
        lottieAnimationView.setAnimation("images/loading.json");
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();
    }

    /**
     * 隐藏等待提示框
     */
    public void hideWaitingDialog() {
        if(null != lottieAnimationView){
            loading_dialog.setVisibility(View.GONE);
            lottieAnimationView.cancelAnimation();
        }
    }


    @Override
    protected boolean regEvent() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdapteListTopicEvent(UpdapteListTopicEvent event) {
        if(null != this){
            Log.e("tag", "run:--------->当前呈现的类名是: "+ getClass().getSimpleName() + " name " + typename);
            getTopicListByCate();
        }
    }

}
