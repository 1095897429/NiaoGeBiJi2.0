package com.qmkj.niaogebiji.module.activity;

import android.animation.ObjectAnimator;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.AuthorAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.ActicleAllBean;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.IndexBulltin;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.event.UpdateHomeListEvent;
import com.qmkj.niaogebiji.module.event.toFlashEvent;
import com.qmkj.niaogebiji.module.widget.header.XnClassicsHeader;
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

    private int pageSize = 10 ;

    private String catid;

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
        catid = getIntent().getStringExtra("catid");
        tv_title.setText("内容运营");
        initLayout();
        initSamrtLayout();
        catlist();
    }


    private void catlist() {
        KLog.e("tag","当前的页数是 " + page + "");
        Map<String,String> map = new HashMap<>();
        map.put("page_no",page + "");
        map.put("page_size",pageSize + "");
        map.put("catid",catid + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().catlist(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<ActicleAllBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<ActicleAllBean> response) {

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }

                        ActicleAllBean temp  = response.getReturn_data();
                        List<RecommendBean.Article_list> articles = temp.getList();
                        if(null != articles && !articles.isEmpty()){
                            if(1 == page){
                                setActicleData(articles);
                                mFirstItemNewAdapter.setNewData(mAllList);
                                //如果第一次返回的数据不满10条，则显示无更多数据
                                if(articles.size() < Constant.SEERVER_NUM){
                                    mFirstItemNewAdapter.loadMoreEnd();
                                }
                            }else{
                                //已为加载更多有数据
                                if(articles != null && articles.size() > 0){
                                    setActicleData(articles);
                                    mFirstItemNewAdapter.addData(tempList);
                                    mFirstItemNewAdapter.loadMoreComplete();
                                }else{
                                    //已为加载更多无更多数据
                                    mFirstItemNewAdapter.loadMoreEnd();
                                }
                            }
                        }
                    }
                });
    }

    List<MultiNewsBean> tempList = new ArrayList<>();
    private void setActicleData(List<RecommendBean.Article_list> article_lists) {

        tempList.clear();
        RecommendBean.Article_list itemBean;
        MultiNewsBean bean1 ;

        String pic_type;
        for (int i = 0; i < article_lists.size(); i++) {
            itemBean = article_lists.get(i);
            bean1 = new MultiNewsBean();
            pic_type = article_lists.get(i).getPic_type();
            if("1".equals(pic_type)){
                bean1.setItemType(1);
            }else if("2".equals(pic_type)){
                bean1.setItemType(3);
            }else if("3".equals(pic_type)){
                bean1.setItemType(2);
            }else{
                bean1.setItemType(1);
            }
            bean1.setNewsActicleList(itemBean);
            tempList.add(bean1);
        }

        if(page == 1){
            mAllList.addAll(tempList);
        }
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
            catlist();
        },mRecyclerView);

        //点击事件
        mFirstItemNewAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","点击的是 position " + position );

            if(StringUtil.isFastClick()){
                return;
            }
            int type = mFirstItemNewAdapter.getData().get(position).getItemType();
            if(type == 1){
                String aid = mFirstItemNewAdapter.getData().get(position).getNewsActicleList().getAid();
                if (!TextUtils.isEmpty(aid)) {
                    UIHelper.toNewsDetailActivity(this, aid);
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
            catlist();
        });
    }


    @OnClick({R.id.iv_back})
    public void functions(View view){
        if(view.getId() == R.id.iv_back){
            finish();
        }
    }


}
