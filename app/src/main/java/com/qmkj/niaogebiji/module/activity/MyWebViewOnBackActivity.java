package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
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
            mMyWebChromeClientByCamera = new MyWebChromeClientByCamera(this, tv_title);
        }


        webview.setWebChromeClient(mMyWebChromeClientByCamera);


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
