package com.qmkj.niaogebiji.module.adapter;

import android.animation.StateListAnimator;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.CircleBean;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentBeanNew;
import com.qmkj.niaogebiji.module.bean.MulSecondCommentBean;
import com.qmkj.niaogebiji.module.bean.MultiCircleNewsBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.widget.ImageUtil;

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
    }


    @Override
    protected void convert(BaseViewHolder helper,MulSecondCommentBean item) {

        switch (helper.getItemViewType()){
            case RIGHT_IMG_TYPE:
                CommentBeanNew bean = item.getSecondComment();


                User_info userInfo = bean.getUser_info();
                helper.setText(R.id.nickname,userInfo.getName());
                ImageUtil.load(mContext,userInfo.getAvatar(),helper.getView(R.id.head_icon));


                //添加谁回复谁

                StringBuilder sb = new StringBuilder();
                //如果回复者 和 被回复者 一样，则不显示 【回复】
                User_info p_userInfo = bean.getP_user_info();
                if(!TextUtils.isEmpty(userInfo.getName()) &&  !userInfo.getName().equals(p_userInfo.getName())){
                    sb.append(userInfo.getName()).append(" 回复 ").append(p_userInfo.getName())
                            .append(":").append(bean.getComment());
                }else{
                    sb.append(userInfo.getName()).append(":").append(bean.getComment());
                }
                helper.setText(R.id.comment_text,sb.toString());



                Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/DIN-Medium.otf");
                //点赞数
                TextView zan_num = helper.getView(R.id.zan_num);
                zan_num.setTypeface(typeface);
                if(bean.getLike_num().equals("0")){
                    zan_num.setText("赞");
                }else{
                    zan_num.setText(bean.getLike_num() + "+");
                }

                //点赞
                LottieAnimationView lottie = helper.getView(R.id.lottieAnimationView);
                ImageView animationIV = helper.getView(R.id.iamge_priase);
                helper.getView(R.id.circle_priase).setOnClickListener(view -> {

                    lottie.setImageAssetsFolder("images");
                    lottie.setAnimation("images/new_like_28.json");
                    //硬件加速，解决lottie卡顿问题
//                    lottie.useHardwareAcceleration(true);
                    lottie.playAnimation();


                    zan_num.setTextColor(mContext.getResources().getColor(R.color.prise_select_color));
                });

                break;

                default:
        }
    }
}
