package com.qmkj.niaogebiji.module.fragment;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.CircleRecommendAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolItemAdapter;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.MultiToolNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.qmkj.niaogebiji.module.bean.ToollndexBean;
import com.qmkj.niaogebiji.module.event.ToolChangeEvent;
import com.qmkj.niaogebiji.module.event.ToolHomeChangeEvent;
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
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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

    private List<ToollndexBean> mList;


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
        toolindex();
    }


    private void toolindex() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().toolindex(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<ToollndexBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<ToollndexBean>> response) {
                        KLog.d("tag","response " + response.getReturn_code());
                        mList = response.getReturn_data();
                        setData();
                    }
                });
    }

    private void setData() {

        int size = mList.size();
        KLog.d("tag","大小为 " + size);
        if(size <= 11){
            ToollndexBean bean1 ;
            bean1 = new ToollndexBean();
            bean1.setResid(R.mipmap.icon_tool_more);
            bean1.setTitle("更多");
            mList.add(bean1);
        }

        mToolItemAdapter.setNewData(mList);
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


    }




    private void initLayout() {
        mGridLayoutManager = new GridLayoutManager(getActivity(),3);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        //设置适配器
        mToolItemAdapter = new ToolItemAdapter(mList);
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
