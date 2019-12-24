package com.qmkj.niaogebiji.module.widget;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.qmkj.niaogebiji.R;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:
 */
public class ImageUtil {

    /** 加载圆角图片 */
    public static void loadRoundImage(Context context, @Nullable String url,
                                      ImageView imageView, @DrawableRes int placeholderId, int radius) {

        if (context == null){
            return;
        }

        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }

        //设置图片圆角角度
//        RoundedCorners roundedCorners= new RoundedCorners(28);
//        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
//        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(width, height);
//
//        Glide.with(context).load(url).apply(options).placeholder(placeholderId).into(imageView);

    }


    public static void load(Context context, String url, ImageView imageView) {

        if (context == null){
            return;
        }

        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }


        Glide.with(context).load(url).placeholder(R.mipmap.icon_fenxiang).into(imageView);


    }

    public static void load(Context context, int res, ImageView imageView) {

        if (context == null){
            return;
        }

        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }

        RequestOptions options = new RequestOptions();

        Glide.with(context).load(res).apply(options).into(imageView);

    }

}
