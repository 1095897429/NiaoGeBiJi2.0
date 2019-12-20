package com.qmkj.niaogebiji.common.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.qmkj.niaogebiji.BuildConfig;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.CircleSearchAdapter;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.NewsDetailBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.qmkj.niaogebiji.module.bean.TempMsgBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class StringUtil {

    private static final Object sInstanceSync = new Object();



    /** --------------------------------- 用户信息  ---------------------------------*/

    private static RegisterLoginBean.UserInfo userInfoBean;

    public static void setUserInfoBean(RegisterLoginBean.UserInfo userInfoBean) {
        removeUserInfoBean();
        SPUtils.getInstance().put(Constant.USER_INFO, new Gson().toJson(userInfoBean));
    }

    public static RegisterLoginBean.UserInfo getUserInfoBean() {
        synchronized (sInstanceSync) {
            if (userInfoBean == null) {
                String string = SPUtils.getInstance().getString(Constant.USER_INFO);
                if (!TextUtils.isEmpty(string)) {
                    Gson gson = new Gson();
                    userInfoBean = gson.fromJson(string, RegisterLoginBean.UserInfo.class);
                }
            }
        }
        return userInfoBean;
    }

    public static void removeUserInfoBean() {
        userInfoBean = null;
        SPUtils.getInstance().remove(Constant.USER_INFO);
    }



    /** --------------------------------- 快速点击  ---------------------------------*/

    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        //当点击时间 和 之前的时间对比，如果大于1秒，返回false ,表示不是快速点击
        if ((currentClickTime - lastClickTime) >= FAST_CLICK_DELAY_TIME ) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }


    /** --------------------------------- 网络图片 转 bitmap  ---------------------------------*/
    public static Bitmap getURLimage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);//读取图像数据
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /** --------------------------------- 封装的一些方法 ---------------------------------*/



    public static String getLink(String parma){
        String link = BuildConfig.DEBUG ? Constant.TEST_URL + parma: Constant.RELEASE_URL + parma;
        KLog.d("tag","跳转的link是 " + link);
        return link;
    }



    public static void setPublishTime(TextView tx,String publishTime){
        //作者发布时间
        if(null != publishTime){
            tx.setText(TimeUtils.millis2String(Long.parseLong(publishTime)* 1000L,"yyyy/MM/dd"));
        }
    }


    public static void copyLink(String share_url) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) BaseApp.getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label",share_url);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
        ToastUtils.showShort("链接复制成功！");
    }


//     之前的方法
//    public static List<MultiCircleNewsBean> setCircleData(List<CircleBean> list) {
//        if(!list.isEmpty()){
//            List<MultiCircleNewsBean> temps = new ArrayList<>();
//            temps.clear();
//            CircleBean temp;
//            String type;
//            String link;
//            String content;
//            List<String> imgs;
//            MultiCircleNewsBean mulBean;
//            for (int i = 0; i < list.size(); i++) {
//                mulBean = new MultiCircleNewsBean();
//                temp = list.get(i);
//                type = temp.getType();
//                link = temp.getLink();
//                imgs =  temp.getImages();
//                //1 是 转发
//                if(!TextUtils.isEmpty(type) && "1".equals(type)){
//                    //内部图片
//                    List<String> imgsss = temp.getP_blog().getImages();
//                    if(imgsss != null &&  !imgsss.isEmpty()){
//                        mulBean.setItemType(2);
//                    }
//                    //link
//                    String linked = temp.getP_blog().getLink();
//                    if(!TextUtils.isEmpty(linked)){
//                        mulBean.setItemType(3);
//                    }
//
//                    if((imgsss.isEmpty()) && TextUtils.isEmpty(link)){
//                        mulBean.setItemType(5);
//                    }
//                }else{
//                    //原创图片
//                    if(imgs != null &&  !imgs.isEmpty()){
//                        mulBean.setItemType(1);
//                    }
//
//                    //原创link
//                    if(!TextUtils.isEmpty(link)){
//                        mulBean.setItemType(4);
//                    }
//
//                    if((imgs.isEmpty()) && TextUtils.isEmpty(link)){
//                        mulBean.setItemType(5);
//                    }
//
//                    content = temp.getBlog();
//                    //判断内容是否中link
//                    String regex = "https?://(?:[-\\w.]|(?:%[\\da-fA-F]{2}))+[^\\u4e00-\\u9fa5]+[\\w-_/?&=#%:]{0}";
//                    Matcher matcher = Pattern.compile(regex).matcher(content);
//                    while (matcher.find()){
//                        KLog.d("tag","url  " + matcher.group(0));
//                    }
//                }
//                mulBean.setCircleBean(temp);
//                temps.add(mulBean);
//            }
//
//            return temps;
//        }
//
//        return null;
//    }


    /** 判定操作 */
    public static boolean checkNull(String param){
        if(TextUtils.isEmpty(param)){
            return false;
        }

        if("null".equals(param) || "".equals(param)){
            return false;
        }
        return true;
    }


    /** 返回登录的uid */
    public static  String getMyUid(){
        if(getUserInfoBean() != null){
            return getUserInfoBean().getUid();
        }
        return "null";
    }





    //搜索全部中圈子部分
    public static List<MultiCircleNewsBean> setCircleData(List<CircleBean> list) {
        if(!list.isEmpty()){
            List<MultiCircleNewsBean> temps = new ArrayList<>();
            temps.clear();
            CircleBean temp;
            String link;
            List<String> imgs;
            MultiCircleNewsBean mulBean;
            for (int i = 0; i < list.size(); i++) {
                mulBean = new MultiCircleNewsBean();
                temp = list.get(i);
                link = temp.getLink();
                imgs =  temp.getImages();

                if(imgs != null &&  !imgs.isEmpty()){
                    mulBean.setItemType(CircleSearchAdapter.TYPE1);
                    mulBean.setCircleBean(temp);
                    temps.add(mulBean);
                    continue;
                }

                //原创link
                if(!TextUtils.isEmpty(link)){
                    mulBean.setItemType(CircleSearchAdapter.TYPE3);
                    mulBean.setCircleBean(temp);
                    temps.add(mulBean);
                    continue;
                }
                //原创link
                if(!TextUtils.isEmpty(temp.getArticle_id())  && !"0".equals(temp.getArticle_id())){
                    mulBean.setItemType(CircleSearchAdapter.TYPE4);
                    mulBean.setCircleBean(temp);
                    temps.add(mulBean);
                    continue;
                }


                mulBean.setItemType(CircleSearchAdapter.TYPE2);

                mulBean.setCircleBean(temp);
                temps.add(mulBean);
            }

            return temps;
        }

        return null;
    }





    /** 分享 link 链接样式*/
    //分享微信（web) 链接
    public static void shareWxByWeb(Activity activity ,ShareBean bean) {
        if(null != bean){
            String sharepic = bean.getImg();
            String shareurl = bean.getLink();
            String title = bean.getTitle();
            String summary = bean.getContent();
            SHARE_MEDIA platform = null;

            //判断发送路径
            if("weixin_link".equals(bean.getShareType())){
                platform = SHARE_MEDIA.WEIXIN;
            }else if("circle_link".equals(bean.getShareType())){
                platform = SHARE_MEDIA.WEIXIN_CIRCLE;
            }

            UMImage thumb;
            if (TextUtils.isEmpty(sharepic)) {
                thumb = new UMImage(activity, R.mipmap.icon_fenxiang);
            } else {
                thumb = new UMImage(activity, sharepic);
            }
            UMWeb web = new UMWeb(shareurl);
            //标题
            web.setTitle(title);
            //缩略图
            web.setThumb(thumb);
            //描述
            web.setDescription(summary);

            new ShareAction(activity)
                    .setPlatform(platform)
                    .withMedia(web)
                    .share();
        }

    }


}
