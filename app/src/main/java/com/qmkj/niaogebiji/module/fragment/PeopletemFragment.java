package com.qmkj.niaogebiji.module.fragment;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.event.toActionEvent;
import com.qmkj.niaogebiji.module.event.toRefreshEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-2
 * 描述:人脉界面
 */
public class PeopletemFragment extends BaseLazyFragment {

    @BindView(R.id.text_vip)
    TextView text_vip;


    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    private int page = 1;
    //适配器
    FirstItemNewAdapter mFirstItemAdapter;
    //集合
    List<FirstItemBean> mList = new ArrayList<>();
    //组合集合
    List<MultiNewsBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;


    //通过此方式实例化Fragment
    public static PeopletemFragment getInstance(String chainId, String chainName) {
        PeopletemFragment newsItemFragment = new PeopletemFragment();
        Bundle args = new Bundle();
        args.putString("catid", chainId);
        args.putString("chainName", chainName);
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_people_item;
    }


    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected void initView() {

        text_vip.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        initSamrtLayout();
        initLayout();
        getData();


    }

    private void initAnimate() {
//        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(flash_title,"translationY",0f,200f);
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.play(objectAnimatorY);
//        animatorSet.setDuration(3000);
//        animatorSet.start();
    }

    private void initSamrtLayout() {
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            getData();
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
            getData();
        },mRecyclerView);

        mRecyclerView.addOnScrollListener(new RvScrollListener());

        mFirstItemAdapter.setOnItemChildClickListener((adapter, view, position) -> {
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


        mFirstItemAdapter.setOnItemClickListener((adapter, view, position) -> {
            if(StringUtil.isFastClick()){
                return;
            }
            KLog.d("tag", "ddddd");
            String aid = mAllList.get(position).getNewsItemBean().getAid();
            aid = "24689";
            if (!TextUtils.isEmpty(aid)) {
                UIHelper.toNewsDetailActivity(getActivity(), aid);
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

            }

        }
    }


    @SuppressLint("CheckResult")
    @Override
    public void initData() {

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshBus(toRefreshEvent event){
        if(getUserVisibleHint()){
            KLog.d("tag","我是干货界面，我刷新了");
            mRecyclerView.scrollToPosition(0);
            smartRefreshLayout.autoRefresh();
        }
    }


}
