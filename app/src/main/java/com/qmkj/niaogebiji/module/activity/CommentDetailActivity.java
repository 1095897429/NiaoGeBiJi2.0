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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.TalkAlertDialog;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.module.adapter.CommentAdapter;
import com.qmkj.niaogebiji.module.adapter.CommentAdapterByNewBean;
import com.qmkj.niaogebiji.module.adapter.CommentSecondAdapter;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentBeanNew;
import com.qmkj.niaogebiji.module.bean.CommentOkBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

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

    //1级 适配器
    CommentAdapterByNewBean mCommentAdapter;
    //组合集合
    List<CommentBeanNew> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    private int page = 1;

    private String blog_id = "5";

    //点击1级评论的id
    private String blog_comment_id;

    private CircleBean mCircleBean;

    //被评论动态发布人ID
    private String create_uid;

    //一级集合
    private List<CommentBeanNew> mCommentBeanNewList;
    //二级集合
    private List<CommentBeanNew.SecondComment> mCommentSList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment_detail;
    }

    @Override
    protected void initView() {
        blog_id = getIntent().getExtras().getString("blog_id");
        initLayout();
        blogDetail();
        getBlogCommentList();
    }


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
                        setCommentListData();
                    }
                });
    }

    private void setCommentListData() {
        CommentBeanNew bean1 ;
        for (int i = 0; i < mCommentBeanNewList.size(); i++) {
            bean1 = mCommentBeanNewList.get(i);
            mAllList.add(bean1);
        }
        mCommentAdapter.setNewData(mAllList);
    }


    private void setData() {

        //正文
        content.setText(mCircleBean.getBlog());
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
        zan_num.setTypeface(typeface);
        if(mCircleBean.getLike_num().equals("0")){
            zan_num.setText("赞");
        }else{
            zan_num.setText(mCircleBean.getLike_num() + "+");
        }

        circle_priase.setOnClickListener(view -> {
            lottieAnimationView.setImageAssetsFolder("images");
            lottieAnimationView.setAnimation("images/new_like_20.json");
            lottieAnimationView.playAnimation();
            zan_num.setTextColor(mContext.getResources().getColor(R.color.prise_select_color));
        });
    }


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

    private void initEvent() {
        mCommentAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","点击一级评论去二级评论");
            showSheetDialog();
            getCommentData();
            create_uid = mCommentAdapter.getData().get(position).getUid();
        });

        mCommentAdapter.setOnLoadMoreListener(() -> {
            ++page;
            KLog.d("tag","加载更多");
        },mRecyclerView);

        setEmpty(mCommentAdapter);



        mCommentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.comment_delete:
                    blog_id = mCommentAdapter.getData().get(position).getId();
                    KLog.d("tag","删除自己的帖子");
                    showDeteleCircle();


                    break;
                case R.id.toSecondComment:
                case R.id.ll_has_second_comment:
                    create_uid = mCommentAdapter.getData().get(position).getUid();
                    KLog.d("tag","点击一级评论去二级评论");
                    showSheetDialog();
                    getCommentData();
                    getCommentComment();
                    break;
                case R.id.comment_priase:
                    KLog.d("tag","帖子点赞");
                    break;

                    default:
            }
        });
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

    private void getCommentComment() {
        Map<String,String> map = new HashMap<>();
        map.put("blog_comment_id",blog_id + "");
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getCommentComment(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<CommentBeanNew.SecondComment>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<CommentBeanNew.SecondComment>> response) {
                        mCommentSList = response.getReturn_data();
                        setCommentSecondListData();
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
                showTalkDialog(-1,"","first");
                break;
            default:
        }
    }



    String commentString;

    //参数一 用于数据更新      参数三 评论一级 还是 评论二级
    private void showTalkDialog(int position,String talkCid,String from) {
        final TalkAlertDialog talkAlertDialog = new TalkAlertDialog(this).builder();
        talkAlertDialog.setMyPosition(position);
        talkAlertDialog.setTalkLisenter((position1, words) -> {
            KLog.d("tag","接受到的文字是 " + words);
//            commentBulletin(position,words,talkCid,from);
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
                    }
                });
    }


    //追加一条数据，在之前的集合上
//    private void commentBulletin(int position,String content,String talkCid,String from) {
//        if("first".equals(from)){
//            //本地测试
//            CommentBean.FirstComment firstComment = new CommentBean.FirstComment();
//            firstComment.setMessage(content);
//            mAllList.add(firstComment);
//            mCommentAdapter.notifyItemChanged(position);
//        }else if("second".equals(from)){
//            MulSecondCommentBean bean;
//            bean = new MulSecondCommentBean();
//
//            CommentBean.FirstComment  bean1 = new CommentBean.FirstComment();
//            //TODO 这里是相当于二级评论中的一级评论
//            bean1.setMessage(content);
//            bean.setFirstComment(bean1);
//
//            bean.setItemType(1);
//            list.add(bean);
//            bottomSheetAdapter.notifyItemChanged(position);
//        }
//    }


    private void updateListDataStatus(int position,CommentOkBean bean) {
//            if(null == bean){
//                bean = new CommentOkBean();
//                bean.setCid("1");
//                bean.setContent("我是帅哥");
//                bean.setUsername("周六");
//            }

//             CommentBean.FirstComment bean1 =  new CommentBean.FirstComment();
//
//             mAllList.add(bean1);
//
//            mCommentAdapter.notifyItemChanged(position);
    }


    /** --------------------------------- 二级弹框 评论  ---------------------------------*/
    int secondPage = 1;
    RelativeLayout totalk;
    RecyclerView mSecondRV;
    BottomSheetDialog bottomSheetDialog;
    CommentSecondAdapter bottomSheetAdapter;
    BottomSheetBehavior mDialogBehavior;

    List<MulSecondCommentBean> list = new ArrayList<>();




    private void setCommentSecondListData() {

        list.clear();

        MulSecondCommentBean bean;
        bean = new MulSecondCommentBean();
        bean.setItemType(2);
        list.add(bean);

        //模拟评论数据
        CommentBean.FirstComment  bean1 ;
        for (int i = 0; i < mCommentSList.size(); i++) {
            bean = new MulSecondCommentBean();
            bean1 = new CommentBean.FirstComment();
            bean1.setMessage("我是二级评论 " + i);
            bean.setFirstComment(bean1);
            bean.setItemType(1);
            list.add(bean);

        }

        bottomSheetAdapter.setNewData(list);
    }


    private void getCommentData() {
        list.clear();

        MulSecondCommentBean bean;
        bean = new MulSecondCommentBean();
        bean.setItemType(2);
        list.add(bean);

        //模拟评论数据
        CommentBean.FirstComment  bean1 ;
        for (int i = 0; i < 4; i++) {
            bean = new MulSecondCommentBean();
            bean1 = new CommentBean.FirstComment();
            bean1.setMessage("我是二级评论 " + i);
            bean.setFirstComment(bean1);
            bean.setItemType(1);
            list.add(bean);

        }

        bottomSheetAdapter.setNewData(list);

    }

    private void showSheetDialog() {
        View view = View.inflate(this, R.layout.dialog_bottom_comment, null);
        mSecondRV = view.findViewById(R.id.recycler);
        totalk = view.findViewById(R.id.totalk);
        totalk.setOnClickListener(view1 -> showTalkDialog(-1,"111","second"));

        bottomSheetAdapter = new CommentSecondAdapter(list);
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

                if(newState == BottomSheetBehavior.STATE_DRAGGING){
                    KLog.d("tag",bottomSheet.getTop() + "");
                }

                //手指移动布局的高度
                if(newState == BottomSheetBehavior.STATE_SETTLING){

                    KLog.d("tag11","屏幕的高度减去状态栏高度是 : " +  (ScreenUtils.getScreenHeight() - SizeUtils.dp2px(25)) +  "   " + bottomSheet.getTop() + "");

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
    }

    private void initScondEvent() {

        bottomSheetAdapter.setOnLoadMoreListener(() -> {
            ++secondPage;
            KLog.d("tag","加载更多");
        },mSecondRV);

        setEmpty(bottomSheetAdapter);

        bottomSheetAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                showTalkDialog(position,"1111","second");
            }
        });
    }


}



