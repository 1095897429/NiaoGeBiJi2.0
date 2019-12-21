package com.qmkj.niaogebiji.module.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-21
 * 描述:圈子中图片
 */
public class CirclePicAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public CirclePicAdapter(List<String> data) {
        super(R.layout.item_circle_pic,data);
    }

    String scaleSize = "?imageMogr2/auto-orient/thumbnail/300x";

    @Override
    protected void convert(BaseViewHolder helper,String item) {


        ImageUtil.load(mContext,item + scaleSize,helper.getView(R.id.pic));


    }
}