package com.qmkj.niaogebiji.module.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.airbnb.lottie.LottieAnimationView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.PersonUserInfoBean;
import com.qmkj.niaogebiji.module.event.BlogPriaseEvent;
import com.qmkj.niaogebiji.module.event.SendOkCircleEvent;
import com.qmkj.niaogebiji.module.widget.RecyclerViewNoBugLinearLayoutManager;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12.21
 * 描述:用户发表的圈子
 * 1.在链接后面自动加上一个/，为了方便解析
 *
 *      String content = "测试http://www.baidu.com/测测https://www.qq.com/\n再来一个 https://china.nba.com";
 *         //判断内容是否中link
 *         String regex = "https?://(?:[-\\w.]|(?:%[\\da-fA-F]{2}))+[^\\u4e00-\\u9fa5]+[\\w-_/?&=#%:]{0}";
 *         Matcher matcher = Pattern.compile(regex).matcher(content);
 *
 *         while (matcher.find()){
 *             int start =  matcher.start();
 *             int end = matcher.end();
 *             KLog.d("tag","start " + start + " end " + end);
 *             KLog.d("tag"," matcher.group() " +  matcher.group());
 *         }
 */
public class UserCircleFragment extends BaseLazyFragment {

    @BindView(R.id.backtop)
    ImageView backtop;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    @BindView(R.id.iv_empty)
    ImageView iv_empty;

    @BindView(R.id.tv_empty)
    TextView tv_empty;

    //页数
    private int page = 1;
    //适配器
    CircleRecommentAdapterNew mCircleRecommentAdapterNew;
    //组合集合
    List<CircleBean> mAllList = new ArrayList<>();
    //布局管理器
    RecyclerViewNoBugLinearLayoutManager mLinearLayoutManager;

    private String chainName;
    //传递uid
    private String  otherUid;

    public static UserCircleFragment getInstance(String chainId, String chainName) {
        UserCircleFragment newsItemFragment = new UserCircleFragment();
        Bundle args = new Bundle();
        args.putString("catid", chainId);
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
        return R.layout.fragment_circle_recommend;
    }

    @Override
    protected void initView() {
        otherUid = getArguments().getString("catid");
        chainName = getArguments().getString("chainName");
        showWaitingDialog();
        initSamrtLayout();
        initLayout();
    }

    @Override
    protected void lazyLoadData() {
        getUserBlog();
    }

    private void getUserBlog() {
        Map<String,String> map = new HashMap<>();
        map.put("uid",otherUid);
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getUserBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<CircleBean>>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<List<CircleBean>> response) {


                        if(null != smartRefreshLayout){
                            mIsRefreshing = false;
                            smartRefreshLayout.finishRefresh();
                        }
                            hideWaitingDialog();

                            List<CircleBean> serverData = response.getReturn_data();
                            if(1 == page){
                                if(serverData != null && !serverData.isEmpty()){
                                    setData2(serverData);
                                    mCircleRecommentAdapterNew.setNewData(mAllList);
                                    //如果第一次返回的数据不满10条，则显示无更多数据
                                    if(serverData.size() < Constant.SEERVER_NUM){
                                        mCircleRecommentAdapterNew.loadMoreEnd();
                                    }
                                    ll_empty.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                }else{
                                    KLog.d("tag","设置空布局");
                                    //第一次加载无数据
                                    ll_empty.setVisibility(View.VISIBLE);
                                    ((TextView)ll_empty.findViewById(R.id.tv_empty)).setText("暂无推荐内容");
                                    ((ImageView)ll_empty.findViewById(R.id.iv_empty)).setImageResource(R.mipmap.icon_empty_article);
                                    mRecyclerView.setVisibility(View.GONE);
                                }
                            }else{
                                //已为加载更多有数据
                                if(serverData != null && serverData.size() > 0){
                                    setData2(serverData);
                                    mCircleRecommentAdapterNew.loadMoreComplete();
                                    mCircleRecommentAdapterNew.addData(teList);
                                }else{
                                    //已为加载更多无更多数据
                                    mCircleRecommentAdapterNew.loadMoreEnd();
                                }
                            }
                    }
                });
    }




    List<CircleBean> teList = new ArrayList<>();
    private void setData2(List<CircleBean> list) {
        teList.clear();
        if(list != null){
            int type ;
            CircleBean temp;
            for (int i = 0; i < list.size(); i++) {
                temp  = list.get(i);
                //获取类型
                type = StringUtil.getCircleType(temp);
                //检查links同时添加原创文本
                if(temp != null && !TextUtils.isEmpty(temp.getBlog())){
                    StringUtil.addLinksData(temp);
                }

               if(type == CircleRecommentAdapterNew.ZF_TEXT ||
                    type == CircleRecommentAdapterNew.ZF_PIC ||
                       type == CircleRecommentAdapterNew.ZF_ACTICLE ||
                            type == CircleRecommentAdapterNew.ZF_LINK){
                   //检查links同时添加到转发文本
                   StringUtil.addTransLinksData(temp);
                }

                //如果判断有空数据，则遍历下一个数据
                if(100 == type){
                    continue;
                }
                temp.setCircleType(type);
                teList.add(temp);
            }
            if(page == 1){
                mAllList.addAll(teList);
            }
        }
    }

    private void addTransLinksData(CircleBean temp) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> pcLinks = new ArrayList<>();
        //在文本的基础上，再检查一下有无link
        String regex =  "((http|https|ftp|ftps):\\/\\/)?([a-zA-Z0-9-]+\\.){1,5}(com|cn|net|org|hk|tw)((\\/(\\w|-)+(\\.([a-zA-Z]+))?)+)?(\\/)?(\\??([\\.%:a-zA-Z0-9_-]+=[#\\.%:a-zA-Z0-9_-]+(&amp;)?)+)?";
        Matcher matcher = Pattern.compile(regex).matcher(temp.getP_blog().getBlog());
        while (matcher.find()){
            int start =  matcher.start();
            int end = matcher.end();
            KLog.d("tag","start " + start + " end " + end);
            KLog.d("tag"," matcher.group() " +  matcher.group());
            sb.append(start).append(":").append(end).append(":");
            pcLinks.add(matcher.group());
        }
        temp.getP_blog().setPcLinks(pcLinks);
    }

    private void addLinksData(CircleBean temp) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> pcLinks = new ArrayList<>();
        //在文本的基础上，再检查一下有无link
        String regex =  "((http|https|ftp|ftps):\\/\\/)?([a-zA-Z0-9-]+\\.){1,5}(com|cn|net|org|hk|tw)((\\/(\\w|-)+(\\.([a-zA-Z]+))?)+)?(\\/)?(\\??([\\.%:a-zA-Z0-9_-]+=[#\\.%:a-zA-Z0-9_-]+(&amp;)?)+)?";
        Matcher matcher = Pattern.compile(regex).matcher(temp.getBlog());
        while (matcher.find()){
            int start =  matcher.start();
            int end = matcher.end();
            KLog.d("tag","start " + start + " end " + end);
            KLog.d("tag"," matcher.group() " +  matcher.group());
            sb.append(start).append(":").append(end).append(":");
            pcLinks.add(matcher.group());

        }
        temp.setPcLinks(pcLinks);
    }


    private void initLayout() {
        mLinearLayoutManager = new RecyclerViewNoBugLinearLayoutManager(getActivity());
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mCircleRecommentAdapterNew = new CircleRecommentAdapterNew(mAllList);
        mCircleRecommentAdapterNew.setChainName(chainName);
        mRecyclerView.setAdapter(mCircleRecommentAdapterNew);
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }


    private boolean mIsRefreshing;
    @SuppressLint("ClickableViewAccessibility")
    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {

            mCircleRecommentAdapterNew.notifyItemRangeChanged(0,mCircleRecommentAdapterNew.getData().size());
            mAllList.clear();
            mIsRefreshing = true;
            page = 1;
            getUserBlog();
        });

        mRecyclerView.setOnTouchListener(
                (v, event) -> {
                    if (mIsRefreshing) {
                        return true;
                    } else {
                        return false;
                    }
                }
        );
    }


    @SuppressLint("CheckResult")
    private void initEvent() {
        mCircleRecommentAdapterNew.setOnLoadMoreListener(() -> {
            ++page;
            getUserBlog();
        }, mRecyclerView);

    }


    /** --------------------------------- 发布帖子成功  ---------------------------------v*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendCircleEvent(SendOkCircleEvent event) {
        mAllList.clear();
        page = 1;
        mIsRefreshing = true;
        getUserBlog();
    }

//    private void initExitAnim(){
//        ObjectAnimator translationX = ObjectAnimator.ofFloat(ll_show, "translationY", 0f, SizeUtils.dp2px(-36f));
//        ObjectAnimator translationXX = ObjectAnimator.ofFloat(allpart, "translationY", 0f, SizeUtils.dp2px(-36f));
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(translationX,translationXX);
//        animatorSet.setDuration(1000);
//        animatorSet.start();
//        //动画的监听
//        animatorSet.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//                KLog.d("动画开始","");
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                showSendMsg.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//                KLog.d("动画取消","");
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//                KLog.d("动画重复","");
//            }
//        });
//    }



    private void initAnim() {
//        ObjectAnimator translationX = ObjectAnimator.ofFloat(showSendMsg, "scaleX", 1f, 1.1f, 1f);
//        ObjectAnimator alphaX = ObjectAnimator.ofFloat(showSendMsg, "alpha", 0, 1f);
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(translationX, alphaX);
//        animatorSet.setDuration(1000);
//        animatorSet.start();
//        //动画的监听
//        animatorSet.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//                KLog.d("动画开始","");
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                //动画结束跳转
////               new Handler().postDelayed(() -> initExitAnim(),1000);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//                KLog.d("动画取消","");
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//                KLog.d("动画重复","");
//            }
//        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBlogPriaseEvent(BlogPriaseEvent event) {
        if(getActivity() != null && mCircleRecommentAdapterNew != null){
//            mCircleRecommentAdapterNew.getData().get(event.getPosition()).setIs_like(event.getStauts());
//            mCircleRecommentAdapterNew.getData().get(event.getPosition()).setLike_num(event.getLikeNum());
//            mCircleRecommentAdapterNew.getData().get(event.getPosition()).setComment_num(event.getCommentNum());
//            mCircleRecommentAdapterNew.notifyItemChanged(event.getPosition());

            page = 1;
            mIsRefreshing = true;
            mAllList.clear();
            getUserBlog();

            //TODO 12.17 留给自己的疑问：出现情况是，如果是page = 2的 时候，会变化吗
        }
    }



    @BindView(R.id.loading_dialog)
    LinearLayout loading_dialog;

    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottieAnimationView;

    public void showWaitingDialog() {
        loading_dialog.setVisibility(View.VISIBLE);
        lottieAnimationView.setImageAssetsFolder("images");
        lottieAnimationView.setAnimation("images/loading.json");
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();
    }

    /**
     * 隐藏等待提示框
     */
    public void hideWaitingDialog() {
        if(null != lottieAnimationView){
            loading_dialog.setVisibility(View.GONE);
            lottieAnimationView.cancelAnimation();
        }
    }


}


