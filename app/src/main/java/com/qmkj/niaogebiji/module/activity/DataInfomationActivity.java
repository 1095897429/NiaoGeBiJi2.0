package com.qmkj.niaogebiji.module.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.ThingDownAlertDialog;
import com.qmkj.niaogebiji.common.dialog.ThingDownNotEnoughAlertDialog;
import com.qmkj.niaogebiji.common.dialog.ThingDownOkAlertDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:资料详情
 */
public class DataInfomationActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.num_feather)
    TextView num_feather;

    @BindView(R.id.bottom_no_buy_part)
    LinearLayout bottom_no_buy_part;

    @BindView(R.id.bottom_releady_buy_part)
    LinearLayout bottom_releady_buy_part;

    @BindView(R.id.webview)
    MyWebView mMyWebView;

    @BindView(R.id.loading_dialog)
    LinearLayout loading_dialog;

    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottieAnimationView;


    //文章的id
    private String newsId;

    //文章详情bean
    private NewsDetailBean mNewsDetailBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_data_info;
    }

    @Override
    protected void initView() {

        showDownNotEnoughDialog();

        //获取新闻id
        newsId = getIntent().getStringExtra("newsId");
        newsId = "23654";

        showWaitingDialog();
        detail();
    }

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
        if(mNewsDetailBean.getTitle().length() > 15){
            tv_title.setText(mNewsDetailBean.getTitle().substring(0,15) + "...");
        }else{
            mNewsDetailBean.getTitle();
        }

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DIN-Medium.otf");
        num_feather.setTypeface(typeface);
        if(!TextUtils.isEmpty(mNewsDetailBean.getDl_point())){
            num_feather.setText(mNewsDetailBean.getDl_point());
        }

        //是否下载过：1-下载过，0-未下载
        if("0".equals(mNewsDetailBean.getIs_dl())){
            bottom_no_buy_part.setVisibility(View.VISIBLE);
            bottom_releady_buy_part.setVisibility(View.GONE);
        }else{
            bottom_no_buy_part.setVisibility(View.GONE);
            bottom_releady_buy_part.setVisibility(View.VISIBLE);
        }

        if(null != mNewsDetailBean){
            getWebData();
        }
    }


    private void getWebData() {
        mMyWebView.setDrawingCacheEnabled(false);
        mMyWebView.setLayerType(View.LAYER_TYPE_NONE, null);
        mMyWebView.loadUrl(mNewsDetailBean.getApp_content_url());
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
            }
        });
    }



    @OnClick({R.id.down_res,R.id.iv_back,R.id.bottom_releady_buy_part})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.down_res:
                showDownDialog();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.bottom_releady_buy_part:
                showDownOkDialog();
                break;
            default:
        }
    }


    private void showDownDialog() {
        if(null !=mNewsDetailBean){
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
                        bottom_no_buy_part.setVisibility(View.GONE);
                        bottom_releady_buy_part.setVisibility(View.VISIBLE);
                        showDownOkDialog();
                    }
                });
    }


    private void showDownOkDialog() {
        if(null != mNewsDetailBean){
            String link = mNewsDetailBean.getDl_link();
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


    private void showDownNotEnoughDialog() {
        final ThingDownNotEnoughAlertDialog iosAlertDialog = new ThingDownNotEnoughAlertDialog(this).builder();
        iosAlertDialog.setPositiveButton("赚羽毛", v -> {

        }).setNegativeButton("再想想", v -> {

        }).setMsg("您的羽毛余额不足哦").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }



    /** --------------------------------- 进度动画  ---------------------------------*/

    public void showWaitingDialog() {
        loading_dialog.setVisibility(View.VISIBLE);
        lottieAnimationView.setImageAssetsFolder("images");
        lottieAnimationView.setAnimation("images/loading.json");
        lottieAnimationView.loop(true);
        //硬件加速，解决lottie卡顿问题
        lottieAnimationView.useHardwareAcceleration(true);
        lottieAnimationView.playAnimation();
    }

    public void hideWaitingDialog() {
        if(null != loading_dialog ){
            loading_dialog.setVisibility(View.GONE);
        }

        if(null != lottieAnimationView){
            lottieAnimationView.cancelAnimation();
        }
    }

}
