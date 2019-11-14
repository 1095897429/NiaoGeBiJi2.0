package com.qmkj.niaogebiji.module.activity;

import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.adapter.MoringNewsAdapter;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MoringNewsBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:早报列表
 */
public class MoringNewsListActivity extends BaseActivity {

    @BindView(R.id.text)
    TextView text;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    MoringNewsAdapter mMoringNewsAdapter;
    //集合
    List<MoringNewsBean> mList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_moringnewlist;
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

        initLayout();
    }


    //初始化布局管理器
    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mMoringNewsAdapter = new MoringNewsAdapter(mList);
        mRecyclerView.setAdapter(mMoringNewsAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {
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
