package com.qmkj.niaogebiji.module.fragment;

import android.graphics.Typeface;
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
import com.qmkj.niaogebiji.module.adapter.RankItemAdapter;
import com.qmkj.niaogebiji.module.bean.UserRankBean;
import com.qmkj.niaogebiji.module.event.UserFeatherEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.MyNestedScrollView;
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
 * 描述:羽毛界面下的第3个Fragment
 */
@SuppressWarnings("ALL")
public class FeatherItemFragment3 extends BaseLazyFragment {



    @BindView(R.id.scrollView)
    MyNestedScrollView scrollView;

    @BindView(R.id.name_tag)
    TextView name_tag;

    @BindView(R.id.head_icon)
    ImageView head_icon;


    @BindView(R.id.rank_recycler)
    RecyclerView rank_recycler;
    //适配器
    RankItemAdapter mRankItemAdapter;
    //是否是第一次加载标志
    boolean isFristLoad = true;
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    //频道名称
    private String mChainName;
    //频道索引
    private String mChainPos;


    public static FeatherItemFragment3 getInstance(String chainId, String chainName) {
        FeatherItemFragment3 actionItemFragment = new FeatherItemFragment3();
        Bundle args = new Bundle();
        args.putString("chainId", chainId);
        args.putString("chainName", chainName);
        actionItemFragment.setArguments(args);
        return actionItemFragment;
    }


    @Override
    protected int getLayoutId() {
        //这里使用公用的
        return R.layout.firtst_feather_item_3;
    }

    @Override
    protected void initView() {
        scrollView.setNestedScrollingEnabled(true);
    }


    @Override
    protected void lazyLoadData() {
        userRank();
    }

    //初始化布局管理器
    private void initRefreshLayout() {
        //scrollview recyclerview滑动卡顿解决
//        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
//            @Override
//            public boolean canScrollVertically() {
//                return false;
//            }
//        };
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        rank_recycler.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mRankItemAdapter = new RankItemAdapter(temps);
        rank_recycler.setAdapter(mRankItemAdapter);

        rank_recycler.setNestedScrollingEnabled(false);
        rank_recycler.setHasFixedSize(true);


        mRankItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });

    }

    /** --------------------------------- 排行榜  ---------------------------------*/

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    @BindView(R.id.iv_empty)
    ImageView iv_empty;

    @BindView(R.id.tv_empty)
    TextView tv_empty;


    private UserRankBean mUserRankBean;
    private UserRankBean.User_info mUser_info;
    private List<UserRankBean.OtherPoint> mOtherPointList;
    private List<UserRankBean.OtherPoint> temps = new ArrayList<>();

    private void userRank() {

        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);

        RetrofitHelper.getApiService().userRank(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<UserRankBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<UserRankBean> response) {


                        mUserRankBean = response.getReturn_data();

                        if(null != mUserRankBean){
                            mUser_info = mUserRankBean.getUser_info();
                            if(null != mUser_info){
                                Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");
                                name_tag.setTypeface(typeface);

                                name_tag.setText(mUser_info.getRank());
                                ImageUtil.load(getActivity(),mUser_info.getAvater(),head_icon);
                            }
                            mOtherPointList = mUserRankBean.getList();
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
        if(null == mRankItemAdapter){
            initRefreshLayout();
        }
        if(null == mOtherPointList || mOtherPointList.isEmpty()){
            rank_recycler.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
            ll_empty.setNestedScrollingEnabled(true);
            iv_empty.setBackgroundResource(R.mipmap.icon_no_nothing);
            tv_empty.setText("没有排行");
        }else{
            ll_empty.setVisibility(View.GONE);
            rank_recycler.setVisibility(View.VISIBLE);
            //手动添加序列
            addOrderInList(mOtherPointList);
        }

    }

    private void addOrderInList(List<UserRankBean.OtherPoint> otherPointList) {

        for (int i = 0; i < otherPointList.size(); i++) {
            otherPointList.get(i).setOrder((i + 1 )+ "");
            temps.add(otherPointList.get(i));
        }


        mRankItemAdapter.setNewData(temps);
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
        userRank();

    }


}
