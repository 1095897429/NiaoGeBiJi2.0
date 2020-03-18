package com.qmkj.niaogebiji.module.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.RequestManager;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-21
 * 描述:快讯中图片
 */
public class FlashPicAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    //目前只显示3张，总共的用totalSize表示
    private int totalSize;

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public FlashPicAdapter(List<String> data) {
        super(R.layout.item_flash_pic,data);
    }

//    String scaleSize = "?imageMogr2/auto-orient/format/jpg/ignore-error/1/thumbnail/!50p";
//    String scaleSize = "?imageMogr2/auto-orient/format/jpg/ignore-error/1/thumbnail/!40p/imageslim";


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        KLog.d("tag","onCreateViewHolder");
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int positions) {
        super.onBindViewHolder(holder, positions);
    }




    @Override
    protected void convert(BaseViewHolder helper,String item) {



        //投机取巧的方法
//        ViewGroup.LayoutParams layoutParams =  helper.getView(R.id.pic_ll).getLayoutParams();
//        layoutParams.width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(16f  +  16f + 16f)) / 3;
//        helper.getView(R.id.pic_ll).setLayoutParams(layoutParams);



        ImageUtil.loadByCache(mContext,item ,helper.getView(R.id.pic));

    }
}