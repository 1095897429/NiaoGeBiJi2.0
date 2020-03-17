package com.qmkj.niaogebiji.module.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
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
import com.qmkj.niaogebiji.common.dialog.ActiclePointDialog;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.dialog.QuestionResultErrorDialog;
import com.qmkj.niaogebiji.common.dialog.QuestionResultRightDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.dialog.TalkAlertDialog;
import com.qmkj.niaogebiji.common.dialog.TalkCircleAlertDialog;
import com.qmkj.niaogebiji.common.dialog.ThingDownAlertDialog;
import com.qmkj.niaogebiji.common.dialog.ThingDownNotEnoughAlertDialog;
import com.qmkj.niaogebiji.common.dialog.ThingDownOkAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CommentActicleAdapter;
import com.qmkj.niaogebiji.module.adapter.CommentSecondAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.adapter.TestLaunchItemAdapter;
import com.qmkj.niaogebiji.module.bean.ActicleCommentHeadBean;
import com.qmkj.niaogebiji.module.bean.ActiclePointBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentOkBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.RecommendBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.bean.TestBean;
import com.qmkj.niaogebiji.module.bean.TestOkBean;
import com.qmkj.niaogebiji.module.event.ActicleShareEvent;
import com.qmkj.niaogebiji.module.event.AudioEvent;
import com.qmkj.niaogebiji.module.event.RefreshActicleCommentEvent;
import com.qmkj.niaogebiji.module.event.SendOkCircleEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.qmkj.niaogebiji.module.widget.ObservableScrollView;
import com.qmkj.niaogebiji.module.widget.RCImageView;
import com.qmkj.niaogebiji.module.widget.StarBar;
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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-14
 * 描述:文章详情页
 *   1.catid = 113 是带有音频的早报明细
 *   2.评论文章成功后，是否显示此条评论
 *   3.评论某条一级评论，并不显示回复
 *   4.评论某条一级评论下的评论，显示【回复】
 *
 *
 *   0.showTalkDialog
 *   1.showTalkDialogFirstComment
 *   2.showTalkDialogSecondComment
 *
 *
 * 重点
 * 0.评论的实体叫做CommentBean.FirstComment ，文章叫做NewsDetailBean
 * 1.构建FirstComment类型的临时变量 oneComment ，在每次点击列表item时重新赋值
 * 2.二级评论实体adapter中多实体
 *
 *
 *
 * 1.转发到圈子 -- 圈子发布页
 * 2.转发到动态 -- 转发页
 */
public class NewsDetailActivity extends BaseActivity {

    @BindView(R.id.scrollView)
    ObservableScrollView scrollView;

    @BindView(R.id.solid_part)
    LinearLayout solid_part;

    @BindView(R.id.part_small_head)
    LinearLayout part_small_head;

    @BindView(R.id.backtop)
    ImageView backtop;

    @BindView(R.id.focus11111)
    TextView focus11111;

    @BindView(R.id.focus)
    TextView focus;

    @BindView(R.id.tv_title1111)
    TextView tv_title1111;

    @BindView(R.id.head_icon1111)
    CircleImageView head_icon1111;

    @BindView(R.id.head_icon)
    CircleImageView head_icon;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.love)
    ImageView love;

    @BindView(R.id.acticle_point)
    TextView acticle_point;

    @BindView(R.id.webview)
    MyWebView mMyWebView;

    @BindView(R.id.more_read_list)
    RecyclerView more_read_list;

    @BindView(R.id.part1111_reading)
    LinearLayout part1111_reading;

    @BindView(R.id.question_title)
    TextView question_title;

    @BindView(R.id.part_test)
    LinearLayout part_test;


    @BindView(R.id.num_feather_text)
    TextView num_feather_text;

    @BindView(R.id.comment_ll)
    LinearLayout comment_ll;

    //综合评分
    @BindView(R.id.starBar)
    StarBar starBar;

    @BindView(R.id.starMyBar)
    StarBar starMyBar;


    @BindView(R.id.more_comment_list)
    RecyclerView more_comment_list;

    @BindView(R.id.test_recycler)
    RecyclerView test_recycler;

    @BindView(R.id.news_play)
    ImageView news_play;

    @BindView(R.id.rl_audio)
    RelativeLayout rl_audio;

    @BindView(R.id.summary_text)
    TextView summary_text;

    @BindView(R.id.audio_summary)
    TextView audio_summary;


    @BindView(R.id.big_pic)
    RCImageView big_pic;

    @BindView(R.id.data_link_title)
    TextView data_link_title;

    @BindView(R.id.data_link_num_feather)
    TextView data_link_num_feather;

    @BindView(R.id.data_link_num_down)
    TextView data_link_num_down;

    @BindView(R.id.allready_remark)
    LinearLayout allready_remark;


    @BindView(R.id.toRating)
    TextView toRating;

    @BindView(R.id.ll_data)
    LinearLayout ll_data;

    @BindView(R.id.tv_tag1111)
    TextView tv_tag1111;


    @BindView(R.id.ll_empty)
    LinearLayout ll_empty;

    @BindView(R.id.iv_empty)
    ImageView iv_empty;

    @BindView(R.id.tv_empty)
    TextView tv_empty;


    @BindView(R.id.comment)
    ImageView comment;

    @BindView(R.id.rl_msg)
    RelativeLayout rl_msg;

    @BindView(R.id.comment_num)
    TextView comment_num;

    @BindView(R.id.comment_num_all)
    TextView comment_num_all;

    @BindView(R.id.author_type)
    ImageView author_type;



    //一级评论 1
    CommentBean.FirstComment oneComment;

    //二级评论 2
    CommentBean.FirstComment secondComment;

    //因为共有一个接口，就索性利用这个在结果处判断
    private boolean isSecondComment = false;

    //文章的id
    private String newsId;
    //文章详情bean
    private NewsDetailBean mNewsDetailBean;


    //固定标题的高度
    private int solid_title_height ;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_news_detail;
    }

    @Override
    protected void initView() {

        KLog.e("tag","NewsDetailActivity ----  initData");
        showWaitingDialog();
        newsId = getIntent().getStringExtra("newsId");
        initTestLayout();
        initCommentListLayout();
        getCommentData();
        detail();

        viewsAddListener();

        initRxTime();

        mMyWebView.setActivity(h -> solid_webview_height = h);

    }


    @Override
    protected boolean regEvent() {
        return true;
    }

    @OnClick({R.id.backtop,R.id.focus11111,R.id.focus,R.id.love,R.id.iv_back,R.id.toDown,R.id.toRating,
            R.id.test_submit,
            R.id.iv_right,R.id.share,
            R.id.head_icon1111,R.id.head_data,
            R.id.comment,
            R.id.rl_msg,
            R.id.toLlTalk,
            R.id.rl_audio,
    })
    public void clicks(View view){
        Drawable drawable = getResources().getDrawable(R.mipmap.icon_answer_select);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        switch (view.getId()){
            case R.id.rl_audio:
                MobclickAgentUtils.onEvent(UmengEvent.index_detail_play_2_0_0);

                KLog.d("tag","打开音频");
                String audio =  mNewsDetailBean.getVideo();
                String title =  mNewsDetailBean.getSummary();
                EventBus.getDefault().post(new AudioEvent(audio,title,""));
                break;
            case R.id.toLlTalk:

                MobclickAgentUtils.onEvent(UmengEvent.index_detail_commentbar_2_0_0);

                //TODO 弹框1
                isSecondComment = false;
                showTalkDialog(-1,"aimToActicle","");
                break;
            case R.id.comment:
            case R.id.rl_msg:
                MobclickAgentUtils.onEvent(UmengEvent.index_detail_commentbtn_2_0_0);

                scrollView.post(() -> {
                    //移动到位置
                    scrollView.smoothScrollTo(0,comment_ll.getTop());
                });
                break;
            case R.id.head_icon1111:
            case R.id.head_data:
//                KLog.d("tag","h5 去作者详情页");
//                String link =  StringUtil.getLink("authordetail/" + mNewsDetailBean.getAuthor_id());
//                UIHelper.toWebViewActivity(mContext,link);



                //判断是否关联作者，如果关联，则调到用户界面 author_uid ，没有，调到作者详情页 authoid
                if(mNewsDetailBean != null ){
                    if( mNewsDetailBean.getAuthor_uid().equals("0")){
                        UIHelper.toAuthorDetailActivity(mContext,mNewsDetailBean.getAuthor_id());
                    }else{
                        UIHelper.toUserInfoV2Activity(mContext,mNewsDetailBean.getAuthor_uid());

                    }
                }

                break;
            case R.id.iv_right:
                MobclickAgentUtils.onEvent(UmengEvent.index_detail_upsharebtn_2_0_0);
                StringUtil.showShareDialog(NewsDetailActivity.this,mNewsDetailBean);
                break;
            case R.id.share:
                MobclickAgentUtils.onEvent(UmengEvent.index_detail_downsharebtn_2_0_0);

                if(mNewsDetailBean != null){
                    StringUtil.showShareDialog(NewsDetailActivity.this,mNewsDetailBean);
                }
                break;
            case R.id.test_submit:
                if(isQuestionClick){

                    MobclickAgentUtils.onEvent(UmengEvent.index_detail_test_handinbtn_2_0_0);


                    test_submit.setEnabled(true);
                    checkQuestion();
                }else{
                    ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                    ToastUtils.showShort("请选择正确答案");
                }
                break;
            case R.id.toRating:
                showActiclePointDialog();
                break;
            case R.id.toDown:
                //没下载，走下载逻辑  下载过了，详情页
                String is_dl = mNewsDetailBean.getIs_dl();
                if("1".equals(is_dl)){
                    String aid = mNewsDetailBean.getAid();
                    if(!TextUtils.isEmpty(aid)){
                        showDownOkDialog();
                    }
                }else if("0".equals(is_dl)){
                    showDownDialog();
                }
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.love:
                MobclickAgentUtils.onEvent(UmengEvent.index_detail_collectbtn_2_0_0);

                if(null != mNewsDetailBean){
                    if("1".equals(mNewsDetailBean.getIs_favorite())){
                        unfavorite();
                    }else{
                        favorite();
                    }
                }
                break;
            case R.id.backtop:
                scrollView.smoothScrollTo(0,0);
                break;
            case R.id.focus:
            case R.id.focus11111:
                MobclickAgentUtils.onEvent(UmengEvent.index_detail_author_2_0_0);

                showCancelFocusDialog();
                break;
            default:
        }
    }



    private void showDownOkDialog() {
        if(null != mNewsDetailBean){
            String link;
            if(!TextUtils.isEmpty(mNewsDetailBean.getDl_link_code())){
                link  = mNewsDetailBean.getDl_link() + "\n" + "提取码: " + mNewsDetailBean.getDl_link_code();
            }else{
                link  = mNewsDetailBean.getDl_link();
            }

            String name = link;
            final ThingDownOkAlertDialog downOkAlertDialog = new ThingDownOkAlertDialog(this).builder();
            downOkAlertDialog.setNegativeButton("复制下载链接", v -> {
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", name);
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                ToastUtils.showShort("复制成功");
            }).setMsg(name).setTitle("下载链接").setCanceledOnTouchOutside(false);
            downOkAlertDialog.show();
        }
    }


    //detail 中的字段  或者 评论区的字段 -- 待确认
    private void remarkNum(String commentNum){
        //点评数
        if(!TextUtils.isEmpty(commentNum)){
            int size = Integer.parseInt(commentNum);

            if(size == 0){
                comment.setVisibility(View.VISIBLE);
                rl_msg.setVisibility(View.GONE);
            }else {
                comment_num.setTypeface(typeface);
                if(size > 99){
                    comment_num.setText(99 + "+");
                }else{
                    comment_num.setText(size + "");
                }
                comment.setVisibility(View.GONE);
                rl_msg.setVisibility(View.VISIBLE);
            }

            //总的评论数
            comment_num_all.setText("评论");
        }
    }


    /** --------------------------------- 明细  ---------------------------------*/
    private void detail() {
        Map<String,String> map = new HashMap<>();
        map.put("aid",newsId);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().detail(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<NewsDetailBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<NewsDetailBean> response) {
                        mNewsDetailBean = response.getReturn_data();
                        if(null != mNewsDetailBean){
                            mRelateList = mNewsDetailBean.getRelate();
                            mQueAnswerJsons = mNewsDetailBean.getQue_answer_json();
                            commonLogic();
                        }
                    }
                });
    }


    String catId;
    String summary;
    String dataLink;
    String dl_link_code;
    Typeface typeface;
    private void commonLogic() {

        //新增作者等级 -- 目前后台还没添加type字段
//        if(专栏作者){
//            显示：author_type 1
//        }else (新手作者){
//            显示：author_type 2
//        }


        //作者类型:1-作者（不显示），2-新手作者，3-新锐作者，4-专栏作者',
        if("1".equals(mNewsDetailBean.getAuthor_type())){
            author_type.setVisibility(View.GONE);
        }else if("2".equals(mNewsDetailBean.getAuthor_type())){
            author_type.setVisibility(View.VISIBLE);
            author_type.setImageResource(R.mipmap.hot_author_newuser);
        }else if("3".equals(mNewsDetailBean.getAuthor_type())){
            author_type.setVisibility(View.VISIBLE);
            author_type.setImageResource(R.mipmap.hot_author_new);
        }else if("4".equals(mNewsDetailBean.getAuthor_type())){
            author_type.setVisibility(View.VISIBLE);
            author_type.setImageResource(R.mipmap.hot_author_professor);
        }

       typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");

        remarkNum(mNewsDetailBean.getCommentnum());

        StringUtil.setPublishTime(tv_tag1111,mNewsDetailBean.getPublished_at());

        //TODO 相关资料 之前是标题，现在是链接,有的下载后台没有写标题，，，，
        if(!TextUtils.isEmpty(mNewsDetailBean.getDl_link())){
            data_link_title.setText(mNewsDetailBean.getDl_mat_title());
            data_link_num_feather.setText(mNewsDetailBean.getDl_point());
            data_link_num_down.setText("下载数 " + mNewsDetailBean.getDl_times());
            dataLink = mNewsDetailBean.getDl_link();
            dl_link_code = mNewsDetailBean.getDl_link_code();
            ll_data.setVisibility(View.VISIBLE);
        }

        //文章概述
        summary = mNewsDetailBean.getSummary();
        if(TextUtils.isEmpty(summary)){
            summary = "没数据，测试数据";
        }
        //文章类型 catid = 113 是带有音频的文章
        catId = mNewsDetailBean.getCatid();
        if(!TextUtils.isEmpty(catId) && "113".equals(catId)){
            rl_audio.setVisibility(View.VISIBLE);
            audio_summary.setText(summary);
        }else{
            summary_text.setVisibility(View.VISIBLE);
            summary_text.setText(summary);
        }

        //大图
        if(!TextUtils.isEmpty(mNewsDetailBean.getPic())){
            ImageUtil.load(this,mNewsDetailBean.getPic(),big_pic);
        }


        title.setText(mNewsDetailBean.getTitle());
        tv_title.setText(mNewsDetailBean.getAuthor());
        tv_title1111.setText(mNewsDetailBean.getAuthor());
        //获取文章作者
        ImageUtil.load(this,mNewsDetailBean.getAuthor_avatar(),head_icon1111);
        ImageUtil.load(this,mNewsDetailBean.getAuthor_avatar(),head_icon);

        //是否收藏：1-已收藏，0-未收藏
        if(null != mNewsDetailBean){
            if("1".equals(mNewsDetailBean.getIs_favorite())){
                love.setImageResource(R.mipmap.icon_news_love_2);
            }else{
                love.setImageResource(R.mipmap.icon_news_love_1);
            }
        }


        //是否关注了作者：1-已关注，0-未关注
        changeFocusStatus(mNewsDetailBean.getIs_follow_author());


        if(null != mNewsDetailBean){
            //设置样式
            acticle_point.setTypeface(typeface);
            if(!TextUtils.isEmpty(mNewsDetailBean.getArticle_point()) &&
                !"0".equals(mNewsDetailBean.getArticle_point())){
                acticle_point.setText(Float.parseFloat(mNewsDetailBean.getArticle_point()) + "");
                acticle_point.setVisibility(View.VISIBLE);
            }

            if(null != mNewsDetailBean){
                starBar.setIntegerMark(true);
                starBar.setStarMark(Float.parseFloat(mNewsDetailBean.getArticle_point()));

                //是否评分过
                String is_add_point = mNewsDetailBean.getIs_add_point();
                if("0".equals(is_add_point)){
                    toRating.setVisibility(View.VISIBLE);
                }else if("1".equals(is_add_point)){
                    allready_remark.setVisibility(View.VISIBLE);
                    String my_add_point = mNewsDetailBean.getMy_add_point();
                    if(!TextUtils.isEmpty(my_add_point)){
                        starMyBar.setStarMark(Float.parseFloat(my_add_point));
                    }
                }

                //源头上拦截事件
                starBar.setOnTouchListener((view, motionEvent) -> true);
                starMyBar.setOnTouchListener((view, motionEvent) -> true);
            }
        }


        //webview
        getWebData();
        //扩展阅读
        getMoreReadData();
        //测一测
        getTestData();
        //相关资料
        getRelativeData();
    }

    private void getRelativeData() {
        //设置样式
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        data_link_num_feather.setTypeface(typeface);
        num_feather_text.setTypeface(typeface);

    }

    private void getTestData() {

        question_title.setText(mNewsDetailBean.getQue_title());

        if (null == mQueAnswerJsons || mQueAnswerJsons.isEmpty()) {
            part_test.setVisibility(View.GONE);
        } else {
            TestBean bean1;
            for (int i = 0; i < mQueAnswerJsons.size(); i++) {
                bean1 = new TestBean();
                bean1.setAnswer(mQueAnswerJsons.get(i).getAnswer_title());
                mTestAllList.add(bean1);
            }
            mTestLaunchItemAdapter.setNewData(mTestAllList);
        }

        //判断是否测过测一测 不为空表示已回答过了 ， 已给出正确答案
        myAnswer = mNewsDetailBean.getMy_answer();
        rightAnswer = mNewsDetailBean.getRight_answer();


        if(!TextUtils.isEmpty(myAnswer)){
            //设置不可点击
            for (int i = 0; i < mTestLaunchItemAdapter.getData().size(); i++) {
                mTestLaunchItemAdapter.getData().get(i).setClick(true);
            }

            //设置正确答案外框
            setBound();

            if(!TextUtils.isEmpty(rightAnswer) && !rightAnswer.equals(myAnswer)){
                //设置提交按钮隐藏
                test_submit.setVisibility(View.GONE);
                test_error.setVisibility(View.VISIBLE);
                test_error.setText("很遗憾你答错了，下次请继续努力");
            }else{
                test_submit.setVisibility(View.GONE);
                test_error.setVisibility(View.VISIBLE);
                test_error.setText("恭喜你答对了，羽毛奖励已到账");
            }

        }
    }


    private void setBound(){
        //设置正确答案外框
        int rightPostion = Integer.parseInt(rightAnswer) - 1;
        mTestLaunchItemAdapter.getData().get(rightPostion).setError(true);
        //设置自己选的答案
        int myPosition = Integer.parseInt(myAnswer) - 1;
        mTestLaunchItemAdapter.getData().get(myPosition).setSelect(true);
        mTestLaunchItemAdapter.notifyDataSetChanged();
    }


    private void getWebData() {

        mMyWebView.addJavascriptInterface(new MJavascriptInterface(this), "imagelistener");
        mMyWebView.setDrawingCacheEnabled(false);
        mMyWebView.setLayerType(View.LAYER_TYPE_NONE, null);

        if(null != mNewsDetailBean){
            mMyWebView.loadUrl(mNewsDetailBean.getApp_content_url());
        }

        mMyWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(100 == newProgress){
                    hideWaitingDialog();
                }
            }

        });
        mMyWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //待网页加载完全后设置图片点击的监听方法
                addImageClickListener(view);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                KLog.d("tag","---- " + url);
                Uri uri = Uri.parse(url);
                KLog.e("打印Scheme", uri.getScheme() + "==" + url);
                if (url == null) {
                    return false;
                }
                try{
                    if(url.startsWith("ngbjlink")){
                        int index = "ngbjlink".length();
                        String resultUrl = url.substring(index);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(resultUrl));
                        startActivity(intent);
                        return true;
                    }
                }catch (Exception e){//防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    KLog.e("tag", "ActivityNotFoundException: " + e.getLocalizedMessage());
                    return true;//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
                }
                return false;
            }

        });


        mMyWebView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                mMyWebView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }



    /** --------------------------------- 收藏  ---------------------------------*/
    //收藏对象类型：1-文章
    private String tartget_id = "1";

    private void favorite() {
        Map<String,String> map = new HashMap<>();
        if(null != mNewsDetailBean){
            map.put("target_id",mNewsDetailBean.getAid());
        }
        map.put("target_type",tartget_id);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().acticleFavorite(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40f));
                        ToastUtils.showShort("收藏成功");
                        mNewsDetailBean.setIs_favorite("1");
                        love.setImageResource(R.mipmap.icon_news_love_2);

                        //TODO 10.15 明细界面发送事件，刷新收藏列表
//                        EventBus.getDefault().post(new CollectionEvent());
                    }
                });
    }


    private void unfavorite() {
        Map<String,String> map = new HashMap<>();
        if(null != mNewsDetailBean){
            map.put("target_id",mNewsDetailBean.getAid());
        }
        map.put("target_type",tartget_id);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().acticleUnfavorite(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40f));
                        ToastUtils.showShort("取消收藏");

                        mNewsDetailBean.setIs_favorite("0");
                        love.setImageResource(R.mipmap.icon_news_love_1);
                        //TODO 10.15 明细界面发送事件，刷新收藏列表
//                        EventBus.getDefault().post(new CollectionEvent());
                    }

                });
    }


    /** --------------------------------- 关注 取关  ---------------------------------*/
    //1-去关注，0-取消关注
    private String focus_type;
    private String author_id;

    public void showCancelFocusDialog(){
        String name = "";
        String author;
        if(null != mNewsDetailBean){
            author_id = mNewsDetailBean.getAuthor_id();
            author = mNewsDetailBean.getAuthor();
            if("1".equals(mNewsDetailBean.getIs_follow_author())){
                name = "取消";
                focus_type = "0";
                final FocusAlertDialog iosAlertDialog = new FocusAlertDialog(this).builder();
                iosAlertDialog.setPositiveButton("取消关注", v -> {
                    followAuthor(focus_type);
                }).setNegativeButton("再想想", v -> {}).setMsg("取消关注?").setCanceledOnTouchOutside(false);
                iosAlertDialog.show();
            }else if("0".equals(mNewsDetailBean.getIs_follow_author())){
                MobclickAgentUtils.onEvent(UmengEvent.index_detail_follow_2_0_0);
                focus_type = "1";

                //TODO  判断是否关联作者，如果关联，走关注流程 0未关注
                uid = mNewsDetailBean.getAuthor_uid();
                KLog.d("tag","author_uid " + uid);
                if(uid.equals("0")){
                    followAuthor(focus_type);
                }else{
                    RegisterLoginBean.UserInfo user = StringUtil.getUserInfoBean();
                    if(TextUtils.isEmpty(user.getCompany_name()) &&
                            TextUtils.isEmpty(user.getPosition()) ){
                        showProfessionAuthenNo();
                        return;
                    }

                    //认证过了直接去打招呼界面
                    if("1".equals(user.getAuth_email_status()) || "1".equals(user.getAuth_card_status())){
                        UIHelper.toHelloMakeActivity(this);
                        overridePendingTransition(R.anim.activity_enter_bottom, R.anim.activity_alpha_exit);
                    }else{
                        showProfessionAuthen();
                    }
                }
            }

        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(data != null){
                message = data.getExtras().getString("message");
                KLog.d("tqg","接收到的文字是 " + message);
            }
        }

        followUser();
    }

    //打招呼返回的字段
    private String message = "";
    private String uid;
    private void followUser() {
        Map<String,String> map = new HashMap<>();
        map.put("follow_uid",uid);
        map.put("message",message + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().followUser(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse response) {
                        mNewsDetailBean.setIs_follow_author("1");
                        changeFocusStatus(mNewsDetailBean.getIs_follow_author());
                    }
                });
    }




    private void followAuthor(String focus_type) {
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
                        KLog.e("tag",response.getReturn_code());
                        if("1".equals(mNewsDetailBean.getIs_follow_author())){
                            mNewsDetailBean.setIs_follow_author("0");
                            changeFocusStatus(mNewsDetailBean.getIs_follow_author());
                        }else{
                            ToastUtils.showShort("关注成功，关注作者的文章已加入关注列表");
                            mNewsDetailBean.setIs_follow_author("1");
                            changeFocusStatus(mNewsDetailBean.getIs_follow_author());
                        }
                    }
                });
    }

    private void changeFocusStatus(String is_follow_author) {
        if("1".equals(is_follow_author)){
            focus.setBackgroundResource(R.drawable.bg_corners_4_gray);
            focus.setTextColor(getResources().getColor(R.color.text_second_color));
            focus.setText("已关注");

            focus11111.setBackgroundResource(R.drawable.bg_corners_8_gray);
            focus11111.setTextColor(getResources().getColor(R.color.text_second_color));
            focus11111.setText("已关注");


        }else{
            focus.setBackgroundResource(R.drawable.bg_corners_4_yellow);
            focus.setTextColor(getResources().getColor(R.color.text_first_color));
            focus.setText("关注");

            focus11111.setBackgroundResource(R.drawable.bg_corners_8_yellow);
            focus11111.setTextColor(getResources().getColor(R.color.text_first_color));
            focus11111.setText("关注");
        }
    }


    /** --------------------------------- 更多阅读  ---------------------------------*/

    //后台返回数据 -- 相关文章
    private List<NewsDetailBean.Relate> mRelateList;
    private LinearLayoutManager mLinearLayoutManager;
    private List<MultiNewsBean> mMultiNewsBeanList = new ArrayList<>();
    private FirstItemNewAdapter mFirstItemNewAdapter;


    private void getMoreReadData(){
        if(null != mRelateList && !mRelateList.isEmpty()){
            initMoreReadListLayout();
            MultiNewsBean bean;
            RecommendBean.Article_list newsItemBean;
            //TODO 之前是NewsItemBean,现在是RecommendBean.Article_list
            for (NewsDetailBean.Relate temp : mRelateList) {
                newsItemBean  = new RecommendBean.Article_list();
                newsItemBean.setAid(temp.getAid());
                newsItemBean.setAuthor(temp.getAuthor());
                newsItemBean.setTitle(temp.getTitle());
                newsItemBean.setPic(temp.getPic());
                newsItemBean.setPublished_at(temp.getPublished_at());

                bean = new MultiNewsBean();
                bean.setItemType(1);
                bean.setNewsActicleList(newsItemBean);
                mMultiNewsBeanList.add(bean);
            }

            mFirstItemNewAdapter.setNewData(mMultiNewsBeanList);

            part1111_reading.setVisibility(View.VISIBLE);
        }else{
            part1111_reading.setVisibility(View.GONE);
        }
    }

    //初始化布局管理器
    private void initMoreReadListLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置默认垂直布局
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置布局管理器
        more_read_list.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mFirstItemNewAdapter = new FirstItemNewAdapter(mMultiNewsBeanList);
        more_read_list.setAdapter(mFirstItemNewAdapter);
        //解决数据加载不完
        more_read_list.setNestedScrollingEnabled(false);
        more_read_list.setHasFixedSize(true);

        mFirstItemNewAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","点击的索引 " + position);
            int type = adapter.getItemViewType(position);
            switch (type) {
                case FirstItemNewAdapter.RIGHT_IMG_TYPE:
                    String aid = mMultiNewsBeanList.get(position).getNewsActicleList().getAid();
                    if(!TextUtils.isEmpty(aid)){
                        UIHelper.toNewsDetailActivity(NewsDetailActivity.this,aid);
                    }
                    break;
                default:
            }
        });
    }

    /** --------------------------------- 问答  ---------------------------------*/
    private boolean isQuestionClick ;

    //问答集合
    private List<NewsDetailBean.Que_answer_json> mQueAnswerJsons;

    @BindView(R.id.test_submit)
    TextView test_submit;

    @BindView(R.id.test_error)
    TextView test_error;


    String myAnswer;
    String rightAnswer;


    private void checkQuestion() {
        KLog.d("tag","myAnswer " + myAnswer + " rightAnswer " + rightAnswer);
        answerArticleQa();
    }


    private void answerArticleQa() {

        Map<String,String> map = new HashMap<>();
        map.put("aid",newsId);
        map.put("answer_no",myAnswer);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().answerArticleQa(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<TestOkBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<TestOkBean> response) {
                        test_submit.setEnabled(false);
                        test_submit.setBackgroundResource(R.drawable.bg_corners_8_light_yellow);
                        test_submit.setTextColor(getResources().getColor(R.color.text_second_color));
                        TestOkBean tee = response.getReturn_data();
                        if(null != tee){

                            setBound();
                            test_submit.setVisibility(View.GONE);
                            test_error.setVisibility(View.VISIBLE);
                            if(myAnswer.equals(rightAnswer)){
                                String result = tee.getIs_show_tip() ;
                                KLog.d("tag","结果是： " + result);
                                //1显示 0 不显示羽毛数）
                                if("1".equals(result)){
                                    showQuestionRightDialog();
                                }else {
                                    showQuestionRightDialogNoPoint();
                                }
                                test_error.setText("恭喜你答对了，羽毛奖励已到账");
                            }else{
                                showQuestionErrorDialog();
                                test_error.setText("很遗憾你答错了，下次请继续努力");
                            }
                        }
                    }

                    @Override
                    public void onHintError(String return_code, String errorMes) {
                        super.onHintError(return_code, errorMes);

                    }
                });
    }


    private void showQuestionRightDialog() {
        QuestionResultRightDialog iosAlertDialog = new QuestionResultRightDialog(this).builder();
        iosAlertDialog.setNegativeButton("知道了", v -> {
        });
        iosAlertDialog.setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }


    private void showQuestionErrorDialog() {
        QuestionResultErrorDialog iosAlertDialog = new QuestionResultErrorDialog(this).builder();
        iosAlertDialog.setNegativeButton("查看正确答案", v -> {

            //显示正常答案的边框
            int rightPostion = Integer.parseInt(rightAnswer) - 1;

            //设置不可点击
            for (int i = 0; i < mTestLaunchItemAdapter.getData().size(); i++) {
                mTestLaunchItemAdapter.getData().get(i).setClick(true);
            }
            //设置外框
            mTestLaunchItemAdapter.getData().get(rightPostion).setError(true);

            mTestLaunchItemAdapter.notifyDataSetChanged();

            //设置提交按钮隐藏
            test_submit.setVisibility(View.GONE);
            test_error.setVisibility(View.VISIBLE);



        });
        iosAlertDialog.setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }

    private void showQuestionRightDialogNoPoint() {
        QuestionResultRightDialog iosAlertDialog = new QuestionResultRightDialog(this).builder();
        iosAlertDialog.setFeatherNumShow(false);
        iosAlertDialog.setNegativeButton("知道了", v -> {

        });
        iosAlertDialog.setContent("回答正确-你成功领悟了这篇文章的精髓");
        iosAlertDialog.setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }



    /** --------------------------------- 分享  ---------------------------------*/

    private void showShareDialog() {
        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(this).builder();
        alertDialog.setSharelinkView();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position){
                case 0:
                    //用于验证微信分享成功 显示弹框 判断
                    Constant.isActicleShare = true;

                    ShareBean bean1 = new ShareBean();
                    bean1.setShareType("circle_link");
                    bean1.setImg(mNewsDetailBean.getPic());
                    bean1.setLink(mNewsDetailBean.getShare_url());
                    bean1.setTitle(mNewsDetailBean.getShare_title());
                    bean1.setContent(mNewsDetailBean.getShare_summary());
                    StringUtil.shareWxByWeb(this,bean1);
                    break;
                case 1:
                    //用于验证微信分享成功 显示弹框 判断
                    Constant.isActicleShare = true;

                    KLog.d("tag","朋友 是链接");
                    ShareBean bean = new ShareBean();
                    bean.setShareType("weixin_link");
                    bean.setImg(mNewsDetailBean.getPic());
                    bean.setLink(mNewsDetailBean.getShare_url());
                    bean.setTitle(mNewsDetailBean.getShare_title());
                    bean.setContent(mNewsDetailBean.getShare_summary());
                    StringUtil.shareWxByWeb(this,bean);
                    break;
                case 2:
                    if(null != mNewsDetailBean){
                        KLog.d("tag","复制链接");
                        StringUtil.copyLink(mNewsDetailBean.getShare_url());
                    }

                    break;
                default:
            }
        });
        alertDialog.show();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActicleShareEvent(ActicleShareEvent event){
        Constant.isActicleShare = false;
        ToastUtils.showShort("                           分享成功\n被朋友阅读后，你可以获得羽毛奖励！");
//        View view = LayoutInflater.from(this).inflate(R.layout.activity_share_success,null);
//        ((TextView)view.findViewById(R.id.msg)).setText("分享成功！\n被朋友阅读后，你可以获得羽毛奖励！");
//        ToastUtils.showCustomShort(view);
    }


    /** --------------------------------- 评分  ---------------------------------*/
    private void showActiclePointDialog() {
        final ActiclePointDialog iosAlertDialog = new ActiclePointDialog(this).builder();
        iosAlertDialog.setOnDialogItemClickListener((position, value) -> {
            if (position == 1) {
                KLog.d("tag","评分提交");
                addArticlePoint(value);
            }
        });
        iosAlertDialog.setCanceledOnTouchOutside(true);
        iosAlertDialog.show();
    }

    private ActiclePointBean mActiclePointBean;

    private void addArticlePoint(double value) {

        Map<String,String> map = new HashMap<>();
        map.put("aid",newsId);
        map.put("point",value + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().addArticlePoint(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<ActiclePointBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<ActiclePointBean> response) {
                        mActiclePointBean = response.getReturn_data();
                        if(null != mActiclePointBean){
                            //改变状态
                            mNewsDetailBean.setIs_add_point("1");

                            //手动设值
                            toRating.setVisibility(View.GONE);
                            allready_remark.setVisibility(View.VISIBLE);
                            starMyBar.setStarMark((float) value);

                            int result = mActiclePointBean.getIs_show_tip();
                            //1显示 0 不显示羽毛数）
                            if("1".equals(result)){
                                ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                                ToastUtils.showShort("评分成功，获取5羽毛奖励");
                            }else{
                                ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                                ToastUtils.showShort("评分成功");
                            }
                            //TODO 10.16 获取明细接口，给与平均分
                            change_grade();
                        }
                    }
                });
    }


    private void change_grade() {
        Map<String,String> map = new HashMap<>();
        map.put("aid",newsId);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().detail(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<NewsDetailBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<NewsDetailBean> response) {
                        mNewsDetailBean = response.getReturn_data();
                        if(null != mNewsDetailBean &&
                                !"0".equals(mNewsDetailBean.getArticle_point())){
                            acticle_point.setText(mNewsDetailBean.getArticle_point());
                            acticle_point.setVisibility(View.VISIBLE);
                            starBar.setStarMark(Float.parseFloat(mNewsDetailBean.getArticle_point()));

                        }
                    }
                });
    }


    /** --------------------------------- 下载资料  ---------------------------------*/
    private void showDownDialog() {
        if(null != mNewsDetailBean){
            String point = mNewsDetailBean.getDl_point();
            final ThingDownAlertDialog iosAlertDialog = new ThingDownAlertDialog(this).builder();
            iosAlertDialog.setPositiveButton("确认", v -> {
                //先本地比较，在发送到后台
                compareData();
            }).setNegativeButton("再想想", v -> {
            }).setMsg("确认消耗" + point + "羽毛下载").setCanceledOnTouchOutside(false);
            iosAlertDialog.show();
        }
    }

    private void compareData() {
        RegisterLoginBean.UserInfo userInfo = StringUtil.getUserInfoBean();
        if(null != userInfo){
            String myPoint = userInfo.getPoint();
            String needPoint = mNewsDetailBean.getDl_point();
            KLog.d("tag","myPoint " + myPoint + " needPoint " + needPoint);

            int result = myPoint.compareTo(needPoint);
            if(result < 0){
                //表明我的积分不够
                showDownNotEnoughDialog();
            }else{
                toPayPoint();
            }

//            if(!TextUtils.isEmpty(myPoint) && !TextUtils.isEmpty(needPoint)){
//                int mPoint = Integer.parseInt(myPoint);
//                int nPoint = Integer.parseInt(needPoint);
//                if(mPoint <= nPoint){
//                    //表明我的积分不够
//                    showDownNotEnoughDialog();
//                }else{
//                    toPayPoint();U
//                }
//            }else{
//                toPayPoint();
//            }


        }
    }


    private void showDownNotEnoughDialog() {
        final ThingDownNotEnoughAlertDialog iosAlertDialog = new ThingDownNotEnoughAlertDialog(this).builder();
        iosAlertDialog.setPositiveButton("赚羽毛", v -> {
            UIHelper.toFeatherctivity(this);
        }).setNegativeButton("再想想", v -> {

        }).setMsg("您的羽毛余额不足哦").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }



    private void getUserInfo() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getUserInfo(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<RegisterLoginBean.UserInfo>>() {
                    @Override
                    public void onSuccess(HttpResponse<RegisterLoginBean.UserInfo> response) {
                        RegisterLoginBean.UserInfo mUserInfo = response.getReturn_data();
                        if(null != mUserInfo){
                            StringUtil.setUserInfoBean(mUserInfo);
                        }
                    }

                    @Override
                    public void onHintError(String return_code, String errorMes) {
                        if("2003".equals(return_code) || "1008".equals(return_code)){
                            UIHelper.toLoginActivity(BaseApp.getApplication());
                        }
                    }
                });

    }



    private void toPayPoint() {
        Map<String,String> map = new HashMap<>();
        map.put("aid",newsId);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().dlArticle(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        //成功后台给数据，我渲染界面
                        mNewsDetailBean.setIs_dl("1");
                        showDownOkDialog();

                        getUserInfo();

                    }

                    @Override
                    public void onNetFail(String msg) {
                        //不是code == 200 直接显示
                        showDownNotEnoughDialog();
                    }
                });
    }

    /** --------------------------------- 评论  ---------------------------------*/
    private CommentBean mTempCommentBean;
    private List<CommentBean.FirstComment> mFirstComments = new ArrayList<>();
    private List<CommentBean.FirstComment> allFirstList = new ArrayList<>();
    private void getCommentData(){
        Map<String,String> map = new HashMap<>();
        map.put("aid",newsId);
        map.put("page_no",page + "");
        map.put("page_size",pageSize + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().commentList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<com.qmkj.niaogebiji.module.bean.CommentBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<com.qmkj.niaogebiji.module.bean.CommentBean> response) {
                        mTempCommentBean = response.getReturn_data();
                        if(null != mTempCommentBean){
                            //后台可能返回list为0
                            mFirstComments = mTempCommentBean.getList();
                            if(1 == page){
                                if(!mFirstComments.isEmpty() && mFirstComments.size() > 0){
                                    setData2(mFirstComments);
                                    mCommentAdapter.setNewData(allFirstList);
                                    //如果第一次返回的数据不满10条，则显示无更多数据
                                    if(mFirstComments.size() < Constant.SEERVER_NUM){
                                        mCommentAdapter.loadMoreEnd();
                                    }
                                    ll_empty.setVisibility(View.GONE);


                                    remarkNum(mFirstComments.size() + "");

                                }else{
                                    ll_empty.setVisibility(View.VISIBLE);
                                    iv_empty.setImageResource(R.mipmap.icon_empty_comment);
                                    tv_empty.setText("期待你的精彩评论，让更多人看到");
                                    comment.setImageResource(R.mipmap.icon_empty_comment_seat);
                                    comment.setVisibility(View.VISIBLE);
                                    rl_msg.setVisibility(View.GONE);
                                }

                            }else{
                                //已为加载更多有数据
                                if(mFirstComments != null && mFirstComments.size() > 0){
                                    setData2(mFirstComments);
                                    mCommentAdapter.loadMoreComplete();
                                    mCommentAdapter.addData(tempList);
                                }else{
                                    //已为加载更多无更多数据
                                    mCommentAdapter.loadMoreComplete();
                                    mCommentAdapter.loadMoreEnd();
                                }
                            }
                            }
                        }
                });
    }


    List<CommentBean.FirstComment> tempList = new ArrayList<>();
    private void setData2(List<CommentBean.FirstComment> list) {
        tempList.clear();
        CommentBean.FirstComment bean1 ;
        for (int i = 0; i < list.size(); i++) {
            bean1 = list.get(i);
            tempList.add(bean1);
        }

        if(page == 1){
            allFirstList.addAll(tempList);
        }
    }

    List<MulSecondCommentBean> mMulSecondCommentBeans = new ArrayList<>();
    private void setDataSecond(List<CommentBean.FirstComment> list) {
        mMulSecondCommentBeans.clear();
        MulSecondCommentBean bean;
        for (int i = 0; i < list.size(); i++) {
            bean = new MulSecondCommentBean();
            bean.setActicleComment(list.get(i));
            bean.setItemType(CommentSecondAdapter.ACTICLE);
            mMulSecondCommentBeans.add(bean);
        }

        if(secondPage == 1){
            allCommentList.addAll(mMulSecondCommentBeans);
        }
    }

    /** --------------------------------- 二级弹框 评论  ---------------------------------
     * @param superiorComment*/






    /** --------------------------------- 点赞评论  ---------------------------------*/

    //用于记录在二级评论点赞后，一级界面数据没有刷新
    private int zanPosition;

    private void zanChange(TextView zan_num,ImageView imageView, String good_num, int is_good) {
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
        zan_num.setTypeface(typeface);
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
        //点赞图片
        if("0".equals(is_good + "")){
            imageView.setImageResource(R.mipmap.icon_flash_priase_28v2);
            zan_num.setTextColor(mContext.getResources().getColor(R.color.zan_select_no));
        }else if("1".equals(is_good + "")){
            imageView.setImageResource(R.mipmap.icon_flash_priase_select);
            zan_num.setTextColor(mContext.getResources().getColor(R.color.zan_select));
        }
    }

    private void goodArticle(CommentBean.FirstComment bean) {
        Map<String,String> map = new HashMap<>();
        map.put("type","4");
        map.put("id",bean.getCid());
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().goodArticle(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        //手动修改
                        bean.setIs_good(1);
                        bean.setGood_num((Integer.parseInt(bean.getGood_num()) + 1) + "");
                        zanChange(zan_second_num_second,zan_second_img_second,bean.getGood_num(),bean.getIs_good());
                        //更新第一列表数据
                        mCommentAdapter.getData().get(zanPosition).setGood_num(bean.getGood_num());
                        mCommentAdapter.getData().get(zanPosition).setIs_good(bean.getIs_good());
                        mCommentAdapter.notifyItemChanged(zanPosition);
                    }
                });
    }

    private void cancelGoodArticle(CommentBean.FirstComment bean) {
        Map<String,String> map = new HashMap<>();
        map.put("type","4");
        map.put("id",bean.getCid());

        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().cancelGoodArticle(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) mContext)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        //手动修改
                        bean.setIs_good(0);
                        bean.setGood_num((Integer.parseInt(bean.getGood_num()) - 1) + "");
                        zanChange(zan_second_num_second,zan_second_img_second,bean.getGood_num(),bean.getIs_good());

                        mCommentAdapter.getData().get(zanPosition).setGood_num(bean.getGood_num());
                        mCommentAdapter.getData().get(zanPosition).setIs_good(bean.getIs_good());
                        mCommentAdapter.notifyItemChanged(zanPosition);
                    }
                });
    }




    /** --------------------------------- 测一测  ---------------------------------*/
    int myTestPosition;
    //适配器
    TestLaunchItemAdapter mTestLaunchItemAdapter;
    //组合集合
    List<TestBean> mTestAllList = new ArrayList<>();

    private void initTestLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        test_recycler.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mTestLaunchItemAdapter = new TestLaunchItemAdapter(mTestAllList);
        test_recycler.setAdapter(mTestLaunchItemAdapter);
        //解决数据加载不完
        test_recycler.setNestedScrollingEnabled(true);
        test_recycler.setHasFixedSize(true);
        initTestEvent();
    }

    private void initTestEvent() {
        mTestLaunchItemAdapter.setOnItemClickListener((adapter, view, position) -> {
            myTestPosition = position;
            //我选择的答案
            myAnswer = (position + 1) + "";
            List<TestBean> mDatas = adapter.getData();
            //① 将所有的selected设置false，当前点击的设为true
            for (TestBean data : mDatas) {
                data.setSelect(false);
            }
            TestBean temp = mDatas.get(position);
            temp.setSelect(true);
            mTestLaunchItemAdapter.notifyDataSetChanged();
            isQuestionClick = true;
            test_submit.setBackgroundResource(R.drawable.bg_corners_10_yellow);
            test_submit.setTextColor(getResources().getColor(R.color.text_first_color));
        });
    }



    /** --------------------------------- 文章阅读加积分  ---------------------------------*/

    //倒计时ok
    private boolean isTimeOk;

    private Disposable disposable;
    //请求发送成功ok
    private boolean isSendOk;

    //滑动距离满足ok
    private boolean sendOnce;

    //webview的高度
    private int solid_webview_height ;

    private void viewsAddListener() {

        solid_part.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                solid_title_height = solid_part.getHeight();
//                KLog.d("tag ", "标题的固定高度为 " + solid_title_height);
                solid_part.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        //手指向上移动，scrollY为正
        scrollView.setScrollViewListener(scrollY -> {
            if(scrollY >= solid_title_height){
                part_small_head.setVisibility(View.VISIBLE);
                backtop.setVisibility(View.VISIBLE);
            }else{
                part_small_head.setVisibility(View.GONE);
                backtop.setVisibility(View.GONE);
            }

            if(isTimeOk && scrollY > solid_webview_height * 2/3 + solid_title_height ){
                sendOnce = true;

                if(isTimeOk && sendOnce && !isSendOk){
                    isSendOk = true;
                    if(null != mNewsDetailBean){
                        if("0".equals(mNewsDetailBean.getIs_read())){
                            readArticle();
                        }
                    }
                    return;
                }
            }

        });

    }


    private void initRxTime() {
        //倒计时60秒
        final int count = 60;
        //参数依次为：从0开始，发送次数是4次，0秒延时,每隔1秒发射,主线程中
        disposable = Observable.intervalRange(0,count + 1,0,1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .doOnNext(aLong -> {
                    //接收到消息，这里需要判空，因为3秒倒计时中间如果页面结束了，会造成找不到 tvAdCountDown
//                    KLog.d("tag",(count - aLong));
                }).doOnComplete(() -> {
                    isTimeOk = true;

                    if(isTimeOk && sendOnce && !isSendOk){
                        isSendOk = true;
                        KLog.d("tag","发送请求");
                        if(null != mNewsDetailBean){
                            if("0".equals(mNewsDetailBean.getIs_read())){
                                readArticle();
                            }
                        }
                        return;
                    }
                })
                .subscribe();
    }


    private void readArticle() {
        Map<String,String> map = new HashMap<>();
        map.put("aid",newsId);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().readArticle(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<TestOkBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<TestOkBean> response) {
                        KLog.d("tag","发送成功");

                        TestOkBean temp = response.getReturn_data();
                        if(null != temp){
                            String woooo = temp.getIs_show_tip() ;
                            //1显示 0 不显示）
                            if("1".equals(woooo)){
                                showRewardDialog();
                            }
                        }
                    }

                    @Override
                    public void onHintError(String return_code, String errorMes) {
                        super.onHintError(return_code, errorMes);
                        isSendOk = false;
                        KLog.d("tag","发送失败");
                    }

                    @Override
                    public void onNetFail(String msg) {
                        super.onNetFail(msg);
                        isSendOk = false;
                        KLog.d("tag","发送失败");
                    }
                });
    }




    private void showRewardDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_news_reward,null);
        ToastUtils.setGravity(Gravity.CENTER,0,0);
        ToastUtils.showCustomShort(view);
        ToastUtils.setGravity(Gravity.BOTTOM,0,0);
    }


    /** --------------------------------- webview图片点击放大  ---------------------------------*/

    private void addImageClickListener(WebView webView) {
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].οnclick=function()  " +
                "    {  "
                + "        window.imagelistener.openImage(this.src);  " +//通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
                "    }  " +
                "}" +
                "})()");
    }


    public static class MJavascriptInterface {
        private Context context;

        public MJavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            ArrayList<String> photos = new ArrayList<>();
            photos.add(img);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("imageList", photos);
            bundle.putBoolean("fromNet", true);
            bundle.putInt("index", 0);
            Intent intent = new Intent(context, PicPreviewActivity.class);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

    String blog;
    //文章Id
    String article_id;
    //文字标题
    String article_title = "";
    //文字图片
    String article_image = "";

    //评论成功并发布帖子 article_image 给pic字段
    private void createBlog(){

        Map<String,String> map = new HashMap<>();
        map.put("blog",blog + "");
        map.put("images",  "");
        map.put("link", "");
        map.put("link_title", "");
        map.put("type",0 + "");
        map.put("pid",  "");
        map.put("is_comment","");
        map.put("article_id", article_id + "");
        map.put("article_title", article_title + "");
        map.put("article_image",article_image + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().createBlog(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        //评论成功后发送事件刷新圈子
                        EventBus.getDefault().post(new SendOkCircleEvent());
                    }
                });
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


    /** --------------------------------- 文章评论弹框 ---------------------------------*/

    private String commentString;
    private boolean isSendToCircle;
    //参数一 用于数据更新      参数三 评论一级 还是 评论二级
    private void showTalkDialog(int position,String from,String replyWho) {
        final TalkAlertDialog talkAlertDialog = new TalkAlertDialog(this).builder();
        talkAlertDialog.setIsneedtotrans(true);
        talkAlertDialog.setNum(500);
        //单次保存草稿
        if(!TextUtils.isEmpty(saveContent)){
            talkAlertDialog.setCaoGao(saveContent);
        }
        talkAlertDialog.setMyPosition(position);
        if(!TextUtils.isEmpty(replyWho)){
            talkAlertDialog.setHint(replyWho);
        }
        talkAlertDialog.setTalkLisenter((position1, words) -> {
            commentString = words;
            saveContent = words;
        });

        talkAlertDialog.setOnIsToCircleLister(bug -> {
            MobclickAgentUtils.onEvent(UmengEvent.index_detail_publishcomment_2_0_0);
            isSendToCircle = bug;
            commentBulletinNew(commentString,"",from);
        });
        talkAlertDialog.show();
    }


    /** --------------------------------- 一级评论列表 及 点击事件---------------------------------*/
    //1级 适配器
    CommentActicleAdapter mCommentAdapter;
    //组合集合
    List<CommentBean.FirstComment> mAllList = new ArrayList<>();

    private int page = 1;
    private int pageSize = 10;

    //初始化布局管理器
    private void initCommentListLayout() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        more_comment_list.setLayoutManager(mLinearLayoutManager);
        //设置适配器
        mCommentAdapter = new CommentActicleAdapter(mAllList);
        mCommentAdapter.setToShowActicleDialogListener((item, position) -> {
            isSecondComment = false;
            oneComment = mCommentAdapter.getData().get(position);
            KLog.d("tag","点击此评论的id 为  " + oneComment.getCid());
            //🍅 记录帖子position
            zanPosition  = position;
            getCommentDetail(oneComment.getCid());
        });
        more_comment_list.setAdapter(mCommentAdapter);
        ((SimpleItemAnimator)more_comment_list.getItemAnimator()).setSupportsChangeAnimations(false);
        //解决数据加载不完
        more_comment_list.setNestedScrollingEnabled(true);
        more_comment_list.setHasFixedSize(true);
        initCommentEvent();
    }

    private void initCommentEvent() {

        mCommentAdapter.setOnItemClickListener((adapter, view, position) -> {

            MobclickAgentUtils.onEvent("index_detail_comment_comment"+ (position  + 1) +"_2_0_0");


            oneComment = mCommentAdapter.getData().get(position);
            KLog.d("tag","点击此评论的id 为  " + oneComment.getCid());
            showTalkDialogSecondComment(position,oneComment);
//            showTalkDialogFirstComment(position, oneComment);
        });

        mCommentAdapter.setOnLoadMoreListener(() -> {
            ++page;
            getCommentData();
            KLog.d("tag", "加载更多");
        }, more_comment_list);


        mCommentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.comment_delete:
                    KLog.d("tag", "删除自己的帖子");
                    break;
                case R.id.toFirstComment:
                    isSecondComment = false;
                    oneComment = mCommentAdapter.getData().get(position);
                    KLog.d("tag","点击此评论的id 为  " + oneComment.getCid());
//                    showTalkDialogFirstComment(position, oneComment);
                    showTalkDialogSecondComment(position,oneComment);
                    break;
                case R.id.ll_has_second_comment:
                    isSecondComment = false;
                    oneComment = mCommentAdapter.getData().get(position);
                    KLog.d("tag","点击此评论的id 为  " + oneComment.getCid());
                    //🍅 记录帖子position
                    zanPosition  = position;
                    getCommentDetail(oneComment.getCid());
                    break;

                default:
            }
        });

    }


    private void getCommentDetail(String relatedid) {
        Map<String,String> map = new HashMap<>();
        map.put("cid",relatedid + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getCommentDetail(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<ActicleCommentHeadBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<ActicleCommentHeadBean> response) {
                        oneComment = response.getReturn_data().getData();
                        showSheetDialog(oneComment);
                    }
                });
    }

    /** --------------------------------- 二级评论弹框 ---------------------------------*/
    private void showTalkDialogSecondComment(int position,CommentBean.FirstComment beanNew) {
        final TalkCircleAlertDialog talkAlertDialog = new TalkCircleAlertDialog(this).builder();
        talkAlertDialog.setNum(140);
        talkAlertDialog.setCheckBoxNoShow();
        talkAlertDialog.setMyPosition(position);
        talkAlertDialog.setHint(beanNew.getUsername());
        talkAlertDialog.setTalkLisenter((position1, words) -> {
            KLog.d("tag","接受到的文字是 " + words);
            commentBulletinNew(words,beanNew.getCid(),"");
        });
        talkAlertDialog.show();
    }



    /** 更新评论数据 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshActicleCommentEvent(RefreshActicleCommentEvent event){
        page = 1;
        allFirstList.clear();
        getCommentData();

    }


    /** --------------------------------- 一级评论弹框 ---------------------------------*/
        private void showTalkDialogFirstComment(int position,CommentBean.FirstComment beanNew) {
            final TalkCircleAlertDialog talkAlertDialog = new TalkCircleAlertDialog(this).builder();
            talkAlertDialog.setMyPosition(position);
            talkAlertDialog.setHint(beanNew.getUsername());
            talkAlertDialog.setTalkLisenter((position1, words) -> {
                commentBulletinNew(words,beanNew.getCid(),"");
            });
            talkAlertDialog.show();
        }


    /** --------------------------------- 二级评论列表 及 点击事件---------------------------------*/
    String saveContent = "";
    int secondPage = 1;
    LinearLayout ll_second_empty;
    RelativeLayout totalk;
    ImageView second_close;
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
    TextView hint_text;
    LinearLayout last_reply_ll;
    TextView comment_text_second;
    TextView time_publish_second;
    TextView zan_second_num_second;
    TextView comment_num_second;


    private void showSheetDialog(CommentBean.FirstComment superiorComment) {
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
            KLog.d("tag","评论的是一级评论，同时点击此评论的id 为  " + superiorComment.getCid());
            showTalkDialogSecondComment(-1, superiorComment);
        });

        mCommentSecondAdapter = new CommentSecondAdapter(allSecondComments);
        //TODO !!!!!!!!! 12.28 非常重要
        mCommentSecondAdapter.setSuperiorActicleComment(superiorComment);
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

        initScondEvent(superiorComment);

        secondPage = 1;
        allCommentList.clear();
        getSecondCommentData(superiorComment.getCid());

    }

    private List<CommentBean.FirstComment> mSecondComments;
    private List<MulSecondCommentBean> allCommentList = new ArrayList<>();
    private void getSecondCommentData(String cid) {
        Map<String, String> map = new HashMap<>();
        map.put("topid", cid);
        map.put("page_no", secondPage + "");
        map.put("page_size", pageSize + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().subCommentList(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<CommentBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<CommentBean> response) {
                        CommentBean mTempSecondCommentBean = response.getReturn_data();
                        if (null != mTempSecondCommentBean) {
                            mSecondComments = mTempSecondCommentBean.getList();
                            //集合数据不为空
                            if (null != mSecondComments && !mSecondComments.isEmpty()) {
                                if (1 == secondPage) {
                                    setDataSecond(mSecondComments);
                                    mCommentSecondAdapter.setNewData(allCommentList);
                                    //如果第一次返回的数据不满10条，则显示无更多数据
                                    if (mSecondComments.size() < Constant.SEERVER_NUM) {
                                        mCommentSecondAdapter.loadMoreEnd();
                                    }
                                } else {
                                    //已为加载更多有数据
                                    if (mSecondComments != null && mSecondComments.size() > 0) {
                                        setDataSecond(mSecondComments);
                                        mCommentSecondAdapter.loadMoreComplete();
                                        mCommentSecondAdapter.addData(mMulSecondCommentBeans);
                                    } else {
                                        //已为加载更多无更多数据
                                        mCommentSecondAdapter.loadMoreEnd();
                                    }
                                }
                            }
                        }
                    }

                });
    }

    private void initScondEvent(CommentBean.FirstComment superiorComment) {
        mCommentSecondAdapter.setOnLoadMoreListener(() -> {
            ++secondPage;
            KLog.d("tag","加载更多");
            getSecondCommentData(superiorComment.getCid());
        },mSecondRV);



        mCommentSecondAdapter.setOnReduceListener(() -> setSecondReply(superiorComment,-1));

        mCommentSecondAdapter.setOnItemClickListener((adapter, view, position) -> {
            //此处逻辑和点击一级评论item一样
            isSecondComment = true;
            KLog.d("tag","点击此评论的id 为  " + oneComment.getCid());
            showTalkDialogSecondComment(position, oneComment);
        });


        //设值
        hint_text.setHint("回复 " + superiorComment.getUsername());


//        if("1".equals(superiorComment.getAuth_status())){
//            nickname_second.setText(superiorComment.getUsername() + (TextUtils.isEmpty(superiorComment.getCompany_name())?"":superiorComment.getCompany_name()) + " "+
//                    (TextUtils.isEmpty(superiorComment.getPosition())?"":superiorComment.getPosition()));
//        }else{
//            nickname_second.setText(superiorComment.getUsername() + " TA还未职业认证");
//        }

        //TODO 2020.1.7 有一个则显示
        if(!StringUtil.checkNull((superiorComment.getCompany_name()))
                    && !StringUtil.checkNull((superiorComment.getPosition()))){
                nickname_second.setText(superiorComment.getUsername() + " TA 还未职业认证");
            }else{
                nickname_second.setText(superiorComment.getUsername() +  " " + (StringUtil.checkNull((superiorComment.getCompany_name()))?superiorComment.getCompany_name()+" ":"") +
                        (TextUtils.isEmpty(superiorComment.getPosition())?"":superiorComment.getPosition()));
            }


        //是否认证
        if("1".equals(superiorComment.getAuth_status())){
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_authen_company);
            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            nickname_second.setCompoundDrawables(null,null,drawable,null);
        }else{
            nickname_second.setCompoundDrawables(null,null,null,null);
        }


        comment_num_second.setText("评论详情");
        ImageUtil.loadByDefaultHead(this,superiorComment.getAvatar(),head_second_icon);
        //发布时间
        if(StringUtil.checkNull(superiorComment.getDateline())){
            String s =  GetTimeAgoUtil.getTimeAgoByApp(Long.parseLong(superiorComment.getDateline()) * 1000L);
            time_publish_second.setText(s);
        }
        comment_text_second.setText(superiorComment.getMessage());
        ImageUtil.loadByDefaultHead(this,superiorComment.getAvatar(),head_second_icon);
        zanChange(zan_second_num_second,zan_second_img_second,superiorComment.getGood_num(),superiorComment.getIs_good());

        //赞赞赞
        comment_priase.setOnClickListener((view22)->{
            if("0".equals(superiorComment.getIs_good() + "")){
                goodArticle(superiorComment);
            }else if("1".equals(superiorComment.getIs_good() + "")){
                cancelGoodArticle(superiorComment);
            }
        });

        //评论区头像跳转
        head_second_icon.setOnClickListener(v -> {
            UIHelper.toUserInfoActivity(NewsDetailActivity.this,superiorComment.getUid());
        });


        mCommentSecondAdapter.setOnLoadMoreListener(() -> {
            ++secondPage;
            KLog.d("tag","加载更多");
            getSecondCommentData(superiorComment.getCid());
        },mSecondRV);


        mCommentSecondAdapter.setOnItemClickListener((adapter, view1, position) -> {
            //此处逻辑和点击一级评论item一样
            isSecondComment = true;
            secondComment = mCommentSecondAdapter.getData().get(position).getActicleComment();
            KLog.d("tag","点击此评论的id 为  " + this.secondComment.getCid() + " 被回复的人事 " + this.secondComment.getUsername());
            showTalkDialogSecondComment(position,secondComment);

        });

        mCommentSecondAdapter.setOnItemChildClickListener((adapter, view13, position) -> {
            isSecondComment = true;
            secondComment = mCommentSecondAdapter.getData().get(position).getActicleComment();
            KLog.d("tag","点击此评论的id 为  " + this.secondComment.getUid() + " 被回复的人是 " + this.secondComment.getUsername());
            showTalkDialogSecondComment(position, this.secondComment);
        });

    }

    private void setSecondReply(CommentBean.FirstComment bean, int value){
        //如果没有数据了，显示空布局
        if(bean.getCommentslist().size() == 0){
            last_reply_ll.setVisibility(View.GONE);
            ll_second_empty.setVisibility(View.VISIBLE);
        }
    }


    //同一个接口，构造参数
    //target_id一直是文章的id  reply_id 为空则是文章评论  不为空为一级评论id
    private void commentBulletinNew(String content,String talkCid,String from) {
        String target_id = newsId;
        Map<String,String> map = new HashMap<>();
        map.put("target_id",target_id);
        map.put("content",content);
        map.put("reply_id",talkCid);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().createComment(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<CommentOkBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<CommentOkBean> response) {
                        KLog.d(response.getReturn_code());

                        if(!isSecondComment){
                            if("aimToActicle".equals(from)){
                                ToastUtils.showShort("                       评论成功\n审核通过后展示，优质评论+10羽毛");
                            }else{
                                ToastUtils.showShort("评论成功");
                            }

                            //直接刷新
                            EventBus.getDefault().post(new RefreshActicleCommentEvent());

                            //清空草稿
                            saveContent = "";

                        }else{
                            secondPage = 1;
                            allCommentList.clear();
                            getSecondCommentData(oneComment.getCid());
                        }

                        //TODO 如果评论转发到圈子，则发送请求
                        if("aimToActicle".equals(from) && isSendToCircle){
                            article_id  = mNewsDetailBean.getAid();
                            article_title = mNewsDetailBean.getTitle();
                            article_image = mNewsDetailBean.getPic();
                            blog = content;
                            createBlog();
                        }
                    }
                });
    }


}
