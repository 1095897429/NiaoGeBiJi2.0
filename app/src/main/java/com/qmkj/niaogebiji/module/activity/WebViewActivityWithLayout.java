package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.dialog.TalkCircleAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CommentSecondAdapter;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentBeanNew;
import com.qmkj.niaogebiji.module.bean.MessageAllH5Bean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.ProBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.bean.VipBean;
import com.qmkj.niaogebiji.module.event.RefreshCircleDetailCommentEvent;
import com.qmkj.niaogebiji.module.event.ShowRedPointEvent;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.MyWebChromeClientJieTu;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

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
 * 创建时间 2019-11-20
 * 描述:在布局中添加了webview
 *
 * 1.消息界面
 * 2.徽章界面 编辑徽章
 * 3.个人信息认证
 * 4.vip
 *
 */
public class WebViewActivityWithLayout extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.tv_canccle)
    TextView tv_cancle;


    @BindView(R.id.tv_done)
    TextView tv_done;

    @BindView(R.id.tv_zengsong)
    TextView tv_zengsong;



    @BindView(R.id.ll_webview)
    LinearLayout mLayout;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.webview)
    WebView webview;

    @BindView(R.id.tv_right)
    TextView tv_right;

    @BindView(R.id.all_part)
    RelativeLayout allpart;


    @BindView(R.id.iv_back)
    ImageView iv_back;


    private String link;

    private String fromWhere;

    private MyWebChromeClientJieTu mMyWebChromeClient;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview_1;
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initView() {
        link = getIntent().getStringExtra("link");
        fromWhere = getIntent().getStringExtra("fromWhere");
        KLog.d("tag","link " + link);

        if(fromWhere.equals("显示一键已读消息")){
            tv_right.setVisibility(View.VISIBLE);
        }else if(fromWhere.equals("mybadge")){
            allpart.setBackgroundColor(getResources().getColor(R.color.badge_color));
            iv_back.setImageResource(R.mipmap.icon_back_white);
            tv_title.setVisibility(View.GONE);
        }else if(fromWhere.equals("webview_badges")) {
            allpart.setBackgroundColor(Color.parseColor("#3D3D3F"));
            iv_back.setVisibility(View.GONE);
            tv_title.setText("编辑展示徽章");
            tv_done.setVisibility(View.VISIBLE);
            tv_cancle.setVisibility(View.VISIBLE);
        }else if(fromWhere.equals("vipmember")){
            getUserInfo();
            getVipStatus();
        }


        if(TextUtils.isEmpty(link)){
            return;
        }

        initSetting();


        mMyWebChromeClient = new MyWebChromeClientJieTu(this,mProgressBar,tv_title);


        //js交互 -- 给js调用app的方法，xnNative是协调的对象
        webview.addJavascriptInterface(new AndroidtoJs(), "ngbjNative");

        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                KLog.d("tag","---- " + url);
                return false;
            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // 接受所有网站的证书
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        webview.setWebChromeClient(mMyWebChromeClient);
        webview.loadUrl(link);
    }

    private void getVipStatus() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getVipStatus(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<VipBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<VipBean> response) {
                        VipBean  temp = response.getReturn_data();
                        if(temp.isIs_show()){
                            tv_zengsong.setVisibility(View.VISIBLE);
                        }else{
                            tv_zengsong.setVisibility(View.GONE);
                        }
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁WebView
        if(null != webview){
            webview.removeAllViews();
            webview = null;
        }
    }


    @SuppressLint("JavascriptInterface")
    public class AndroidtoJs extends Object {
        //通用方法
        @JavascriptInterface
        public void sendMessage(String param) {
            KLog.d("tag","param " + param);
            if(!TextUtils.isEmpty(param)){
                try {
                    JSONObject b= new JSONObject(param);
                    String result = b.optString("type");
                    //消息中心
                    if("toMessage".equals(result)){
                        MessageAllH5Bean javaBean = JSON.parseObject(param, MessageAllH5Bean.class);
                        MessageAllH5Bean.MessageH5Bean bean = javaBean.getParams();
                        String toType = bean.getType();
                       if("1".equals(toType)){
                           UIHelper.toMsgDetailActivity(WebViewActivityWithLayout.this,bean);
                        }else if("14".equals(toType) || "15".equals(toType) || "16".equals(toType) || "17".equals(toType)){
                           //文章二级评论
                           UIHelper.toNewsDetailActivity(WebViewActivityWithLayout.this,bean.getRelatedid());
                       }else if("24".equals(toType) || "25".equals(toType) || "26".equals(toType) || "27".equals(toType)){
                           //圈子二级评论
                           mTempId = bean.getRelatedid();
                           runOnUiThread(() ->  showSheetDialog(bean.getRelatedid()));
                       }else if("21".equals(toType) || "22".equals(toType) || "23".equals(toType)){
                           //圈子详情 - ok
                           UIHelper.toCommentDetailActivity(WebViewActivityWithLayout.this,bean.getRelatedid());
                       }else if("31".equals(toType) || "32".equals(toType)){
                           //用户信息 - ok
                           UIHelper.toUserInfoActivity(WebViewActivityWithLayout.this,bean.getAuthorid());
                       }
                    }else if("toEditBadge".equals(result)){
                        // - ok
                        String link = StringUtil.getLink("editbadge");
                        UIHelper.toWebViewActivityWithOnLayout(WebViewActivityWithLayout.this,link,"webview_badges");
                    }else if("toTestList".equals(result)){
                        // - ok
                        Constant.isReLoad = true;
                        UIHelper.toTestListActivity(WebViewActivityWithLayout.this);
                    }else if("toConfirmOk".equals(result)){
                        //职业认证成功页面 - ok
                        finish();
                    }else if("finish".equals(result)){
                        finish();
                    }else if("toHome".equals(result)){
                        //去文章首页干货 -- ok
                        Constant.isReLoad = true;
                        UIHelper.toHomeActivity(WebViewActivityWithLayout.this,0);
                    }else if("toKnow".equals(result)){
                        //去更懂你 -- ok
                        Constant.isReLoad = true;
                        runOnUiThread(() -> getProfession());
                    }else if("shareVip".equals(result)){
                        //VIP 个人分享
                        RegisterLoginBean.UserInfo userInfo = StringUtil.getUserInfoBean();
                        showShareVipDialog(userInfo);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /** --------------------------------- 二级评论列表 及 点击事件---------------------------------*/
    int secondPage = 1;
    View view;
    LinearLayout ll_second_empty;
    RelativeLayout totalk;
    ImageView second_close;
    RecyclerView mSecondRV;
    ImageView head_second_icon;
    LinearLayout comment_priase;
    ImageView zan_second_img_second;
    ImageView icon;
    //临时变量
    CommentBeanNew mCommentBeanNew;
    View headView;
    BottomSheetDialog bottomSheetDialog;
    CommentSecondAdapter bottomSheetAdapter;
    BottomSheetBehavior mDialogBehavior;

    String mTempId;

    List<MulSecondCommentBean> allSecondComments = new ArrayList<>();
    TextView nickname_second;
    TextView comment_text_second;
    TextView time_publish_second;
    TextView zan_second_num_second;
    TextView comment_num_second;

    //显示二级评论视图
    private void showSheetDialog(String blog_comment_id) {
        view = View.inflate(this, R.layout.dialog_bottom_comment, null);
        mSecondRV = view.findViewById(R.id.recycler);
        ll_second_empty = view.findViewById(R.id.ll_second_empty);
        icon = view.findViewById(R.id.icon);
        comment_num_second = view.findViewById(R.id.comment_num_second);
        second_close = view.findViewById(R.id.second_close);
        second_close.setOnClickListener(view12 -> bottomSheetDialog.dismiss());
        totalk = view.findViewById(R.id.totalk);
        totalk.setOnClickListener(view1 -> {
            showTalkDialogSecondComment(-1, mCommentBeanNew);
        });
        //清空一下数据
        allSecondComments.clear();
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
            KLog.d("tag","点击此评论的id 为  " + bottomSheetAdapter.getData().get(position).getCircleComment().getId());
            showTalkDialogSecondComment(position, bottomSheetAdapter.getData().get(position).getCircleComment());

        });

        //右边的小图片
        bottomSheetAdapter.setOnItemChildClickListener((adapter, view13, position) -> {
            KLog.d("tag","点击此评论的id 为  " + bottomSheetAdapter.getData().get(position).getCircleComment().getId());
            showTalkDialogSecondComment(position, bottomSheetAdapter.getData().get(position).getCircleComment());
        });
        //减少逻辑
        bottomSheetAdapter.setOnReduceListener(() -> {
            //实体bean - 1
            mCommentBeanNew.setComment_num((Integer.parseInt(mCommentBeanNew.getComment_num()) - 1) +"");
            comment_num_second.setText(mCommentBeanNew.getComment_num() +"条回复");

        });

        bottomSheetAdapter.setOnLoadMoreListener(() -> {
            ++secondPage;
            getSecondCommentComment(blog_comment_id);
            KLog.d("tag","加载更多");
        },mSecondRV);


        blogCommentDetail(blog_comment_id);

    }


    /** --------------------------------- 二级评论弹框 ---------------------------------*/
    private void showTalkDialogSecondComment(int position,CommentBeanNew beanNew) {
        final TalkCircleAlertDialog talkAlertDialog = new TalkCircleAlertDialog(this).builder();
        talkAlertDialog.setMyPosition(position);
        talkAlertDialog.setHint(beanNew.getUser_info().getName());
        talkAlertDialog.setTalkLisenter((position1, words) -> {
            createCommentComment(beanNew,words);
        });
        talkAlertDialog.show();
    }



    private void createCommentComment(CommentBeanNew temp,String commentString) {
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

                        //实体bean + 1
                        mCommentBeanNew.setComment_num((Integer.parseInt(temp.getComment_num()) + 1) +"");
                        comment_num_second.setText(mCommentBeanNew.getComment_num() +"条回复");

                        secondPage = 1;
                        allSecondComments.clear();
                        getSecondCommentComment(mTempId);

                    }
                });
    }



    //动态评论id
    private void blogCommentDetail(String blog_id) {
        Map<String,String> map = new HashMap<>();
        map.put("blog_comment_id",blog_id + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().blogCommentDetail(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<CommentBeanNew>>() {
                    @Override
                    public void onSuccess(HttpResponse<CommentBeanNew> response) {
                        mCommentBeanNew = response.getReturn_data();
                        setSecondHeadData(mCommentBeanNew);
                        allSecondComments.clear();
                        getSecondCommentComment(blog_id);
                    }
                });

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



    private void setSecondHeadData(CommentBeanNew temp) {
        //底部使用者头像
        ImageUtil.loadByDefaultHead(mContext,StringUtil.getUserInfoBean().getAvatar(),icon);
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
                    }
                });
    }




    @Override
    protected void onResume() {
        super.onResume();

        if(Constant.isReLoad){
            webview.reload();
            getUserInfo();
            Constant.isReLoad = false;
        }
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
                });

    }


    private void showShareVipDialog(RegisterLoginBean.UserInfo item) {
        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(mContext).builder();
        alertDialog.setSharelinkView().setTitleGone();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            String name1 ;

            switch (position) {
                case 0:
                    RegisterLoginBean.UserInfo mUserInfo = StringUtil.getUserInfoBean();
                    ShareBean bean1 = new ShareBean();
                    bean1.setShareType("circle_link");
                    bean1.setLink(mUserInfo.getInvite_url());
                    bean1.setResId(R.mipmap.icon_fenxiang);

                    if(!TextUtils.isEmpty(mUserInfo.getName())){
                        name1 = mUserInfo.getName();
                    }else{
                        name1 = mUserInfo.getNickname();
                    }
                    bean1.setTitle("你的好友" + name1 + "分享给你VIP资格，价值99元！");
                    bean1.setContent("23:59分内可领取，过时作废！8大权益免费领取，还不快来");
                    StringUtil.shareWxByWeb((Activity) mContext,bean1);
                    break;
                case 1:
                    KLog.d("tag","朋友 是链接");
                    RegisterLoginBean.UserInfo mUserInfo2 = StringUtil.getUserInfoBean();

                    ShareBean bean = new ShareBean();
                    bean.setResId(R.mipmap.icon_fenxiang);
                    bean.setShareType("weixin_link");
                    bean.setLink(mUserInfo2.getInvite_url());

                    if(!TextUtils.isEmpty(mUserInfo2.getName())){
                        name1 = mUserInfo2.getName();
                    }else{
                        name1 = mUserInfo2.getNickname();
                    }
                    bean.setTitle("你的好友" + name1 + "分享给你VIP资格，价值99元！");
                    bean.setContent("23:59分内可领取，过时作废！8大权益免费领取，还不快来");
                    StringUtil.shareWxByWeb((Activity) mContext,bean);
                    break;
                case 2:
                    RegisterLoginBean.UserInfo mUserInf= StringUtil.getUserInfoBean();
                    StringUtil.copyLink(mUserInf.getInvite_url());
                    break;
                default:
            }
        });
        alertDialog.show();
    }


    @OnClick({R.id.iv_back,
            R.id.tv_right,
            R.id.tv_done,
            R.id.tv_zengsong,
            R.id.tv_canccle
    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.tv_zengsong:
                String link = StringUtil.getLink("vipshare");
                UIHelper.toWebViewActivityWithOnLayout(this,link,"vipshare");
                break;
            case R.id.tv_done:

                break;
            case R.id.tv_right:
                readInformation();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }

    // 1-单个已读，2-所有已读
    private String mesId = "";
    private String mesType = "2";
    private void readInformation() {
        Map<String,String> map = new HashMap<>();
        map.put("type",mesType);
        map.put("id",mesId);
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().readInformation(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse>() {
                    @Override
                    public void onSuccess(HttpResponse response) {
                        //请求成功 -- 如何操作
                        //1-有新消息，0-无新消息
                        webview.reload();
                    }
                });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mMyWebChromeClient != null) {
            mMyWebChromeClient.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }



    private ArrayList<ProBean> temp1;
    private void getProfession() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getProfession(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<List<ProBean>>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(HttpResponse<List<ProBean>> response) {
                        temp1 = (ArrayList<ProBean>) response.getReturn_data();
                        if(temp1 != null && !temp1.isEmpty()){
                            UIHelper.toMoreKnowYouActivity(WebViewActivityWithLayout.this,temp1);
                        }
                    }
                });
    }



    private void initSetting() {
        WebSettings webSettings = webview.getSettings();
        if(null == webSettings){
            return;
        }
        //开启 js交互功能
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MyWebView.setWebContentsDebuggingEnabled(true);
        }
        //不因手机修改字体变化
        webSettings.setTextZoom(100);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);

        //开启DomStorage缓存
        webSettings.setDomStorageEnabled(true);
        //开启 H5缓存 功能
        webSettings.setAppCacheEnabled(true);
        webSettings.setGeolocationEnabled(true);
        //兼容所有的手机界面,使网页始终按照webview宽度设定(如果设置为true,此项功能为失效,导致部分手机网页如淘宝显示为PC样式,但能完整显示PC网页)
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        //加快内容加载速度
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //阻止图片网络加载
        webSettings.setBlockNetworkImage(false);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //势焦点
        webview.requestFocusFromTouch();
        //视频播放需要
        webSettings.setPluginState(WebSettings.PluginState.ON);

        //在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webview.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }








}
