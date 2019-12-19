package com.qmkj.niaogebiji.module.activity;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.FeatherProductItemNewAdapter;
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
 * 描述:商品列表页
 */
public class FeatherListActivity extends BaseActivity {

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;




    LinearLayoutManager mLinearLayoutManager;
    FeatherProductItemNewAdapter mProductItemNewAdapter;
    //集合1
    List<FeatherProductBean.Productean> temps = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feather_list;
    }

    @Override
    protected void initView() {

        initSamrtLayout();
        getMallList();
    }



    //初始化布局管理器
    private void initVertical() {
        mLinearLayoutManager = new LinearLayoutManager(FeatherListActivity.this);
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        recycler.setLayoutManager(mLinearLayoutManager);
        //禁用change动画
        ((SimpleItemAnimator)recycler.getItemAnimator()).setSupportsChangeAnimations(false);
        //设置适配器
        mProductItemNewAdapter = new FeatherProductItemNewAdapter(temps);
        recycler.setAdapter(mProductItemNewAdapter);
        //解决数据加载不完
        recycler.setNestedScrollingEnabled(false);
        recycler.setHasFixedSize(true);

        //点击事件
        mProductItemNewAdapter.setOnItemClickListener((adapter, view, position) -> {

        });

        //全部
        mProductItemNewAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                KLog.d("tag","tag");
                UIHelper.toFeatherCatListActivity(mContext,temps.get(position).getId(),temps.get(position).getTitle());
            }
        });


    }



    /** --------------------------------- 商品列表  ---------------------------------*/

    private FeatherProductBean mFeatherProductBean;
    private List<FeatherProductBean.Productean> mProducteanList;


    private void getMallList() {

        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().getMallList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<FeatherProductBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<FeatherProductBean> response) {
                        mFeatherProductBean = response.getReturn_data();
                        if(null != mFeatherProductBean){
                            mProducteanList = mFeatherProductBean.getList();

                            if(null != smartRefreshLayout){
                                smartRefreshLayout.finishRefresh();
                                smartRefreshLayout.finishLoadMore();
                            }
                            listCommonLogic();
                        }
                    }

                });
    }


    private void listCommonLogic() {
        if(null == mProductItemNewAdapter){
            initVertical();
        }

        if(null == mProducteanList || mProducteanList.isEmpty()){
            recycler.setVisibility(View.GONE);
//            ll_empty.setVisibility(View.VISIBLE);
//            ll_empty.setNestedScrollingEnabled(true);
//            iv_empty.setBackgroundResource(R.mipmap.icon_no_search_result);
//            tv_empty.setText("空空如也，一毛不拔");
        }else{
//            ll_empty.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
            temps.addAll(mProducteanList);
            mProductItemNewAdapter.setNewData(temps);
        }

    }



    @OnClick({R.id.iv_back,R.id.iv_right})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_right:
                UIHelper.toExchangeAllListActivity(this);
                break;
            default:
        }
    }


    private void initSamrtLayout() {

        smartRefreshLayout.setDisableContentWhenRefresh(true);
        smartRefreshLayout.setDisableContentWhenLoading(true);
        smartRefreshLayout.setEnableLoadMore(false);


        //下拉刷新
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            temps.clear();
            getMallList();

        });

    }



}
