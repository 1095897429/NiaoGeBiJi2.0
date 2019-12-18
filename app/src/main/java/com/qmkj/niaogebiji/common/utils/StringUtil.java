package com.qmkj.niaogebiji.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.qmkj.niaogebiji.BuildConfig;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.RegisterLoginBean;
import com.qmkj.niaogebiji.module.bean.TempMsgBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

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




    public static List<MultiCircleNewsBean> setCircleData(List<CircleBean> list) {
        if(!list.isEmpty()){
            List<MultiCircleNewsBean> temps = new ArrayList<>();
            temps.clear();
            CircleBean temp;
            String type;
            String link;
            String content;
            List<String> imgs;
            MultiCircleNewsBean mulBean;
            for (int i = 0; i < list.size(); i++) {
                mulBean = new MultiCircleNewsBean();
                temp = list.get(i);
                type = temp.getType();
                link = temp.getLink();
                imgs =  temp.getImages();
                //1 是 转发
                if(!TextUtils.isEmpty(type) && "1".equals(type)){
                    //内部图片
                    List<String> imgsss = temp.getP_blog().getImages();
                    if(imgsss != null &&  !imgsss.isEmpty()){
                        mulBean.setItemType(2);
                    }
                    //link
                    String linked = temp.getP_blog().getLink();
                    if(!TextUtils.isEmpty(linked)){
                        mulBean.setItemType(3);
                    }

                    if((imgsss.isEmpty()) && TextUtils.isEmpty(link)){
                        mulBean.setItemType(5);
                    }
                }else{
                    //原创图片
                    if(imgs != null &&  !imgs.isEmpty()){
                        mulBean.setItemType(1);
                    }

                    //原创link
                    if(!TextUtils.isEmpty(link)){
                        mulBean.setItemType(4);
                    }

                    if((imgs.isEmpty()) && TextUtils.isEmpty(link)){
                        mulBean.setItemType(5);
                    }

                    content = temp.getBlog();
                    //判断内容是否中link
                    String regex = "https?://(?:[-\\w.]|(?:%[\\da-fA-F]{2}))+[^\\u4e00-\\u9fa5]+[\\w-_/?&=#%:]{0}";
                    Matcher matcher = Pattern.compile(regex).matcher(content);
                    while (matcher.find()){
                        KLog.d("tag","url  " + matcher.group(0));
                    }
                }
                mulBean.setCircleBean(temp);
                temps.add(mulBean);
            }

            return temps;
        }

        return null;
    }





}
