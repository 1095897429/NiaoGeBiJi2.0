package com.qmkj.niaogebiji.module.fragment;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.adapter.CircleRecommendAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolItemAdapter;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.MultiToolNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.event.ToolChangeEvent;
import com.qmkj.niaogebiji.module.event.ToolHomeChangeEvent;
import com.socks.library.KLog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class ToolFragment extends BaseLazyFragment {


    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    //适配器
    ToolItemAdapter mToolItemAdapter;
    //组合集合
    List<ToolBean> mAllList = new ArrayList<>();
    //布局管理器
    GridLayoutManager mGridLayoutManager;


    public static ToolFragment getInstance() {
        return new ToolFragment();
    }


    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tool;
    }


    @Override
    protected void initView() {
        initLayout();
        getData();
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


    private void getData() {

        ToolBean bean1 ;
        for (int i = 0; i < 12; i++) {
            bean1 = new ToolBean();
            mAllList.add(bean1);
        }

        mToolItemAdapter.setNewData(mAllList);
    }




    private void initLayout() {
        mGridLayoutManager = new GridLayoutManager(getActivity(),3);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        //设置适配器
        mToolItemAdapter = new ToolItemAdapter(mAllList);
        mRecyclerView.setAdapter(mToolItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {
        mToolItemAdapter.setOnItemClickListener((adapter, view, position) -> {

            if(position == adapter.getData().size() - 1){
                UIHelper.toToolEditActivity(getActivity());
                return;
            }

            KLog.d("tag","去webview");
        });
    }


    @Override
    public void initData() {

    }


    @OnClick({R.id.icon_search})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.icon_search:
                UIHelper.toToolSearchActivity(getActivity());
                break;
            default:
        }
    }

    //点击item的收藏 更改首页数据源
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToolHomeChangeEvent(ToolHomeChangeEvent event) {

        KLog.d("tag"," 更改首页数据源");
    }




}
