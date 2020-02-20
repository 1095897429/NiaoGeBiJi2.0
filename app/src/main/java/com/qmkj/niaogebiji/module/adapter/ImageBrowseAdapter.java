package com.qmkj.niaogebiji.module.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.blankj.utilcode.util.ToastUtils;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.TestResultFailActivity;
import com.socks.library.KLog;

import java.io.File;
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

    private RequestManager mRequestManager;

    private static final int MAX_SIZE = 4096;
    private static final int MAX_SCALE = 8;


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


        final Context cxt = container.getContext();

        View view = LayoutInflater.from(cxt).inflate(R.layout.pict_pager_item_view,null);
        final PhotoView photoView = view.findViewById(R.id.photoView);
        final SubsamplingScaleImageView scaleImageView = (SubsamplingScaleImageView) view.findViewById(R.id.sub_imageview);
        photoView.setVisibility(View.VISIBLE);
        scaleImageView.setVisibility(View.GONE);
        scaleImageView.setMaxScale(10.0F);

        mRequestManager = Glide.with(cxt);
        mRequestManager.load(imageList.get(position))
                .dontAnimate()
                .dontTransform()
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        int h = resource.getIntrinsicHeight();
                        int w = resource.getIntrinsicWidth();

                        if(h >= MAX_SIZE || h/w > MAX_SCALE) {
                            photoView.setVisibility(View.GONE);
                            scaleImageView.setVisibility(View.VISIBLE);

                            mRequestManager.load(imageList.get(position))
                                    .downloadOnly(new SimpleTarget<File>() {
                                        @Override
                                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                                            float scale = getImageScale(cxt,resource.getAbsolutePath());
                                            scaleImageView.setImage(ImageSource.uri(resource.getAbsolutePath()),
                                                    new ImageViewState(scale, new PointF(0, 0), 0));
                                        }
                                    });

                        } else {
//                            photoView.setImageBitmap(DrawableToBitmap(resource));
                            Glide.with(activity)
                                .load(imageList.get(position))
                                .placeholder(R.mipmap.img_loading)
                                .into(photoView);
                            //开启图片缩放功能
                            photoView.enable();
                            //设置缩放级别
                            photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            //设置最大缩放倍数
                            photoView.setMaxScale(2.5f);
                            //点击事件，返回
                            photoView.setOnClickListener(v -> {
                                photoView.disenable();
                                KLog.d("tag","单击");
                            });
                        }
                    }
                });



//        final PhotoView photoView = new PhotoView(activity);
//        //开启图片缩放功能
//        photoView.enable();
//        //设置缩放级别
//        photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        //设置最大缩放倍数
//        photoView.setMaxScale(2.5f);
//
//        //加载图片
//        Glide.with(activity)
//                .load(imageList.get(position))
//                .placeholder(R.mipmap.img_loading)
//                .into(photoView);
//        //点击事件，返回
//        photoView.setOnClickListener(v -> {
//            photoView.disenable();
//            KLog.d("tag","单击");
//        });

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

        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem (@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView ((View) object);
    }




    /**
     * 计算出图片初次显示需要放大倍数
     * @param imagePath 图片的绝对路径
     */
    public static float getImageScale(Context context, String imagePath){
        if(TextUtils.isEmpty(imagePath)) {
            return 2.0f;
        }

        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeFile(imagePath);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }

        if(bitmap == null) {
            return 2.0f;
        }

        // 拿到图片的宽和高
        int dw = bitmap.getWidth();
        int dh = bitmap.getHeight();

        WindowManager wm = ((Activity)context).getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        float scale = 1.0f;
        //图片宽度大于屏幕，但高度小于屏幕，则缩小图片至填满屏幕宽
        if (dw > width && dh <= height) {
            scale = width * 1.0f / dw;
        }
        //图片宽度小于屏幕，但高度大于屏幕，则放大图片至填满屏幕宽
        if (dw <= width && dh > height) {
            scale = width * 1.0f / dw;
        }
        //图片高度和宽度都小于屏幕，则放大图片至填满屏幕宽
        if (dw < width && dh < height) {
            scale = width * 1.0f / dw;
        }
        //图片高度和宽度都大于屏幕，则缩小图片至填满屏幕宽
        if (dw > width && dh > height) {
            scale = width * 1.0f / dw;
        }
        bitmap.recycle();
        return scale;
    }


    // 5. Drawable----> Bitmap
    public static Bitmap DrawableToBitmap(Drawable drawable) {

        // 获取 drawable 长宽
        int width = drawable.getIntrinsicWidth();
        int heigh = drawable.getIntrinsicHeight();

        drawable.setBounds(0, 0, width, heigh);

        // 获取drawable的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 创建bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, heigh, config);
        // 创建bitmap画布
        Canvas canvas = new Canvas(bitmap);
        // 将drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }


}
