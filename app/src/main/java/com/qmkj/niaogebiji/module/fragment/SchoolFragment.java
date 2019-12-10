package com.qmkj.niaogebiji.module.fragment;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.adapter.SchoolBaiduAdapter;
import com.qmkj.niaogebiji.module.adapter.SchoolBookAdapter;
import com.qmkj.niaogebiji.module.adapter.SchoolTestAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolItemAdapter;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
        getData();
        initEvent();

    }

    private void getData() {

        SchoolBean.SchoolBaiDu baiDu;
        for (int i = 0; i < 5; i++) {
            baiDu = new SchoolBean.SchoolBaiDu();
            baiDu.setImg(imgs[i]);
            baiDu.setName(names[i]);
            mSchoolBaiDus.add(baiDu);
        }

        SchoolBean.SchoolTest test;
        for (int i = 0; i < 6; i++) {
            test = new SchoolBean.SchoolTest();
            mSchoolTests.add(test);
        }

        SchoolBean.SchoolBook book;
        for (int i = 0; i < 6; i++) {
            book = new SchoolBean.SchoolBook();
            mSchoolBooks.add(book);
        }

    }

    private void initEvent() {
        mSchoolBaiduAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","去wiki");
            UIHelper.toWebViewActivity(getActivity(),"www.baidu.com");
        });

        mSchoolTestAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","去测试");
            UIHelper.toTestDetailActivity(getActivity());
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
