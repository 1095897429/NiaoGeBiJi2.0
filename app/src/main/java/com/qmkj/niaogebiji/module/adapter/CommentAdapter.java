package com.qmkj.niaogebiji.module.adapter;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.AuthorBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-21
 * 描述:1级评论适配器
 */
public class CommentAdapter extends BaseQuickAdapter<CommentBean.FirstComment, BaseViewHolder> {

    public CommentAdapter(@Nullable List<CommentBean.FirstComment> data) {
        super(R.layout.first_comment_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper,CommentBean.FirstComment item) {
        //设置子View点击事件
        helper.addOnClickListener(R.id.comment_delete)
                .addOnClickListener(R.id.ll_has_second_comment)
                .addOnClickListener(R.id.comment_priase)
                .addOnClickListener(R.id.toSecondComment);


        helper.setText(R.id.comment_text,item.getMessage());

        List<CommentBean.SecondComment> list = item.getCommentslist();
        //如果一级评论下的二级评论条数大于 1 条才显示
        if(null != list && !list.isEmpty() && list.size() > 1){
            helper.setVisible(R.id.all_comment,true);
        }else{
            helper.setVisible(R.id.all_comment,false);
        }

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
        //点赞数
        TextView zan_num = helper.getView(R.id.zan_num);
        zan_num.setTypeface(typeface);
        zan_num.setText(99 + "+");

        //点赞
        LottieAnimationView lottie = helper.getView(R.id.lottieAnimationView);

        ImageView animationIV = helper.getView(R.id.iamge_priase);

        helper.getView(R.id.circle_priase).setOnClickListener(view -> {

            lottie.setImageAssetsFolder("images");
            lottie.setAnimation("images/new_like_28.json");
            //硬件加速，解决lottie卡顿问题
            lottie.useHardwareAcceleration(true);
            lottie.playAnimation();


            zan_num.setTextColor(mContext.getResources().getColor(R.color.prise_select_color));
        });


    }
}

