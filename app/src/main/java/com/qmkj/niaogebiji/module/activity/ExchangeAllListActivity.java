package com.qmkj.niaogebiji.module.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.AccountItemAdapter;
import com.qmkj.niaogebiji.module.bean.IncomeBean;
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
 * 描述:全部兑换记录
 */
public class ExchangeAllListActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;


    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    AccountItemAdapter mAccountItemAdapter;

    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_exchange_all_list;
    }

    @Override
    protected void initView() {
        tv_title.setText("兑换记录");
        initSamrtLayout();
        pointlist();
    }

    //初始化布局管理器
    private void initRefreshLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mAccountItemAdapter = new AccountItemAdapter(temps);
        mRecyclerView.setAdapter(mAccountItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);


        mAccountItemAdapter.setOnItemClickListener((adapter, view, position) -> {


            mInComeDetail = temps.get(position);

            //明细关联数据的类型：1-渠道，2-代理，3-用户，4-评论，5-兑换，6-文章，7-快讯
            if("5".equals(mInComeDetail.getRelated_type())){
                //其中界面包含 实体  + 虚拟
                UIHelper.toExchangeDetailActivity2(ExchangeAllListActivity.this,null,"income",mInComeDetail.getId());
            }else if("6".equals(mInComeDetail.getRelated_type()) && "2".equals(mInComeDetail.getOpe_type())){
                String aid = mInComeDetail.getRelated_id();
                UIHelper.toNewsThingDetailActivity(ExchangeAllListActivity.this,aid);
            }

        });

    }


    /** --------------------------------- 收支明细  ---------------------------------*/

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;


    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    @BindView(R.id.iv_empty)
    ImageView iv_empty;

    @BindView(R.id.tv_empty)
    TextView tv_empty;

    //类型：1-增加积分，2-减少积分，默认所有
    private String ope_type = "2";

    private int page_no = 1;

    private IncomeBean mIncomeBean;

    private List<IncomeBean.InComeDetail> mInComeDetails;

    private IncomeBean.InComeDetail mInComeDetail;

    private List<IncomeBean.InComeDetail> temps = new ArrayList<>();

    private void pointlist() {

        Map<String,String> map = new HashMap<>();
        map.put("ope_type",ope_type);
        map.put("page_no",page_no + "");
        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().pointlist(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<IncomeBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<IncomeBean> response) {
                        KLog.e("tag",response.getReturn_code());
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                            smartRefreshLayout.finishLoadMore();
                        }

                        mIncomeBean = response.getReturn_data();
                        if(null != mIncomeBean){
                            mInComeDetails = mIncomeBean.getList();
                            listCommonLogic();
                        }

                    }

                    @Override
                    public void onNetFail(String msg) {
                        super.onNetFail(msg);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                            smartRefreshLayout.finishLoadMore();
                        }
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


    private void initSamrtLayout() {

        smartRefreshLayout.setDisableContentWhenRefresh(true);
        smartRefreshLayout.setDisableContentWhenLoading(true);
        smartRefreshLayout.setEnableLoadMore(true);
        //加载更多
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            ++page_no;
            pointlist();
        });

        //下拉刷新
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            page_no = 1;
            temps.clear();
            mInComeDetails.clear();
            pointlist();
        });

    }


    private void listCommonLogic() {
        if(null == mAccountItemAdapter){
            initRefreshLayout();
        }
        //第一次进入
        if(1 == page_no){
            if(null == mInComeDetails || mInComeDetails.isEmpty()){
                smartRefreshLayout.setEnableLoadMore(false);
                mRecyclerView.setVisibility(View.GONE);
                ll_empty.setVisibility(View.VISIBLE);
                iv_empty.setBackgroundResource(R.mipmap.icon_no_nothing);
                tv_empty.setText("空空如也，一毛不拔");
            }else{
                smartRefreshLayout.setEnableLoadMore(true);
                ll_empty.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                temps.addAll(mInComeDetails);
                mAccountItemAdapter.setNewData(temps);
            }
        }else{
            if(null != mInComeDetails && !mInComeDetails.isEmpty()){
                mAccountItemAdapter.addData(mInComeDetails);
                KLog.e("tag","数据的总大小 " + mAccountItemAdapter.getData().size());
            }

        }

    }



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
