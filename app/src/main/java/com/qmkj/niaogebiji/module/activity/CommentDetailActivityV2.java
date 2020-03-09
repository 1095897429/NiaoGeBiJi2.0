package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.dialog.ShowCommentDialog;
import com.qmkj.niaogebiji.common.dialog.TalkCircleAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CirclePicAdapter;
import com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew;
import com.qmkj.niaogebiji.module.adapter.CircleTransferPicAdapter;
import com.qmkj.niaogebiji.module.adapter.CommentCircleAdapter;
import com.qmkj.niaogebiji.module.adapter.CommentSecondAdapter;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentCircleBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.event.BlogPriaseEvent;
import com.qmkj.niaogebiji.module.event.RefreshCircleDetailCommentEvent;
import com.qmkj.niaogebiji.module.widget.CustomImageSpan;
import com.qmkj.niaogebiji.module.widget.HorizontalSpacesDecoration;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.NoLineCllikcSpan;
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
 * 版本 2.0
 * 创建时间 2020-2.26
 *
 *  动态详情 blogDetail
 *  动态评论列表  getBlogComment
 *  动态举报 reportBlog
 *  动态删除 deleteBlog
 *  动态点赞 likeBlog
 *
 * 1.评论动态 createBlogComment
 * 2.评论评论 createCommentComment
 * 3.评论详情 blogCommentDetail
 * 4.评论点赞 likeComment
 * 5.评论删除 deleteComment
 */



public class CommentDetailActivityV2 extends BaseActivity {

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

    @BindView(R.id.ll_report)
    LinearLayout ll_report;

    @BindView(R.id.circle_remove)
    ImageView circle_remove;

    @BindView(R.id.circle_report)
    ImageView circle_report;


    @BindView(R.id.image_circle_priase)
    ImageView image_circle_priase;

    //点赞布局
    @BindView(R.id.circle_priase)
    LinearLayout circle_priase;

    @BindView(R.id.circle_share)
    LinearLayout circle_share;


    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottieAnimationView;

    //显示全部评论数布局
    @BindView(R.id.ll_have_first_comment)
    LinearLayout ll_have_first_comment;
    //显示全部评论数
    @BindView(R.id.first_comment_num)
    TextView first_comment_num;

    @BindView(R.id.more_comment_recycler)
    RecyclerView blogCommentsRecyclerView;

    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;


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

    @BindView(R.id.transfer_article_img)
    ImageView transfer_article_img;


    @BindView(R.id.transfer_article_title)
    TextView transfer_article_title;


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

    //原创的图片列表
    @BindView(R.id.pic_recyler)
    RecyclerView pic_recyler;

    //动态id有问题时显示的默认布局（比如圈子被删除了）
    @BindView(R.id.all_part_empty)
    LinearLayout all_part_empty;



    //动态评论 适配器
    CommentCircleAdapter mBolgCommentAdapter;
    //组合集合
    List<CommentCircleBean> mAllList = new ArrayList<>();
    //布局管理器
    LinearLayoutManager mLinearLayoutManager;

    //圈子明细 1
    private CircleBean mCircleBean;
    //页数 2
    private int page = 1;
 
    //临时评论数据 4
    private CommentCircleBean secondTempComment;
    //当前展现的层级 5
    private boolean isSecondComment = false;
    //用于记录在二级评论点赞后，一级界面数据没有刷新 6
    private int zanPosition;




    //单单对应从列表中过来的
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
        String blog_id = getIntent().getExtras().getString("blog_id");
        blogDetail(blog_id);

        initPicLayout();
        initSamrtLayout();
        initTransferPicLayout();
    }

    private void initSamrtLayout() {
        XnClassicsHeader header =  new XnClassicsHeader(this);
        smartRefreshLayout.setRefreshHeader(header);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mAllList.clear();
            page = 1;
            blogDetail(mCircleBean.getId());
        });
    }


    /** --------------------------------- 一级圈子列表 及 点击事件---------------------------------*/
    private void blogDetail(String blog_id) {
        Map<String,String> map = new HashMap<>();
        map.put("blog_id", blog_id + "");
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

                        setBolgData();
                        //如果在这里initLayout的话，下拉刷新时界面会闪烁
                        if(mBolgCommentAdapter == null){
                            initBlogCommentLayout();
                        }
                        //需重新设置
                        mBolgCommentAdapter.setCircleBean(mCircleBean);

                        //根据动态的id获取评论
                        getBlogCommentList(mCircleBean.getId());
                    }

                    @Override
                    public void onNetFail(String msg) {
                        if(smartRefreshLayout != null){
                            smartRefreshLayout.finishRefresh();
                        }
                        all_part_empty.setVisibility(View.VISIBLE);
                    }

                    //{"return_code":"60001","return_msg":"\u5708\u5b50\u52a8\u6001\u4e0d\u5b58\u5728","return_data":{}}
                    @Override
                    public void onHintError(String return_code, String errorMes) {
                        super.onHintError(return_code, errorMes);
                        if(smartRefreshLayout != null){
                            smartRefreshLayout.finishRefresh();
                        }
                        all_part_empty.setVisibility(View.VISIBLE);
                    }
                });
    }

    @BindView(R.id.ll_topic)
    LinearLayout ll_topic;

    @BindView(R.id.select_topic_text)
    TextView select_topic_text;

    @SuppressLint("SetTextI18n")
    private void setBolgData() {
        if(mCircleBean.getUser_info() != null){
            //话题
            if(mCircleBean.getTopic_info() != null && !TextUtils.isEmpty(mCircleBean.getTopic_info().getTitle())){
                ll_topic.setVisibility(View.VISIBLE);
                select_topic_text.setText("#" + mCircleBean.getTopic_info().getTitle());
            }else{
                ll_topic.setVisibility(View.GONE);
            }

            //加载不同的布局
            getIconType(mCircleBean);

            // 检查links同时添加原创文本
            mCircleBean  =  StringUtil.addLinksData(mCircleBean);


            if(mCircleBean.getPcLinks() !=  null && !mCircleBean.getPcLinks().isEmpty()){
                //TODO 2.26 判断是否添加回复xxx的骚操作
                if(!TextUtils.isEmpty(mCircleBean.getComment_uid()) && !"0".equals(mCircleBean.getComment_uid())){
                    if(!TextUtils.isEmpty(mCircleBean.getComment_nickname())){
                        getApplyAndIconLinkShow(mCircleBean, (Activity) mContext,content);
                    }else{
                        StringUtil.getIconLinkShow(mCircleBean, (Activity) mContext,content);
                    }
                }else{
                    //对有links的原创文本进行富文本
                    StringUtil.getIconLinkShow(mCircleBean, (Activity) mContext,content);
                }
            }else{
                //TODO 没有link 走这里  【是否有回复】
                if(!TextUtils.isEmpty(mCircleBean.getComment_uid()) && !"0".equals(mCircleBean.getComment_uid())){
                    if(!TextUtils.isEmpty(mCircleBean.getComment_nickname())){
                        getCircleReply(content,mCircleBean);
                    }else{
                        content.setText(mCircleBean.getBlog());
                    }
                }else{
                    content.setText(mCircleBean.getBlog());
                }
            }

            //发帖人
            sender_name.setText(mCircleBean.getUser_info().getName() );
            //头像
            ImageUtil.load(mContext,mCircleBean.getUser_info().getAvatar(),head_icon);
            //底部使用者头像
            ImageUtil.load(mContext,StringUtil.getUserInfoBean().getAvatar(),user_head_icon);

            //TODO 2020.1.7 根据返回的内容
            if(!StringUtil.checkNull((mCircleBean.getUser_info().getCompany_name()))
                    && !StringUtil.checkNull((mCircleBean.getUser_info().getPosition()))){
                sender_tag.setText("TA 还未职业认证");
            }else{
                sender_tag.setText( (StringUtil.checkNull((mCircleBean.getUser_info().getCompany_name()))?mCircleBean.getUser_info().getCompany_name() + " ":"") +
                        (TextUtils.isEmpty(mCircleBean.getUser_info().getPosition())?"":mCircleBean.getUser_info().getPosition()));
            }

            //是否认证
            if("1".equals(mCircleBean.getUser_info().getAuth_email_status())
                    || "1".equals(mCircleBean.getUser_info().getAuth_card_status())){
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_authen_company);
                drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
                sender_tag.setCompoundDrawables(null,null,drawable,null);
            }else{
                sender_tag.setCompoundDrawables(null,null,null,null);
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
            if(!TextUtils.isEmpty(mCircleBean.getComment_num())){
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


        //① 获取动态的类型
        int layoutType = StringUtil.getCircleType(mCircleBean);
        //② 在文本的基础上判断链接
        mCircleBean  =  StringUtil.addLinksData(mCircleBean);

        if(layoutType == CircleRecommentAdapterNew.ZF_TEXT ||
                layoutType == CircleRecommentAdapterNew.ZF_PIC ||
                layoutType == CircleRecommentAdapterNew.ZF_ACTICLE ||
                layoutType == CircleRecommentAdapterNew.ZF_LINK){
            //③ 在文本的基础上判断链接
            mCircleBean = StringUtil.addTransLinksData(mCircleBean);
        }

        //④ 根据类型显示隐藏动态布局
        if(CircleRecommentAdapterNew.YC_TEXT == layoutType){
            KLog.d("tag","do nothing");
        }else if(CircleRecommentAdapterNew.YC_PIC == layoutType){
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
        }else{
            if(mCircleBean.getP_blog() != null && mCircleBean.getP_blog().getP_user_info() != null){


                //⑤ 转发者的一些信息
                if(mCircleBean.getP_blog().getPcLinks() !=  null && !mCircleBean.getP_blog().getPcLinks().isEmpty()){
                    if(!TextUtils.isEmpty(mCircleBean.getP_blog().getComment_uid())
                            && !"0".equals(mCircleBean.getP_blog().getComment_uid())){

                        if(!TextUtils.isEmpty(mCircleBean.getP_blog().getComment_nickname())){
                            getAppTransIconLinkShow(mCircleBean.getP_blog(), (Activity) mContext,transfer_zf_content);
                        }else{
                            StringUtil.getTransIconLinkShow(mCircleBean.getP_blog(), (Activity) mContext,transfer_zf_content);
                        }
                    }else{
                        //对有links的转发文本进行富文本
                        StringUtil.getTransIconLinkShow(mCircleBean.getP_blog(), (Activity) mContext,transfer_zf_content);
                    }
                }else{
                    if(!TextUtils.isEmpty(mCircleBean.getP_blog().getComment_uid())
                            && !"0".equals(mCircleBean.getP_blog().getComment_uid())){
                        if(!TextUtils.isEmpty(mCircleBean.getP_blog().getComment_nickname())){
                            getTransCircleReply(transfer_zf_content,mCircleBean.getP_blog());
                        }else{
                            transfer_zf_content.setText(mCircleBean.getP_blog().getBlog());
                        }
                    }else{
                        transfer_zf_content.setText(mCircleBean.getP_blog().getBlog());
                    }
                }


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
                transfer_link_http.setText(mCircleBean.getP_blog().getLink());
            }else if(CircleRecommentAdapterNew.ZF_ACTICLE == layoutType){
                part_zf_article.setVisibility(View.VISIBLE);
                transfer_article_title.setText(mCircleBean.getP_blog().getArticle_title());
                ImageUtil.load(this,mCircleBean.getP_blog().getArticle_image(),transfer_article_img);
            }
        }

    }



    SpannableString spannableString ;
    StringBuilder sb = new StringBuilder();

    private void getCircleReply(TextView msg, CircleBean item) {
        sb.setLength(0);
        String name = item.getComment_nickname();
        sb.append("回复 ").append(name).append(":").append(item.getBlog().trim());

        int authorNamelength = name.length();
        spannableString = new SpannableString(sb.toString().trim());
        ForegroundColorSpan fCs2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_blue));
        //中间有 回复 两个字 + 1个空格
        spannableString.setSpan(fCs2,   3,   3 + authorNamelength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        NoLineCllikcSpan clickableSpan = new NoLineCllikcSpan() {
            @Override
            public void onClick(View widget) {
                KLog.d("tag","点击的是 " + name);
                UIHelper.toUserInfoV2Activity(mContext,item.getComment_uid());
            }
        };
        spannableString.setSpan(clickableSpan, 3, 3 + authorNamelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        msg.setText(spannableString);
        msg.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void getTransCircleReply(TextView msg,CircleBean.P_blog pBlog) {
        sb.setLength(0);
        String name = pBlog.getComment_nickname();
        KLog.d("tag","名字是 " + name);
        sb.append("回复 ").append(name).append(":").append(pBlog.getBlog().trim());

        int authorNamelength = name.length();
        spannableString = new SpannableString(sb.toString().trim());
        ForegroundColorSpan fCs2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_blue));
        //中间有 回复 两个字 + 1个空格
        spannableString.setSpan(fCs2,   3,   3 + authorNamelength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        NoLineCllikcSpan clickableSpan = new NoLineCllikcSpan() {
            @Override
            public void onClick(View widget) {
                KLog.d("tag","点击的是 " + name);
                UIHelper.toUserInfoV2Activity(mContext,pBlog.getComment_uid());
            }
        };
        spannableString.setSpan(clickableSpan, 3, 3 + authorNamelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        msg.setText(spannableString);
        msg.setMovementMethod(LinkMovementMethod.getInstance());

    }

    public  void getAppTransIconLinkShow(CircleBean.P_blog item, Activity activity, TextView msg) {
        SpannableString spanString2;
        String name = item.getComment_nickname();
        String content = "回复 " + name + ":" + item.getBlog();
        String icon = "[icon]";
        //获取链接
        int size  =  item.getPcLinks().size();
        if(size >  0){
            for (int k = 0; k < size; k++) {
                content = content.replace(item.getPcLinks().get(k),icon);
            }
        }
//        KLog.d("tag","最新字符串是 " + content);

        String newContent = content;

        //保存字符的开始下标
        List<Integer> pos = new ArrayList<>();

        int c = 0;
        for(int i = 0; i< size ;i++ ){
            c = content.indexOf(icon,c);
            //如果有S这样的子串。则C的值不是-1.
            if(c != -1){
                //记录找到字符的索引
                pos.add(c);
                //记录字符串后面的
                c = c + 1;
                //这里的c+1 而不是 c+ s.length();这是因为。如果str的字符串是“aaaa”， s = “aa”，则结果是2个。但是实际上是3个子字符串
                //将剩下的字符冲洗取出放到str中
                //content = content.substring(c + 1);
            }
            else {
                //i++;
                KLog.d("tag","没有");
                break;
            }
        }

        //拼接链接
        Drawable drawableLink = activity.getResources().getDrawable(R.mipmap.icon_link_http);
        drawableLink.setBounds(0, 0, drawableLink.getMinimumWidth(), drawableLink.getMinimumHeight());

        spanString2 = new SpannableString(newContent);

        int w;
        for (int k = 0; k < size; k++) {
            w = k;
            int finalW = w;
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    String li = item.getPcLinks().get(finalW);
                    KLog.d("tag","点击了网页 " + li);
                    UIHelper.toWebViewActivityWithOnLayout(activity,li,"stringUtil");
                }
            };

            //居中对齐imageSpan  -- 每次都要创建一个新的 才有效果
            CustomImageSpan imageSpan = new CustomImageSpan(BaseApp.getApplication(),R.mipmap.icon_link_http,2);
            spanString2.setSpan(imageSpan, pos.get(k), pos.get(k) + icon.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanString2.setSpan(clickableSpan, pos.get(k), pos.get(k) + icon.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        int authorNamelength = name.length();
        ForegroundColorSpan fCs2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_blue));
        //中间有 回复 两个字 + 1个空格
        spanString2.setSpan(fCs2,   3,   3 + authorNamelength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        NoLineCllikcSpan clickableSpan = new NoLineCllikcSpan() {
            @Override
            public void onClick(View widget) {
                KLog.d("tag","点击的是 " + name);
                UIHelper.toUserInfoV2Activity(mContext,item.getComment_uid());
            }
        };
        spanString2.setSpan(clickableSpan, 3, 3 + authorNamelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        msg.setText( spanString2);
        //下面语句不写的话，点击clickablespan没效果
        msg.setMovementMethod(LinkMovementMethod.getInstance());
    }




    //先构建 回复xxx + 正文 -- 再富文本话
    public  void getApplyAndIconLinkShow(CircleBean item, Activity activity,TextView msg) {

        SpannableString spanString2;
        String name = item.getComment_nickname();
        String content = "回复 " + name + ":" + item.getBlog() + "";

        String icon = "[icon]";
        //获取链接
        int size  =  item.getPcLinks().size();
        if(size >  0){
            for (int k = 0; k < size; k++) {
                content = content.replace(item.getPcLinks().get(k),icon);
            }
        }
        //如果没有匹配，则还是原来的字符串
        String newContent = content;
        //保存字符的开始下标
        List<Integer> pos = new ArrayList<>();
        int c = 0;
        for(int i = 0; i< size ;i++ ){
            c = content.indexOf(icon,c);
            //如果有S这样的子串。则C的值不是-1.
            if(c != -1){
                //记录找到字符的索引
                pos.add(c);
                //记录字符串后面的
                c = c + 1;
                //这里的c+1 而不是 c+ s.length();这是因为。如果str的字符串是“aaaa”， s = “aa”，则结果是2个。但是实际上是3个子字符串
                //将剩下的字符冲洗取出放到str中
                //content = content.substring(c + 1);
            }
            else {
                //i++;
                KLog.d("tag","没有");
                break;
            }
        }

        //拼接链接
//        Drawable drawableLink = activity.getResources().getDrawable(R.mipmap.icon_link_http);
//        drawableLink.setBounds(0, 0, drawableLink.getMinimumWidth(), drawableLink.getMinimumHeight());

        spanString2 = new SpannableString(newContent);

        int w;
        for (int k = 0; k < size; k++) {
            w = k;
            int finalW = w;
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    String li = item.getPcLinks().get(finalW);
                    UIHelper.toWebViewActivity(activity,li);
                }
            };

            //居中对齐imageSpan  -- 每次都要创建一个新的 才有效果
            CustomImageSpan imageSpan = new CustomImageSpan(BaseApp.getApplication(),R.mipmap.icon_link_http,2);
            spanString2.setSpan(imageSpan, pos.get(k), pos.get(k) + icon.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanString2.setSpan(clickableSpan, pos.get(k), pos.get(k) + icon.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }


        int authorNamelength = name.length();
        ForegroundColorSpan fCs2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_blue));
        //中间有 回复 两个字 + 1个空格
        spanString2.setSpan(fCs2,   3,   3 + authorNamelength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        NoLineCllikcSpan clickableSpan = new NoLineCllikcSpan() {
            @Override
            public void onClick(View widget) {
                KLog.d("tag","点击的是 " + name);
                UIHelper.toUserInfoV2Activity(mContext,item.getComment_uid());
            }
        };
        spanString2.setSpan(clickableSpan, 3, 3 + authorNamelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        msg.setText( spanString2);

        //下面语句不写的话，点击clickablespan没效果
        msg.setMovementMethod(LinkMovementMethod.getInstance());

    }


    private void initBlogCommentLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        blogCommentsRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mBolgCommentAdapter = new CommentCircleAdapter(mAllList);

        mBolgCommentAdapter.setMyPotion(myPotion);
        blogCommentsRecyclerView.setAdapter(mBolgCommentAdapter);
        ((SimpleItemAnimator)blogCommentsRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //解决数据加载不完
        blogCommentsRecyclerView.setNestedScrollingEnabled(true);
        blogCommentsRecyclerView.setHasFixedSize(true);

        initEvent();
    }

    private void getBlogCommentList(String blog_id) {
        Map<String,String> map = new HashMap<>();
        map.put("blog_id",blog_id + "");
        map.put("page",page + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getBlogComment(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<CommentCircleBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<CommentCircleBean>> response) {
                        //临时数据
                        List<CommentCircleBean> mCommentCircleBeanList = response.getReturn_data();
                        setAllData(mCommentCircleBeanList);
                    }

                    //{"return_code":"200","return_msg":"success","return_data":{}} -- 后台空集合返回{}，那么会出现解析异常，在这里所判断
                    @Override
                    public void onNetFail(String msg) {
                        if("解析错误".equals(msg)){
                            if(page == 1){
                                setBlogCommentListData(null);
                            }else{
                                mBolgCommentAdapter.loadMoreComplete();
                                mBolgCommentAdapter.loadMoreEnd();
                            }
                        }
                    }
                });
    }

    private void setAllData(List<CommentCircleBean> serverData) {
        if(1 == page){
            if(serverData != null && !serverData.isEmpty()){
                setBlogCommentListData(serverData);
                mBolgCommentAdapter.setNewData(mAllList);
                //如果第一次返回的数据不满10条，则显示无更多数据
                if(serverData.size() < Constant.SEERVER_NUM){
                    mBolgCommentAdapter.loadMoreEnd();
                }
                ll_empty.setVisibility(View.GONE);
                blogCommentsRecyclerView.setVisibility(View.VISIBLE);
            }else{
                ll_empty.setVisibility(View.VISIBLE);
                blogCommentsRecyclerView.setVisibility(View.GONE);
                ((ImageView)ll_empty.findViewById(R.id.iv_empty)).setImageResource(R.mipmap.icon_empty_comment);
                ((TextView)ll_empty.findViewById(R.id.tv_empty)).setText("成为第一个评论者～");
            }
        }else{
            //已为加载更多有数据
            if(serverData != null && serverData.size() > 0){
                setBlogCommentListData(serverData);
                mBolgCommentAdapter.loadMoreComplete();
                mBolgCommentAdapter.addData(bolgCommentList);
            }else{
                //已为加载更多无更多数据
                mBolgCommentAdapter.loadMoreEnd();
            }
        }
    }


    //临时数据转化地
    private List<CommentCircleBean> bolgCommentList = new ArrayList<>();
    private void setBlogCommentListData(List<CommentCircleBean> list) {

        bolgCommentList.clear();
        bolgCommentList.addAll(list);

        if(page == 1){
            mAllList.addAll(bolgCommentList);
        }
    }


    @OnClick({R.id.toBlogComment,
            R.id.circle_comment,
            R.id.iv_back,
            R.id.part_zf_link,
            R.id.part_yc_link,
            R.id.part1111,
            R.id.part_yc_acticle,
            R.id.transfer_zf_ll,
            R.id.ll_topic
    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.ll_topic:
                UIHelper.toTopicDetailActivity(this,mCircleBean.getTopic_id());
                break;
            case R.id.part1111:
                //头像跳转事件
                UIHelper.toUserInfoActivity(this,mCircleBean.getUid());
                break;
            case R.id.part_yc_link:
                //原创外链跳转事件
                UIHelper.toWebViewActivity(this,mCircleBean.getLink());
                break;
            case R.id.part_zf_link:
                //转发外链点击事件
                UIHelper.toWebViewActivity(this,mCircleBean.getP_blog().getLink());
                break;
            case R.id.part_yc_acticle:
                //原创文章跳转事件
                UIHelper.toNewsDetailActivity(this,mCircleBean.getArticle_id());
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.transfer_zf_ll:
                //转发帖子item点击事件
                UIHelper.toCommentDetailActivity(mContext,mCircleBean.getP_blog().getId());
                break;
            case R.id.circle_comment:
            case R.id.toBlogComment:
                //评论圈子
                ToBlogDialog(mCircleBean.getId(),"");
                break;
            default:
        }
    }


    private String mWords;
    private void ToBlogDialog(String blog_id, String replyWho) {
        final ShowCommentDialog talkAlertDialog = new ShowCommentDialog(this).builder();
        talkAlertDialog.setNum(500);
        if(!TextUtils.isEmpty(replyWho)){
            talkAlertDialog.setHint(replyWho);
        }
        //获取输入的文字
        talkAlertDialog.setWriteWordLisenter((position, words) -> mWords = words);

        //发布 【转发到圈子】 是否转发动态 1是 0否s
        talkAlertDialog.setOnIsToCircleLister(bug -> {
            int is_repost = 0;
            if(bug){
                is_repost = 1;
            }
            createBlogComment(blog_id,mWords,is_repost);
        });
        talkAlertDialog.show();
    }


    private void createBlogComment(String blog_id, String words, int is_repost) {
        Map<String,String> map = new HashMap<>();
        //是否转发动态 1是 0否
        map.put("is_repost",is_repost + "");
        map.put("blog_id",blog_id);
        map.put("comment",words);
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
                        getBlogCommentList(mCircleBean.getId());

                        //全部回复重定义
                        mCircleBean.setComment_num((Integer.parseInt(mCircleBean.getComment_num()) + 1) + "");
                        first_comment_num.setText("全部" + mCircleBean.getComment_num() + "条评论");
                        //回调列表事件
                        EventBus.getDefault().post(new BlogPriaseEvent(myPotion,mCircleBean.getIs_like(),mCircleBean.getLike_num(),mCircleBean.getComment_num()));
                        //顶部帖子重定义
                        zanCommentChange(comment,zan_num,image_circle_priase,
                                mCircleBean.getLike_num() + "",mCircleBean.getIs_like(),mCircleBean.getComment_num());
                    }
                });
    }




    /** ------------------------------------------------------------------ 二级评论列表 及 点击事件------------------------------------------*/
    int secondPage = 1;
    LinearLayout ll_second_empty;
    RelativeLayout totalk;
    ImageView second_close;
    TextView hint_text;
    RecyclerView mSecondRV;
    ImageView head_second_icon;
    LinearLayout comment_priase;
    ImageView zan_second_img_second;
    ImageView icon;
    BottomSheetDialog bottomSheetDialog;
    CommentSecondAdapter mCommentSecondAdapter;
    BottomSheetBehavior mDialogBehavior;

    List<MulSecondCommentBean> allSecondComments = new ArrayList<>();
    TextView nickname_second;
    LinearLayout last_reply_ll;
    TextView comment_text_second;
    TextView time_publish_second;
    TextView zan_second_num_second;
    TextView comment_num_second;

    private void showBlogCommentDetailDialog(CommentCircleBean superiorComment) {
        View view = View.inflate(this, R.layout.dialog_bottom_comment, null);
        mSecondRV = view.findViewById(R.id.recycler);
        hint_text = view.findViewById(R.id.hint_text);
        zan_second_num_second = view.findViewById(R.id.zan_second_num_second);
        last_reply_ll = view.findViewById(R.id.last_reply_ll);
        nickname_second = view.findViewById(R.id.nickname_second);
        comment_text_second = view.findViewById(R.id.comment_text_second);
        time_publish_second = view.findViewById(R.id.time_publish_second);
        head_second_icon = view.findViewById(R.id.head_second_icon);
        zan_second_img_second = view.findViewById(R.id.zan_second_img_second);
        comment_priase = view.findViewById(R.id.comment_priase);
        comment_num_second = view.findViewById(R.id.comment_num_second);
        second_close = view.findViewById(R.id.second_close);
        second_close.setOnClickListener(view12 -> bottomSheetDialog.dismiss());
        ll_second_empty = view.findViewById(R.id.ll_second_empty);
        icon = view.findViewById(R.id.icon);
        totalk = view.findViewById(R.id.totalk);
        totalk.setOnClickListener(view1 -> {
            isSecondComment = true;
            KLog.d("tag","评论的是一级评论，同时点击此评论的id 为  " + superiorComment.getId());
            showBlogCommentDialog(-1,superiorComment);
        });

        mCommentSecondAdapter = new CommentSecondAdapter(allSecondComments);
        mCommentSecondAdapter.setSuperiorComment(superiorComment);
        mSecondRV.setHasFixedSize(true);
        ((SimpleItemAnimator)mSecondRV.getItemAnimator()).setSupportsChangeAnimations(false);
        mSecondRV.setLayoutManager(new LinearLayoutManager(this));
        mSecondRV.setAdapter(mCommentSecondAdapter);
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
//                    KLog.d("tag","屏幕的高度减去状态栏高度是 : " +  (ScreenUtils.getScreenHeight() - SizeUtils.dp2px(25)) +  "   " + bottomSheet.getTop() + "");
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

        //设值
        hint_text.setHint("回复 " + superiorComment.getUser_info().getName());
        comment_num_second.setText(superiorComment.getComment_num() + "条回复");

        User_info temp = superiorComment.getUser_info();
        if(null != temp){
            if(!StringUtil.checkNull((temp.getCompany_name()))
                    && !StringUtil.checkNull((temp.getPosition()))){
                nickname_second.setText(temp.getName() + " TA 还未职业认证");
            }else{
                nickname_second.setText(temp.getName() +  " " + (StringUtil.checkNull((temp.getCompany_name()))?temp.getCompany_name() + " ":"") +
                        (TextUtils.isEmpty(temp.getPosition())?"":temp.getPosition()));
            }


            //是否认证
            if("1".equals(temp.getAuth_email_status()) || "1".equals(temp.getAuth_card_status())){
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_authen_company);
                drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
                nickname_second.setCompoundDrawables(null,null,drawable,null);
            }else{
                nickname_second.setCompoundDrawables(null,null,null,null);
            }
        }


        comment_text_second.setText(superiorComment.getComment());
        ImageUtil.load(this,superiorComment.getUser_info().getAvatar(),head_second_icon);
        //发布时间
        if(StringUtil.checkNull(superiorComment.getCreated_at())){
            String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(superiorComment.getCreated_at()) * 1000L);
            time_publish_second.setText(s);
        }
        comment_text_second.setText(superiorComment.getComment());
        ImageUtil.loadByDefaultHead(this,superiorComment.getUser_info().getAvatar(),head_second_icon);
        zanChange(zan_second_num_second,zan_second_img_second,superiorComment.getLike_num(),superiorComment.getIs_like());

        //赞赞赞
        comment_priase.setOnClickListener((view22)->{
            likeComment(superiorComment);
        });

        //头像跳转
        head_second_icon.setOnClickListener(v -> UIHelper.toUserInfoActivity(CommentDetailActivityV2.this,superiorComment.getUid()));


        mCommentSecondAdapter.setOnLoadMoreListener(() -> {
            ++secondPage;
            KLog.d("tag","加载更多");
            getSecondCommentComment(superiorComment.getId());
        },mSecondRV);


        mCommentSecondAdapter.setOnItemClickListener((adapter, view1, position) -> {
            //此处逻辑和点击一级评论item一样
            isSecondComment = true;
            secondTempComment = mCommentSecondAdapter.getData().get(position).getCircleComment();
            KLog.d("tag","点击此评论的id 为  " + this.secondTempComment.getId() + " 被回复的人事 " + this.secondTempComment.getUser_info().getName());
//            showTalkDialogSecondComment(position,secondTempComment);
            showBlogCommentDialog(position,secondTempComment);

        });

        mCommentSecondAdapter.setOnItemChildClickListener((adapter, view13, position) -> {
            isSecondComment = true;
            secondTempComment = mCommentSecondAdapter.getData().get(position).getCircleComment();
            KLog.d("tag","点击此评论的id 为  " + this.secondTempComment.getId() + " 被回复的人是 " + this.secondTempComment.getUser_info().getName());
//            showTalkDialogSecondComment(position, this.secondTempComment);
            showBlogCommentDialog(position,secondTempComment);
        });

        mCommentSecondAdapter.setOnReduceListener(() -> {
          setSecondReply(superiorComment,-1);
        });

        secondPage = 1;
        allSecondComments.clear();
        getSecondCommentComment(superiorComment.getId());
    }


    private void getSecondCommentComment(String blog_comment_id) {
        Map<String,String> map = new HashMap<>();
        map.put("blog_comment_id",blog_comment_id + "");
        map.put("page",secondPage + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getCommentComment(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<CommentCircleBean>>>() {
                    @Override
                    public void onSuccess(HttpResponse<List<CommentCircleBean>> response) {
                        List<CommentCircleBean> mCommentSList = response.getReturn_data();
                        if(1 == secondPage){
                            if(!mCommentSList.isEmpty()){
                                setData2(mCommentSList);
                                mCommentSecondAdapter.setNewData(allSecondComments);
                                //如果第一次返回的数据不满10条，则显示无更多数据
                                if(mCommentSList.size() < Constant.SEERVER_NUM){
                                    mCommentSecondAdapter.loadMoreEnd();
                                }
                                ll_second_empty.setVisibility(View.GONE);
                                last_reply_ll.setVisibility(View.VISIBLE);
                                mSecondRV.setVisibility(View.VISIBLE);
                            }else{
                                mSecondRV.setVisibility(View.GONE);
                                last_reply_ll.setVisibility(View.GONE);
                                ll_second_empty.setVisibility(View.VISIBLE);
                            }
                        }else{
                            //已为加载更多有数据
                            if(mCommentSList != null && mCommentSList.size() > 0){
                                setData2(mCommentSList);
                                mCommentSecondAdapter.loadMoreComplete();
                                mCommentSecondAdapter.addData(mMulSecondCommentBeans);
                            }else{
                                //已为加载更多无更多数据
                                mCommentSecondAdapter.loadMoreComplete();
                                mCommentSecondAdapter.loadMoreEnd();
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
                                mCommentSecondAdapter.loadMoreComplete();
                                mCommentSecondAdapter.loadMoreEnd();
                            }
                        }
                    }

                });
    }


    List<MulSecondCommentBean> mMulSecondCommentBeans = new ArrayList<>();
    private void setData2(List<CommentCircleBean> list) {
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


    /** --------------------------------- 点赞评论  ---------------------------------*/


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



    private void initEvent() {
        //右下角的信息
        mBolgCommentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.toFirstComment:
                    showBlogCommentDialog(position,mBolgCommentAdapter.getData().get(position));
                    break;
                case R.id.ll_has_second_comment:
                    KLog.d("tag","ll_has_second_comment");
                    //🍅 记录帖子position
                    zanPosition  = position;
                    //得到点击索引的item
                    blogCommentDetail = mBolgCommentAdapter.getData().get(position);
                    blogCommentDetail(blogCommentDetail.getId());


                    break;
                default:
            }
        });

        //点击item项
        mBolgCommentAdapter.setToShowDialogListener((item, position) -> {
            zanPosition  = position;
            //得到点击索引的item
            blogCommentDetail = mBolgCommentAdapter.getData().get(position);
            blogCommentDetail(blogCommentDetail.getId());
        });

        //点击删除后的回调
        mBolgCommentAdapter.setChangeDetailListener((good_num, is_good, com_num) -> {
            zanCommentChange(comment,zan_num,image_circle_priase, good_num,is_good ,com_num);
            first_comment_num.setText("全部" + com_num + "条评论");
        });

        mBolgCommentAdapter.setOnLoadMoreListener(() -> {
            ++page;
            KLog.d("tag","加载更多");
            getBlogCommentList(mCircleBean.getId());
        },blogCommentsRecyclerView);




    }



    //动态评论详情(把一级评论的数据带过去)
    private CommentCircleBean blogCommentDetail;
    private void blogCommentDetail(String blog_id) {
        Map<String,String> map = new HashMap<>();
        map.put("blog_comment_id",blog_id + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().blogCommentDetail(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<CommentCircleBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<CommentCircleBean> response) {
                        blogCommentDetail = response.getReturn_data();
                        showBlogCommentDetailDialog(blogCommentDetail);
                    }
                });
    }







    private String mmWords;
    private void showBlogCommentDialog(int position, CommentCircleBean beanNew) {
        final ShowCommentDialog showCommentDialog = new ShowCommentDialog(this).builder();
        showCommentDialog.setHint(beanNew.getUser_info().getName());
        showCommentDialog.setMyPosition(position);
        //设置最大数还是140
        showCommentDialog.setNum(140);
        //获取输入的文字
        showCommentDialog.setWriteWordLisenter((myPositon, words) -> mmWords = words);

        //发布 【转发到圈子】 是否转发动态 1是 0否s
        showCommentDialog.setOnIsToCircleLister(bug -> {
            int is_repost = 0;
            if(bug){
                is_repost = 1;
            }
            createCommentComment(beanNew,mmWords,is_repost);
        });
        showCommentDialog.show();
    }


    //评论 以及 评论的评论 
    private void createCommentComment(CommentCircleBean temp, String words, int is_repost) {
        Map<String,String> map = new HashMap<>();
        map.put("comment",words);
        map.put("class",temp.getComment_class());
        map.put("comment_id",temp.getId());
        map.put("create_uid",temp.getUid());
        map.put("is_repost",is_repost + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().createCommentComment(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        ToastUtils.showShort("评论成功");
                        if(!isSecondComment){
                            //评论列表直接刷新
                            page = 1;
                            mAllList.clear();
                            getBlogCommentList(mCircleBean.getId());
                        }else{
                            secondPage = 1;
                            allSecondComments.clear();
                            getSecondCommentComment(blogCommentDetail.getId());
                            EventBus.getDefault().post(new RefreshCircleDetailCommentEvent());
                            //增加1
                            setSecondReply(blogCommentDetail,1);
                        }
                    }
                });
    }


    private void setSecondReply(CommentCircleBean bean,int value){
        bean.setComment_num(((Integer.parseInt(bean.getComment_num())  +  value) + ""));
        comment_num_second.setText(bean.getComment_num() + "条回复");
        //如果没有数据了，显示空布局
        if("0".equals(bean.getComment_num())){
            last_reply_ll.setVisibility(View.GONE);
            ll_second_empty.setVisibility(View.VISIBLE);
        }
    }




    /** --------------------------------- 二级评论弹框 ---------------------------------*/
//    private void showTalkDialogSecondComment(int position, CommentCircleBean beanNew) {
//        final TalkCircleAlertDialog talkAlertDialog = new TalkCircleAlertDialog(this).builder();
//        talkAlertDialog.setMyPosition(position);
//        talkAlertDialog.setHint(beanNew.getUser_info().getName());
//        talkAlertDialog.setTalkLisenter((position1, words) -> {
//            createCommentComment(beanNew,words, is_repost);
//        });
//        talkAlertDialog.show();
//    }


    /** --------------------------------- 动态评论弹框 ---------------------------------*/





    //二级评论上的圈子
    private void likeComment(CommentCircleBean circleBean) {
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
                        //更新第一列表数据 需要加上改变后的数据
                        if(zanPosition != -1){
                            mBolgCommentAdapter.getData().get(zanPosition).setLike_num(circleBean.getLike_num());
                            mBolgCommentAdapter.getData().get(zanPosition).setIs_like(circleBean.getIs_like());
                            mBolgCommentAdapter.notifyItemChanged(zanPosition);
                        }
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
        mCirclePicAdapter.setOnItemClickListener((adapter, view, position) -> {
            if(!mCircleBean.getImages().isEmpty()){
                UIHelper.toPicPreViewActivity(mContext,  mCircleBean.getImages(),position,true);
            }
        });
    }



    /** 更新评论数据 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshCircleDetailCommentEvent(RefreshCircleDetailCommentEvent event){
        page = 1;
        mAllList.clear();
        getBlogCommentList(mCircleBean.getId());
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



    //通过uid加载布局
    private void getIconType(CircleBean item) {
        String uid = item.getUid();
        String myUid = StringUtil.getUserInfoBean().getUid();
        ll_report.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(uid) && uid.equals(myUid)){
            circle_remove.setVisibility(View.VISIBLE);
            circle_report.setVisibility(View.GONE);
        }else{
            circle_remove.setVisibility(View.GONE);
            circle_report.setVisibility(View.VISIBLE);
        }

        //帖子举报/删除 -- 为了增大触摸面积
        ll_report.setOnClickListener(view -> {
            if(!TextUtils.isEmpty(uid) && uid.equals(myUid)){
                showRemoveDialog(item);
            }else{
                showPopupWindow(item,circle_report);
                StringUtil.setBackgroundAlpha((Activity) mContext, 0.6f);
            }
        });
    }



    private void showRemoveDialog(CircleBean circleBean) {
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(mContext).builder();
        iosAlertDialog.setPositiveButton("删除", v -> {
            deleteBlog(circleBean);
        }).setNegativeButton("取消", v -> {
        }).setMsg("确定要删除这条动态？").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }


    private void deleteBlog(CircleBean mCircleBean) {
        Map<String,String> map = new HashMap<>();
        map.put("blog_id",mCircleBean.getId());
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().deleteBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        finish();
                        Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showPopupWindow(CircleBean circleBean,View view) {
        //加载布局
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_report, null);
        PopupWindow mPopupWindow = new PopupWindow(inflate);
        TextView report = inflate.findViewById(R.id.report);
        TextView share = inflate.findViewById(R.id.share);
        //必须设置宽和高
        mPopupWindow.setWidth(SizeUtils.dp2px(80f));
        mPopupWindow.setHeight(SizeUtils.dp2px(44f));
        //点击其他地方隐藏,false为无反应
        mPopupWindow.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //以view的左下角为原点，xoff为正表示向x轴正方向偏移像素
            mPopupWindow.showAsDropDown(view, -SizeUtils.dp2px(52f + 12), SizeUtils.dp2px(10f));
        }
        //对popupWindow进行显示
        mPopupWindow.update();
        //消失时将透明度设置回来
        mPopupWindow.setOnDismissListener(() -> {
            if (null != mContext) {
                StringUtil.setBackgroundAlpha((Activity) mContext, 1f);
            }
        });

        report.setOnClickListener(view1 -> {
            reportBlog(circleBean);
            mPopupWindow.dismiss();
        });

        share.setOnClickListener(view1 -> {
            mPopupWindow.dismiss();
        });
    }


    private void reportBlog(CircleBean mCircleBean) {
        Map<String,String> map = new HashMap<>();
        map.put("blog_id",mCircleBean.getId());
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().reportBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        ToastUtils.showShort("举报成功");
                    }

                });
    }


}



