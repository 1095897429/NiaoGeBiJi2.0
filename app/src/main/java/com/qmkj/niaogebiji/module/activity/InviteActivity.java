package com.qmkj.niaogebiji.module.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.BuildConfig;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.FileHelper;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.adapter.ViewPagerAdapter;
import com.qmkj.niaogebiji.module.bean.InviteBean;
import com.qmkj.niaogebiji.module.bean.InvitePosterBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.WxShareBean;
import com.qmkj.niaogebiji.module.widget.AlphaTransformer;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.qmkj.niaogebiji.module.widget.MyWebView;
import com.qmkj.niaogebiji.module.widget.ScaleTransformer;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-27
 * 描述:邀请好友
 * 1.保存图片过大如何处理？
 * 2.qq分享先没做
 */
public class InviteActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.iv_text)
    TextView iv_text;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @BindView(R.id.webview)
    MyWebView mMyWebView;

    @BindView(R.id.dot)
    LinearLayout dot;



    private InvitePosterBean mPosterBean;
    private String picLink;

    private List<String> pics = new ArrayList<>();


    private ViewPagerAdapter viewPagerAdapter;


    private int mPosition = -1;
    //底部小图片数组
    private ImageView[] dotArray;
    //图片列表数据源
    private List<View> views;
    //小圆点id
    private int[] ids={R.id.dot1,R.id.dot2,R.id.dot3,R.id.dot4};

    private ExecutorService mExecutorService;

    String permissions1[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_invite;
    }

    @Override
    protected void initView() {
        tv_title.setText("邀请好友");
        iv_text.setText("邀请记录");
        iv_text.setVisibility(View.VISIBLE);

        mExecutorService = Executors.newFixedThreadPool(2);

        //webview
        getWebData();

        getshareinfo();
    }

    private void getshareinfo() {
        Map<String,String> map = new HashMap<>();
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().getshareinfo(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<InvitePosterBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<InvitePosterBean> response) {

                        mPosterBean = response.getReturn_data();
                        if(null != mPosterBean){
                           initViewPage();
                           initDot();
                        }
                    }
                });
    }



    private void getWebData() {

        mMyWebView.setDrawingCacheEnabled(false);
        mMyWebView.setLayerType(View.LAYER_TYPE_NONE, null);

        mMyWebView.loadUrl(StringUtil.getLink("invitationrule"));

        mMyWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(100 == newProgress){
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
                mMyWebView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }



    @Override
    public void initData() {
//        mInviteBean = new InviteBean();
//        for (int i = 0; i < 4; i++) {
//            if(i == 0){
//                pics.add("https://desk-fd.zol-img.com.cn/t_s2560x1440c5/g2/M00/05/09/ChMlWl1BAz-IcV0oADKEXBJ0ncgAAMP0gAAAAAAMoR0279.jpg");
//            }else{
//                pics.add("https://article-fd.zol-img.com.cn/g2/M00/0E/00/ChMlWVyJwQeIRQrvAA_BjB8NhecAAIyDANWGdgAD8Gk692.jpg");
//
//            }
//        }
//        mInviteBean.setPic_links(pics);
    }

    //初始化小圆点的id
    private void initDot() {
        dot.removeAllViews();
        dotArray = new ImageView[views.size()];
        LinearLayout.LayoutParams lp;
        for (int i = 0; i < views.size(); i++) {

            ImageView imageView = new ImageView(this);
            imageView.setId( i + 1);
            if(i == 1){
                imageView.setImageResource(R.mipmap.welcome_long_pic);
            }else{
                imageView.setImageResource(R.mipmap.welcome_short_pic);
            }
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            if(i != 0){
                lp.setMargins(SizeUtils.dp2px(10),0,0,0);
                imageView.setLayoutParams(lp);
            }

            dotArray[i] = imageView;

            dot.addView(imageView);
        }
    }



    //初始化viewpager
    private void initViewPage() {

        views = new ArrayList<>();
        for (int i = 0; i < mPosterBean.getPic_list().size(); i++) {
            ImageView imageView = new ImageView(this);
            ImageUtil.load(this,mPosterBean.getPic_list().get(i),imageView);
            views.add(imageView);
        }

        viewPagerAdapter = new ViewPagerAdapter(views);

        viewpager.setAdapter(viewPagerAdapter);
        viewpager.setOffscreenPageLimit(3);
        viewpager.setPageMargin(24);
        viewpager.setCurrentItem(1);
//        viewpager.setPageTransformer(false, new ScaleTransformer());

        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 设置底部小点选中状态
                for(int i = 0;i<mPosterBean.getPic_list().size();i ++){
                    if(position==i){
                        dotArray[i].setImageResource(R.mipmap.welcome_long_pic);
                    }else {
                        dotArray[i].setImageResource(R.mipmap.welcome_short_pic);
                    }
                }

                mPosition = position;
                KLog.d("tag",mPosterBean.getPic_list().get(mPosition));


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }



    @OnClick({R.id.iv_back,R.id.iv_text,
            R.id.share_wx,R.id.share_circle,R.id.share_qq,R.id.share_save,R.id.share_link
    })
    public void clicks(View view){
        switch (view.getId()){
            case R.id.iv_text:
                KLog.d("tag","去 h5界面");
                MobclickAgentUtils.onEvent(UmengEvent.i_invite_record_2_0_0);

                UIHelper.toWebViewActivity(InviteActivity.this,StringUtil.getLink("invitationrecords"));
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.share_wx:
                MobclickAgentUtils.onEvent(UmengEvent.i_invite_wx_2_0_0);
                toWxShare(0);
                break;
            case R.id.share_circle:
                MobclickAgentUtils.onEvent(UmengEvent.i_invite_moments_2_0_0);

                toWxShare(1);
                break;
            case R.id.share_qq:
                MobclickAgentUtils.onEvent(UmengEvent.i_invite_qq_2_0_0);

                if(hasPermissions(this,permissions1)){
                    toWxShare(3);
                }else{
                    ActivityCompat.requestPermissions(InviteActivity.this, permissions1, 100);
                }

                break;
            case R.id.share_save:
                MobclickAgentUtils.onEvent(UmengEvent.i_invite_save_2_0_0);

                if(hasPermissions(this,permissions1)){
                    toWxShare(2);
                }else{
                    ActivityCompat.requestPermissions(InviteActivity.this, permissions1, 100);
                }
                break;
            case R.id.share_link:
                MobclickAgentUtils.onEvent(UmengEvent.i_invite_copylink_2_0_0);

                RegisterLoginBean.UserInfo userInfo = StringUtil.getUserInfoBean();
                StringUtil.copyLink(userInfo.getInvite_url());


                //验证的话需要在？前面加/
//                StringUtil.copyLink(" http://share.xy860.com/?ivtoken=O2UDe6Pdagduo9uL#/appshare");

                ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                ToastUtils.showShort("链接复制成功！");

                break;
            default:
        }
    }

    private void toWxShare(int type) {
        if(-1 == mPosition){
            picLink = mPosterBean.getPic_list().get(1);
        }else{
            picLink = mPosterBean.getPic_list().get(mPosition);
        }

        mExecutorService.submit(() -> {
//            picLink = "https://article-fd.zol-img.com.cn/g2/M00/0E/00/ChMlWVyJwQeIRQrvAA_BjB8NhecAAIyDANWGdgAD8Gk692.jpg";
            Bitmap bitmap = StringUtil.getURLimage(picLink);
            Message message = Message.obtain();
            message.obj = bitmap;
            message.what = type;
            mHandler.sendMessage(message);
        });
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = (Bitmap) msg.obj;
            if(bitmap == null){
                KLog.d("tag","图片出错");
                return;
            }
            int type = msg.what;
            if(type == 0 ){
                shareWxByPic(bitmap);
            }else if(type == 1){
                shareWxCircleByPic(bitmap);
            }else if(type == 2){
                //把图片保存到指定的文件夹，同时又需要图片出现在图库里呢
                saveImageToGrery(bitmap);
            }else if(type == 3){
                shareQQ(bitmap);
            }
        }
    };

    private void shareQQ(Bitmap bitmap) {

        if (this == null){
            return;
        }

        SHARE_MEDIA platform;
        platform = SHARE_MEDIA.QQ;
        UMImage image = new UMImage(this, bitmap);

        //传入平台
        new ShareAction(this)
                .withText("哈哈")
                .setPlatform(platform)
                .withMedia(image)
                .share();
    }

    private void saveImageToGrery(Bitmap bitmap) {
        File root = FileHelper.getOutputInviteDirFile(this);
        KLog.d("tag","下载图片的地址放在 " + root);
        if(!root.exists()){
            root.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(root,fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            ToastUtils.showShort("保存成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 通知图库更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null,
                    (path, uri) -> {
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        mediaScanIntent.setData(uri);
                        sendBroadcast(mediaScanIntent);
                    });
        } else {
            String relationDir = file.getParent();
            File file1 = new File(relationDir);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file1.getAbsoluteFile())));
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        KLog.d("tag","权限是： " + permissions[0]);
        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
            //用户勾选了不再提示，函数返回false
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                //解释原因，并且引导用户至设置页手动授权
                ToastUtils.showShort("请到设置中开启存储卡权限");
                return;
            }
        }else{
            KLog.d("tag","存储卡已同意");
            toWxShare(2);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


}
