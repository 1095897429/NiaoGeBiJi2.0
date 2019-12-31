package com.qmkj.niaogebiji.module.activity;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.TestItemAdapter;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.event.TestListEvent;
import com.qmkj.niaogebiji.module.widget.header.XnClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
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
 * 创建时间 2019-11-22
 * 描述:测试列表
 */
public class TestListActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_testlist;
    }

    @Override
    protected void initView() {
        tv_title.setText("测一测 得徽章");
        initLayout();
        initSamrtLayout();
        testCateList();
    }

    @Override
    protected boolean regEvent() {
        return true;
    }

    private void testCateList() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().testCateList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<SchoolBean.SchoolTest>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<SchoolBean.SchoolTest>> response) {
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }

                        List<SchoolBean.SchoolTest> list = response.getReturn_data();
                        if(null != list && !list.isEmpty()){

                            if(1 == page){
                                mTestItemAdapter.setNewData(list);
                            }else{
                                //已为加载更多有数据
                                if(list != null && list.size() > 0){
                                    mTestItemAdapter.loadMoreComplete();
                                    mTestItemAdapter.addData(list);
                                }else{
                                    //已为加载更多无更多数据
                                    mTestItemAdapter.loadMoreEnd();
                                }
                            }
                        }
                    }

                    @Override
                    public void onNetFail(String msg) {
                        super.onNetFail(msg);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                    }

                    @Override
                    public void onHintError(String return_code, String errorMes) {
                        super.onHintError(return_code, errorMes);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                    }
                });
    }


    /** --------------------------------- 通用的配置  ---------------------------------*/
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    TestItemAdapter mTestItemAdapter;
    //组合集合
    List<SchoolBean.SchoolTest> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;
    private int page = 1;

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mTestItemAdapter = new TestItemAdapter(mAllList);
        mRecyclerView.setAdapter(mTestItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {

//        mTestItemAdapter.setOnLoadMoreListener(() -> {
//            ++page;
//            testCateList();
//        },mRecyclerView);


        //点击事件
        mTestItemAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","点击的是 position " + position );
            if(StringUtil.isFastClick()){
                return;
            }
            MobclickAgentUtils.onEvent("academy_testlist_testlist" + (position + 1) +"_2_0_0");

            SchoolBean.SchoolTest temp = mTestItemAdapter.getData().get(position);
            SchoolBean.Record tempRecord =  temp.getRecord();
            if("0".equals(tempRecord.getIs_tested() + "")){
                UIHelper.toTestDetailActivity(this,temp);
            }else if("1".equals(tempRecord.getIs_tested() + "")){
                temp.setMyScore(tempRecord.getScore());
                if(Integer.parseInt(tempRecord.getScore()) < Integer.parseInt(temp.getPass_score())){
                    KLog.d("tag","不及格");
                    UIHelper.toTestResultFailActivity(this,temp);
                }else{
                    UIHelper.toTestResultActivity(this,temp);
                }
            }

        });

    }


    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(this);
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            testCateList();
        });
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



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTestListEvent(TestListEvent event){
        mAllList.clear();
        page = 1;
        testCateList();
    }
}
