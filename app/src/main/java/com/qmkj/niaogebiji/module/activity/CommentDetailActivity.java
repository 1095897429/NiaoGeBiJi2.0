package com.qmkj.niaogebiji.module.activity;

import android.graphics.Typeface;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.qmkj.niaogebiji.common.dialog.TalkAlertDialog;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CommentAdapter;
import com.qmkj.niaogebiji.module.adapter.CommentAdapterByNewBean;
import com.qmkj.niaogebiji.module.adapter.CommentSecondAdapter;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentBeanNew;
import com.qmkj.niaogebiji.module.bean.CommentOkBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.event.BlogPriaseEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
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
 * 创建时间 2019-11-21
 * 描述:一级评论 (帖子 文章 快讯)
 *
 * 脉脉是所有评论中回复一条之后 二级评论大于1 显示 共2条回复>> !! 擦，2条也不显示显示更多
 *
 *
 * 0.showTalkDialog
 * 1.showTalkDialogFirstComment
 * 2.
 */
public class CommentDetailActivity extends BaseActivity {

    @BindView(R.id.sender_name)
    TextView sender_name;

    @BindView(R.id.sender_tag)
    TextView sender_tag;

    @BindView(R.id.content)
    TextView content;

    @BindView(R.id.head_icon)
    ImageView head_icon;

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

    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottieAnimationView;

    @BindView(R.id.ll_have_comment)
    LinearLayout ll_have_comment;

    @BindView(R.id.first_comment_num)
    TextView first_comment_num;

    @BindView(R.id.more_comment_recycler)
    RecyclerView mRecyclerView;


    @BindView(R.id.part_yc_pic)
    LinearLayout part_yc_pic;

    @BindView(R.id.part_yc_link)
    LinearLayout part_yc_link;

    @BindView(R.id.part_zf_pic)
    LinearLayout part_zf_pic;

    @BindView(R.id.part_zf_link)
    LinearLayout part_zf_link;


    //转发图片
    @BindView(R.id.transfer_content)
    TextView transfer_content;

    @BindView(R.id.transfer_name)
    TextView transfer_name;

    @BindView(R.id.transfer_two_img_imgs)
    ImageView transfer_two_img_imgs;

    @BindView(R.id.transfer_one_img_imgs)
    ImageView transfer_one_img_imgs;

    @BindView(R.id.transfer_three_img_imgs)
    ImageView transfer_three_img_imgs;


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
    //布局类型
    private String layoutType;
    //评论的是 一级评论还是二级评论
    private boolean isSecondComment = false;

    private int myPotion = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment_detail;
    }

    @Override
    protected void initView() {
        myPotion = getIntent().getExtras().getInt("clickPostion");
        blog_id = getIntent().getExtras().getString("blog_id");
        layoutType= getIntent().getExtras().getString("layoutType");
        initLayout();
        blogDetail();
        getBlogCommentList();
    }



    String scaleSize = "?imageMogr2/auto-orient/thumbnail/300x";

    private void setImageStatus( List<String> imgs) {
        int size = imgs.size();
        if(size < 3 ){
            if(1 == size){
                ImageUtil.load(mContext,imgs.get(0) + scaleSize,transfer_one_img_imgs);
                transfer_one_img_imgs.setVisibility(View.VISIBLE);
                transfer_two_img_imgs.setVisibility(View.GONE);
                transfer_three_img_imgs.setVisibility(View.GONE);
            }
            if(2 == size){
                ImageUtil.load(mContext,imgs.get(0) + scaleSize,transfer_one_img_imgs);
                ImageUtil.load(mContext,imgs.get(1) + scaleSize,transfer_two_img_imgs);
                transfer_one_img_imgs.setVisibility(View.VISIBLE);
                transfer_two_img_imgs.setVisibility(View.VISIBLE);
                transfer_three_img_imgs.setVisibility(View.GONE);
            }

        }else{
            ImageUtil.load(mContext,imgs.get(0) + scaleSize,transfer_one_img_imgs);
            ImageUtil.load(mContext,imgs.get(1) + scaleSize,transfer_two_img_imgs);
            ImageUtil.load(mContext,imgs.get(2) + scaleSize,transfer_three_img_imgs);
            transfer_one_img_imgs.setVisibility(View.VISIBLE);
            transfer_two_img_imgs.setVisibility(View.VISIBLE);
            transfer_three_img_imgs.setVisibility(View.VISIBLE);
        }
    }







    public void showDeteleCircle(){
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
        iosAlertDialog.setPositiveButton("删除", v -> {
            deleteBlog();
        }).setNegativeButton("再想想", v -> {

        }).setMsg("删除帖子?").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }



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
                        KLog.d("tag","response " + response.getReturn_code());
                        ToastUtils.showShort("删除成功");
                    }
                });
    }



    @OnClick({R.id.toComment,R.id.circle_comment,
            R.id.iv_back

    })
    public void clicks(View view){
        switch (view.getId()){
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
    RelativeLayout totalk;
    ImageView second_close;
    RecyclerView mSecondRV;
    ImageView head_second_icon;
    LinearLayout comment_priase;
    ImageView zan_second_img_second;
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


    //临时需要评论数据
    private CommentBeanNew secondComment;

    private void showSheetDialog() {
        View view = View.inflate(this, R.layout.dialog_bottom_comment, null);
        mSecondRV = view.findViewById(R.id.recycler);
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
        mSecondRV.setLayoutManager(new LinearLayoutManager(this));
        mSecondRV.setItemAnimator(new DefaultItemAnimator());
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

        initScondEvent();
        allSecondComments.clear();
        getSecondCommentComment();
    }



    private void setSecondHeadData(CommentBeanNew temp) {
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


        comment_num_second.setText(temp.getComment_num() + "条回复");
        nickname_second.setText(temp.getUser_info().getName());
        comment_text_second.setText(temp.getComment());
        ImageUtil.load(this,temp.getUser_info().getAvatar(),head_second_icon);

        //点赞
        zanChange(zan_num,image_circle_priase,temp.getLike_num(),temp.getIs_like());

        comment_priase.setOnClickListener((view)->{
            likeComment(temp);
        });
    }


    private void getSecondCommentComment() {
        blog_comment_id = oneComment.getId();
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
                            }else{
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

        bottomSheetAdapter.setOnItemClickListener((adapter, view, position) -> {
            //此处逻辑和点击一级评论item一样
            isSecondComment = true;
            KLog.d("tag","点击此评论的id 为  " + oneComment.getId());
            showTalkDialogSecondComment(position, oneComment);
        });
    }




    /** --------------------------------- 点赞评论  ---------------------------------*/

    //用于记录在二级评论点赞后，一级界面数据没有刷新
    private int zanPosition;

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
        mRecyclerView.setAdapter(mCommentAdapter);
        //解决数据加载不完
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
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
                        if(null != mCommentBeanNewList){
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

        //第一部分设置空布局
        setEmpty(mCommentAdapter);


        mCommentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.comment_delete:
                    blog_id = mCommentAdapter.getData().get(position).getId();
                    showDeteleCircle();
                    break;
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
                        KLog.d("tag","response " + response.getReturn_code());
                        mCircleBean = response.getReturn_data();
                        setData();
                    }
                });
    }


    private void setData() {
        //正文
        content.setText(mCircleBean.getBlog());
        //布局
        if("1".equals(layoutType)){

        }else if("2".equals(layoutType)){
            //转发图片
            part_zf_pic.setVisibility(View.VISIBLE);
            CircleBean.P_blog pBlog = mCircleBean.getP_blog();
            CircleBean.P_user_info pUserInfo = pBlog.getP_user_info();
            transfer_content.setText(pBlog.getBlog());
            transfer_name.setText(pUserInfo.getName() + pUserInfo.getCompany_name());
            setImageStatus(pBlog.getImages());
        }
        //发布时间
        if(null != mCircleBean.getCreated_at()){
            if(!TextUtils.isEmpty(mCircleBean.getCreated_at())){
                String s =  GetTimeAgoUtil.getTimeAgo(Long.parseLong(mCircleBean.getCreated_at()) * 1000L);
                if(!TextUtils.isEmpty(s)){
                    if("天前".contains(s)){
                        publish_time.setText(TimeUtils.millis2String(Long.parseLong(mCircleBean.getCreated_at()) * 1000L,"yyyy/MM/dd"));
                    }else{
                        publish_time.setText(s);
                    }
                }
            }
        }else{
            publish_time.setText("");
        }
        //发帖人
        sender_name.setText(mCircleBean.getUser_info().getName());
        //头像
        ImageUtil.load(mContext,mCircleBean.getUser_info().getAvatar(),head_icon);
        //职位
        sender_tag.setText(mCircleBean.getUser_info().getPosition());
        //徽章
        if(null != mCircleBean.getUser_info().getBadge() && !mCircleBean.getUser_info().getBadge().isEmpty()){
            ll_badge.removeAllViews();
            for (int i = 0; i < mCircleBean.getUser_info().getBadge().size(); i++) {
                ImageView imageView = new ImageView(mContext);
                imageView.setImageResource(R.mipmap.icon_test_detail_icon1);
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
        //评论
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
        comment.setTypeface(typeface);
        if(mCircleBean.getComment_num().equals("0")){
            comment.setText("评论");
            first_comment_num.setText("全部0条评论");
        }else{
            comment.setText(mCircleBean.getComment_num() + "");
            first_comment_num.setText("全部 " + mCircleBean.getComment_num() +" 条评论");
        }
        //点赞数
        zanChange(zan_num,image_circle_priase,mCircleBean.getLike_num() + "",mCircleBean.getIs_like());

        circle_priase.setOnClickListener(view -> {
            likeBlog(mCircleBean);
        });
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
        map.put("create_uid","");
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
                    //更新头部数据
                    zanChange(zan_num,image_circle_priase,circleBean.getLike_num(),circleBean.getIs_like());
                    //TODO 更新首页数据 12.20
                    if(myPotion != -1){
                        EventBus.getDefault().post(new BlogPriaseEvent(myPotion,circleBean.getIs_like(),circleBean.getLike_num()));
                    }
                    }
                });
    }



}



