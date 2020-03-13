package com.qmkj.niaogebiji.module.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.Utils;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.jakewharton.disklrucache.DiskLruCache;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.common.utils.FileHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.IncomeBean;
import com.qmkj.niaogebiji.module.bean.PicBean;
import com.qmkj.niaogebiji.module.widget.BitmapCache;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:图片选择 Item适配器
 *
 * 1.我采用的是磁盘缓存
 */
public class PicPreViewItemAdapter extends BaseQuickAdapter<PicBean, BaseViewHolder> {

    public PicPreViewItemAdapter(@Nullable List<PicBean> data,Context context) {
        super(R.layout.pict_pager_item_view,data);
        mExecutorService = Executors.newFixedThreadPool(2);
        diskLruCache(context);
    }


    private RequestManager mRequestManager;

    private static final int MAX_SIZE = 4096;
    private static final int MAX_SCALE = 8;

    private Bitmap bitmap;

    private ExecutorService mExecutorService;


    /**
     * 获取网络图片
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
    public Bitmap GetImageInputStream(String imageurl){
        URL url;
        HttpURLConnection connection;
        Bitmap bitmap=null;
        try {
            url = new URL(imageurl);
            connection=(HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream=connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private DiskLruCache mDiskLruCache;
    private static final long DISK_CACHE_SIZE=1024*1024*10;

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

    Bitmap temp;

    //下载文件的长度
    private int contentLength;
    //当前进度
    private int progress;
    //之前的进度
    private int oldProgress;

    private boolean downloadUrlToStream(String urlString, OutputStream outputStream, int position,final ProgressBar progressBar, final TextView pic_look) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            temp = BitmapFactory.decodeStream(in);

            contentLength = urlConnection.getContentLength();

            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);

            int count = 0;
            byte [] bytes = new byte[8* 1024];
            int len;
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes,0,len);
//                count += len;
//                progress = (int) (count * 100L / contentLength);
//                //如果进度与之前进度相等，这不更新，如果更新太频繁，造成界面卡顿
//                if(oldProgress != progress){
//                    progressBar.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressBar.setProgress(progress);
//                            pic_look.setText(progress + "%");
//                        }
//                    });
//                }
//                oldProgress = progress;
            }
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {

//            progressBar.setVisibility(View.GONE);
//            progressBar.setProgress(0);
//            pic_look.setVisibility(View.GONE);

            if(temp != null ){
                BitmapCache.getInstance().addBitmapToMemoryCache(hashKeyFormUrl(urlString),temp);
            }

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


    @SuppressLint("CheckResult")
    @Override
    protected void convert(BaseViewHolder helper, PicBean item) {


//        KLog.d("tag","展示的图片缩略路径是 " + item.getScalePic());
//        KLog.d("tag","展示的图片原始路径是 " + item.getPic());


        final PhotoView photoView = helper.getView(R.id.photoView);
        final SubsamplingScaleImageView scaleImageView = helper.getView(R.id.sub_imageview);
        final TextView pic_look = helper.getView(R.id.pic_look);
        final ProgressBar progressBar = helper.getView(R.id.progressBar);


        pic_look.setOnClickListener(v -> mExecutorService.submit(() -> {

//            progressBar.setVisibility(View.VISIBLE);

            //图片的路径 -- 转移 -- 创建Editor
            String imgUrl = item.getPic();
            String key = hashKeyFormUrl(imgUrl);
            try {
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                     if (downloadUrlToStream(imgUrl, outputStream,helper.getAdapterPosition(),progressBar,pic_look)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
                mDiskLruCache.flush();
                notifyItemChanged(helper.getAdapterPosition());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //网络线程
//            Bitmap temp = GetImageInputStream(item.getPic());
//            if(temp != null ){
//                BitmapCache.getInstance().addBitmapToMemoryCache(hashKeyFormUrl(item.getPic()),temp);
//                notifyItemChanged(helper.getAdapterPosition());
//            }
        }));


        //加载图片
        String ur11 = hashKeyFormUrl(item.getPic());
        bitmap = BitmapCache.getInstance().getBitmap(ur11);
        if(bitmap == null){
            try {
                String imageUrl = item.getPic();
                String key = hashKeyFormUrl(imageUrl);
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

                mRequestManager = Glide.with(mContext);
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
                                                    float scale = getImageScale(mContext,resource.getAbsolutePath());
                                                    scaleImageView.setImage(ImageSource.uri(resource.getAbsolutePath()),
                                                            new ImageViewState(scale, new PointF(0, 0), 0));
                                                }
                                            });

                                } else {
                                    photoView.setVisibility(View.VISIBLE);
                                    scaleImageView.setVisibility(View.GONE);
                                    Glide.with(mContext)
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
                doLoadOldData(item,pic_look,photoView,scaleImageView);
            }
        }else{
            KLog.d("tag","加载的是内存缓存  " );
            doLoadOldData(item,pic_look,photoView,scaleImageView);
        }
    }

    private void doLoadOldData(PicBean item,TextView pic_look,PhotoView photoView,SubsamplingScaleImageView scaleImageView) {
        pic_look.setVisibility(View.GONE);

        mRequestManager = Glide.with(mContext);
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

                            mRequestManager.load(item.getPic() )
                                    .downloadOnly(new SimpleTarget<File>() {
                                        @Override
                                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                                            float scale = getImageScale(mContext,resource.getAbsolutePath());
                                            scaleImageView.setImage(ImageSource.uri(resource.getAbsolutePath()),
                                                    new ImageViewState(scale, new PointF(0, 0), 0));
                                        }
                                    });

                        } else {
                            photoView.setVisibility(View.VISIBLE);
                            scaleImageView.setVisibility(View.GONE);
                            Glide.with(mContext)
                                    .load(item.getPic())
                                    .dontAnimate()
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


}
