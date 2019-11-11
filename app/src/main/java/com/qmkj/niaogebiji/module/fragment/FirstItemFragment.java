package com.qmkj.niaogebiji.module.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.adapter.FirstItemAdapter;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.widget.EndlessRecyclerOnScrollListener;
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
    public static final String TAG = "FirstItemFragment";

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    FirstItemAdapter mFirstItemAdapter;
    //集合
    List<FirstItemBean> mList = new ArrayList<>();
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

        initLayout();

        /* 设置加载更多监听 */
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {

                mFirstItemAdapter.setLoadState(mFirstItemAdapter.LOADING);
                KLog.d("tag","加载更多");
                if (mList.size() < 52 ) {
                    // 模拟获取网络数据，延时1s
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getData();
                                    //数量判断，如果不超过10条，则loadState 到底
                                    mFirstItemAdapter.setLoadState(mFirstItemAdapter.LOADING_COMPLETE);
                                }
                            });
                        }
                    }, 1000);
                } else {
                    // 显示加载到底的提示
                    mFirstItemAdapter.setLoadState(mFirstItemAdapter.LOADING_END);
                }
            }
        });


    }

    private void getData() {
        FirstItemBean bean;
        for (int i = 0; i < 10; i++) {
            bean = new FirstItemBean();
            bean.setName("名字 " + i);
            if(i == 2){
                bean.setType("2");//实时快讯
            }else if(i == 8){
                bean.setType("5");//推荐
            }else {
                bean.setType("1");
            }
            mList.add(bean);
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
        mFirstItemAdapter = new FirstItemAdapter(getActivity(),mList);
        mRecyclerView.setAdapter(mFirstItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);

    }












    @Override
    public void initData() {

    }



}
