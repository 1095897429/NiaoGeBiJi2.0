package com.qmkj.niaogebiji.module.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.LiveHouseActivity;
import com.qmkj.niaogebiji.module.adapter.SchoolBaiduAdapter;
import com.qmkj.niaogebiji.module.adapter.SchoolBookAdapter;
import com.qmkj.niaogebiji.module.adapter.SchoolTestAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolItemAdapter;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.event.TestListEvent;
import com.qmkj.niaogebiji.module.widget.header.XnClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.vhall.uilibs.Param;
import com.vhall.uilibs.util.VhallUtil;
import com.vhall.uilibs.watch.WatchActivity;

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
 * 创建时间 2019-11-21
 * 描述:学院Fragment
 */
public class SchoolFragment extends BaseLazyFragment {

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;


    @BindView(R.id.recycler00)
    RecyclerView recycler00;

    @BindView(R.id.recycler11)
    RecyclerView recycler11;

    @BindView(R.id.recycler22)
    RecyclerView recycler22;

    @BindView(R.id.part3333)
    LinearLayout part3333;


    @BindView(R.id.search_first)
    TextView search_first;


    @BindView(R.id.test_title)
    TextView test_title;


    @BindView(R.id.school_title)
    TextView school_title;



    SchoolBaiduAdapter mSchoolBaiduAdapter;
    SchoolTestAdapter mSchoolTestAdapter;
    SchoolBookAdapter mSchoolBookAdapter;
    //布局管理器
    GridLayoutManager mGridLayoutManager;
    LinearLayoutManager mLinearLayoutManager;

    private List<SchoolBean.SchoolBaiDu> mSchoolBaiDus = new ArrayList<>();

    private List<SchoolBean.SchoolTest> mSchoolTests = new ArrayList<>();

    private List<SchoolBean.SchoolBook> mSchoolBooks = new ArrayList<>();

    private SchoolBean mSchoolBean;


    private int [] imgs = new int[]{R.mipmap.icon_school_1,R.mipmap.icon_school_2,R.mipmap.icon_school_3,
            R.mipmap.icon_school_4,R.mipmap.icon_school_5};

    private String [] names = new String[]{"初入职场","营销基础","营销进阶","产品研发","视野格局"};

    public static SchoolFragment getInstance() {
        return new SchoolFragment();
    }

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_school;
    }

    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mSchoolBaiDus.clear();
            mSchoolTests.clear();
            mSchoolBooks.clear();
            schoolindex();
        });
    }


    @BindView(R.id.loading_dialog)
    LinearLayout loading_dialog;

    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottieAnimationView;

    public void showWaitingDialog() {
        loading_dialog.setVisibility(View.VISIBLE);
        lottieAnimationView.setImageAssetsFolder("images");
        lottieAnimationView.setAnimation("images/loading.json");
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();
    }

    /**
     * 隐藏等待提示框
     */
    public void hideWaitingDialog() {
        if(null != lottieAnimationView){
            loading_dialog.setVisibility(View.GONE);
            lottieAnimationView.cancelAnimation();
        }
    }



    @Override
    protected void initView() {

        if(!TextUtils.isEmpty(Constant.firstSearchName)){
            search_first.setHint(Constant.firstSearchName);
        }

        showWaitingDialog();
        initLayout0();
        initSamrtLayout();
        initLayout1();
        initLayout2();
        initEvent();
        schoolindex();
    }


    private void schoolindex() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().index(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SchoolBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<SchoolBean> response) {

                        hideWaitingDialog();

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }

                        mSchoolBean  = response.getReturn_data();
                        setData();
                    }

                    @Override
                    public void onNetFail(String msg) {
                        hideWaitingDialog();
                    }
                });
    }


    private void initLayout0() {

        mGridLayoutManager = new GridLayoutManager(getActivity(),5);
        //设置布局管理器
        recycler00.setLayoutManager(mGridLayoutManager);
        mSchoolBaiduAdapter = new SchoolBaiduAdapter(mSchoolBaiDus);
        recycler00.setAdapter(mSchoolBaiduAdapter);
        //解决数据加载不完
        recycler00.setNestedScrollingEnabled(true);
        recycler00.setHasFixedSize(true);
    }


    private void setData() {
        if(null != mSchoolBean){
            mSchoolBaiDus.addAll(mSchoolBean.getWiki());

            if(mSchoolBaiDus.size() > 0){
                mGridLayoutManager = new GridLayoutManager(getActivity(),mSchoolBaiDus.size());
                //设置布局管理器
                recycler00.setLayoutManager(mGridLayoutManager);
            }

            mSchoolTests.addAll(mSchoolBean.getCates());
            if(mSchoolBean.getCourse() != null && !mSchoolBean.getCourse().isEmpty()){
                mSchoolBooks.addAll(mSchoolBean.getCourse());
                part3333.setVisibility(View.VISIBLE);
            }else{
                part3333.setVisibility(View.GONE);
            }
        }

        mSchoolBaiduAdapter.setNewData(mSchoolBaiDus);
        mSchoolTestAdapter.setNewData(mSchoolTests);
        mSchoolBookAdapter.setNewData(mSchoolBooks);
    }

    private void initEvent() {
        mSchoolBaiduAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","根据cateid 去wiki");

            MobclickAgentUtils.onEvent("academy_wiki_wikicate" + (position + 1) +"_2_0_0");

            SchoolBean.SchoolBaiDu temp =  mSchoolBaiduAdapter.getData().get(position);
            if(!TextUtils.isEmpty(temp.getCate_id() + "")){
                if(StringUtil.isFastClick()){
                    return;
                }
                String link = StringUtil.getLink("wikilist/" + temp.getCate_id());
                UIHelper.toWebViewActivityWithOnLayout(getActivity(),link,"");
            }
        });

        mSchoolTestAdapter.setOnItemClickListener((adapter, view, position) -> {
            if(StringUtil.isFastClick()){
                return;
            }

            MobclickAgentUtils.onEvent("academy_test_test" + (position + 1) +"_2_0_0");

            SchoolBean.SchoolTest temp = mSchoolTestAdapter.getData().get(position);
            SchoolBean.Record tempRecord =  temp.getRecord();
            if("0".equals(tempRecord.getIs_tested() + "")){
                UIHelper.toTestDetailActivity(getActivity(),temp);
            }else if("1".equals(tempRecord.getIs_tested() + "")){
                if(Integer.parseInt(tempRecord.getScore()) < Integer.parseInt(temp.getPass_score())){
                    KLog.d("tag","不及格");
                    temp.setMyScore(tempRecord.getScore());
                    UIHelper.toTestResultFailActivity(getActivity(),temp);
                }else{
                    temp.setMyScore(tempRecord.getScore());
                    UIHelper.toTestResultActivity(getActivity(),temp);
                }
            }


        });

        mSchoolBookAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","去课程");

            if(StringUtil.isFastClick()){
                return;
            }


            //TODO 3.30 去直播

            //注册登录
            if(position == 0){
                Param param = BaseApp.param;
                Intent intent = new Intent(getActivity(), WatchActivity.class);
                intent.putExtra("param", param);
                intent.putExtra("type", VhallUtil.WATCH_LIVE);
                startActivity(intent);

            }else{
                Param param = BaseApp.param;
                Intent intent = new Intent(getActivity(), WatchActivity.class);
                intent.putExtra("param", param);
                intent.putExtra("type", VhallUtil.WATCH_PLAYBACK);
                startActivity(intent);

            }



//            if(position <= 9){
//                MobclickAgentUtils.onEvent("academy_newcourse_" + (position + 1) +"_2_0_0");
//            }
//
//
//            SchoolBean.SchoolBook book = mSchoolBookAdapter.getData().get(position);
//            recordCourse(book.getId());
//
//            String link = book.getLink();
//            if(!TextUtils.isEmpty(link)){
//                UIHelper.toWebViewActivity(getActivity(),link);
//            }

        });
    }


    private void recordCourse(String course_id) {
        Map<String,String> map = new HashMap<>();
        map.put("course_id",course_id);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().recordCourse(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<HttpResponse<String>>() {
                    @Override
                    public void onSuccess(HttpResponse<String> response) {
                        KLog.d("tag","记录课程参与人数");
                    }

                    @Override
                    public void onNetFail(String msg) {

                    }
                });
    }



    private void initLayout2() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //设置布局管理器
        recycler22.setLayoutManager(mLinearLayoutManager);
        mSchoolBookAdapter = new SchoolBookAdapter(mSchoolBooks);
        recycler22.setAdapter(mSchoolBookAdapter);
        //解决数据加载不完
        recycler22.setNestedScrollingEnabled(true);
        recycler22.setHasFixedSize(true);
    }


    private void initLayout1() {
        mGridLayoutManager = new GridLayoutManager(getActivity(),3);
        //设置布局管理器
        recycler11.setLayoutManager(mGridLayoutManager);
        mSchoolTestAdapter = new SchoolTestAdapter(mSchoolTests);
        recycler11.setAdapter(mSchoolTestAdapter);
        //解决数据加载不完
        recycler11.setNestedScrollingEnabled(true);
        recycler11.setHasFixedSize(true);
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


    @OnClick({R.id.tomoretest,R.id.search_part})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.search_part:
                UIHelper.toSearchActivity(getActivity());
                break;
            case R.id.tomoretest:
                MobclickAgentUtils.onEvent(UmengEvent.academy_test_more_2_0_0);
                UIHelper.toTestListActivity(getActivity());
                break;
            default:
        }
    }



    @Override
    public void initData() {
       test_title.getPaint().setFakeBoldText(true);
       school_title.getPaint().setFakeBoldText(true);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTestListEvent(TestListEvent event){
        mSchoolBaiDus.clear();
        mSchoolTests.clear();
        mSchoolBooks.clear();
        schoolindex();
    }


}
