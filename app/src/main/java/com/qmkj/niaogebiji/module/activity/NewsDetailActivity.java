package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.ActiclePointDialog;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.dialog.QuestionResultErrorDialog;
import com.qmkj.niaogebiji.common.dialog.QuestionResultRightDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.dialog.TalkAlertDialog;
import com.qmkj.niaogebiji.common.dialog.ThingDownAlertDialog;
import com.qmkj.niaogebiji.common.dialog.ThingDownNotEnoughAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.service.MediaService;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CommentAdapter;
import com.qmkj.niaogebiji.module.adapter.CommentSecondAdapter;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.adapter.TestLaunchItemAdapter;
import com.qmkj.niaogebiji.module.bean.ActiclePointBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.TestBean;
import com.qmkj.niaogebiji.module.bean.TestOkBean;
import com.qmkj.niaogebiji.module.event.AudioEvent;
import com.qmkj.niaogebiji.module.event.AudioEvent1;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.qmkj.niaogebiji.module.widget.ObservableScrollView;
import com.qmkj.niaogebiji.module.widget.StarBar;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.udesk.photoselect.PreviewActivity;
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

    @BindView(R.id.num_feather)
    TextView num_feather;

    @BindView(R.id.num_feather_text)
    TextView num_feather_text;

    @BindView(R.id.comment_ll)
    LinearLayout comment_ll;

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
        newsId = getIntent().getStringExtra("newsId");
        initTestLayout();
        initCommentListLayout();
        getCommentData();
        detail();

        viewsAddListener();

        initEvent();

        initRxTime();

        mMyWebView.setActivity(h -> solid_webview_height = h);

    }


    @Override
    protected boolean regEvent() {
        return true;
    }


    private void initEvent() {


    }



    @OnClick({R.id.backtop,R.id.focus11111,R.id.focus,R.id.love,R.id.iv_back,R.id.toDown,R.id.toRating,
            R.id.test_submit,
            R.id.iv_right,R.id.share,
            R.id.head_icon1111,R.id.head_data,
            R.id.comment,
            R.id.toLlTalk,
            R.id.rl_audio,
    })
    public void clicks(View view){
        Drawable drawable = getResources().getDrawable(R.mipmap.icon_answer_select);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        switch (view.getId()){
            case R.id.rl_audio:
                KLog.d("tag","打开音频");
                EventBus.getDefault().post(new AudioEvent(MediaService.musicPath[1]));

                break;
            case R.id.toLlTalk:
                showTalkDialog(-1,"111","first");
                break;
            case R.id.comment:
                scrollView.post(() -> {
                    //移动到位置
                    scrollView.smoothScrollTo(0,comment_ll.getTop());
                });
                break;
            case R.id.head_icon1111:
            case R.id.head_data:
                KLog.d("tag","h5 去作者详情页");
                break;
            case R.id.iv_right:
            case R.id.share:
                showShareDialog();
                break;
            case R.id.test_submit:
                if(isQuestionClick){
                    test_submit.setEnabled(true);
                    checkQuestion();
                }else{
                    ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                    ToastUtils.showShort("请选择正确答案");
                }
                break;
            case R.id.toRating:
//                if(null != mNewsDetailBean){
//                    if("0".equals(mNewsDetailBean.getIs_add_point())){
//                        showActiclePointDialog();
//                    }else{
//                        return;
//                    }
//                }
                showActiclePointDialog();
                break;
            case R.id.toDown:
                //没下载，走下载逻辑  下载过了，详情页
                if(true){
                    UIHelper.toDataInfoActivity(this);
                }else{
                    showDownDialog();
                }

                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.love:
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
                showCancelFocusDialog();
                break;
            default:
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


    private void commonLogic() {



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
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
            acticle_point.setTypeface(typeface);
            acticle_point.setText(mNewsDetailBean.getArticle_point());

            if(null != mNewsDetailBean){
                starBar.setIntegerMark(true);
                starBar.setStarMark(Float.parseFloat(mNewsDetailBean.getArticle_point()));

                starMyBar.setStarMark(4.0f);

                //源头上拦截事件
                starBar.setOnTouchListener((view, motionEvent) -> true);
                starMyBar.setOnTouchListener((view, motionEvent) -> true);
            }
        }


        //webview
        getWebData();
        //扩展阅读
        getMoreReadData();
        //评论
        getCommentData();
        //测一测
        getTestData();
        //相关资料
        getRelativeData();
    }

    private void getRelativeData() {
        //设置样式
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Bold.otf");
        num_feather.setTypeface(typeface);
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

        myAnswer = "1";

        if(!TextUtils.isEmpty(myAnswer)){
            //设置不可点击
            for (int i = 0; i < mTestLaunchItemAdapter.getData().size(); i++) {
                mTestLaunchItemAdapter.getData().get(i).setClick(true);
            }

            if(!rightAnswer.equals(myAnswer)){
                //设置正确答案外框
                int rightPostion = Integer.parseInt(rightAnswer) - 1;
                mTestLaunchItemAdapter.getData().get(rightPostion).setError(true);
            }

            //设置自己选的答案
            int myPosition = Integer.parseInt(myAnswer) - 1;
            mTestLaunchItemAdapter.getData().get(myPosition).setSelect(true);

            //设置提交按钮隐藏
            test_submit.setVisibility(View.GONE);
        }
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
//                    hideWaitingDialog();
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
            }else if("0".equals(mNewsDetailBean.getIs_follow_author())){
                name = "";
                focus_type = "1";
            }
            final FocusAlertDialog iosAlertDialog = new FocusAlertDialog(this).builder();
            iosAlertDialog.setPositiveButton("确定", v -> {
                followAuthor(focus_type);
            }).setNegativeButton("取消", v -> {}).setMsg("确定要 " + name +"关注「" + author  +"」").setCanceledOnTouchOutside(false);
            iosAlertDialog.show();
        }
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
            NewsItemBean newsItemBean;
            if(null != mRelateList && !mRelateList.isEmpty()){
                for (NewsDetailBean.Relate temp : mRelateList) {
                    newsItemBean  = new NewsItemBean();
                    newsItemBean.setAid(temp.getAid());
                    newsItemBean.setAuthor(temp.getAuthor());
                    newsItemBean.setTitle(temp.getTitle());
                    newsItemBean.setPic(temp.getPic());
                    newsItemBean.setPublished_at(temp.getPublished_at());

                    bean = new MultiNewsBean();
                    bean.setItemType(1);
                    bean.setNewsItemBean(newsItemBean);
                    mMultiNewsBeanList.add(bean);
                }
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
                    String aid = mMultiNewsBeanList.get(position).getNewsItemBean().getAid();
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
    private String answer_no;

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

        showQuestionErrorDialog();


        Map<String,String> map = new HashMap<>();
        if(null != mNewsDetailBean){
            map.put("aid",mNewsDetailBean.getAid());
        }
        map.put("answer_no",answer_no);
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
                            if(myAnswer.equals(rightAnswer)){
                                String result = tee.getIs_show_tip() ;
                                KLog.d("tag","结果是： " + result);
                                //1显示 0 不显示羽毛数）
                                if("1".equals(result)){
                                    showQuestionRightDialog();
                                }else {
                                    showQuestionRightDialogNoPoint();
                                }
                            }else{
                                showQuestionErrorDialog();
                            }
                        }
                    }

                    @Override
                    public void onHintError(String errorMes) {
                        super.onHintError(errorMes);

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
                    KLog.d("tag","朋友圈 是张图片");
                    shareWxCircleByWeb();
                    break;
                case 1:
                    KLog.d("tag","朋友 是链接");
                    shareWxByWeb();
                    break;
                case 2:
                    if(null != mNewsDetailBean){
                        KLog.d("tag","复制链接");
                        //获取剪贴板管理器：
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", mNewsDetailBean.getShare_url());
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                        ToastUtils.showShort("链接复制成功！");
                    }


                    break;
                default:
            }
        });
        alertDialog.show();
    }



    // 分享微信（web）
    public void shareWxCircleByWeb() {
        if (this == null){
            return;
        }
        if(null != mNewsDetailBean){
            NewsDetailBean shareBean = mNewsDetailBean;
            String sharepic = shareBean.getPic();
            String shareurl = shareBean.getShare_url();
            String title = shareBean.getShare_title();
            String summary = shareBean.getShare_summary();
            SHARE_MEDIA platform;
            platform = SHARE_MEDIA.WEIXIN_CIRCLE;
            UMImage thumb;
            if (TextUtils.isEmpty(sharepic)) {
                thumb = new UMImage(this, R.mipmap.icon_fenxiang);
            } else {
                thumb = new UMImage(this, sharepic);
            }
            UMWeb web = new UMWeb(shareurl);
            //标题
            web.setTitle(title);
            //缩略图
            web.setThumb(thumb);
            //描述
            web.setDescription(summary);
            //传入平台
            new ShareAction(this)
                    .setPlatform(platform)
                    .withMedia(web)
                    .share();
        }
    }

    //分享微信（web) 链接
    private void shareWxByWeb() {
        if(null == this){
            return;
        }
        if(null != mNewsDetailBean){
            NewsDetailBean shareBean = mNewsDetailBean;
            String sharepic = shareBean.getPic();
            String shareurl = shareBean.getShare_url();
            String title = shareBean.getShare_title();
            String summary = shareBean.getShare_summary();
            SHARE_MEDIA platform;
            platform = SHARE_MEDIA.WEIXIN;
            UMImage thumb;
            if (TextUtils.isEmpty(sharepic)) {
                thumb = new UMImage(this, R.mipmap.icon_fenxiang);
            } else {
                thumb = new UMImage(this, sharepic);
            }
            UMWeb web = new UMWeb(shareurl);
            //标题
            web.setTitle(title);
            //缩略图
            web.setThumb(thumb);
            //描述
            web.setDescription(summary);
            //传入平台
            new ShareAction(this)
                    .setPlatform(platform)
                    .withMedia(web)
                    .share();
        }

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
        if(null != mNewsDetailBean){
            map.put("aid",mNewsDetailBean.getAid());
        }
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
                        if(null != mNewsDetailBean){
                            acticle_point.setText(mNewsDetailBean.getArticle_point());
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
            myPoint = "40";
            String needPoint = mNewsDetailBean.getPointnum();

            int result = myPoint.compareTo(needPoint);
            if(result < 0){
                //表明我的积分不够
                showDownNotEnoughDialog();
            }else{
                toPayPoint();
            }
        }
    }


    private void showDownNotEnoughDialog() {
        final ThingDownNotEnoughAlertDialog iosAlertDialog = new ThingDownNotEnoughAlertDialog(this).builder();
        iosAlertDialog.setPositiveButton("赚羽毛", v -> {

        }).setNegativeButton("再想想", v -> {

        }).setMsg("您的羽毛余额不足哦").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
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
                    }
                });
    }

    /** --------------------------------- 评论  ---------------------------------*/

    //1级 适配器
    CommentAdapter mCommentAdapter;
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
        mCommentAdapter = new CommentAdapter(mAllList);
        more_comment_list.setAdapter(mCommentAdapter);
        //解决数据加载不完
        more_comment_list.setNestedScrollingEnabled(true);
        more_comment_list.setHasFixedSize(true);
        initCommentEvent();
    }

    private void initCommentEvent() {
        mCommentAdapter.setOnItemClickListener((adapter, view, position) -> {
            KLog.d("tag","点击一级评论去二级评论");
            showSheetDialog();
            getCommentData();
        });

        mCommentAdapter.setOnLoadMoreListener(() -> {
            ++page;
            KLog.d("tag","加载更多");
        },more_comment_list);

        setEmpty(mCommentAdapter);


        mCommentAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.comment_delete:
                    KLog.d("tag","删除自己的帖子");
                    break;
                case R.id.toSecondComment:
                case R.id.ll_has_second_comment:
                    KLog.d("tag","点击一级评论去二级评论");
                    showSheetDialog();
                    getCommentData();
                    break;
                case R.id.comment_priase:
                    KLog.d("tag","帖子点赞");
                    break;

                default:
            }
        });
    }

    //后台整体 Bean
    private CommentBean mTempCommentBean;

    private void getCommentData(){
//        Map<String,String> map = new HashMap<>();
//        if(null != mNewsDetailBean){
//            map.put("aid",mNewsDetailBean.getAid());
//        }
//        map.put("page_no",page + "");
//        map.put("page_size",pageSize + "");
//        String result = RetrofitHelper.commonParam(map);
//        RetrofitHelper.getApiService().commentList(result)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
//                .subscribe(new BaseObserver<HttpResponse<com.qmkj.niaogebiji.module.bean.CommentBean>>() {
//                    @Override
//                    public void onSuccess(HttpResponse<com.qmkj.niaogebiji.module.bean.CommentBean> response) {
//                        mTempCommentBean = response.getReturn_data();
//                        if(null != mTempCommentBean){
//
//                        }
//                    }
//
//                });

        //模拟评论数据
        CommentBean.FirstComment  bean1 ;
        List<CommentBean.SecondComment> secondCommentList = new ArrayList<>();
        List<CommentBean.SecondComment> secondCommentList2 = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            bean1 = new CommentBean.FirstComment();
            //模拟一级评论下有一条评论
            if(i == 0 ){
                CommentBean.SecondComment secondComment = new CommentBean.SecondComment();
                secondCommentList.add(secondComment);
                bean1.setCommentslist(secondCommentList);
            }else if(i == 1){
                //模拟一级评论下有来两条评论
                CommentBean.SecondComment secondComment = new CommentBean.SecondComment();
                secondCommentList2.add(secondComment);
                CommentBean.SecondComment secondComment2 = new CommentBean.SecondComment();
                secondCommentList2.add(secondComment2);
                bean1.setCommentslist(secondCommentList2);
            }else{

            }

            bean1.setMessage("10月31日，格力电器公告拟修订公司章程，其中，经营范围新增了「研发、制造、销售新能源发电产品、储能系统及充电桩」的内容。");
            mAllList.add(bean1);
        }

        mCommentAdapter.setNewData(mAllList);
    }



    /** --------------------------------- 二级弹框 评论  ---------------------------------*/
    int secondPage = 1;
    RelativeLayout totalk;
    RecyclerView mSecondRV;
    BottomSheetDialog bottomSheetDialog;
    CommentSecondAdapter bottomSheetAdapter;
    BottomSheetBehavior mDialogBehavior;

    List<MulSecondCommentBean> list = new ArrayList<>();

    private void getSecondCommentData() {

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



    //参数一 用于数据更新      参数三 评论一级 还是 评论二级
    private void showTalkDialog(int position,String talkCid,String from) {
        final TalkAlertDialog talkAlertDialog = new TalkAlertDialog(this).builder();
        talkAlertDialog.setMyPosition(position);
        talkAlertDialog.setTalkLisenter((position1, words) -> {
            KLog.d("tag","接受到的文字是 " + words);
            commentBulletin(position,words,talkCid,from);
        });
        talkAlertDialog.show();
    }


    //追加一条数据，在之前的集合上
    private void commentBulletin(int position,String content,String talkCid,String from) {
        if("first".equals(from)){
            //本地测试
            CommentBean.FirstComment firstComment = new CommentBean.FirstComment();
            firstComment.setMessage(content);
            mAllList.add(firstComment);
            mCommentAdapter.notifyItemChanged(position);
        }else if("second".equals(from)){
            MulSecondCommentBean bean;
            bean = new MulSecondCommentBean();

            CommentBean.FirstComment  bean1 = new CommentBean.FirstComment();
            //TODO 这里是相当于二级评论中的一级评论
            bean1.setMessage(content);
            bean.setFirstComment(bean1);

            bean.setItemType(1);
            list.add(bean);
            bottomSheetAdapter.notifyItemChanged(position);
        }
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
                KLog.d("tag ", "标题的固定高度为 " + solid_title_height);
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
        if(null != mNewsDetailBean){
            map.put("aid",mNewsDetailBean.getAid());
        }

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
                    public void onHintError(String errorMes) {
                        super.onHintError(errorMes);
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



}
