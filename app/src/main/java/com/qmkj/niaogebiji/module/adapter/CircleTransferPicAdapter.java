package com.qmkj.niaogebiji.module.adapter;

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

    public CircleTransferPicAdapter(List<String> data) {
        super(R.layout.item_circle_transfer_pic,data);
    }

    String scaleSize = "?imageMogr2/auto-orient/thumbnail/300x";

    @Override
    protected void convert(BaseViewHolder helper,String item) {

        ImageUtil.load(mContext,item + scaleSize,helper.getView(R.id.pic));
    }
}