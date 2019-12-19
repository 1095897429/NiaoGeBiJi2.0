package com.qmkj.niaogebiji.module.activity;

import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.NewsFeatherCatItemAdapter;
import com.qmkj.niaogebiji.module.bean.FeatherProductBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

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
 * 创建时间 2019-12-19
 * 描述:商品cat列表页
 */
public class FeatherCatListActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    NewsFeatherCatItemAdapter mNewsThinsItemAdapter;
    //集合
    List<FeatherProductBean.Productean> temps = new ArrayList<>();

    //布局管理器
    LinearLayoutManager mLinearLayoutManager;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_feather_cat_list;
    }

    @Override
    protected void initView() {
        catId = getIntent().getExtras().getString("cat_id");
        name = getIntent().getExtras().getString("name");
        tv_title.setText(name);
        initSamrtLayout();
        getCatList();
    }



    //初始化布局管理器
    private void initRefreshLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mNewsThinsItemAdapter = new NewsFeatherCatItemAdapter(temps);
        mRecyclerView.setAdapter(mNewsThinsItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        //点击事件
        mNewsThinsItemAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","点击的索引 " + position);
            String aid = temps.get(position).getId();
            UIHelper.toFeatherListDetailActivity(this,aid);
        });
    }



    private void initSamrtLayout() {

        smartRefreshLayout.setDisableContentWhenRefresh(true);
        smartRefreshLayout.setDisableContentWhenLoading(true);
        smartRefreshLayout.setEnableLoadMore(true);

        //下拉刷新
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            temps.clear();
            getCatList();
        });

    }




    /** --------------------------------- 获取单个分类商品列表  ---------------------------------*/

    String catId;

    String name;

    private FeatherProductBean mFeatherProductBean;
    private List<FeatherProductBean.Productean> mProducteanLists;

    private void getCatList() {

        Map<String,String> map = new HashMap<>();
        map.put("cat_id",catId + "");

        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().getCatList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<FeatherProductBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<FeatherProductBean> response) {

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                            smartRefreshLayout.finishLoadMore();
                        }

                        mFeatherProductBean = response.getReturn_data();
                        if(null != mFeatherProductBean){
                            mProducteanLists =  mFeatherProductBean.getList();

                            listCommonLogic();

                        }



                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onNetFail(String msg) {
                        super.onNetFail(msg);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                            smartRefreshLayout.finishLoadMore();
                        }

//                        showEmptyLayout(smartRefreshLayout,mRecyclerView,ll_empty,iv_empty,tv_empty,"no_net");
                    }

                    @Override
                    public void onHintError(String return_code, String errorMes) {
                        super.onHintError(return_code, errorMes);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                            smartRefreshLayout.finishLoadMore();
                        }
                    }
                });
    }






    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void listCommonLogic() {
        if(null == mNewsThinsItemAdapter){
            initRefreshLayout();
        }

        if(null == mProducteanLists || mProducteanLists.isEmpty()){

        }else{
            smartRefreshLayout.setEnableLoadMore(true);
            ll_empty.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            temps.addAll(mProducteanLists);
            mNewsThinsItemAdapter.setNewData(temps);
        }



    }


    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    @BindView(R.id.iv_empty)
    ImageView iv_empty;

    @BindView(R.id.tv_empty)
    TextView tv_empty;




    @OnClick({R.id.iv_back})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }



}
