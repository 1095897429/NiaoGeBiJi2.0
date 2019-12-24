package com.qmkj.niaogebiji.module.adapter;

import android.view.View;
import android.widget.TextView;

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