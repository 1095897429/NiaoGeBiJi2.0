package com.qmkj.niaogebiji.module.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.adapter.FirstItemAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.widget.EndlessRecyclerOnScrollListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

import static com.blankj.utilcode.util.Utils.runOnUiThread;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class FirstItemFragment extends BaseLazyFragment {

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
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
    protected void initView() {

        getData();
        initSamrtLayout();
        initLayout();


    }

    private void initSamrtLayout() {

        smartRefreshLayout.setEnableLoadMore(false);

        //下拉刷新
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
           mAllList.clear();
           getData();
            if(null != smartRefreshLayout){
                smartRefreshLayout.finishRefresh();
            }
        });

    }


    private void getData() {

//        FirstItemBean bean;
//        for (int i = 0; i < 10; i++) {
//            bean = new FirstItemBean();
//            bean.setName("名字 " + i);
//            if(i == 2){
//                bean.setType("2");//实时快讯
//            }else if(i == 8){
//                bean.setType("5");//推荐
//            }else {
//                bean.setType("1");
//            }
//            mList.add(bean);
//        }

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
              }else{
                  bean1.setItemType(1);
              }

              bean1.setNewsItemBean(itemBean);
          }
            mAllList.add(bean1);
        }

    }

    private void getData2() {
        FirstItemBean bean;
        for (int i = 0; i < 1; i++) {
            bean = new FirstItemBean();
            bean.setName("名字 " + i);
            bean.setType("1");
            mList.add(bean);
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

    private void initEvent() {
        mFirstItemAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.toMoreActivity:
                    ToastUtils.showShort("去活动");
                    break;
                case R.id.toMoreFlash:
                    ToastUtils.showShort("去快讯信息流");
                    break;

                default:
            }


        });
    }


    @Override
    public void initData() {

    }






}
