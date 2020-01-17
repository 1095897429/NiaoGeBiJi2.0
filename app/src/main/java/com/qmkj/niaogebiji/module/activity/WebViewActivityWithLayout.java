package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.qmkj.niaogebiji.common.dialog.TalkAlertDialog;
import com.qmkj.niaogebiji.common.dialog.TalkCircleAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.GetTimeAgoUtil;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.CommentSecondAdapter;
import com.qmkj.niaogebiji.module.bean.ActicleCommentHeadBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentCircleBean;
import com.qmkj.niaogebiji.module.bean.CommentOkBean;
import com.qmkj.niaogebiji.module.bean.MessageAllH5Bean;
import com.qmkj.niaogebiji.module.bean.MessageUserBean;
import com.qmkj.niaogebiji.module.bean.MessageVipBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.ProBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.bean.VipBean;
import com.qmkj.niaogebiji.module.event.RefreshActicleCommentEvent;
import com.qmkj.niaogebiji.module.widget.AndroidBug5497Workaround;
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
 *
 * 如果是deeplink -- 走父类的shouldOverrideUrlLoading方法
 * 1.重新构造 -- 返回上一层级
 */
public class WebViewActivityWithLayout extends WebViewAllActivity {

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



    @Override
    public void initData() {
        AndroidBug5497Workaround.assistActivity(this);
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



    @OnClick({R.id.iv_back,
            R.id.tv_right,
            R.id.tv_zengsong,
            R.id.tv_canccle
    })
    @Override
    public void clicks(View view){
        switch (view.getId()){
            case R.id.tv_canccle:
                finish();
                break;
            case R.id.tv_zengsong:
                String link = StringUtil.getLink("vipshare");
                UIHelper.toWebViewActivityWithOnLayout(this,link,"vipshare");
                break;
            case R.id.tv_right:

                MobclickAgentUtils.onEvent(UmengEvent.quanzi_recommendlist_laud10_2_0_0);

                readInformation();
                break;
            case R.id.iv_back:
                if(webview != null && webview.canGoBack()){
                    webview.goBack();
                    return;
                }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }







}
