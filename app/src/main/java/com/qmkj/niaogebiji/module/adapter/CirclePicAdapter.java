package com.qmkj.niaogebiji.module.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.constant.Constant;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.PicPreViewBean;
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

    //目前只显示3张，总共的用totalSize表示
    private int totalSize;

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public CirclePicAdapter(List<String> data) {
        super(R.layout.item_circle_pic,data);
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
//        KLog.d("tag","onBindViewHolder");
    }

    @Override
    protected void convert(BaseViewHolder helper,String item) {

        //投机取巧的方法
        ViewGroup.LayoutParams layoutParams =  helper.getView(R.id.pic_ll).getLayoutParams();
        layoutParams.width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(16f  +  16f + 16f)) / 3;
        helper.getView(R.id.pic_ll).setLayoutParams(layoutParams);


        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) helper.getView(R.id.count).getLayoutParams();
        lp.width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(16f  +  16f + 16f)) / 3;
        lp.height = SizeUtils.dp2px(109f);
        helper.getView(R.id.count).setLayoutParams(lp);


        ImageUtil.loadByCache(mContext,item + Constant.scaleSize,helper.getView(R.id.pic));

        TextView textView  =  helper.getView(R.id.count);
        if(2 == helper.getAdapterPosition()){
            if(totalSize != 3){
                textView.setVisibility(View.VISIBLE);
                textView.setText("+ "  + ( totalSize - 3));
            }else{
                textView.setVisibility(View.GONE);
            }
        }else{
            textView.setVisibility(View.GONE);
        }



    }
}