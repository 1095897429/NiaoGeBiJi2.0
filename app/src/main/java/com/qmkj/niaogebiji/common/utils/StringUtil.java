package com.qmkj.niaogebiji.common.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.qmkj.niaogebiji.BuildConfig;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.module.adapter.CircleRecommentAdapterNew;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.Manifest.permission.READ_PHONE_STATE;

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
        if ((currentClickTime - lastClickTime) >= FAST_CLICK_DELAY_TIME) {
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


    public static String getLink(String parma) {
        String link = BuildConfig.DEBUG ? Constant.TEST_URL + parma : Constant.RELEASE_URL + parma;
        KLog.d("tag", "跳转的link是 " + link);
        return link;
    }


    public static void setPublishTime(TextView tx, String publishTime) {
        //作者发布时间
        if (null != publishTime) {
            tx.setText(TimeUtils.millis2String(Long.parseLong(publishTime) * 1000L, "yyyy/MM/dd"));
        }
    }


    public static void copyLink(String share_url) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) BaseApp.getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", share_url);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        ToastUtils.setGravity(Gravity.BOTTOM, 0, SizeUtils.dp2px(40));
        ToastUtils.showShort("链接复制成功！");
    }

    /** 判定操作 */
    public static boolean checkNull(String param) {
        if (TextUtils.isEmpty(param)) {
            return false;
        }

        if ("null".equals(param) || "".equals(param)) {
            return false;
        }
        return true;
    }


    /** 返回登录的uid */
    public static String getMyUid() {
        if (getUserInfoBean() != null) {
            return getUserInfoBean().getUid();
        }
        return "null";
    }


    /**  朋友圈 朋友 复制链接*/
    public static void showShareDialog(Activity activity, NewsDetailBean mNewsDetailBean) {
        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(activity).builder();
        alertDialog.setSharelinkView();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position) {
                case 0:
                    //用于验证微信分享成功 显示弹框 判断
                    Constant.isActicleShare = true;
                    ShareBean bean1 = new ShareBean();
                    bean1.setShareType("circle_link");
                    bean1.setImg(mNewsDetailBean.getPic());
                    bean1.setLink(mNewsDetailBean.getShare_url());
                    bean1.setTitle(mNewsDetailBean.getShare_title());
                    bean1.setContent(mNewsDetailBean.getShare_summary());
                    StringUtil.shareWxByWeb(activity, bean1);
                    break;
                case 1:
                    //用于验证微信分享成功 显示弹框 判断
                    Constant.isActicleShare = true;

                    KLog.d("tag", "朋友 是链接");
                    ShareBean bean = new ShareBean();
                    bean.setShareType("weixin_link");
                    bean.setImg(mNewsDetailBean.getPic());
                    bean.setLink(mNewsDetailBean.getShare_url());
                    bean.setTitle(mNewsDetailBean.getShare_title());
                    bean.setContent(mNewsDetailBean.getShare_summary());
                    StringUtil.shareWxByWeb(activity, bean);
                    break;
                case 2:
                    if (null != mNewsDetailBean) {
                        KLog.d("tag", "复制链接");
                        StringUtil.copyLink(mNewsDetailBean.getShare_url());
                    }

                    break;
                default:
            }
        });
        alertDialog.show();
    }


    //搜索全部中圈子部分
    public static List<MultiCircleNewsBean> setCircleData(List<CircleBean> list) {
        if (!list.isEmpty()) {
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
                imgs = temp.getImages();

                if (imgs != null && !imgs.isEmpty()) {
                    mulBean.setItemType(CircleSearchAdapter.TYPE1);
                    mulBean.setCircleBean(temp);
                    temps.add(mulBean);
                    continue;
                }

                //原创link
                if (!TextUtils.isEmpty(link)) {
                    mulBean.setItemType(CircleSearchAdapter.TYPE3);
                    mulBean.setCircleBean(temp);
                    temps.add(mulBean);
                    continue;
                }
                //原创link
                if (!TextUtils.isEmpty(temp.getArticle_id()) && !"0".equals(temp.getArticle_id())) {
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
    public static void shareWxByWeb(Activity activity, ShareBean bean) {
        if (null != bean) {
            String sharepic = bean.getImg();
            String shareurl = bean.getLink();
            String title = bean.getTitle();
            String summary = bean.getContent();
            SHARE_MEDIA platform = null;

            //判断发送路径
            if ("weixin_link".equals(bean.getShareType())) {
                platform = SHARE_MEDIA.WEIXIN;
            } else if ("circle_link".equals(bean.getShareType())) {
                platform = SHARE_MEDIA.WEIXIN_CIRCLE;
            }

            UMImage thumb;
            if (TextUtils.isEmpty(sharepic)) {
                thumb = new UMImage(activity, R.mipmap.icon_fenxiang);
            } else {
                if (bean.getBitmap() != null) {
                    Bitmap bitmap = drawableBitmapOnWhiteBg(BaseApp.getApplication(), bean.getBitmap());
                    thumb = new UMImage(activity, bitmap);
                } else {
                    thumb = new UMImage(activity, sharepic);
                }

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


    /**
     * 把bitmap画到一个白底的newBitmap上,将newBitmap返回
     * @param context 上下文
     * @param bitmap 要绘制的位图
     * @return Bitmap
     */
    public static Bitmap drawableBitmapOnWhiteBg(Context context, Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(context.getResources().getColor(android.R.color.white));
        Paint paint = new Paint();
        //将原图使用给定的画笔画到画布上
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return newBitmap;
    }


    // 找不到type[比如plog返回空..],那么返回默认的type 100
    // 先判断原创0  再判断图片 再判断link，最后只剩下全文本
    // 转发1

    /** 返回圈子的类型 */
    public static int getCircleType(CircleBean item) {
        if (null != item) {
            if ("0".equals(item.getType())) {
                //原创图片
                if (item.getImages() != null && !item.getImages().isEmpty()) {
                    return CircleRecommentAdapterNew.YC_PIC;
                }

                //原创link
                if (!TextUtils.isEmpty(item.getLink())) {
                    return CircleRecommentAdapterNew.YC_LINK;
                }

                //原创 文章
                if (!TextUtils.isEmpty(item.getArticle_id()) && !"0".equals(item.getArticle_id())) {
                    return CircleRecommentAdapterNew.YC_ACTICLE;
                }

                //原创 文本
                if (!TextUtils.isEmpty(item.getBlog())) {
                    return CircleRecommentAdapterNew.YC_TEXT;
                }

            } else if ("1".equals(item.getType())) {

                CircleBean.P_blog temp = item.getP_blog();
                if (null != temp) {
                    //转发图片
                    if (temp.getImages() != null && !temp.getImages().isEmpty()) {
                        return CircleRecommentAdapterNew.ZF_PIC;
                    }

                    //转发link
                    if (!TextUtils.isEmpty(temp.getLink())) {
                        return CircleRecommentAdapterNew.ZF_LINK;
                    }

                    //转发 文章
                    if (!TextUtils.isEmpty(temp.getArticle_id()) && !"0".equals(temp.getArticle_id())) {
                        return CircleRecommentAdapterNew.ZF_ACTICLE;
                    }

                    //转发 文本
                    if (!TextUtils.isEmpty(temp.getBlog())) {
                        return CircleRecommentAdapterNew.ZF_TEXT;
                    }

                }
            }
        }
        return 100;
    }


    /**
     * 根据传入的URL获取主域名
     *
     * @param url
     * @return
     */
    public static String getDomain(String url) {
        String domain = "";
        if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
            try {
                String host = Uri.parse(url).getHost();
                return host;
            } catch (Exception ex) {
            }
        }
        return domain;
    }


    /**
     * 根据url路径 转bitmap
     *
     * @param urlpath
     * @return
     */
    public static Bitmap getBitmap(String urlpath) {

        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


    /** 保存图片到 /storage/emulated/0/DCIM/pipi/ */
    public static void saveImageToGallery(Bitmap bitmap, BaseApp activity) {
        // 首先保存图片
        File file = null;
        String fileName = System.currentTimeMillis() + ".jpg";
        File root = FileHelper.getOutputImgDirFile(activity);
        KLog.d("tag", "路径是：" + root);
        File dir = new File(root, "ngbj");
        KLog.d("tag", "路径是：" + dir);
        if (dir.mkdirs() || dir.isDirectory()) {
            file = new File(dir, fileName);
        }
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
            ToastUtils.showShort("保存失败");
        }
        //其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(activity.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 通知图库更新
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            MediaScannerConnection.scanFile(activity, new String[]{file.getAbsolutePath()}, null,
//                    (path, uri) -> {
//                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                        mediaScanIntent.setData(uri);
//                        activity.sendBroadcast(mediaScanIntent);
//                    });
//        } else {
//            String relationDir = file.getParent();
//            File file1 = new File(relationDir);
//            activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file1.getAbsoluteFile())));
//        }
    }

    /** 格式化参与人数显示 */
    public static String formatPeopleNum(String num) {
        int pnum = Integer.parseInt(num);
        if (pnum > 10000) {
            float result = pnum / 10000f;
            // 保留一位小数
            DecimalFormat decimalFormat = new DecimalFormat(".0");
            String pri = decimalFormat.format(result);
            return pri + "w";
        }
        return pnum + "";
    }


    public static CircleBean addLinksData(CircleBean temp) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> pcLinks = new ArrayList<>();
        //在文本的基础上，再检查一下有无link
        String regex = "((http|https|ftp|ftps):\\/\\/)?([a-zA-Z0-9-]+\\.){1,5}(com|cn|net|org|hk|tw)((\\/(\\w|-)+(\\.([a-zA-Z]+))?)+)?(\\/)?(\\??([\\.%:a-zA-Z0-9_-]+=[#\\.%:a-zA-Z0-9_-]+(&amp;)?)+)?";
        Matcher matcher = Pattern.compile(regex).matcher(temp.getBlog());
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            KLog.d("tag", "start " + start + " end " + end);
            KLog.d("tag", " matcher.group() " + matcher.group());
            sb.append(start).append(":").append(end).append(":");
            pcLinks.add(matcher.group());

        }
        temp.setPcLinks(pcLinks);

        return temp;
    }




    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getIMEI() {
        TelephonyManager tm = (TelephonyManager) Utils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Class clazz = tm.getClass();
                //noinspection unchecked
                Method getImeiMethod = clazz.getDeclaredMethod("getImei");
                getImeiMethod.setAccessible(true);
                String imei = (String) getImeiMethod.invoke(tm);
                if (imei != null){
                    return imei;
                }
            } catch (Exception e) {
                Log.e("PhoneUtils", "getIMEI: ", e);
            }
        }
        String imei = tm.getDeviceId();
        if (imei != null && imei.length() == 15) {
            return imei;
        }
        return "";

}


    /**
     * 获取AndroidID
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        @SuppressLint("HardwareIds")
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidID;
    }





}
