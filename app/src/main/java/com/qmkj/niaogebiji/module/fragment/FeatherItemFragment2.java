package com.qmkj.niaogebiji.module.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.AccountItemAdapter;
import com.qmkj.niaogebiji.module.bean.IncomeBean;
import com.qmkj.niaogebiji.module.event.UserFeatherEvent;
import com.qmkj.niaogebiji.module.widget.MyNestedScrollView;
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
 * 创建时间 2019-12-19
 * 描述:
 */
@SuppressWarnings("ALL")
public class FeatherItemFragment2 extends BaseLazyFragment {

    @BindView(R.id.scrollView)
    MyNestedScrollView scrollView;


    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    AccountItemAdapter mAccountItemAdapter;
    //是否是第一次加载标志
    boolean isFristLoad = true;
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    //频道名称
    private String mChainName;
    //频道索引
    private String mChainPos;


    public static FeatherItemFragment2 getInstance(String chainId, String chainName) {
        FeatherItemFragment2 actionItemFragment = new FeatherItemFragment2();
        Bundle args = new Bundle();
        args.putString("chainId", chainId);
        args.putString("chainName", chainName);
        actionItemFragment.setArguments(args);
        return actionItemFragment;
    }



    @Override
    protected int getLayoutId() {
        //这里使用公用的
        return R.layout.firtst_feather_item_2;
    }

    @Override
    protected void initView() {
        ope_type = getArguments().getString("chainId");
        scrollView.setNestedScrollingEnabled(true);

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                KLog.d("tag","scrollY " + scrollY);
            }
        });
    }


    @Override
    protected void lazyLoadData() {
        pointlist();
    }

    //初始化布局管理器
    private void initRefreshLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
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


        mAccountItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {


                mInComeDetail = temps.get(position);

                //明细关联数据的类型：1-渠道，2-代理，3-用户，4-评论，5-兑换，6-文章，7-快讯
                if("5".equals(mInComeDetail.getRelated_type())){
                    UIHelper.toExchangeDetailActivity2(getActivity(),null,"income",mInComeDetail.getId());
                }else if("6".equals(mInComeDetail.getRelated_type()) && "2".equals(mInComeDetail.getOpe_type())){
                    String aid = mInComeDetail.getRelated_id();
                    UIHelper.toDataInfoActivity(getActivity(),aid);
                }

            }
        });

    }

    /** --------------------------------- 收支明细  ---------------------------------*/



    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    @BindView(R.id.iv_empty)
    ImageView iv_empty;

    @BindView(R.id.tv_empty)
    TextView tv_empty;

    //类型：1-增加积分，2-减少积分，默认所有
    private String ope_type = "0";

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


                        mIncomeBean = response.getReturn_data();
                        if(null != mIncomeBean){
                            mInComeDetails = mIncomeBean.getList();
                            listCommonLogic();
                        }

                    }

                    @Override
                    public void onNetFail(String msg) {
                        super.onNetFail(msg);

                    }

                    @Override
                    public void onHintError(String return_code, String errorMes) {
                        super.onHintError(return_code, errorMes);

                    }
                });
    }





    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void listCommonLogic() {
        if(null == mAccountItemAdapter){
            initRefreshLayout();
        }
        //第一次进入
        if(1 == page_no){
            if(null == mInComeDetails || mInComeDetails.isEmpty()){
                mRecyclerView.setVisibility(View.GONE);
                ll_empty.setVisibility(View.VISIBLE);
                ll_empty.setNestedScrollingEnabled(true);
                iv_empty.setBackgroundResource(R.mipmap.icon_no_nothing);
                tv_empty.setText("暂无记录");
            }else{
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



    @OnClick({R.id.to_exchange})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.to_exchange:
                UIHelper.toFeatherProductListActivity(getActivity());
                break;
            default:
        }
    }


    @Override
    protected boolean regEvent() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(UserFeatherEvent event){
        temps.clear();
        pointlist();
    }





}
