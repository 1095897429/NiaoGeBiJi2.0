package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.module.adapter.TestItemAdapter;
import com.qmkj.niaogebiji.module.adapter.TestLaunchItemAdapter;
import com.qmkj.niaogebiji.module.adapter.ToolItemAdapter;
import com.qmkj.niaogebiji.module.bean.TestAllBean;
import com.qmkj.niaogebiji.module.bean.TestBean;
import com.qmkj.niaogebiji.module.bean.ToolBean;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-22
 * 描述:开始测试
 */
public class TestLauchActivity extends BaseActivity {


    @BindView(R.id.current_page)
    TextView current_page;

    @BindView(R.id.total)
    TextView total;




    @BindView(R.id.toNext)
    TextView toNext;

    @BindView(R.id.toSubmit)
    TextView toSubmit;


    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.ll_conent)
    LinearLayout ll_conent;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;


    //适配器
    TestLaunchItemAdapter mTestLaunchItemAdapter;
    //组合集合
    List<TestBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    //此类包含所有的选项
    private TestAllBean mTestAllBean;

    private String answer_no;

    //总共有10题
    private int totalNum = 5;
    //当前的题数
    private int currentNum = 1;

    private String [] contents = new String[]{
            "A 留存是延长用户生命周期",
            "B 留存是延长用户生命周期",
            "C 留存是延长用户生命周期",
            "D 留存是延长用户生命周期"};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_launch;
    }

    @Override
    protected void initView() {
        tv_title.setText("初级ASO测试");
        total.setText(" /" + totalNum);
        initLayout();
        getData(1);

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        current_page.setTypeface(typeface);
        total.setTypeface(typeface);
    }

    private void getData(int currentNum) {
        toNext.setEnabled(false);
        toSubmit.setEnabled(false);
        mAllList.clear();

        if(currentNum == totalNum){
            toSubmit.setVisibility(View.GONE);
            toNext.setText("交卷");

        }

        if(currentNum == 1){
            TestBean bean1;
            for (int i = 0; i < 4; i++) {
                bean1 = new TestBean();
                bean1.setAnswer(" 留存是延长用户生命周期" + i);
                mAllList.add(bean1);
            }
        }else{
            TestBean bean1;
            for (int i = 0; i < 4; i++) {
                bean1 = new TestBean();
                bean1.setAnswer(" 我是题目" + currentNum);
                mAllList.add(bean1);
            }
        }

        mTestLaunchItemAdapter.setNewData(mAllList);
    }



    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mTestLaunchItemAdapter = new TestLaunchItemAdapter(mAllList);
        mRecyclerView.setAdapter(mTestLaunchItemAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {
        mTestLaunchItemAdapter.setOnItemClickListener((adapter, view, position) -> {
                List<TestBean> mDatas = adapter.getData();
                //① 将所有的selected设置false，当前点击的设为true
                for (TestBean data : mDatas) {
                    data.setSelect(false);
                }
                TestBean temp = mDatas.get(position);
                temp.setSelect(true);
                toNext.setEnabled(true);
                toSubmit.setEnabled(true);
                mTestLaunchItemAdapter.notifyDataSetChanged();
        });
    }



    @OnClick({R.id.iv_back,R.id.toNext})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.toNext:
                //最后一题
                if(totalNum == currentNum){
                    KLog.d("tag","交卷了");
                    return;
                }
                //更换数据源
                ++currentNum;
                current_page.setText(currentNum + "");
                getData(currentNum);
                break;
            case R.id.iv_back:
                finish();
                break;

            default:
        }
    }





}
