package com.qmkj.niaogebiji.module.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.adapter.FocusAdapter;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-14
 * 描述:作者列表
 */
public class AuthorListActivity extends BaseActivity {



    @Override
    protected int getLayoutId() {
        return R.layout.activity_author_list;
    }

    @Override
    protected void initView() {

    }


    /** --------------------------------- 通用的配置  ---------------------------------*/
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    FocusAdapter mFocusAdapter;
    //组合集合
    private List<AuthorBean.Author> mAuthors;
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;
    private int page = 1;
    //作者列表
    private List<IndexFocusBean.Auther_list> mAuther_lists;
    //文章列表
    private List<IndexFocusBean.Article_list> mArticle_lists;

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mFocusAdapter = new FocusAdapter(mAllList);
        mRecyclerView.setAdapter(mFocusAdapter);

        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);

        initEvent();

    }

    private void initEvent() {
        mFocusAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            UIHelper.toLoginActivity(getActivity());
        });
        mFocusAdapter.setOnItemClickListener((adapter, view, position) -> {
            int type = adapter.getItemViewType(position);
            switch (type) {
                case FocusAdapter.RIGHT_IMG_TYPE:
                    break;
                default:
            }
        });
    }


    private void initSamrtLayout() {
        smartRefreshLayout.setEnableLoadMore(false);
        //下拉刷新
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            getIndexArticle();
        });

    }
}
