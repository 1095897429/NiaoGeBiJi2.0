package com.qmkj.niaogebiji.module.activity;

import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.adapter.CategoryAdapter;
import com.qmkj.niaogebiji.module.bean.ChannelBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述: 文章分类
 */
public class CategoryActivity extends BaseActivity {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    CategoryAdapter mCategoryAdapter;
    //集合
    List<ChannelBean> mList = new ArrayList<>();
    //布局管理器
    GridLayoutManager mGridLayoutManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_catogory;
    }

    @Override
    protected void initView() {

        ChannelBean channelBean;
        for (int i = 0; i < 10; i++) {
            channelBean = new ChannelBean();
            mList.add(channelBean);
        }
        
        initLayout();
    }


    @Override
    public void initData() {
        //透明度
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.alpha = 0.5f;
//        getWindow().setAttributes(lp);
        //alpha在0.0f到1.0f之间。
    }

    //初始化布局管理器
    private void initLayout() {
        mGridLayoutManager = new GridLayoutManager(this,3);
        //设置默认垂直布局
        mGridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        //设置适配器
        mCategoryAdapter = new CategoryAdapter(mList);
        mRecyclerView.setAdapter(mCategoryAdapter);
        initEvent();

    }

    private void initEvent() {
        mCategoryAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","点击的索引 " + position);
            UIHelper.toCategoryListActivity(this);
        });
    }


    @OnClick({R.id.toDown})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.toDown:
                finish();
                //参数一：Activity1进入动画，参数二：Activity2退出动画
                overridePendingTransition(R.anim.activity_alpha_enter, R.anim.activity_exit_bottom);
                break;

            default:
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_alpha_enter, R.anim.activity_exit_bottom);
    }


}
