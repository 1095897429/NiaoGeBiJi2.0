package com.qmkj.niaogebiji.module.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.SchoolBaiduAdapter;
import com.qmkj.niaogebiji.module.adapter.SchoolBookAdapter;
import com.qmkj.niaogebiji.module.adapter.SchoolTestAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolItemAdapter;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
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
 * 创建时间 2019-11-21
 * 描述:学院Fragment
 */
public class SchoolFragment extends BaseLazyFragment {

    @BindView(R.id.recycler00)
    RecyclerView recycler00;

    @BindView(R.id.recycler11)
    RecyclerView recycler11;

    @BindView(R.id.recycler22)
    RecyclerView recycler22;


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
    protected int getLayoutId() {
        return R.layout.fragment_school;
    }


    @Override
    protected void initView() {
        initLayout0();
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
                        KLog.d("tag","response " + response.getReturn_code());
                        mSchoolBean  = response.getReturn_data();
                        setData();
                    }
                });
    }

    private void setData() {
        if(null != mSchoolBean){
            mSchoolBaiDus.addAll(mSchoolBean.getWiki());
            mSchoolTests.addAll(mSchoolBean.getCates());
            mSchoolBooks.addAll(mSchoolBean.getCourse());
        }

        mSchoolBaiduAdapter.setNewData(mSchoolBaiDus);
        mSchoolTestAdapter.setNewData(mSchoolTests);
        mSchoolBookAdapter.setNewData(mSchoolBooks);
    }

    private void getData() {

//        SchoolBean.SchoolBaiDu baiDu;
//        for (int i = 0; i < 5; i++) {
//            baiDu = new SchoolBean.SchoolBaiDu();
//            baiDu.setImg(imgs[i]);
//            baiDu.setName(names[i]);
//            mSchoolBaiDus.add(baiDu);
//        }
//
//        SchoolBean.SchoolTest test;
//        for (int i = 0; i < 6; i++) {
//            test = new SchoolBean.SchoolTest();
//            mSchoolTests.add(test);
//        }
//
//        SchoolBean.SchoolBook book;
//        for (int i = 0; i < 6; i++) {
//            book = new SchoolBean.SchoolBook();
//            mSchoolBooks.add(book);
//        }

    }

    private void initEvent() {
        mSchoolBaiduAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","根据cateid 去wiki");
            SchoolBean.SchoolBaiDu temp =  mSchoolBaiduAdapter.getData().get(position);
            if(!TextUtils.isEmpty(temp.getCate_id() + "")){
                String link = StringUtil.getLink("wikilist/" + temp.getCate_id());
                UIHelper.toWebViewActivity(getActivity(),link);
            }
        });

        mSchoolTestAdapter.setOnItemClickListener((adapter, view, position) -> {
            SchoolBean.SchoolTest temp = mSchoolTestAdapter.getData().get(position);
            SchoolBean.Record tempRecord =  temp.getRecord();
            if("0".equals(tempRecord.getIs_tested() + "")){
                UIHelper.toTestDetailActivity(getActivity(),temp);
            }else if("1".equals(tempRecord.getIs_tested() + "")){
                String score =  tempRecord.getScore();
                String passScore = temp.getPass_score();
                if(score.compareTo(passScore) < 0){
                    KLog.d("tag","不及格");
                    UIHelper.toTestResultFailActivity(getActivity(),temp);
                }else{
                    UIHelper.toTestResultActivity(getActivity(),temp);
                }
            }


        });

        mSchoolBookAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","去课程");
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
                UIHelper.toTestListActivity(getActivity());
                break;
            default:
        }
    }












    @Override
    public void initData() {

    }



}
