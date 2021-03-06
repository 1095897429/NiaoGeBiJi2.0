package com.qmkj.niaogebiji.module.fragment;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.qmkj.niaogebiji.module.adapter.TopicSelectAdapter;
import com.qmkj.niaogebiji.module.bean.TopicBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
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
 * 创建时间 2020-02-13
 * 描述:话题选择Fragment
 */
public class TopicSelectFragment extends BaseLazyFragment {

    @BindView(R.id.tex)
    TextView tex;

    List<TopicBean> list =  new ArrayList<>();
    TopicSelectAdapter mTopicSelectAdapter;

    String typename;
    String chainId;
    String selectTopicId;

    public static TopicSelectFragment getInstance(String chainName,String chainId,String selectTopicId) {
        TopicSelectFragment newsItemFragment = new TopicSelectFragment();
        Bundle args = new Bundle();
        args.putString("typename", chainName);
        args.putString("chainId", chainId);
        args.putString("selectTopicId", selectTopicId);
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
        selectTopicId = getArguments().getString("selectTopicId");
        tex.setText(typename);
        initLayout();

    }


    @Override
    protected void lazyLoadData() {
        showWaitingDialog();
        getTopicListByCate();
    }

    private void getData() {

        TopicBean bean;
        for (int i = 0; i < 10; i++) {
            bean = new TopicBean();
            bean.setName(typename);
            list.add(bean);
        }

        mTopicSelectAdapter.setNewData(list);
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

                            if(!TextUtils.isEmpty(selectTopicId)){
                                for (TopicBean bean : mTopicBeanList) {
                                    if(selectTopicId.equals(bean.getId() + "")){
                                        bean.setIs_select(1);
                                        break;
                                    }
                                }
                            }


                            mTopicSelectAdapter.setNewData(mTopicBeanList);
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
                    }
                });
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
        mTopicSelectAdapter = new TopicSelectAdapter(list);
        mRecyclerView.setAdapter(mTopicSelectAdapter);
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
}
