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

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.PicPreviewActivity;
import com.qmkj.niaogebiji.module.adapter.CircleRecommendAdapter;
import com.qmkj.niaogebiji.module.adapter.CircleSearchAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.SearchAllAuthorBean;
import com.qmkj.niaogebiji.module.bean.SearchAllCircleBean;
import com.qmkj.niaogebiji.module.event.SearchWordEvent;
import com.qmkj.niaogebiji.module.event.SendOkCircleEvent;
import com.qmkj.niaogebiji.module.event.toActionEvent;
import com.qmkj.niaogebiji.module.widget.header.XnClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
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
 * 创建时间 2019-12-18
 * 描述: 搜索动态
 */
public class SearchCircleFragment extends BaseLazyFragment {

    @BindView(R.id.allpart)
    LinearLayout allpart;

    @BindView(R.id.ll_show)
    LinearLayout ll_show;

    @BindView(R.id.showSendMsg)
    TextView showSendMsg;

    @BindView(R.id.backtop)
    ImageView backtop;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    //页数
    private int page = 1;
    //适配器
    CircleSearchAdapter mCircleSearchAdapter;
    //组合集合
    List<MultiCircleNewsBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    //先用这个bean代替
    NewsDetailBean mNewsDetailBean;

    private int myPosition;

    //后台直接返回一个集合
    private List<CircleBean> serverData = new ArrayList<>();

    private String blog_id;


    private int pageSize = 10;
    private String myKeyword;


    public static SearchCircleFragment getInstance(String chainId, String chainName) {
        SearchCircleFragment newsItemFragment = new SearchCircleFragment();
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
    }

    @Override
    protected void lazyLoadData() {
        searchBlog();
    }



    private void searchBlog() {
        Map<String,String> map = new HashMap<>();
        map.put("keyword",myKeyword);
        map.put("page",page + "");
        map.put("page_size",pageSize + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().searchBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<SearchAllCircleBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<SearchAllCircleBean> response) {

                        if(null != smartRefreshLayout){
                            smartRefreshLayout.finishRefresh();
                        }
                        SearchAllCircleBean temp  = response.getReturn_data();
                        if(null != temp){
                            List<CircleBean> tempList = temp.getList();
                            if(page == 1){
                                if(!tempList.isEmpty()){
                                    setData2(tempList);
                                    mCircleSearchAdapter.setNewData(mAllList);
                                    //如果第一次返回的数据不满10条，则显示无更多数据
                                    if(tempList.size() < Constant.SEERVER_NUM){
                                        mCircleSearchAdapter.loadMoreEnd();
                                    }
                                }else{
                                    KLog.d("tag","显示空布局");
                                    setEmpty(mCircleSearchAdapter);
                                }
                            }else{
                                //已为加载更多有数据
                                if(tempList != null && tempList.size() > 0){
                                    setData2(tempList);
                                    mCircleSearchAdapter.loadMoreComplete();
                                    mCircleSearchAdapter.addData(teList);
                                }else{
                                    //已为加载更多无更多数据
                                    mCircleSearchAdapter.loadMoreComplete();
                                    mCircleSearchAdapter.loadMoreEnd();
                                }
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
                                setEmpty(mCircleSearchAdapter);
                            }else{
                                mCircleSearchAdapter.loadMoreComplete();
                                mCircleSearchAdapter.loadMoreEnd();
                            }
                        }
                    }

                });
    }


    //先判断原创 再判断图片 再判断link，最后只剩下全文本
    List<MultiCircleNewsBean> teList = new ArrayList<>();
    public void setData2(List<CircleBean> list) {
        CircleBean temp;
        String link;
        String content;
        List<String> imgs;
        MultiCircleNewsBean mulBean;
        //遍历
        for (int i = 0; i < list.size(); i++) {
            mulBean = new MultiCircleNewsBean();
            temp = list.get(i);
            link = temp.getLink();
            imgs =  temp.getImages();

            if(imgs != null &&  !imgs.isEmpty()){
                mulBean.setItemType(CircleSearchAdapter.TYPE1);
                mulBean.setCircleBean(temp);
                teList.add(mulBean);
                continue;
            }

            //原创link
            if(!TextUtils.isEmpty(link)){
                mulBean.setItemType(CircleSearchAdapter.TYPE3);
                mulBean.setCircleBean(temp);
                teList.add(mulBean);
                continue;
            }

            //原创link
            if(!TextUtils.isEmpty(temp.getArticle_id())  && !"0".equals(temp.getArticle_id())){
                mulBean.setItemType(CircleSearchAdapter.TYPE4);
                mulBean.setCircleBean(temp);
                teList.add(mulBean);
                continue;
            }


            mulBean.setItemType(CircleSearchAdapter.TYPE2);

            mulBean.setCircleBean(temp);
            teList.add(mulBean);
        }

        if(page == 1){
            mAllList.addAll(teList);
        }

    }




    protected void setEmpty(BaseQuickAdapter adapter){
        //不需要可以配置加载更多
        adapter.disableLoadMoreIfNotFullPage();
        //TODO 预加载，当列表滑动到倒数第N个Item的时候(默认是1)回调onLoadMoreRequested方法
        adapter.setPreLoadNumber(2);
        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_empty,null);
        adapter.setEmptyView(emptyView);
        ((TextView)emptyView.findViewById(R.id.tv_empty)).setText("没有数据");
    }




    private void getData() {
//
//        NewsItemBean itemBean;
//        FirstItemBean firstItemBean;
//        MultiCircleNewsBean bean1;
//        for (int i = 0; i < 10; i++) {
//            if (i == 2) {
//                firstItemBean = new FirstItemBean();
//                bean1 = new MultiCircleNewsBean();
//                bean1.setItemType(4);
//                bean1.setFirstItemBean(firstItemBean);
//            } else {
//                itemBean = new NewsItemBean();
//                itemBean.setTitle("名称 " + i);
//                bean1 = new MultiCircleNewsBean();
//                if (i == 4) {
//                    bean1.setItemType(2);
//                } else if (i == 5) {
//                    bean1.setItemType(3);
//                } else {
//                    bean1.setItemType(1);
//                }
//
//                bean1.setNewsItemBean(itemBean);
//            }
//            mAllList.add(bean1);
//        }
//
//        mCircleSearchAdapter.setNewData(mAllList);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                smartRefreshLayout.finishRefresh();
//            }
//        },2000);


    }



    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mCircleSearchAdapter = new CircleSearchAdapter(mAllList);
        mRecyclerView.setAdapter(mCircleSearchAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        //添加动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        initEvent();
    }



    private void initSamrtLayout() {

        XnClassicsHeader header =  new XnClassicsHeader(getActivity());
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            searchBlog();
        });
    }





    @SuppressLint("CheckResult")
    private void initEvent() {
        mCircleSearchAdapter.setOnLoadMoreListener(() -> {
            ++page;
            searchBlog();
        }, mRecyclerView);

        mRecyclerView.addOnScrollListener(new RvScrollListener());

        //item点击事件
        mCircleSearchAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag", "评论去圈子详情");
            blog_id = mAllList.get(position).getCircleBean().getId();
//            UIHelper.toCommentDetailActivity(getActivity(),blog_id,"1",position);
        });

        mCircleSearchAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            myPosition = position;
            switch (view.getId()) {
//                case R.id.circle_priase:
//                    if(mTempBuilltinBean.getIs_good() == 0){
////                        goodBulletin(mTempBuilltinBean.getId());
////                    }else{
////                        cancleGoodBulletin(mTempBuilltinBean.getId());
////                    }

//                    goodBulletin("111");
//                    break;
                case R.id.ll_report:
                    KLog.d("tag", "举报或分享 或删除");
                    blog_id = mCircleSearchAdapter.getData().get(position).getCircleBean().getId();
                    String clickUid = mCircleSearchAdapter.getData().get(position).getCircleBean().getUid();
                    if("300579".equals(clickUid)){
                        showRemoveDialog();
                    }else{

                        showPopupWindow(adapter.getViewByPosition(position, R.id.ll_report));
                        setBackgroundAlpha(getActivity(), 0.6f);
                    }

                    break;
                case R.id.circle_comment:
                    KLog.d("tag", "评论去圈子详情");
                    blog_id = mAllList.get(position).getCircleBean().getId();
                    int layoutType=  mAllList.get(position).getItemType();
//                    UIHelper.toCommentDetailActivity(getActivity(),blog_id,layoutType + "",position);
                    break;
                case R.id.circle_share:
                    KLog.d("tag", "圈子分享");
                    showShareDialog();
                    break;
                case R.id.part2222:
                    KLog.d("tag", "图片预览");
//                    UIHelper.toCommentDetailActivity(getActivity(),"5","1",position);
                    break;
                case R.id.toMoreActivity:
                    EventBus.getDefault().post(new toActionEvent("去活动界面"));
                    break;
                case R.id.toMoreFlash:
                    EventBus.getDefault().post(new toActionEvent("去快讯信息流"));
                    break;

                default:
            }
        });


        mCircleSearchAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (StringUtil.isFastClick()) {
                return;
            }
            int type = adapter.getItemViewType(position);
            switch (type) {
                case FirstItemNewAdapter.RIGHT_IMG_TYPE:
//                    UIHelper.toCommentDetailActivity(getActivity(),"5","1",position);
                    break;
                default:
            }

        });
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


    /**
     * --------------------------------- 分享  ---------------------------------
     */

    private void showShareDialog() {
        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(getActivity()).builder();
        alertDialog.setShareDynamicView().setTitleGone();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position) {
                case 0:
                    KLog.d("tag", "朋友圈 是张图片");
                    shareWxCircleByWeb();
                    break;
                case 1:
                    KLog.d("tag", "朋友 是链接");
                    shareWxByWeb();
                    break;
                case 4:
                    KLog.d("tag", "转发到动态");
//                    UIHelper.toTranspondActivity(getActivity());
//                    //参数一：目标Activity1进入动画，参数二：之前Activity2退出动画
//                    getActivity().overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                    break;
                default:
            }
        });
        alertDialog.show();
    }

    // 分享微信（web）
    public void shareWxCircleByWeb() {
        if (this == null) {
            return;
        }
        if (null != mNewsDetailBean) {
            NewsDetailBean shareBean = mNewsDetailBean;
            String sharepic = shareBean.getPic();
            String shareurl = shareBean.getShare_url();
            String title = shareBean.getShare_title();
            String summary = shareBean.getShare_summary();
            SHARE_MEDIA platform;
            platform = SHARE_MEDIA.WEIXIN_CIRCLE;
            UMImage thumb;
            if (TextUtils.isEmpty(sharepic)) {
                thumb = new UMImage(getActivity(), R.mipmap.icon_fenxiang);
            } else {
                thumb = new UMImage(getActivity(), sharepic);
            }
            UMWeb web = new UMWeb(shareurl);
            //标题
            web.setTitle(title);
            //缩略图
            web.setThumb(thumb);
            //描述
            web.setDescription(summary);
            //传入平台
            new ShareAction(getActivity())
                    .setPlatform(platform)
                    .withMedia(web)
                    .share();
        }
    }

    //分享微信（web) 链接
    private void shareWxByWeb() {
        if (null == this) {
            return;
        }
        if (null != mNewsDetailBean) {
            NewsDetailBean shareBean = mNewsDetailBean;
            String sharepic = shareBean.getPic();
            String shareurl = shareBean.getShare_url();
            String title = shareBean.getShare_title();
            String summary = shareBean.getShare_summary();
            SHARE_MEDIA platform;
            platform = SHARE_MEDIA.WEIXIN;
            UMImage thumb;
            if (TextUtils.isEmpty(sharepic)) {
                thumb = new UMImage(getActivity(), R.mipmap.icon_fenxiang);
            } else {
                thumb = new UMImage(getActivity(), sharepic);
            }
            UMWeb web = new UMWeb(shareurl);
            //标题
            web.setTitle(title);
            //缩略图
            web.setThumb(thumb);
            //描述
            web.setDescription(summary);
            //传入平台
            new ShareAction(getActivity())
                    .setPlatform(platform)
                    .withMedia(web)
                    .share();
        }

    }


    /**
     * --------------------------------- 浮层  ---------------------------------
     */

    private void showPopupWindow(View view) {
        //加载布局
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.popupwindow_report, null);
        PopupWindow mPopupWindow = new PopupWindow(inflate);
        TextView report = inflate.findViewById(R.id.report);
        TextView share = inflate.findViewById(R.id.share);
        //必须设置宽和高
        mPopupWindow.setWidth(SizeUtils.dp2px(80f));
        mPopupWindow.setHeight(SizeUtils.dp2px(88f));
        //点击其他地方隐藏,false为无反应
        mPopupWindow.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //以view的左下角为原点，xoff为正表示向x轴正方向偏移像素
            mPopupWindow.showAsDropDown(view, -SizeUtils.dp2px(10f), SizeUtils.dp2px(10f));
        }
        //对popupWindow进行显示
        mPopupWindow.update();
        //消失时将透明度设置回来
        mPopupWindow.setOnDismissListener(() -> {
            if (null != getActivity()) {
                setBackgroundAlpha(getActivity(), 1f);
            }
        });

        report.setOnClickListener(view1 -> {
            reportBlog();
            mPopupWindow.dismiss();
        });

        share.setOnClickListener(view1 -> {
            showShareDialog();
            mPopupWindow.dismiss();
        });
    }

    private void reportBlog() {
        Map<String,String> map = new HashMap<>();
        map.put("blog_id",blog_id );
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().reportBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        ToastUtils.showShort("举报成功");
                    }

                });
    }


    //设置页面的透明度   1表示不透明
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            //不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            //此行代码主要是解决在华为手机上半透明效果无效的bug
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        activity.getWindow().setAttributes(lp);
    }


    @Override
    protected void changePriaseStatus() {

    }


    /** --------------------------------- 删除帖子  ---------------------------------v*/

    private void deleteBlog() {
        Map<String,String> map = new HashMap<>();
        map.put("blog_id",blog_id);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().deleteBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        mCircleSearchAdapter.getData().remove(myPosition);
                        mCircleSearchAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showRemoveDialog() {
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(getActivity()).builder();
        iosAlertDialog.setPositiveButton("删除", v -> {
            deleteBlog();
        }).setNegativeButton("取消", v -> {
        }).setMsg("确定要删除这条动态？").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }


    /** --------------------------------- 发布帖子成功  ---------------------------------v*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendCircleEvent(SendOkCircleEvent event) {
        showSendMsg.setVisibility(View.VISIBLE);
        initAnim();
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
        ObjectAnimator translationX = ObjectAnimator.ofFloat(showSendMsg, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator alphaX = ObjectAnimator.ofFloat(showSendMsg, "alpha", 0, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationX, alphaX);
        animatorSet.setDuration(1000);
        animatorSet.start();
        //动画的监听
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                KLog.d("动画开始","");
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //动画结束跳转
//               new Handler().postDelayed(() -> initExitAnim(),1000);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                KLog.d("动画取消","");
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                KLog.d("动画重复","");
            }
        });

    }


    //点击全部里的查看更多事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchWordEvent(SearchWordEvent event) {
        myKeyword = event.getWord();
        KLog.d("tag","myKeyword = " + myKeyword);
    }



}