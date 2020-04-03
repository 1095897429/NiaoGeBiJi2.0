package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.js.JSInterface;
import com.qmkj.niaogebiji.module.widget.MyWebChromeClientByCamera;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-18
 * 描述:
 * 0.获取link
 * 1.有返回上一层的操作
 */
public class MyWebViewOnBackActivity extends BaseActivity {

    @BindView(R.id.webview)
    WebView webview;

    @BindView(R.id.tv_title)
    TextView tv_title;


    @BindView(R.id.iv_back)
    ImageView iv_back;


    private String link;

    private String mTitle;

    //拍照 + 图片
    private MyWebChromeClientByCamera mMyWebChromeClientByCamera;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview_1;
    }

    @Override
    protected void initView() {

    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void initFirstData() {
        mTitle = getIntent().getStringExtra("title");
        link = getIntent().getStringExtra("link");
        KLog.d("tag","link " + link);



        initSetting(webview);

        if(!TextUtils.isEmpty(mTitle)){
            tv_title.setText(mTitle);
            mMyWebChromeClientByCamera = new MyWebChromeClientByCamera(this, null);
        }else{
            //加了webview可图片上传功能
//            mMyWebChromeClientByCamera = new MyWebChromeClientByCamera(this, tv_title);

            //4.3 带有进度条
            mMyWebChromeClientByCamera = new MyWebChromeClientByCamera(this,progressBar,tv_title);

        }


        webview.setWebChromeClient(mMyWebChromeClientByCamera);


        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                KLog.d("tag","---- " + url);
                if (url == null) {
                    return false;
                }
                try{
                    if(!url.startsWith("http://") && !url.startsWith("https://")){
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                }catch (Exception e){//防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    KLog.e("tag", "ActivityNotFoundException: " + e.getLocalizedMessage());
                    return true;//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
                }

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


        //js交互
        webview.addJavascriptInterface(new JSInterface(this,webview), "ngbjNative");

        //加载link
        webview.loadUrl(link);
    }

    @Override
    public void initData() {
        iv_back.setOnClickListener(v -> {
            if(webview != null && webview.canGoBack()){
                webview.goBack();// 返回前一个页面
            }else{
                finish();
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




    //用于webview图片选择权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    //用于webview图片选择的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mMyWebChromeClientByCamera != null) {
            mMyWebChromeClientByCamera.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
