package com.qmkj.niaogebiji.module.activity;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.CategoryAdapter;
import com.qmkj.niaogebiji.module.bean.CateAllBean;
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
 * 创建时间 2019-11-12
 * 描述: 文章分类
 */
public class CategoryActivity extends BaseActivity {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    CategoryAdapter mCategoryAdapter;
    //集合
    List<CateAllBean.CateBean> mList = new ArrayList<>();
    //布局管理器
    GridLayoutManager mGridLayoutManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_catogory;
    }

    @Override
    protected void initView() {

        initLayout();

        allCategory();

    }

    private void allCategory() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().allCategory(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<CateAllBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<CateAllBean> response) {
                        CateAllBean bean = response.getReturn_data();
                        if(bean != null){
                            setData(bean.getList());
                        }
                    }
                });
    }

    private void setData(List<CateAllBean.CateBean> list) {

        if(null != list && !list.isEmpty()){
            mList.addAll(list);
        }

        mCategoryAdapter.setNewData(mList);
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
            int catid = mCategoryAdapter.getData().get(position).getCatid();
            UIHelper.toCategoryListActivity(this,catid + "");
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
