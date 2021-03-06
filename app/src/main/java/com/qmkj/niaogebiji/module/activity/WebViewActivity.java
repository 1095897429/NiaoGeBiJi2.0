package com.qmkj.niaogebiji.module.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.ProBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.event.ProfessionEvent;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
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
 * 描述: 动态方式加载布局
 * 1.vip界面
 * 2.
 */
public class WebViewActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.ll_webview)
    LinearLayout mLayout;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    MyWebView mMyWebView;


    private String link;
    private String mTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initView() {
        link = getIntent().getStringExtra("link");
        mTitle = getIntent().getStringExtra("title");
//        KLog.d("tag","link " +link);
        if(TextUtils.isEmpty(link)){
            return;
        }

        //创建WebView
        mMyWebView = new MyWebView(getApplicationContext());

        //js交互 -- 给js调用app的方法，xnNative是协调的对象
        mMyWebView.addJavascriptInterface(new AndroidtoJs(), "ngbjNative");

        mMyWebView.setWebViewClient(new WebViewClient(){
            //给的链接是 https://pan.baidu.com/s/1CARAgwkjH7JzM61LunaQTQ
            //  url-scheme deeplink-- bdnetdisk://n/action.SHARE_LINK?m_n_v=8.3.0&surl=CARAgwkjH7JzM61LunaQTQ&origin=2
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                KLog.d("tag","---- " + url);
                Uri uri = Uri.parse(url);
                KLog.e("打印Scheme", uri.getScheme() + "==" + url);
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
                    //自行处理
                    if(!TextUtils.isEmpty(url) && url.startsWith("bdnetdisk")){
                        ToastUtils.showShort("您未安装相应的百度网盘app");
                    }
                    return true;//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
                }

                //处理http和https开头的url
//                view.loadUrl(url);
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
                String title = view.getTitle();
                KLog.d("tag","title: " + title);
                //js交互
//                toGiveToken();
            }
        });

        mMyWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(100 == newProgress || 1 >= newProgress){
                    mProgressBar.setVisibility(View.GONE);
                }else{
                    if (mProgressBar.getVisibility() == View.GONE) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
            }


            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if(!TextUtils.isEmpty(mTitle)){
                    tv_title.setText(mTitle);
                }else{
                    tv_title.setText(title);
                }
            }


        });

        mLayout.addView(mMyWebView);

        mMyWebView.loadUrl(link);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁WebView
        if(null != mMyWebView){
            mMyWebView.removeAllViews();
            mMyWebView.onDestroy();
            mMyWebView = null;
        }
    }


    @SuppressLint("JavascriptInterface")
    public class AndroidtoJs extends Object {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @JavascriptInterface
        public void getToken() {
            KLog.d("heihei", "JS调用了Android的我的方法告诉我 它需要token,重新发起初始化界面，给与token");
            // 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
            if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                String value = "传递字符串";
                String result = "javascript:" + "xyApplication.getAppToken(\"" + value + "\")";
                mMyWebView.loadUrl(result);
            } else {
                String value = "传递字符串";
                String result = "javascript:" + "xyApplication.getAppToken(\"" + value + "\")";
                KLog.d("tag",result);
                mMyWebView.evaluateJavascript(result, null);
            }
        }


        @JavascriptInterface
        public void copyText(String text) {
            // 从API11开始android推荐使用android.content.ClipboardManager
            // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
            ClipboardManager cm = (ClipboardManager) WebViewActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            cm.setText(text);
            Toast.makeText(WebViewActivity.this, text + "复制成功", Toast.LENGTH_SHORT).show();
        }

        //跳转文章详情
        @JavascriptInterface
        public void toActicleDetail(String articleId) {
            if(!TextUtils.isEmpty(articleId)){
                try {
                    JSONObject b= new JSONObject(articleId);
                    String result = b.optString("id");
                    UIHelper.toNewsDetailActivity(WebViewActivity.this,result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            KLog.d("tag","articleId " + articleId);
        }


        //通用方法
        @JavascriptInterface
        public void sendMessage(String param) {
            KLog.d("tag","param " + param);
            if(!TextUtils.isEmpty(param)){
                try {
                    JSONObject b= new JSONObject(param);
                    String result = b.optString("type");
                    JSONObject object  = b.getJSONObject("params");
                    String id = object.optString("id");
                    //去文章详情
                    if("toArticleDetail".equals(result)){
                        UIHelper.toNewsDetailActivity(WebViewActivity.this,id);
                    }else if("toKnow".equals(result)){
                        KLog.d("tag","线程名称 " + Thread.currentThread().getName() + "");
                        //去更懂你 -- ok
                        runOnUiThread(() -> getProfession());
                    }else if("toConfirmOk".equals(result)){
                        // id 1 职业认证成功(回主界面不刷新)  id 2  审核认证成功(回主界面刷新)

                        EventBus.getDefault().post(new ProfessionEvent("职业认证 or 审核认证",id));
                    }else if("toTestList".equals(result)){
                        //徽章 尚未获得，立即前往获取
//                        UIHelper.toHomeActivity(WebViewActivity.this,0);
                    }else if("toEditBadge".equals(result)){
                        //去编辑徽章页面
//                        UIHelper.toHomeActivity(WebViewActivity.this,0);
                    }else if("followAuthor".equals(result)){
                        //更新列表 + 首页
                    }else if("toCommentDetail".equals(result)){
                        //我的动态 评论 - 去详情
                        String comment = object.optString("comment");
                        String created_at= object.optString("created_at");
                        String good_num= object.optString("good_num");
                        String type= object.optString("type");
                        String relatedid= object.optString("relatedid");
                        String post_title= object.optString("post_title");
                        if(!TextUtils.isEmpty(type)){
                            if("1".equals(type)){
                                //去文章详情页
                                UIHelper.toNewsDetailActivity(WebViewActivity.this,relatedid);
                            }else if("2".equals(type)){
                                UIHelper.toCommentDetailActivity(WebViewActivity.this,relatedid);
                                //圈子一级评论
                            }else if("3".equals(type)){
                                //圈子二级评论
                                UIHelper.toCommentDetailActivity(WebViewActivity.this,relatedid);
                            }
                        }
                    }else if("toActivityDetail".equals(result)){
                        //发布 去圈子明细
                        UIHelper.toCommentDetailActivity(WebViewActivity.this,id);

                    }else if("shareActivity".equals(result)){
                        ArrayList<String> ins = new ArrayList<>();
                        //发布 弹出分享框，这里加上自己的uid
                        String uid = object.optString("uid");
                        String blog = object.optString("blog");
                        JSONArray images= object.getJSONArray("images");
                        int size = images.length();
                        if(size > 0){
                            ins.add(images.optString(0));
                        }
                        String link= object.optString("link");
                        String link_title= object.optString("link_title");
                        String type= object.optString("type");
                        String pid= object.optString("pid");
                        String like_num = object.optString("like_num");
                        String show_num= object.optString("show_num");
                        String sort= object.optString("sort");
                        String created_at= object.optString("created_at");
                        String article_id= object.optString("article_id");
                        String article_title= object.optString("article_title");
                        String article_image= object.optString("article_image");
                        String comment_num= object.optString("comment_num");
                        String share_url= object.optString("share_url");
                        int is_like = object.optInt("is_like");
                        CircleBean item = new CircleBean();
                        item.setId(id);
                        item.setImages(ins);
                        item.setLink(link);
                        item.setLink_title(link_title);
                        item.setType(type);
                        item.setArticle_id(article_id);
                        item.setArticle_image(article_image);
                        item.setArticle_title(article_title);
                        item.setIs_like(is_like);
                        item.setComment(blog);
                        item.setBlog(blog);
                        item.setShare_url(share_url);

                        showShareDialog(item);


                    }else if("tolink".equals(result)){
                        //TODO 圈子的链接 -- 还未测试
                        String link =  object.optString("link");
                        UIHelper.toWebViewActivityWithOnStep(WebViewActivity.this,link);

                    }else if("toUserDetail".equals(result)){
                        //关注列表去跳转
                        String uid =  object.optString("uid");
                        UIHelper.toUserInfoActivity(WebViewActivity.this,uid);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

    }


    @OnClick({R.id.iv_back})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }


    private String token;
    public void toGiveToken() {

        RegisterLoginBean.UserInfo userInfo = StringUtil.getUserInfoBean();
        if(null != userInfo){
            token = userInfo.getAccess_token();
        }

        if (!TextUtils.isEmpty(token)) {
            mMyWebView.post(() -> {
                //小于4.4
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    String result = "javascript:" + "localStorage.setItem('accessToken',\"" + token + "\")";
                    KLog.d("tag",result + "");
                    if (mMyWebView != null){
                        //传递参数
                        mMyWebView.loadUrl(result);
                    }
                } else {//4.4以上 包括4.4
                    String result =  "javascript:" + "localStorage.setItem('accessToken',\"" + token + "\")";
                    KLog.d("tag",result);
                    if (mMyWebView != null){
                        mMyWebView.evaluateJavascript(result, value -> {});
                    }
                }
            });
        }
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
                            UIHelper.toMoreKnowYouActivity(WebViewActivity.this,temp1);
                        }
                    }
                });
    }





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
                    }else{
                        RegisterLoginBean.UserInfo mUserInfo = StringUtil.getUserInfoBean();
                         if(!TextUtils.isEmpty(mUserInfo.getName())){
                             name1 = mUserInfo.getName();
                         }else{
                             name1 = mUserInfo.getNickname();
                         }
                    }
                    bean1.setTitle("分享一条" + name1 + "的营销圈动态");
                    bean1.setContent(item.getBlog());
                    String img ;
                    if(item.getImages() != null &&  !item.getImages().isEmpty()){
                        img  = item.getImages().get(0);
                    }else if(item.getUser_info() != null){
                        img = item.getUser_info().getAvatar();
                    }else{
                        img = StringUtil.getUserInfoBean().getAvatar();
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
                    }else{
                        RegisterLoginBean.UserInfo mUserInfo = StringUtil.getUserInfoBean();
                        if(!TextUtils.isEmpty(mUserInfo.getName())){
                            name = mUserInfo.getName();
                        }else{
                            name = mUserInfo.getNickname();
                        }
                    }
                    bean.setTitle("分享一条" + name + "的营销圈动态");
                    bean.setContent(item.getBlog());
                    String img2 ;
                    if(item.getImages() != null &&  !item.getImages().isEmpty()){
                        img2  = item.getImages().get(0);
                    }else if(item.getUser_info() != null){
                        img2 = item.getUser_info().getAvatar();
                    }else{
                        img2 = StringUtil.getUserInfoBean().getAvatar();
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




}
