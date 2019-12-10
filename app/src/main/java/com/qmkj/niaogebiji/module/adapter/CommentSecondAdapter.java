package com.qmkj.niaogebiji.module.adapter;

import android.animation.StateListAnimator;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-27
 * 描述:二级评论item适配
 */
public class CommentSecondAdapter extends BaseMultiItemQuickAdapter<MulSecondCommentBean, BaseViewHolder> {

    public  static final int RIGHT_IMG_TYPE = 1;
    public static final int  NORMAL_IMG_TYPE = 2;


    public CommentSecondAdapter(List<MulSecondCommentBean> data) {
        super(data);
        //正常
        addItemType(RIGHT_IMG_TYPE, R.layout.second_comment_item);
        //头部
        addItemType(NORMAL_IMG_TYPE,R.layout.second_comment_head);
    }


    @Override
    protected void convert(BaseViewHolder helper,MulSecondCommentBean item) {

        switch (helper.getItemViewType()){
            case RIGHT_IMG_TYPE:
                CommentBean.FirstComment bean = item.getFirstComment();
                helper.setText(R.id.comment_text,bean.getMessage());

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

                break;

            case NORMAL_IMG_TYPE:
                break;
                default:
        }
    }
}
