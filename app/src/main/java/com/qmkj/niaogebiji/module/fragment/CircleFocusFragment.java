package com.qmkj.niaogebiji.module.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.PicPreviewActivity;
import com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.event.BlogPriaseEvent;
import com.qmkj.niaogebiji.module.event.SendOkCircleEvent;
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
 * 描述:圈子关注
 */
public class CircleFocusFragment extends BaseLazyFragment {


    @BindView(R.id.backtop)
    ImageView backtop;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;


    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    //页数
    private int page = 1;
    //适配器
    CircleRecommentAdapterNew mCircleRecommentAdapterNew;
    //组合集合
    List<CircleBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    public static CircleFocusFragment getInstance(String chainId, String chainName) {
        CircleFocusFragment newsItemFragment = new CircleFocusFragment();
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
        initSamrtLayout();
        initLayout();


        String content = "测试http://www.baidu.com 测测https://www.qq.com/再来一个 https://china.nba.com";
        //判断内容是否中link
        String regex = "https?://(?:[-\\w.]|(?:%[\\da-fA-F]{2}))+[^\\u4e00-\\u9fa5]+[\\w-_/?&=#%:]{0}";
        Matcher matcher = Pattern.compile(regex).matcher(content);
        int size = matcher.groupCount();
        KLog.d("tag","link条数 " + size);
        while (matcher.find()){
            int start =  matcher.start();
            int end = matcher.end();
            KLog.d("tag","start " + start + " end " + end);
            KLog.d("tag"," matcher.group() " +  matcher.group());
        }

    }

    @Override
    protected void initData() {
        followBlogList();
    }

    private void followBlogList() {
        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().followBlogList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<CircleBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<CircleBean>> response) {

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }

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
                                ((TextView)ll_empty.findViewById(R.id.tv_empty)).setText("没有推荐圈子数据~");
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

                    //{"return_code":"200","return_msg":"success","return_data":{}} -- 后台空集合返回{}，那么会出现解析异常，在这里所判断
                    @Override
                    public void onNetFail(String msg) {
                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                        if("解析错误".equals(msg)){
                            if(page == 1){
                                setData2(null);
                            }else{
                                mCircleRecommentAdapterNew.loadMoreComplete();
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
                type = StringUtil.getCircleType(temp);
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




    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mCircleRecommentAdapterNew = new CircleRecommentAdapterNew(mAllList);
        mRecyclerView.setAdapter(mCircleRecommentAdapterNew);
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        initEvent();
    }



    private void initSamrtLayout() {

        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            followBlogList();
        });
    }





    @SuppressLint("CheckResult")
    private void initEvent() {
        mCircleRecommentAdapterNew.setOnLoadMoreListener(() -> {
            ++page;
            followBlogList();
        }, mRecyclerView);

        mRecyclerView.addOnScrollListener(new RvScrollListener());

    }


    private class RvScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                //获取最后一个可见view的位置
                int lastItemPosition = linearManager.findLastVisibleItemPosition();
                if (lastItemPosition > 6) {
                    backtop.setVisibility(View.VISIBLE);
                } else {
                    backtop.setVisibility(View.GONE);
                }
            }

        }
    }

    /**
     * --------------------------------- 图片预览  ---------------------------------
     */
    private void toPicPrewView() {
        ArrayList<String> photos = new ArrayList<>();
        photos.add("https://desk-fd.zol-img.com.cn/t_s2560x1440c5/g2/M00/05/09/ChMlWl1BAz-IcV0oADKEXBJ0ncgAAMP0gAAAAAAMoR0279.jpg");
        photos.add("https://desk-fd.zol-img.com.cn/t_s2560x1440c5/g2/M00/05/09/ChMlWV1BA0uIJD2cACgyyOBAl4YAAMP0gOPNF0AKDLg887.jpg");
        photos.add("https://article-fd.zol-img.com.cn/g2/M00/0E/00/ChMlWVyJwQeIRQrvAA_BjB8NhecAAIyDANWGdgAD8Gk692.jpg");
        photos.add("https://b.zol-img.com.cn/desk/bizhi/image/8/4096x2160/1563934008198.png");
        photos.add("https://desk-fd.zol-img.com.cn/t_s4096x2160c5/g2/M00/02/06/ChMlWV03v-eIOEWoAC0lpucbl_sAAMC8AFTL9QALSW-183.jpg");
        photos.add("https://desk-fd.zol-img.com.cn/t_s4096x2160c5/g2/M00/02/06/ChMlWl03wq6IbWwqAA-IxrPijHEAAMDAwJ0cR8AD4je242.jpg");
        //错误图片url
        photos.add("https://desk-fd.zol-img.com.cn/t_s4096x2160c5/g2/M00/02/06/ChMlWl03v_aISd7vABOqKe2IAXEAAMC8QJgIh4AE6pB2971212.jpg");
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("imageList", photos);
        bundle.putBoolean("fromNet", true);
        bundle.putInt("index", 0);
        Intent intent = new Intent(getActivity(), PicPreviewActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }







    @Override
    protected void changePriaseStatus() {

    }





    /** --------------------------------- 发布帖子成功  ---------------------------------v*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendCircleEvent(SendOkCircleEvent event) {
        mAllList.clear();
        page = 1;
        followBlogList();
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
            mCircleRecommentAdapterNew.getData().get(event.getPosition()).setIs_like(event.getStauts());
            mCircleRecommentAdapterNew.getData().get(event.getPosition()).setLike_num(event.getLikeNum());
            mCircleRecommentAdapterNew.getData().get(event.getPosition()).setComment_num(event.getCommentNum());
            mCircleRecommentAdapterNew.notifyItemChanged(event.getPosition());
        }
    }
}


