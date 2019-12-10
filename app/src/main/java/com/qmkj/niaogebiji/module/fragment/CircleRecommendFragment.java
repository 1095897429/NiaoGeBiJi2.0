package com.qmkj.niaogebiji.module.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseLazyFragment;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.PicPreviewActivity;
import com.qmkj.niaogebiji.module.adapter.CircleRecommendAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.FirstItemBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
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

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-14
 * 描述:圈子推荐
 */
public class CircleRecommendFragment extends BaseLazyFragment {

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
    CircleRecommendAdapter mCircleRecommendAdapter;
    //组合集合
    List<MultiCircleNewsBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    //先用这个bean代替
    NewsDetailBean mNewsDetailBean;

    private int myPosition;


    public static CircleRecommendFragment getInstance(String chainId, String chainName) {
        CircleRecommendFragment newsItemFragment = new CircleRecommendFragment();
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
        getData();
    }

    @Override
    protected void lazyLoadData() {
        recommendBlogList();
    }

    private void recommendBlogList() {
        Map<String,String> map = new HashMap<>();
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().recommendBlogList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<CircleBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<CircleBean>> response) {
                        KLog.d("tag","response " + response.getReturn_code());
                    }

                });
    }

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mCircleRecommendAdapter = new CircleRecommendAdapter(mAllList);
        mRecyclerView.setAdapter(mCircleRecommendAdapter);
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
            getData();
        });
    }


    private void getData() {

        NewsItemBean itemBean;
        FirstItemBean firstItemBean;
        MultiCircleNewsBean bean1;
        for (int i = 0; i < 10; i++) {
            if (i == 2) {
                firstItemBean = new FirstItemBean();
                bean1 = new MultiCircleNewsBean();
                bean1.setItemType(4);
                bean1.setFirstItemBean(firstItemBean);
            } else {
                itemBean = new NewsItemBean();
                itemBean.setTitle("名称 " + i);
                bean1 = new MultiCircleNewsBean();
                if (i == 4) {
                    bean1.setItemType(2);
                } else if (i == 5) {
                    bean1.setItemType(3);
                } else {
                    bean1.setItemType(1);
                }

                bean1.setNewsItemBean(itemBean);
            }
            mAllList.add(bean1);
        }

        mCircleRecommendAdapter.setNewData(mAllList);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                smartRefreshLayout.finishRefresh();
            }
        },2000);


    }


    @SuppressLint("CheckResult")
    private void initEvent() {
        mCircleRecommendAdapter.setOnLoadMoreListener(() -> {
            ++page;
            getData();
        }, mRecyclerView);

        mRecyclerView.addOnScrollListener(new RvScrollListener());

        mCircleRecommendAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            myPosition = position;
            switch (view.getId()) {
                case R.id.circle_remove:
//                    showRemoveDialog();
                    break;
//                case R.id.circle_priase:
//                    if(mTempBuilltinBean.getIs_good() == 0){
////                        goodBulletin(mTempBuilltinBean.getId());
////                    }else{
////                        cancleGoodBulletin(mTempBuilltinBean.getId());
////                    }

//                    goodBulletin("111");





//                    break;
                case R.id.ll_report:
                    KLog.d("tag", "举报或分享");
                    showPopupWindow(adapter.getViewByPosition(position, R.id.ll_report));
                    setBackgroundAlpha(getActivity(), 0.6f);
                    break;
                case R.id.circle_comment:
                    KLog.d("tag", "评论去圈子详情");
                    UIHelper.toCommentDetailActivity(getActivity());
                    break;
                case R.id.circle_share:
                    KLog.d("tag", "圈子分享");
                    showShareDialog();
                    break;
                case R.id.part2222:
                    KLog.d("tag", "图片预览");
                    toPicPrewView();
                    break;
                case R.id.part1111:
                    KLog.d("tag", "去个人界面");
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


        mCircleRecommendAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (StringUtil.isFastClick()) {
                return;
            }
            int type = adapter.getItemViewType(position);
            switch (type) {
                case FirstItemNewAdapter.RIGHT_IMG_TYPE:
                    UIHelper.toCommentDetailActivity(getActivity());
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
                    UIHelper.toTranspondActivity(getActivity());
                    //参数一：目标Activity1进入动画，参数二：之前Activity2退出动画
                    getActivity().overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
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
            ToastUtils.showShort("发请求，举报成功");
            mPopupWindow.dismiss();
        });

        share.setOnClickListener(view1 -> {
            showShareDialog();
            mPopupWindow.dismiss();
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

    private void showRemoveDialog() {

        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(getActivity()).builder();
        iosAlertDialog.setPositiveButton("删除", v -> {
            mCircleRecommendAdapter.getData().remove(myPosition);
            mCircleRecommendAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
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

}


