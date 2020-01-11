package com.qmkj.niaogebiji.module.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.blankj.utilcode.util.ToastUtils;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.TestResultFailActivity;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:图片预览适配器
 */
public class ImageBrowseAdapter extends PagerAdapter {


    private Activity activity;
    private ArrayList<String> imageList;
    private Bitmap bitmap =  null;
    private ExecutorService mExecutorService;


    public ImageBrowseAdapter(Activity activity,ArrayList<String> imageList){
        this.activity = activity;
        this.imageList = imageList;
        mExecutorService = Executors.newFixedThreadPool(2);
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
//        RequestOptions requestOptions = new RequestOptions()
//                .placeholder(R.mipmap.img_loading)
//                .error(R.mipmap.image_error);
        //加载图片
        Glide.with(activity)
                .load(imageList.get(position))
                .placeholder(R.mipmap.img_loading)
                .into(photoView);
        //点击事件，返回
        photoView.setOnClickListener(v -> {
            photoView.disenable();
            KLog.d("tag","单击");
        });

        //长按事件 下载
//        photoView.setOnLongClickListener(view -> {
//            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            } else {
//                mExecutorService.submit(() -> {
//                    bitmap =  StringUtil.getBitmap(imageList.get(position));
//                    mHandler.sendEmptyMessage(0x113);
//                });
//            }
//
//            return false;
//        });

        container.addView(photoView);
        return photoView;
    }


    @Override
    public void destroyItem (@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView ((View) object);
    }



    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            StringUtil.saveImageToGallery(bitmap, BaseApp.getApplication());

        }
    };



}
