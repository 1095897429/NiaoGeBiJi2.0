package com.qmkj.niaogebiji.module.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.qmkj.niaogebiji.module.bean.PicBean;
import com.qmkj.niaogebiji.module.widget.BitmapCache;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-19
 * 描述:图片选择 --- 只限头像
 *
 * 1.我采用的是磁盘缓存
 */
public class PicPreViewItemAdapterByHeadIcon extends BaseQuickAdapter<PicBean, BaseViewHolder> {

    public PicPreViewItemAdapterByHeadIcon(@Nullable List<PicBean> data, Context context) {
        super(R.layout.pict_pager_item_view,data);
    }



    @SuppressLint("CheckResult")
    @Override
    protected void convert(BaseViewHolder helper, PicBean item) {


        final PhotoView photoView = helper.getView(R.id.photoView);
        final SubsamplingScaleImageView scaleImageView = helper.getView(R.id.sub_imageview);
        final TextView pic_look = helper.getView(R.id.pic_look);
        final ProgressBar progressBar = helper.getView(R.id.progressBar);

        photoView.setVisibility(View.VISIBLE);
        pic_look.setVisibility(View.GONE);
        ImageUtil.loadByDefaultHead(mContext,item.getPic(),photoView);


    }


}
