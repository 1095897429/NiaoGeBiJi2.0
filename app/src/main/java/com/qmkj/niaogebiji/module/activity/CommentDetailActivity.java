package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.dialog.TalkAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CirclePicAdapter;
import com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew;
import com.qmkj.niaogebiji.module.adapter.CircleTransferPicAdapter;
import com.qmkj.niaogebiji.module.adapter.CommentAdapter;
import com.qmkj.niaogebiji.module.adapter.CommentAdapterByNewBean;
import com.qmkj.niaogebiji.module.adapter.CommentSecondAdapter;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentBeanNew;
import com.qmkj.niaogebiji.module.bean.CommentOkBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.event.BlogPriaseEvent;
import com.qmkj.niaogebiji.module.event.RefreshActicleCommentEvent;
import com.qmkj.niaogebiji.module.event.RefreshCircleDetailCommentEvent;
import com.qmkj.niaogebiji.module.widget.HorizontalSpacesDecoration;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.header.XnClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
 * 创建时间 2019-11-21
 * 描述:一级评论 (帖子 文章 快讯)
 *
 * 0.showTalkDialog
 * 1.showTalkDialogFirstComment
 * 2.
 *
 * 1级适配器 CommentAdapterByNewBean
 * 2级适配器 CommentSecondAdapter
 * zanPosition 记录到二级评论时评论的索引
 *
 *
 * 1.h5跳转 过来，此时layoutType还不能用。blogdetail请求后再判断
 */
public class CommentDetailActivity extends BaseActivity {

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.sender_name)
    TextView sender_name;

    @BindView(R.id.sender_tag)
    TextView sender_tag;

    @BindView(R.id.content)
    TextView content;

    @BindView(R.id.head_icon)
    ImageView head_icon;

    @BindView(R.id.user_head_icon)
    ImageView user_head_icon;


    @BindView(R.id.publish_time)
    TextView publish_time;

    @BindView(R.id.ll_badge)
    LinearLayout ll_badge;

    @BindView(R.id.comment)
    TextView comment;

    @BindView(R.id.zan_num)
    TextView zan_num;

    @BindView(R.id.image_circle_priase)
    ImageView image_circle_priase;

    //点赞布局
    @BindView(R.id.circle_priase)
    LinearLayout circle_priase;

    @BindView(R.id.circle_share)
    LinearLayout circle_share;


    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottieAnimationView;

    @BindView(R.id.ll_have_first_comment)
    LinearLayout ll_have_first_comment;

    @BindView(R.id.first_comment_num)
    TextView first_comment_num;

    @BindView(R.id.more_comment_recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    @BindView(R.id.tv_empty)
    TextView tv_empty;

    @BindView(R.id.iv_empty)
    ImageView iv_empty;


    @BindView(R.id.part_yc_pic)
    LinearLayout part_yc_pic;

    @BindView(R.id.part_yc_link)
    LinearLayout part_yc_link;

    @BindView(R.id.part_yc_acticle)
    LinearLayout part_yc_acticle;

    @BindView(R.id.transfer_zf_ll)
    LinearLayout transfer_zf_ll;

    @BindView(R.id.part_zf_pic)
    LinearLayout part_zf_pic;


    @BindView(R.id.link_text)
    TextView link_text;

    @BindView(R.id.link_http)
    TextView link_http;

    @BindView(R.id.acticle_img)
    ImageView acticle_img;

    @BindView(R.id.acticle_title)
    TextView acticle_title;


    @BindView(R.id.part_zf_link)
    LinearLayout part_zf_link;

    @BindView(R.id.part_zf_article)
    LinearLayout part_zf_article;


    @BindView(R.id.transfer_link_text)
    TextView transfer_link_text;

    @BindView(R.id.transfer_link_http)
    TextView transfer_link_http;


    @BindView(R.id.transfer_zf_content)
    TextView transfer_zf_content;

    @BindView(R.id.transfer_zf_author)
    TextView transfer_zf_author;

    @BindView(R.id.pic_recyler_transfer)
    RecyclerView pic_recyler_transfer;

    @BindView(R.id.pic_recyler)
    RecyclerView pic_recyler;


    //1级 适配器
    CommentAdapterByNewBean mCommentAdapter;
    //组合集合
    List<CommentBeanNew> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;


    private int page = 1;

    private String blog_id = "";

    //点击1级评论的id,此id就是整个item的id
    private String blog_comment_id = "";
    //圈子明细
    private CircleBean mCircleBean;
    //评论内容
    String commentString;
    //一级集合
    private List<CommentBeanNew> mCommentBeanNewList = new ArrayList<>();
    //布局类型 请求数据后加载
    private int layoutType;
    //评论的是 一级评论还是二级评论
    private boolean isSecondComment = false;

    private int myPotion = -1;

    @Override
    protected boolean regEvent() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment_detail;
    }

    @Override
    protected void initView() {
        myPotion = getIntent().getExtras().getInt("clickPostion");
        blog_id = getIntent().getExtras().getString("blog_id");
        layoutType = getIntent().getExtras().getInt("layoutType");
        initLayout();
        initPicLayout();
        initSamrtLayout();
        initTransferPicLayout();
        blogDetail();
        getBlogCommentList();
    }



    @OnClick({R.id.toComment,
            R.id.circle_comment,
            R.id.iv_back,
            R.id.part_zf_link,
            R.id.part_yc_link,
            R.id.part1111,
            R.id.part_yc_acticle
    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.part1111:
                UIHelper.toUserInfoActivity(this,mCircleBean.getUid());
                break;
            case R.id.part_yc_link:
                UIHelper.toWebViewActivity(this,mCircleBean.getLink());
                break;
            case R.id.part_zf_link:
                UIHelper.toWebViewActivity(this,mCircleBean.getP_blog().getLink());
                break;
            case R.id.part_yc_acticle:
                UIHelper.toNewsDetailActivity(this,mCircleBean.getArticle_id());
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.circle_comment:
            case R.id.toComment:
                //TODO 弹框1
                showTalkDialog(-1,"","aimtodynamic","");
                break;
            default:
        }
    }



    /** --------------------------------- 二级评论列表 及 点击事件---------------------------------*/
    int secondPage = 1;
    LinearLayout ll_second_empty;
    RelativeLayout totalk;
    ImageView second_close;
    RecyclerView mSecondRV;
    ImageView head_second_icon;
    LinearLayout comment_priase;
    ImageView zan_second_img_second;
    ImageView icon;
    View headView;
    BottomSheetDialog bottomSheetDialog;
    CommentSecondAdapter bottomSheetAdapter;
    BottomSheetBehavior mDialogBehavior;

    List<MulSecondCommentBean> allSecondComments = new ArrayList<>();
    TextView nickname_second;
    TextView comment_text_second;
    TextView time_publish_second;
    TextView zan_second_num_second;
    TextView comment_num_second;

    private void showSheetDialog() {
        View view = View.inflate(this, R.layout.dialog_bottom_comment, null);
        mSecondRV = view.findViewById(R.id.recycler);
        ll_second_empty = view.findViewById(R.id.ll_second_empty);
        icon = view.findViewById(R.id.icon);
        comment_num_second = view.findViewById(R.id.comment_num_second);
        second_close = view.findViewById(R.id.second_close);
        second_close.setOnClickListener(view12 -> bottomSheetDialog.dismiss());
        totalk = view.findViewById(R.id.totalk);
        totalk.setOnClickListener(view1 -> {
            //此处逻辑和点击一级评论item一样
            isSecondComment = true;
            KLog.d("tag","点击此评论的id 为  " + oneComment.getId());
            showTalkDialogSecondComment(-1, oneComment);
        });

        bottomSheetAdapter = new CommentSecondAdapter(allSecondComments);
        mSecondRV.setHasFixedSize(true);
        ((SimpleItemAnimator)mSecondRV.getItemAnimator()).setSupportsChangeAnimations(false);
        mSecondRV.setLayoutManager(new LinearLayoutManager(this));
        mSecondRV.setAdapter(bottomSheetAdapter);

        bottomSheetDialog = new BottomSheetDialog(this, R.style.MyCommentDialog);
        bottomSheetDialog.setContentView(view);

        mDialogBehavior = BottomSheetBehavior.from((View) view.getParent());
        mDialogBehavior.setPeekHeight(ScreenUtils.getScreenHeight());
        mDialogBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetDialog.dismiss();
                    mDialogBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                //手指移动布局的高度
                if(newState == BottomSheetBehavior.STATE_SETTLING){
                    KLog.d("tag","屏幕的高度减去状态栏高度是 : " +  (ScreenUtils.getScreenHeight() - SizeUtils.dp2px(25)) +  "   " + bottomSheet.getTop() + "");
                    if(bottomSheet.getTop() >= 200){
                        bottomSheetDialog.dismiss();
                        mDialogBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        bottomSheetDialog.show();

        bottomSheetAdapter.setOnItemClickListener((adapter, view1, position) -> {
            //此处逻辑和点击一级评论item一样
            isSecondComment = true;
            oneComment = bottomSheetAdapter.getData().get(position).getCircleComment();
            KLog.d("tag","点击此评论的id 为  " + oneComment.getId() + " 被回复的人事 " + oneComment.getUser_info().getName());
            showTalkDialogSecondComment(position, oneComment);

        });

        bottomSheetAdapter.setOnItemChildClickListener((adapter, view13, position) -> {
            isSecondComment = true;
            oneComment = bottomSheetAdapter.getData().get(position).getCircleComment();
            KLog.d("tag","点击此评论的id 为  " + oneComment.getId() + " 被回复的人是 " + oneComment.getUser_info().getName());
            showTalkDialogSecondComment(position, oneComment);
        });

        initScondEvent();
        allSecondComments.clear();
        blog_comment_id = oneComment.getId();
        getSecondCommentComment();
    }



    private void setSecondHeadData(CommentBeanNew temp) {
        //底部使用者头像
        ImageUtil.load(mContext,StringUtil.getUserInfoBean().getAvatar(),icon);
        //设置一些头信息
        headView = LayoutInflater.from(this).inflate(R.layout.second_comment_head,null);
        zan_second_num_second = headView.findViewById(R.id.zan_second_num_second);
        nickname_second = headView.findViewById(R.id.nickname_second);
        comment_text_second = headView.findViewById(R.id.comment_text_second);
        time_publish_second = headView.findViewById(R.id.time_publish_second);
        head_second_icon = headView.findViewById(R.id.head_second_icon);
        zan_second_img_second = headView.findViewById(R.id.zan_second_img_second);
        comment_priase = headView.findViewById(R.id.comment_priase);
        bottomSheetAdapter.setHeaderView(headView);

        comment_num_second.setText(temp.getComment_num() + "条回复");
        nickname_second.setText(temp.getUser_info().getName());
        comment_text_second.setText(temp.getComment());
        ImageUtil.load(this,temp.getUser_info().getAvatar(),head_second_icon);
        //发布时间
        if(StringUtil.checkNull(temp.getCreated_at())){
            String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(temp.getCreated_at()) * 1000L);
            time_publish_second.setText(s);
        }

        nickname_second.setText(temp.getUser_info().getName());
        comment_text_second.setText(temp.getComment());
        ImageUtil.load(this,temp.getUser_info().getAvatar(),head_second_icon);

        //点赞
        zanChange(zan_second_num_second,zan_second_img_second,temp.getLike_num(),temp.getIs_like());

        comment_priase.setOnClickListener((view)->{
            likeComment(temp);
        });
    }


    private void getSecondCommentComment() {
        Map<String,String> map = new HashMap<>();
        map.put("blog_comment_id",blog_comment_id + "");
        map.put("page",secondPage + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getCommentComment(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<CommentBeanNew>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<CommentBeanNew>> response) {
                        List<CommentBeanNew> mCommentSList = response.getReturn_data();
                        if(1 == secondPage){
                            if(!mCommentSList.isEmpty()){
                                setData2(mCommentSList);
                                bottomSheetAdapter.setNewData(allSecondComments);
                                //如果第一次返回的数据不满10条，则显示无更多数据
                                if(mCommentSList.size() < Constant.SEERVER_NUM){
                                    bottomSheetAdapter.loadMoreEnd();
                                }
                                mSecondRV.setVisibility(View.VISIBLE);
                                ll_second_empty.setVisibility(View.GONE);
                            }else{
                                mSecondRV.setVisibility(View.GONE);
                                ll_second_empty.setVisibility(View.VISIBLE);
                            }
                        }else{
                            //已为加载更多有数据
                            if(mCommentSList != null && mCommentSList.size() > 0){
                                setData2(mCommentSList);
                                bottomSheetAdapter.loadMoreComplete();
                                bottomSheetAdapter.addData(mMulSecondCommentBeans);
                            }else{
                                //已为加载更多无更多数据
                                bottomSheetAdapter.loadMoreComplete();
                                bottomSheetAdapter.loadMoreEnd();
                            }
                        }
                    }

                    //{"return_code":"200","return_msg":"success","return_data":{}} -- 后台空集合返回{}，那么会出现解析异常，在这里所判断
                    @Override
                    public void onNetFail(String msg) {
                        if("解析错误".equals(msg)){
                            if(page == 1){
                                setData2(null);
                            }else{
                                bottomSheetAdapter.loadMoreComplete();
                                bottomSheetAdapter.loadMoreEnd();
                            }
                        }
                    }

                });
    }


    List<MulSecondCommentBean> mMulSecondCommentBeans = new ArrayList<>();
    private void setData2(List<CommentBeanNew> list) {
        mMulSecondCommentBeans.clear();
        MulSecondCommentBean bean;
        for (int i = 0; i < list.size(); i++) {
            bean = new MulSecondCommentBean();
            bean.setCircleComment(list.get(i));
            bean.setItemType(CommentSecondAdapter.CIRCLE);
            mMulSecondCommentBeans.add(bean);
        }

        if(secondPage == 1){
            allSecondComments.addAll(mMulSecondCommentBeans);
        }
    }

    private void initScondEvent() {
        bottomSheetAdapter.setOnLoadMoreListener(() -> {
            ++secondPage;
            KLog.d("tag","加载更多");
        },mSecondRV);

        setSecondHeadData(oneComment);
    }




    /** --------------------------------- 点赞评论  ---------------------------------*/
    //用于记录在二级评论点赞后，一级界面数据没有刷新
    private int zanPosition;


    private void zanCommentChange(TextView com_text,TextView zan_num, ImageView zan_img,
                                  String good_num, int is_good,String com_num) {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
        zan_num.setTypeface(typeface);
        com_text.setTypeface(typeface);

        if(StringUtil.checkNull(good_num)){
            if("0".equals(good_num)){
                zan_num.setText("赞");
            }else{
                int size = Integer.parseInt(good_num);
                if(size > 99){
                    zan_num.setText(99 + "+");
                }else{
                    zan_num.setText(size + "");
                }
            }
        }
        //点赞图片
        if("0".equals(is_good + "")){
            zan_img.setImageResource(R.mipmap.icon_flash_priase_28);
            zan_num.setTextColor(mContext.getResources().getColor(R.color.zan_select_no));
        }else if("1".equals(is_good + "")){
            zan_img.setImageResource(R.mipmap.icon_flash_priase_select_28);
            zan_num.setTextColor(mContext.getResources().getColor(R.color.zan_select));
        }


        if(StringUtil.checkNull(com_num)){
            if("0".equals(com_num)){
                com_text.setText("评论");
            }else{
                int size = Integer.parseInt(com_num);
                if(size > 99){
                    com_text.setText(99 + "");
                }else{
                    com_text.setText(size + "");
                }
            }
        }
    }


    private void zanChange(TextView zan_num,ImageView imageView, String good_num, int is_good) {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
        zan_num.setTypeface(typeface);


        if(StringUtil.checkNull(good_num)){
            if("0".equals(good_num)){
                zan_num.setText("赞");
            }else{
                int size = Integer.parseInt(good_num);
                if(size > 99){
                    zan_num.setText(99 + "+");
                }else{
                    zan_num.setText(size + "");
                }
            }
        }

        //点赞图片
        if("0".equals(is_good + "")){
            imageView.setImageResource(R.mipmap.icon_flash_priase_28);
            zan_num.setTextColor(mContext.getResources().getColor(R.color.zan_select_no));
        }else if("1".equals(is_good + "")){
            imageView.setImageResource(R.mipmap.icon_flash_priase_select_28);
            zan_num.setTextColor(mContext.getResources().getColor(R.color.zan_select));
        }
    }


    /** --------------------------------- 一级评论列表 及 点击事件---------------------------------*/

    private void initLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mCommentAdapter = new CommentAdapterByNewBean(mAllList);
        mCommentAdapter.setMyPotion(myPotion);
        mRecyclerView.setAdapter(mCommentAdapter);
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        mCommentAdapter.setChangeDetailListener((good_num, is_good, com_num) -> {
            zanCommentChange(comment,zan_num,image_circle_priase, good_num,is_good ,com_num);
            first_comment_num.setText("全部" + com_num + "条评论");
        });
        initEvent();
    }


    private void getBlogCommentList() {
        Map<String,String> map = new HashMap<>();
        map.put("blog_id",blog_id + "");
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getBlogComment(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<CommentBeanNew>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<CommentBeanNew>> response) {
                        mCommentBeanNewList = response.getReturn_data();
                            if(1 == page){
                                setCommentListData();
                                mCommentAdapter.setNewData(mAllList);
                                //如果第一次返回的数据不满10条，则显示无更多数据
                                if(mCommentBeanNewList.size() < Constant.SEERVER_NUM){
                                    mCommentAdapter.loadMoreEnd();
                                }
                            }else{
                                //已为加载更多有数据
                                if(mCommentBeanNewList != null && mCommentBeanNewList.size() > 0){
                                    setCommentListData();
                                    mCommentAdapter.loadMoreComplete();
                                    mCommentAdapter.addData(mAllList);
                                }else{
                                    //已为加载更多无更多数据
                                    mCommentAdapter.loadMoreEnd();
                                }
                            }
                    }


                    //{"return_code":"200","return_msg":"success","return_data":{}} -- 后台空集合返回{}，那么会出现解析异常，在这里所判断
                    @Override
                    public void onNetFail(String msg) {
                        if("解析错误".equals(msg)){
                            if(page == 1){
                                setCommentListData();
                            }else{
                                mCommentAdapter.loadMoreComplete();
                                mCommentAdapter.loadMoreEnd();
                            }
                        }
                    }
                });
    }



    private void setCommentListData() {
        if(!mCommentBeanNewList.isEmpty()){
            CommentBeanNew bean1 ;
            for (int i = 0; i < mCommentBeanNewList.size(); i++) {
                bean1 = mCommentBeanNewList.get(i);
                mAllList.add(bean1);
            }
            mRecyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
            ll_have_first_comment.setVisibility(View.VISIBLE);
        }else{
            ll_have_first_comment.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
            iv_empty.setImageResource(R.mipmap.icon_empty_comment);
            tv_empty.setText("成为第一个评论者～");
        }
    }

    //临时需要评论数据
    private CommentBeanNew oneComment;
    private void initEvent() {
        mCommentAdapter.setOnItemClickListener((adapter, view, position) -> {
            isSecondComment = false;
            oneComment = mCommentAdapter.getData().get(position);
            KLog.d("tag","点击此评论的id 为  " + oneComment.getId());
            showTalkDialogFirstComment(position,oneComment);
        });

        mCommentAdapter.setOnLoadMoreListener(() -> {
            ++page;
            KLog.d("tag","加载更多");
        },mRecyclerView);



        mCommentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.toFirstComment:
                    oneComment = mCommentAdapter.getData().get(position);
                    KLog.d("tag","点击此评论的id 为  " + oneComment.getId());
                    showTalkDialogFirstComment(position, oneComment);
                    break;
                case R.id.ll_has_second_comment:
                    //🍅 记录帖子position
                    zanPosition  = position;
                    oneComment = mCommentAdapter.getData().get(position);
                    showSheetDialog();
                    break;
                case R.id.comment_priase:
                    KLog.d("tag","帖子点赞");
                    break;

                default:
            }
        });
    }

    /** --------------------------------- 一级评论弹框 ---------------------------------*/

    private void showTalkDialogFirstComment(int position,CommentBeanNew beanNew) {
        final TalkAlertDialog talkAlertDialog = new TalkAlertDialog(this).builder();
        talkAlertDialog.setMyPosition(position);
        talkAlertDialog.setHint(beanNew.getUser_info().getName());
        talkAlertDialog.setTalkLisenter((position1, words) -> {
            commentString = words;
            createCommentComment(oneComment);
        });
        talkAlertDialog.show();
    }


    private void createCommentComment(CommentBeanNew temp) {
        Map<String,String> map = new HashMap<>();
        map.put("comment",commentString);
        map.put("class",temp.getComment_class());
        map.put("comment_id",temp.getId());
        map.put("create_uid",temp.getUid());
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().createCommentComment(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        KLog.d("tag","response " + response.getReturn_code());
                        ToastUtils.showShort("评论成功");
                        if(!isSecondComment){
                            //直接刷新
                            page = 1;
                            mAllList.clear();
                            getBlogCommentList();
                        }else{
                            secondPage = 1;
                            allSecondComments.clear();
                            getSecondCommentComment();

                            EventBus.getDefault().post(new RefreshCircleDetailCommentEvent());
                        }

                    }
                });
    }


    /** --------------------------------- 一级圈子列表 及 点击事件---------------------------------*/
    private void blogDetail() {
        Map<String,String> map = new HashMap<>();
        map.put("blog_id",blog_id + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().blogDetail(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<CircleBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<CircleBean> response) {
                        if(smartRefreshLayout != null){
                            smartRefreshLayout.finishRefresh();
                        }
                        mCircleBean = response.getReturn_data();
                        if(mCommentAdapter != null){
                            mCommentAdapter.setCircleBean(mCircleBean);
                        }
                        setData();
                    }
                });
    }


    @SuppressLint("SetTextI18n")
    private void setData() {
        if(mCircleBean.getUser_info() != null){
            //正文
            content.setText(mCircleBean.getBlog());
            //发帖人
            sender_name.setText(mCircleBean.getUser_info().getName() );
            //头像
            ImageUtil.load(mContext,mCircleBean.getUser_info().getAvatar(),head_icon);
            //底部使用者头像
            ImageUtil.load(mContext,StringUtil.getUserInfoBean().getAvatar(),user_head_icon);
            //职位
            sender_tag.setText(mCircleBean.getUser_info().getCompany_name() + mCircleBean.getUser_info().getPosition());

            //是否认证
            if("0".equals(mCircleBean.getIs_auth())){
                sender_name.setCompoundDrawables(null,null,null,null);
            }else if("1".equals(mCircleBean.getIs_auth())){
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_authen_company);
                drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
                sender_name.setCompoundDrawables(null,null,drawable,null);
            }

            //发布时间
            if(StringUtil.checkNull(mCircleBean.getCreated_at())){
                    String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(mCircleBean.getCreated_at()) * 1000L);
                    publish_time.setText(s);
            }

            //点赞数
            zanCommentChange(comment,zan_num,image_circle_priase,
                    mCircleBean.getLike_num() + "",mCircleBean.getIs_like(),mCircleBean.getComment_num());

            //评论数
            if(!TextUtils.isEmpty(mCircleBean.getComment_num()) && !"0".equals(mCircleBean.getComment_num())){
                first_comment_num.setText("全部" + mCircleBean.getComment_num() + "条评论");
            }

            //点赞事件
            circle_priase.setOnClickListener(view -> {
              likeBlog(mCircleBean);
             });

            //分享事件
            circle_share.setOnClickListener(view -> {
                showShareDialog(mCircleBean);
            });

        }

        if(CircleRecommentAdapterNew.YC_PIC == layoutType){
            part_yc_pic.setVisibility(View.VISIBLE);
            mPicList = mCircleBean.getImages();
            if(mPicList != null && mPicList.size() > 3){
                mPicList = mPicList.subList(0,3);
            }
            mCirclePicAdapter.setNewData(mPicList);
            mCirclePicAdapter.setTotalSize(mCircleBean.getImages().size());

        }else if(CircleRecommentAdapterNew.YC_LINK == layoutType){
            part_yc_link.setVisibility(View.VISIBLE);
            link_http.setText(mCircleBean.getLink());
            link_text.setText(mCircleBean.getLink_title());
        }else if(CircleRecommentAdapterNew.YC_ACTICLE == layoutType){
            part_yc_acticle.setVisibility(View.VISIBLE);
            acticle_title.setText(mCircleBean.getArticle_title());
            ImageUtil.load(this,mCircleBean.getArticle_image(),acticle_img);

        }else if(CircleRecommentAdapterNew.YC_TEXT == layoutType){
            KLog.d("tag","纯文本");
        }else{

            if(mCircleBean.getP_blog() != null && mCircleBean.getP_blog().getP_user_info() != null){

                transfer_zf_content.setText(mCircleBean.getP_blog().getBlog());

                CircleBean.P_user_info temp = mCircleBean.getP_blog().getP_user_info();
                transfer_zf_author.setText(temp.getName()  + "  " + temp.getCompany_name() +
                        temp.getPosition());
            }


            transfer_zf_ll.setVisibility(View.VISIBLE);
            if(CircleRecommentAdapterNew.ZF_PIC == layoutType){
                part_zf_pic.setVisibility(View.VISIBLE);

                mPicList = mCircleBean.getP_blog().getImages();
                if(mPicList != null && mPicList.size() > 3){
                    mPicList = mPicList.subList(0,3);
                }
                mCircleTransferPicAdapter.setNewData(mPicList);
                mCircleTransferPicAdapter.setTotalSize(mCircleBean.getP_blog().getImages().size());

            }else if(CircleRecommentAdapterNew.ZF_LINK == layoutType){
                part_zf_link.setVisibility(View.VISIBLE);
                transfer_link_text.setText(mCircleBean.getP_blog().getLink_title());
                transfer_link_http.setText(mCircleBean.getP_blog().getLink_title());
            }else if(CircleRecommentAdapterNew.ZF_ACTICLE == layoutType){
                part_zf_article.setVisibility(View.VISIBLE);
            }
        }

        //徽章
        if(null != mCircleBean.getUser_info().getBadge() && !mCircleBean.getUser_info().getBadge().isEmpty()){
            ll_badge.removeAllViews();
            for (int i = 0; i < mCircleBean.getUser_info().getBadge().size(); i++) {
                ImageView imageView = new ImageView(mContext);
                String icon = mCircleBean.getUser_info().getBadge().get(i).getIcon();
                if(!TextUtils.isEmpty(icon)){
                    ImageUtil.load(mContext,icon,imageView);
                }
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.width = SizeUtils.dp2px(22);
                lp.height = SizeUtils.dp2px(22);
                lp.gravity = Gravity.CENTER;
                lp.setMargins(0,0,SizeUtils.dp2px(8),0);
                imageView.setLayoutParams(lp);
                ll_badge.addView(imageView);
            }
        }

    }


    /** --------------------------------- 二级评论弹框 ---------------------------------*/
    private void showTalkDialogSecondComment(int position,CommentBeanNew beanNew) {
        final TalkAlertDialog talkAlertDialog = new TalkAlertDialog(this).builder();
        talkAlertDialog.setMyPosition(position);
        talkAlertDialog.setHint(beanNew.getUser_info().getName());
        talkAlertDialog.setTalkLisenter((position1, words) -> {
            commentString = words;
            createCommentComment(beanNew);
        });
        talkAlertDialog.show();
    }


    /** --------------------------------- 动态评论弹框 ---------------------------------*/

    private void showTalkDialog(int position,String talkCid,String from,String replyWho) {
        final TalkAlertDialog talkAlertDialog = new TalkAlertDialog(this).builder();
        talkAlertDialog.setMyPosition(position);
        if(!TextUtils.isEmpty(replyWho)){
            talkAlertDialog.setHint(replyWho);
        }
        talkAlertDialog.setTalkLisenter((position1, words) -> {
            KLog.d("tag","接受到的文字是 " + words);
            commentString = words;
            createBlogComment();
        });
        talkAlertDialog.show();
    }

    private void createBlogComment() {
        Map<String,String> map = new HashMap<>();
        map.put("blog_id",blog_id);
        map.put("comment",commentString);
        map.put("create_uid",mCircleBean.getUid());
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().createBlogComment(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        KLog.d("tag","response " + response.getReturn_code());
                        ToastUtils.showShort("评论成功");
                        //直接刷新
                        page = 1;
                        mAllList.clear();
                        getBlogCommentList();

                        //手动添加 评论数1
                        mCircleBean.setComment_num((Integer.parseInt(mCircleBean.getComment_num()) + 1) + "");
                        //更新首页列表中数据
                        EventBus.getDefault().post(new BlogPriaseEvent(myPotion,mCircleBean.getIs_like(),mCircleBean.getLike_num(),mCircleBean.getComment_num()));
                        //全部回复重定义
                        first_comment_num.setText("全部" + mCircleBean.getComment_num() + "条评论");
                        //顶部帖子重定义
                        zanCommentChange(comment,zan_num,image_circle_priase,
                                mCircleBean.getLike_num() + "",mCircleBean.getIs_like(),mCircleBean.getComment_num());
                    }
                });
    }




    //二级评论上的圈子
    private void likeComment(CommentBeanNew circleBean) {
        Map<String,String> map = new HashMap<>();
        map.put("comment_id",circleBean.getId());
        int like = 0;
        if("0".equals(circleBean.getIs_like() + "")){
            like = 1;
        }else if("1".equals(circleBean.getIs_like() + "")){
            like = 0;
        }
        map.put("like",like + "");
        map.put("class",circleBean.getComment_class());
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().likeComment(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        // 测试的
                        int islike = circleBean.getIs_like();
                        if(islike == 0){
                            //手动修改
                            circleBean.setIs_like(1);
                            circleBean.setLike_num((Integer.parseInt(circleBean.getLike_num()) + 1) + "");
                        }else{
                            circleBean.setIs_like(0);
                            circleBean.setLike_num((Integer.parseInt(circleBean.getLike_num()) - 1) + "");
                        }
                        //更新头部数据
                        zanChange(zan_second_num_second,zan_second_img_second,circleBean.getLike_num(),circleBean.getIs_like());
                        //更新第一列表数据
                        mCommentAdapter.notifyItemChanged(zanPosition);
                    }
                });
    }


    /** --------------------------------- 圈子上的点赞/取赞 ---------------------------------*/
    private void likeBlog(CircleBean circleBean)        {
        Map<String,String> map = new HashMap<>();
        map.put("blog_id",circleBean.getId());
        int like = 0;
        if("0".equals(circleBean.getIs_like() + "")){
            like = 1;
        }else if("1".equals(circleBean.getIs_like() + "")){
            like = 0;
        }
        map.put("like",like + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().likeBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                    int islike = circleBean.getIs_like();
                    if(islike == 0){
                        //手动修改
                        circleBean.setIs_like(1);
                        circleBean.setLike_num((Integer.parseInt(circleBean.getLike_num()) + 1) + "");
                    }else{
                        circleBean.setIs_like(0);
                        circleBean.setLike_num((Integer.parseInt(circleBean.getLike_num()) - 1) + "");
                    }
                    //只更新头部赞数据
                    zanChange(zan_num,image_circle_priase,circleBean.getLike_num(),circleBean.getIs_like());
                    if(myPotion != -1){
                        //更新列表数据
                        EventBus.getDefault().post(new BlogPriaseEvent(myPotion,circleBean.getIs_like(),circleBean.getLike_num(),circleBean.getComment_num()));
                    }
                    }
                });
    }

    /** --------------------------------- 圈子上图片的展示 ---------------------------------*/
    private GridLayoutManager mGridLayoutManager;
    private CirclePicAdapter mCirclePicAdapter;
    private CircleTransferPicAdapter mCircleTransferPicAdapter;
    private List<String> mPicList;
    private void initTransferPicLayout() {
        mGridLayoutManager = new GridLayoutManager(this,3);
        //设置布局管理器
        pic_recyler_transfer.setLayoutManager(mGridLayoutManager);
        //设置适配器
        mCircleTransferPicAdapter = new CircleTransferPicAdapter(mPicList);
        pic_recyler_transfer.setAdapter(mCircleTransferPicAdapter);
        //解决数据加载不完
        pic_recyler_transfer.setNestedScrollingEnabled(true);
        pic_recyler_transfer.setHasFixedSize(true);


        int i1 = SizeUtils.dp2px( 6);
        Rect rect1 = new Rect(0, 0, i1, 0);
        int j1 = SizeUtils.dp2px( 0);
        Rect firstAndLastRect1  = new Rect(j1, 0, i1, 0);
        HorizontalSpacesDecoration spacesDecoration1 = new HorizontalSpacesDecoration(rect1, firstAndLastRect1);
        pic_recyler_transfer.addItemDecoration(spacesDecoration1);
        //禁用change动画
        ((SimpleItemAnimator)pic_recyler_transfer.getItemAnimator()).setSupportsChangeAnimations(false);
    }


    private void initPicLayout() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mContext, 3);
        int i = SizeUtils.dp2px( 8);
        Rect rect = new Rect(0, 0, i, 0);
        int j = SizeUtils.dp2px( 0);
        Rect firstAndLastRect = new Rect(j, 0, i, 0);
        HorizontalSpacesDecoration spacesDecoration = new HorizontalSpacesDecoration(rect, firstAndLastRect);
        pic_recyler.addItemDecoration(spacesDecoration);
        //设置布局管理器
        pic_recyler.setLayoutManager(mGridLayoutManager);
        //设置适配器
        mCirclePicAdapter = new CirclePicAdapter(mPicList);
        pic_recyler.setAdapter(mCirclePicAdapter);
        //解决数据加载不完
        pic_recyler.setNestedScrollingEnabled(true);
        pic_recyler.setHasFixedSize(true);
        mCirclePicAdapter.setOnItemClickListener((adapter, view, position) -> UIHelper.toPicPreViewActivity(mContext,  mCircleBean.getImages(),position));
    }

    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(this);
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            blogDetail();
            getBlogCommentList();
        });
    }


    /** 更新评论数据 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshCircleDetailCommentEvent(RefreshCircleDetailCommentEvent event){
        page = 1;
        mAllList.clear();
        getBlogCommentList();
    }


    //分享事件
    private void showShareDialog(CircleBean item) {
        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(mContext).builder();
        alertDialog.setShareDynamicView().setTitleGone();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position) {
                case 0:
                    ShareBean bean1 = new ShareBean();
                    bean1.setShareType("circle_link");
                    bean1.setLink(item.getShare_url());
                    String name1 = "";
                    if( null != item.getUser_info()){
                        name1 = item.getUser_info().getName();
                    }
                    bean1.setTitle("分享一条" + name1 + "的营销圈动态");
                    bean1.setContent(item.getBlog());
                    String img = "";
                    if(item.getImages() != null &&  !item.getImages().isEmpty()){
                        img  = item.getImages().get(0);
                    }else if(item.getUser_info() != null){
                        img = item.getUser_info().getAvatar();
                    }
                    bean1.setImg(img);
                    StringUtil.shareWxByWeb((Activity) mContext,bean1);
                    break;
                case 1:
                    KLog.d("tag","朋友 是链接");
                    ShareBean bean = new ShareBean();
                    bean.setShareType("weixin_link");
                    bean.setLink(item.getShare_url());
                    String name = "";
                    if( null != item.getUser_info()){
                        name = item.getUser_info().getName();
                    }
                    bean.setTitle("分享一条" + name + "的营销圈动态");
                    bean.setContent(item.getBlog());
                    String img2 = "";
                    if(item.getImages() != null &&  !item.getImages().isEmpty()){
                        img2  = item.getImages().get(0);
                    }else if(item.getUser_info() != null){
                        img2 = item.getUser_info().getAvatar();
                    }
                    bean.setImg(img2);
                    StringUtil.shareWxByWeb((Activity) mContext,bean);
                    break;
                case 4:
                    KLog.d("tag", "转发到动态");
                    UIHelper.toTranspondActivity(mContext,item);
                    //参数一：目标Activity1进入动画，参数二：之前Activity2退出动画
                    ((Activity)mContext).overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                    break;
                default:
            }
        });
        alertDialog.show();
    }


}



