package com.qmkj.niaogebiji.module.activity;

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
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.FocusAlertDialog;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.bean.IndexFocusBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.qmkj.niaogebiji.module.widget.ObservableScrollView;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
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



    @OnClick({R.id.backtop,R.id.focus11111,R.id.focus,R.id.love})
    public void clicks(View view){
        switch (view.getId()){
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


}
