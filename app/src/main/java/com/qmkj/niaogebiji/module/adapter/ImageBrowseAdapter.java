package com.qmkj.niaogebiji.module.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.qmkj.niaogebiji.R;

import java.util.ArrayList;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:
 */
public class ImageBrowseAdapter extends PagerAdapter {


    private Activity activity;
    private ArrayList<String> imageList;


    public ImageBrowseAdapter(Activity activity,ArrayList<String> imageList){
        this.activity = activity;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        final PhotoView photoView = new PhotoView(activity);
        //开启图片缩放功能
        photoView.enable();
        //设置缩放级别
        photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        //设置最大缩放倍数
        photoView.setMaxScale(2.5f);
        //参数设置
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.mipmap.img_loading)
                .error(R.mipmap.image_error);
        //加载图片
        Glide.with(activity)
                .load(imageList.get(position))
                .apply(requestOptions)
                .into(photoView);
        //点击事件，返回
        photoView.setOnClickListener(v -> {
            photoView.disenable();
//            activity.finish();
        });
        //双击事件
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                if(imageLongClick != null){
//                    imageLongClick.longClickImage(position);
//                }
                return true;
            }
        });
        container.addView(photoView);
        return photoView;
    }


    @Override
    public void destroyItem (@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView ((View) object);
    }


}
