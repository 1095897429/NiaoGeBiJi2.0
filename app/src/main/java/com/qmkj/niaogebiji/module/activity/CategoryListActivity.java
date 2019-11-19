package com.qmkj.niaogebiji.module.activity;

import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.AuthorAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.event.UpdateHomeListEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
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
 * 创建时间 2019-11-14
 * 描述:分类列表 -- 布局 + Bean 采用 文章的样式
 */
public class CategoryListActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_catogory_list;
    }

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected void initView() {
        tv_title.setText("内容运营");
        initLayout();
        initSamrtLayout();
        getData();
    }




    private void getData() {

        NewsItemBean itemBean;
        FirstItemBean firstItemBean;
        MultiNewsBean bean1 ;
        for (int i = 0; i < 10; i++) {
            itemBean = new NewsItemBean();
            bean1 = new MultiNewsBean();
            if(i == 4){
                bean1.setItemType(2);
            }else if(i == 5){
                bean1.setItemType(3);
            }else{
                bean1.setItemType(1);
            }

            bean1.setNewsItemBean(itemBean);
            mAllList.add(bean1);
        }
        mFirstItemNewAdapter.setNewData(mAllList);

    }



    /** --------------------------------- 通用的配置  ---------------------------------*/
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    FirstItemNewAdapter mFirstItemNewAdapter;
    //组合集合
    List<MultiNewsBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;
    private int page = 1;

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mFirstItemNewAdapter = new FirstItemNewAdapter(mAllList);
        mRecyclerView.setAdapter(mFirstItemNewAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }

    private void initEvent() {

        mFirstItemNewAdapter.setOnLoadMoreListener(() -> {
            ++page;
            getData();
        },mRecyclerView);

        //点击事件
        mFirstItemNewAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","点击的是 position " + position );
        });

    }


    private void initSamrtLayout() {
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            getData();
        });
    }


    @OnClick({R.id.iv_back})
    public void functions(View view){
        if(view.getId() == R.id.iv_back){
            finish();
        }
    }


}
