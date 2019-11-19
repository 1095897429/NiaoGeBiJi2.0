package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.dialog.QuestionResultErrorDialog;
import com.qmkj.niaogebiji.common.dialog.QuestionResultRightDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.FirstItemNewAdapter;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.MultiNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.NewsItemBean;
import com.qmkj.niaogebiji.module.bean.TestOkBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.qmkj.niaogebiji.module.widget.ObservableScrollView;
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

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
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

    @BindView(R.id.answer_part3)
    LinearLayout answer_part3;

    @BindView(R.id.answer_part4)
    LinearLayout answer_part4;

    @BindView(R.id.num_feather)
    TextView num_feather;

    @BindView(R.id.num_feather_text)
    TextView num_feather_text;

    @BindView(R.id.comment_ll)
    LinearLayout comment_ll;



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
        detail();
        initEvent();
    }

    private void initEvent() {

        solid_part.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                solid_title_height = solid_part.getHeight();
                KLog.d("tag ", "标题 + 作者 固定高度为 " + solid_title_height);
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
        });
    }



    @OnClick({R.id.backtop,R.id.focus11111,R.id.focus,R.id.love,R.id.iv_back,R.id.toDown,R.id.toRating,
            R.id.a_rb,R.id.b_rb,R.id.c_rb,R.id.d_rb,R.id.test_submit,
            R.id.iv_right,R.id.share,
            R.id.head_icon1111,R.id.head_data,
            R.id.comment
    })
    public void clicks(View view){
        Drawable drawable = getResources().getDrawable(R.mipmap.icon_answer_select);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        switch (view.getId()){
            case R.id.comment:
                scrollView.post(() -> {
                    //移动到位置
                    scrollView.smoothScrollTo(0,comment_ll.getTop());
                });
                break;
            case R.id.head_icon1111:
            case R.id.head_data:
                KLog.d("去作者详情页");
                break;
            case R.id.iv_right:
            case R.id.share:
                showShareDialog();
                break;
            case R.id.a_rb:
                answer_no = (String) a_rb.getTag();
                myAnswer = "1";
                hideImageStatus();
                a_rb.setCompoundDrawables(null,null,drawable,null);
                break;
            case R.id.b_rb:
                answer_no = (String) b_rb.getTag();
                myAnswer = "2";
                hideImageStatus();
                b_rb.setCompoundDrawables(null,null,drawable,null);
                break;
            case R.id.c_rb:
                answer_no = (String) c_rb.getTag();
                myAnswer = "3";
                hideImageStatus();
                c_rb.setCompoundDrawables(null,null,drawable,null);
                break;
            case R.id.d_rb:
                answer_no = (String) d_rb.getTag();
                myAnswer = "4";
                hideImageStatus();
                d_rb.setCompoundDrawables(null,null,drawable,null);
                break;
            case R.id.test_submit:
                if(isQuestionClick){
                    //设置不可点击
                    a_rb.setClickable(false);
                    b_rb.setClickable(false);
                    c_rb.setClickable(false);
                    d_rb.setClickable(false);
                    test_submit.setEnabled(true);
                    checkQuestion();
                }else{
                    ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                    ToastUtils.showShort("请选择正确答案");
                    test_submit.setEnabled(false);
                }
                break;
            case R.id.toRating:

                break;
            case R.id.toDown:
                UIHelper.toDataInfoActivity(this);
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

        getWebData();

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
        }


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
            if (null != mQueAnswerJsons && !mQueAnswerJsons.isEmpty() && mQueAnswerJsons.size() == 2) {
                a_rb.setText("A. " + mQueAnswerJsons.get(0).getAnswer_title());
                a_rb.setTag(mQueAnswerJsons.get(0).getAnswer_id() + "");
                b_rb.setText("B. " + mQueAnswerJsons.get(1).getAnswer_title());
                b_rb.setTag(mQueAnswerJsons.get(1).getAnswer_id() + "");
            }
            //设置答案选项
            if (null != mQueAnswerJsons && !mQueAnswerJsons.isEmpty() && mQueAnswerJsons.size() == 3) {
                a_rb.setText("A. " + mQueAnswerJsons.get(0).getAnswer_title());
                a_rb.setTag(mQueAnswerJsons.get(0).getAnswer_id() + "");
                b_rb.setText("B. " + mQueAnswerJsons.get(1).getAnswer_title());
                b_rb.setTag(mQueAnswerJsons.get(1).getAnswer_id() + "");
                c_rb.setText("C. " + mQueAnswerJsons.get(2).getAnswer_title());
                c_rb.setTag(mQueAnswerJsons.get(2).getAnswer_id() + "");
                answer_part3.setVisibility(View.VISIBLE);
            }

            if (null != mQueAnswerJsons && !mQueAnswerJsons.isEmpty() && mQueAnswerJsons.size() == 4) {
                a_rb.setText("A. " + mQueAnswerJsons.get(0).getAnswer_title());
                a_rb.setTag(mQueAnswerJsons.get(0).getAnswer_id() + "");
                b_rb.setText("B. " + mQueAnswerJsons.get(1).getAnswer_title());
                b_rb.setTag(mQueAnswerJsons.get(1).getAnswer_id() + "");
                c_rb.setText("C. " + mQueAnswerJsons.get(2).getAnswer_title());
                c_rb.setTag(mQueAnswerJsons.get(2).getAnswer_id() + "");
                d_rb.setText("D. " + mQueAnswerJsons.get(3).getAnswer_title());
                d_rb.setTag(mQueAnswerJsons.get(3).getAnswer_id() + "");
                answer_part3.setVisibility(View.VISIBLE);
                answer_part4.setVisibility(View.VISIBLE);
            }
        }
    }


    private void getWebData() {
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
            }
        });

        mMyWebView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
//                solid_webview_height = mMyWebView.getHeight();
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


    /** --------------------------------- 评论  ---------------------------------*/

    private int page = 1;
    private int pageSize = 10;
    //后台整体 Bean
    private CommentBean mTempCommentBean;

    private void getCommentData(){
        Map<String,String> map = new HashMap<>();
        if(null != mNewsDetailBean){
            map.put("aid",mNewsDetailBean.getAid());
        }
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

                        }
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

    @BindView(R.id.a_rb)
    RadioButton a_rb;

    @BindView(R.id.b_rb)
    RadioButton b_rb;

    @BindView(R.id.c_rb)
    RadioButton c_rb;

    @BindView(R.id.d_rb)
    RadioButton d_rb;

    @BindView(R.id.right_1)
    ImageView right_1;

    @BindView(R.id.right_2)
    ImageView right_2;


    @BindView(R.id.right_3)
    ImageView right_3;

    @BindView(R.id.right_4)
    ImageView right_4;

    String myAnswer;
    String rightAnswer;


    /** 隐藏状态 */
    @SuppressLint("ResourceAsColor")
    private void hideImageStatus(){
        Drawable drawable = getResources().getDrawable(R.mipmap.icon_answer_default);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        a_rb.setCompoundDrawables(null,null,drawable,null);
        b_rb.setCompoundDrawables(null,null,drawable,null);
        c_rb.setCompoundDrawables(null,null,drawable,null);
        d_rb.setCompoundDrawables(null,null,drawable,null);
        isQuestionClick = true;
        test_submit.setBackgroundResource(R.drawable.bg_corners_10_yellow);
        test_submit.setTextColor(getResources().getColor(R.color.text_first_color));
    }

    private void checkQuestion() {
        KLog.d("tag","myAnswer " + myAnswer + " rightAnswer " + rightAnswer);
        answerArticleQa();
    }


    private void answerArticleQa() {
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
            if(!TextUtils.isEmpty(myAnswer)) {
                if ("1".equals(rightAnswer)) {
                    right_1.setVisibility(View.VISIBLE);
                } else if ("2".equals(rightAnswer)) {
                    right_2.setVisibility(View.VISIBLE);
                } else if ("3".equals(rightAnswer)) {
                    right_3.setVisibility(View.VISIBLE);
                } else if ("4".equals(rightAnswer)) {
                    right_4.setVisibility(View.VISIBLE);
                }
            }
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





}
