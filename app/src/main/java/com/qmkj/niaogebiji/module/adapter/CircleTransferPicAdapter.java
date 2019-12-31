package com.qmkj.niaogebiji.module.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-21
 * 描述:圈子转发的图片
 */
public class CircleTransferPicAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    //目前只显示3张，总共的用totalSize表示
    private int totalSize;

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public CircleTransferPicAdapter(List<String> data) {
        super(R.layout.item_circle_transfer_pic,data);
    }

    String scaleSize = "?imageMogr2/auto-orient/thumbnail/300x";

    @Override
    protected void convert(BaseViewHolder helper,String item) {


        //投机取巧的方法
//        ViewGroup.LayoutParams layoutParams =  helper.getView(R.id.pic_ll).getLayoutParams();
//        layoutParams.width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(16f  +  16f + 12f)) / 3;
//        helper.getView(R.id.pic_ll).setLayoutParams(layoutParams);

        ImageView imageView = helper.getView(R.id.pic);
        LinearLayout.LayoutParams imglp = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        imglp.width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(16f  +  16f + 12f)) / 3;
        if(helper.getAdapterPosition() != mData.size() - 1){
            imglp.setMargins(0,0,SizeUtils.dp2px(6),0);
        }else{
            imglp.setMargins(0,0,0,0);
        }

        imageView.setLayoutParams(imglp);


        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) helper.getView(R.id.count).getLayoutParams();
        lp.width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(16f  +  16f + 12f)) / 3;
        lp.height = SizeUtils.dp2px(100f);
        helper.getView(R.id.count).setLayoutParams(lp);

        ImageUtil.load(mContext,item + scaleSize,helper.getView(R.id.pic));

        TextView textView  =  helper.getView(R.id.count);
        if(2 == helper.getAdapterPosition()){
            if(totalSize != 3){
                textView.setVisibility(View.VISIBLE);
                textView.setText("+ "  + ( totalSize - 3));
            }
        }else{
            textView.setVisibility(View.GONE);
        }

    }
}