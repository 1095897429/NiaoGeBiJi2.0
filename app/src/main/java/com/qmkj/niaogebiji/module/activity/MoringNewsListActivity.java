package com.qmkj.niaogebiji.module.activity;

import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.service.MediaService;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.adapter.MoringNewsAdapter;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MoringAllBean;
import com.qmkj.niaogebiji.module.bean.MoringNewsBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.event.AudioEvent;
import com.qmkj.niaogebiji.module.event.AudioEvent2;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;

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
 * 创建时间 2019-11-12
 * 描述:早报列表
 */
public class MoringNewsListActivity extends BaseActivity {

    @BindView(R.id.text)
    TextView text;

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    @BindView(R.id.part111)
    LinearLayout part111;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    MoringNewsAdapter mMoringNewsAdapter;
    //集合
    List<MoringNewsBean> mList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;


    private int page = 1;
    private int pageSize = 10;
    List<MoringAllBean.MoringBean> mMoringList = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_moringnewlist;
    }

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected void initView() {

        //中文加粗
        TextView chineseTv = text;
        TextPaint paint = chineseTv.getPaint();
        paint.setFakeBoldText(true);

        for (int i = 0; i < 10; i++) {
            mList.add(new MoringNewsBean());
        }

        mplist();

        initLayout();
    }



    private void mplist() {
        KLog.d("tag","page 是 " + page);
        Map<String,String> map = new HashMap<>();
        map.put("page_no",page + "");
        map.put("page_size",pageSize + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().mplist(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<MoringAllBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<MoringAllBean> response) {
                        KLog.d("tag","response " + response.getReturn_code());
                        MoringAllBean temp  = response.getReturn_data();
                        mMoringList = temp.getList();
                        setData();
                    }
                });
    }

    private void setData() {
        if(page ==  1){
            if(mMoringList != null && !mMoringList.isEmpty()){
                setData2(mMoringList);
                mMoringNewsAdapter.setNewData(mMoringList);
//                mMoringNewsAdapter.disableLoadMoreIfNotFullPage(mRecyclerView);
                //如果第一次返回的数据不满10条，则显示无更多数据
                if(mMoringList.size() < Constant.SEERVER_NUM){
                    mMoringNewsAdapter.loadMoreEnd();
                }
                ll_empty.setVisibility(View.GONE);
                part111.setVisibility(View.VISIBLE);
            }else{
                //第一次加载无数据
                ll_empty.setVisibility(View.VISIBLE);
                ((TextView)ll_empty.findViewById(R.id.tv_empty)).setText("暂无早报内容");
                ((ImageView)ll_empty.findViewById(R.id.iv_empty)).setImageResource(R.mipmap.icon_empty_article);
                part111.setVisibility(View.GONE);
            }
        }else{
            if(mMoringList != null && mMoringList.size() > 0){
                setData2(mMoringList);
                mMoringNewsAdapter.loadMoreComplete();
                mMoringNewsAdapter.addData(teList);
            }else{
                //已为加载更多无更多数据
                mMoringNewsAdapter.loadMoreEnd();
            }
        }

    }

    List<MoringAllBean.MoringBean> teList = new ArrayList<>();
    private void setData2(List<MoringAllBean.MoringBean> moringList) {
        teList.clear();
        teList.addAll(moringList);
        if(page==1){
            moringList.addAll(teList);
        }
    }





    //初始化布局管理器
    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mMoringNewsAdapter = new MoringNewsAdapter(mMoringList);
        mRecyclerView.setAdapter(mMoringNewsAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {

        mMoringNewsAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            String audio =  mMoringNewsAdapter.getData().get(position).getVideo();
            String title =  mMoringNewsAdapter.getData().get(position).getTitle();
            String newid =  mMoringNewsAdapter.getData().get(position).getAid();
            EventBus.getDefault().post(new AudioEvent(audio,title,newid));
        });


        mMoringNewsAdapter.setOnLoadMoreListener(() -> {
            ++page;
            mplist();
        }, mRecyclerView);
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
