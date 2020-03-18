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
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.jakewharton.disklrucache.DiskLruCache;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.activity.PicPreviewActivity;
import com.qmkj.niaogebiji.module.activity.TestResultFailActivity;
import com.qmkj.niaogebiji.module.bean.PicBean;
import com.qmkj.niaogebiji.module.widget.BitmapCache;
import com.socks.library.KLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.udesk.photoselect.PreviewActivity;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-13
 * 描述:图片预览适配器
 */
public class ImageBrowseAdapter extends PagerAdapter {

    private Activity activity;
    private ArrayList<PicBean> imageList;

    private ExecutorService mExecutorService;

    private RequestManager mRequestManager;

    private static final int MAX_SIZE = 4096;
    private static final int MAX_SCALE = 8;

    private DiskLruCache mDiskLruCache;
    private static final long DISK_CACHE_SIZE = 1024*1024*10;


    public ImageBrowseAdapter(Activity activity,ArrayList<PicBean> imageList){
        this.activity = activity;
        this.imageList = imageList;
        mExecutorService = Executors.newFixedThreadPool(5);
        diskLruCache(activity);
    }


    private void diskLruCache(Context context){
        File diskCacheDir=new File(context.getCacheDir().getPath()+"/bitmap");
        if(!diskCacheDir.exists()){
            diskCacheDir.mkdirs();
        }
        try {
            mDiskLruCache = DiskLruCache.open(diskCacheDir,1,1,DISK_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        KLog.d("tag","getItemPosition ");
        return POSITION_NONE;
    }

    private String bytesToHexString(byte[] bytes){
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0,length=bytes.length;i<length;i++){
            String hex=Integer.toHexString(0xFF&bytes[i]);
            if(hex.length()==1){
                stringBuilder.append('0');
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }

    private String hashKeyFormUrl(String url){
        String cacheKey;
        try {
            final MessageDigest messageDigest= MessageDigest.getInstance("MD5");
            messageDigest.update(url.getBytes());
            cacheKey=bytesToHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey=String.valueOf(url.hashCode());
        }
        return cacheKey;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        final Context cxt = container.getContext();
        View view = LayoutInflater.from(cxt).inflate(R.layout.pict_pager_item_view,null);
        final PhotoView photoView = view.findViewById(R.id.photoView);
        final SubsamplingScaleImageView scaleImageView = view.findViewById(R.id.sub_imageview);
        final TextView pic_look = view.findViewById(R.id.pic_look);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        final ImageView img_loading = view.findViewById(R.id.img_loading);


        PicBean item = imageList.get(position);

        //点击时间
        pic_look.setOnClickListener(v -> {

            if(StringUtil.isFastClick()){
                return;
            }

            mExecutorService.submit(() -> {
                //图片的路径 -- 转移 -- 创建Editor
                String imgUrl = item.getPic();
                String key = hashKeyFormUrl(imgUrl);
                try {
                    DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                    if (editor != null) {
                        OutputStream outputStream = editor.newOutputStream(0);
                        if (downloadUrlToStream(imgUrl, outputStream,position,progressBar,pic_look,container)) {
                            editor.commit();
                            //更新视图
                            doLoadOldData(item,img_loading,photoView,scaleImageView);
                        } else {
                            editor.abort();
                        }
                    }
                    mDiskLruCache.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        });
        //每次都创建一个
        Bitmap bitmap =  null;
        try {
            String imageUrl = item.getPic();
            String key = hashKeyFormUrl(imageUrl);
            KLog.d("tag","imageUrl " + imageUrl + "  key " + key);
            DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
            if (snapShot != null) {
                KLog.d("tag","加载的是磁盘缓存  " );
                InputStream is = snapShot.getInputStream(0);
                bitmap = BitmapFactory.decodeStream(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //内存 磁盘 都没有
        if(bitmap == null){
            pic_look.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            img_loading.setVisibility(View.VISIBLE);
            mRequestManager = Glide.with(activity);
            mRequestManager.load(item.getScalePic())
                    .dontAnimate()
                    .dontTransform()
                    .placeholder(R.mipmap.img_loading)
                    .into(new SimpleTarget<Drawable>() {
                        @SuppressLint("CheckResult")
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            int h = resource.getIntrinsicHeight();
                            int w = resource.getIntrinsicWidth();

                            if(h >= MAX_SIZE || h/w > MAX_SCALE) {
                                photoView.setVisibility(View.GONE);
                                scaleImageView.setVisibility(View.VISIBLE);
                                scaleImageView.setMaxScale(10.0F);
                                mRequestManager.load(item.getScalePic() )
                                        .placeholder(R.mipmap.img_loading)
                                        .downloadOnly(new SimpleTarget<File>() {
                                            @Override
                                            public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                                                float scale = getImageScale(activity,resource.getAbsolutePath());
                                                scaleImageView.setImage(ImageSource.uri(resource.getAbsolutePath()),
                                                        new ImageViewState(scale, new PointF(0, 0), 0));
                                                //隐藏加载图
                                                img_loading.setVisibility(View.GONE);
                                            }
                                        });

                            } else {
                                photoView.setVisibility(View.VISIBLE);
                                scaleImageView.setVisibility(View.GONE);
                                img_loading.setVisibility(View.GONE);
                                Glide.with(activity)
                                        .load(item.getScalePic() )
                                        .dontAnimate()
                                        .dontTransform()
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
        }else{
            pic_look.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            doLoadOldData(item,img_loading,photoView,scaleImageView);
        }

        //设置tag
        view.setTag(R.id.account, position);
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



    //下载文件的长度
    private int contentLength;
    //当前进度
    private int progress;
    //之前的进度
    private int oldProgress;

    private boolean downloadUrlToStream(String urlString, OutputStream outputStream, int position, final ProgressBar progressBar, final TextView pic_look, ViewGroup container) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            contentLength = urlConnection.getContentLength();

            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);

            int count = 0;
            byte [] bytes = new byte[8* 1024];
            int len;
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes,0,len);
                count += len;
                progress = (int) (count * 100L / contentLength);
                //如果进度与之前进度相等，这不更新，如果更新太频繁，造成界面卡顿
                if(oldProgress != progress){
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            KLog.d("tag","当前进度是 " + progress);
                            if(progressBar != null){
                                progressBar.setProgress(progress);
                                pic_look.setText(progress + "%");

                                if(progressBar != null && progressBar.getProgress() == 100){
                                    KLog.d("tag","更新完毕");
                                    //重新加载
                                    activity.runOnUiThread(() -> {
                                        pic_look.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);

                                    });
                                }
                            }
                        }
                    });
                }
                oldProgress = progress;


            }
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }



    private void doLoadOldData(PicBean item,ImageView img_loading,PhotoView photoView,SubsamplingScaleImageView scaleImageView) {

        mRequestManager = Glide.with(activity);
        mRequestManager.load(item.getPic())
                .dontAnimate()
                .dontTransform()
                .into(new SimpleTarget<Drawable>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        int h = resource.getIntrinsicHeight();
                        int w = resource.getIntrinsicWidth();

                        if(h >= MAX_SIZE || h/w > MAX_SCALE) {
                            photoView.setVisibility(View.GONE);
                            scaleImageView.setVisibility(View.VISIBLE);
                            //隐藏加载图
                            img_loading.setVisibility(View.VISIBLE);
                            mRequestManager.load(item.getPic() )
                                    .downloadOnly(new SimpleTarget<File>() {
                                        @Override
                                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                                            float scale = getImageScale(activity,resource.getAbsolutePath());
                                            scaleImageView.setImage(ImageSource.uri(resource.getAbsolutePath()),
                                                    new ImageViewState(scale, new PointF(0, 0), 0));
                                            img_loading.setVisibility(View.GONE);
                                        }
                                    });

                        } else {
                            photoView.setVisibility(View.VISIBLE);
                            scaleImageView.setVisibility(View.GONE);
                            img_loading.setVisibility(View.GONE);
                            Glide.with(activity)
                                    .load(item.getPic())
                                    .dontAnimate()
                                    .placeholder(R.mipmap.img_loading)
                                    .dontTransform()
                                    .into(photoView);
                            //开启图片缩放功能
                            photoView.enable();
                            //设置缩放级别
                            photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            //设置最大缩放倍数
                            photoView.setMaxScale(2.5f);
                            //点击事件，返回
                            photoView.setOnClickListener(v -> {
//                                photoView.disenable();
                                KLog.d("tag","单击");
                            });


                        }
                    }
                });
    }





}
