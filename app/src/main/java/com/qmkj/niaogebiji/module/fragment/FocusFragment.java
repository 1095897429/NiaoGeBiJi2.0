package com.qmkj.niaogebiji.module.fragment;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.activity.AuthorListActivity;
import com.qmkj.niaogebiji.module.adapter.FocusAdapter;
import com.qmkj.niaogebiji.module.bean.FouBBBB;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.event.UpdateHomeListEvent;
import com.qmkj.niaogebiji.module.event.toRefreshEvent;
import com.qmkj.niaogebiji.module.widget.header.XnClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:  去关注作者文章接口前两条数据，没有则不显示
 *  1.猜你喜欢没有加载更多
 *
 */
public class FocusFragment extends BaseLazyFragment {

    public static FocusFragment getInstance(String chainId, String chainName) {
        FocusFragment newsItemFragment = new FocusFragment();
        Bundle args = new Bundle();
        args.putString("chainId", chainId);
        args.putString("chainName", chainName);
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.firtst_focus_item;
    }

    @Override
    protected void initView() {
        initSamrtLayout();
        getIndexArticle();
        initLayout();
    }

    @Override
    protected void lazyLoadData() {

    }


    List<IndexFocusBean.Article_list> guessYouLikeList ;

    private void recommendAuthorArticleList() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().recommendAuthorArticleList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<IndexFocusBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<IndexFocusBean> response) {
                       IndexFocusBean temp = response.getReturn_data();
                        guessYouLikeList = temp.getArticle_list();
                        listCommonLogic();
                    }
                });
    }


    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //适配器
    FocusAdapter mFocusAdapter;
    //组合集合
    List<MultiNewsBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;
    private int page = 1;
    //单个作者
    private IndexFocusBean.Auther_list mAuthor;
    //作者列表
    private List<IndexFocusBean.Auther_list> mAuther_lists;
    //文章列表
    private List<IndexFocusBean.Article_list> mArticle_lists;

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
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
        mFocusAdapter.setAuthorCancleListener(position -> showCancelFocusDialog(position));

        mFocusAdapter.setAuthorDetailListener(position -> {

            String link = Constant.TEST_URL + "authordetail/" + mAuther_lists.get(position);
            UIHelper.toWebViewActivity(getActivity(),link);
        });

        mFocusAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.more_author:
                    UIHelper.toAuthorListActivity(getActivity());
                    break;
                default:
            }
        });
        mFocusAdapter.setOnItemClickListener((adapter, view, position) -> {
            int type = adapter.getItemViewType(position);
            switch (type) {
                case FocusAdapter.RIGHT_IMG_TYPE:
                    KLog.d("tag",mFocusAdapter.getData().get(position).getArticleList());
                    String aid = mFocusAdapter.getData().get(position).getArticleList().getAid();
                    if (!TextUtils.isEmpty(aid)) {
                        UIHelper.toNewsDetailActivity(getActivity(), aid);
                    }
                    break;
                default:
            }
        });
    }


    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);

        smartRefreshLayout.setEnableLoadMore(false);
        //下拉刷新
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            getIndexArticle();
        });

    }


    //from =1 代表来自主界面推荐关注里的关注  from = 0 下拉刷新的请求
    private void getIndexArticle() {
        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getIndexArticle(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<IndexFocusBean>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<IndexFocusBean> response) {
                        IndexFocusBean mIndexFocusBean = response.getReturn_data();
                        if(null != mIndexFocusBean){
                            if(null != smartRefreshLayout){
                                smartRefreshLayout.finishRefresh();
                            }
                            mAuther_lists =  mIndexFocusBean.getAuther_list();
                            mArticle_lists = mIndexFocusBean.getArticle_list();
                            //未关注文章时，请求猜你喜欢接口
                            if(mArticle_lists.isEmpty()){
                                recommendAuthorArticleList();
                            }else{
                                listCommonLogic();
                            }

                        }
                    }

                    @Override
                    public void onHintError(String errorMes) {
                        super.onHintError(errorMes);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onNetFail(String msg) {
                        super.onNetFail(msg);
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                    }
                });
    }

    private void listCommonLogic() {

        int size ;
        //取头两条数据
        if(!mArticle_lists.isEmpty()){
            size = mArticle_lists.size();
            if(size > 2){
                for (int i = 0; i < 2; i++) {
                    MultiNewsBean bean = new MultiNewsBean();
                    bean.setItemType(2);
                    bean.setArticleList(mArticle_lists.get(i));
                    mAllList.add(bean);
                }
            }else{
                for (int i = 0; i < mArticle_lists.size(); i++) {
                    MultiNewsBean bean = new MultiNewsBean();
                    bean.setItemType(2);
                    bean.setArticleList(mArticle_lists.get(i));
                    mAllList.add(bean);
                }
            }
        }

        //推荐关注
        MultiNewsBean bean1 = new MultiNewsBean();
        FouBBBB bbbb = new FouBBBB();
        bbbb.setDjj(mAuther_lists);
        bean1.setFouBBBB(bbbb);
        bean1.setItemType(1);
        mAllList.add(bean1);

        //取不是第二条的数据
        if(!mArticle_lists.isEmpty()){
            for (int i = 2; i < mArticle_lists.size(); i++) {
                MultiNewsBean bean = new MultiNewsBean();
                bean.setItemType(2);
                bean.setArticleList(mArticle_lists.get(i));
                mAllList.add(bean);
            }
        }

        if(null != guessYouLikeList && !guessYouLikeList.isEmpty()){

            //猜你喜欢布局
            MultiNewsBean bean2 = new MultiNewsBean();
            bean2.setItemType(3);
            mAllList.add(bean2);

            //猜你喜欢数据
            for (int i = 0; i < guessYouLikeList.size(); i++) {
                MultiNewsBean bean = new MultiNewsBean();
                bean.setItemType(2);
                bean.setArticleList(guessYouLikeList.get(i));
                mAllList.add(bean);
            }
        }


        if(1 == page){
            mFocusAdapter.setNewData(mAllList);
        }else{
            //已为加载更多有数据
            if(mAllList != null && mAllList.size() > 0){
                mFocusAdapter.loadMoreComplete();
                mFocusAdapter.addData(mAllList);
            }else{
                //已为加载更多无更多数据
                mFocusAdapter.loadMoreEnd();
            }
        }

    }


    /** 点击关注，取关回调此方法 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(UpdateHomeListEvent event){
        KLog.d("tag","更新啦更新啦");
        mAllList.clear();
        page = 1;
        getIndexArticle();
    }



    /** --------------------------------- 关注 取关  ---------------------------------*/
    //1-去关注，0-取消关注
    private String focus_type = "1";
    private String author_id;

    public void showCancelFocusDialog(int position){
        String name ;
        String author;
        mAuthor = mAuther_lists.get(position);
        author_id = mAuthor.getId();
        author = mAuthor.getName();
        if(1 == mAuthor.getIs_follow()){
            name = "取消";
            focus_type = "0";
            final FocusAlertDialog iosAlertDialog = new FocusAlertDialog(getActivity()).builder();
            iosAlertDialog.setPositiveButton("确定", v -> {
                followAuthor();
            }).setNegativeButton("取消", v -> {}).setMsg("确定要 " + name +"关注「" + author  +"」").setCanceledOnTouchOutside(false);
            iosAlertDialog.show();
        }else{
            focus_type = "1";
            followAuthor();
        }

    }

    private void followAuthor() {
        Map<String,String> map = new HashMap<>();
        map.put("type",focus_type);
        map.put("id",author_id);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().followAuthor(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<IndexFocusBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<IndexFocusBean> response) {
                        //同时刷新下方的文章
                        mAllList.clear();
                        page = 1;
                        getIndexArticle();
                    }
                });
    }



    /** 点击首页按钮，当呈现时刷新 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshBus(toRefreshEvent event){
        if(getUserVisibleHint()){
            KLog.d("tag","我是关注界面，我刷新了");
            mRecyclerView.scrollToPosition(0);
            smartRefreshLayout.autoRefresh();
        }
    }


}
