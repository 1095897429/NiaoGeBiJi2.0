package com.qmkj.niaogebiji.module.adapter;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.module.bean.CommentCircleBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.widget.NoLineCllikcSpan;
import com.socks.library.KLog;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-17
 * 描述:最多回复两条评论 -- 圈子
 *
 * 1.一级评论的uid 和 评论的评论的puid比较，如果相同，表示 回复者回复的是此puid的评论 || 不相同的话 表示此uid下回复的其他人
 */
public class Limit2ReplyCircleAdapter extends BaseQuickAdapter<CommentCircleBean, BaseViewHolder> {

    //在哪条评论下操作
    private CommentCircleBean fatherComment;

    public void setFatherComment(CommentCircleBean fatherComment) {
        this.fatherComment = fatherComment;
    }

    public Limit2ReplyCircleAdapter(List<CommentCircleBean> data) {
        super(R.layout.item_limit2,data);
    }


    SpannableString spannableString ;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void convert(BaseViewHolder helper, CommentCircleBean item) {
        getReply(helper,item);


        helper.getView(R.id.textlimit).setOnClickListener(v -> {
            KLog.d("tag","11111");
            if(StringUtil.isFastClick()){
                return;
            }
            if(mToSecondListener != null){
                mToSecondListener.toSecond();
            }
        });

    }

    private void getReply(BaseViewHolder helper, CommentCircleBean item) {
        sb.setLength(0);

        //回复者信息
//        User_info userInfo = item.getUser_info();
//        //被回复者信息
//        User_info p_userInfo = item.getP_user_info();
//        //uid 与 userInfo中的puid比较，如果一样，表示回复者回复的是当前人的评论【回复者：xxx】
//        if(!TextUtils.isEmpty(item.getPuid()) &&  !item.getPuid().equals(fatherComment.getUid())){
//            sb.append(userInfo.getName()).append(" 回复 ").append(p_userInfo.getName())
//                    .append(":").append(item.getComment());
//        }else{
//            sb.append(userInfo.getName()).append(":").append(item.getComment());
//
//        }
//
//        if(!TextUtils.isEmpty(item.getPuid()) &&  !item.getPuid().equals(fatherComment.getUid())){
//
//
//            NoLineCllikcSpan clickableSpan1 = new NoLineCllikcSpan() {
//                @Override
//                public void onClick(View widget) {
//                    UIHelper.toUserInfoActivity(mContext,userInfo.getUid());
//                }
//            };
//
//            NoLineCllikcSpan clickableSpan2 = new NoLineCllikcSpan() {
//                @Override
//                public void onClick(View widget) {
//                    KLog.d("tag","widget2");
//                    UIHelper.toUserInfoActivity(mContext,p_userInfo.getUid());
//                }
//            };
//
//            int userNamelength = userInfo.getName().length();
//            int authorNamelength = p_userInfo.getName().length();
//            spannableString = new SpannableString(sb.toString());
//            spannableString.setSpan(clickableSpan1, 0, userNamelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            //中间有 回复 两个字 + 2个空格
//            spannableString.setSpan(clickableSpan2, userNamelength + 4, userNamelength + 4 + authorNamelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//
//
//        }else{
//            NoLineCllikcSpan clickableSpan1 = new NoLineCllikcSpan() {
//                @Override
//                public void onClick(View widget) {
//                    UIHelper.toUserInfoActivity(mContext,userInfo.getUid());
//                }
//            };
//            int userNamelength = userInfo.getName().length();
//            spannableString = new SpannableString(sb.toString());
//            spannableString.setSpan(clickableSpan1, 0, userNamelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//
//
//        helper.setText(R.id.textlimit,spannableString);
//        ((TextView)helper.getView(R.id.textlimit)).setMovementMethod(LinkMovementMethod.getInstance());





        //如果回复者 和 被回复者 一样，则不显示 【回复】-- 2019.12.27 逻辑废弃
        User_info userInfo = item.getUser_info();
        User_info p_userInfo = item.getP_user_info();
        sb.append(userInfo.getName()).append(" 回复 ").append(p_userInfo.getName())
                .append(":").append(item.getComment());


        NoLineCllikcSpan clickableSpan1 = new NoLineCllikcSpan() {
                @Override
                public void onClick(View widget) {
                    if(StringUtil.isFastClick()){
                        return;
                    }
                    UIHelper.toUserInfoActivity(mContext,userInfo.getUid());
                }
            };

        NoLineCllikcSpan clickableSpan2 = new NoLineCllikcSpan() {
            @Override
            public void onClick(View widget) {
                if(StringUtil.isFastClick()){
                    return;
                }
                KLog.d("tag","widget2");
                UIHelper.toUserInfoActivity(mContext,p_userInfo.getUid());
            }
        };

        int userNamelength = 0;
        if(!TextUtils.isEmpty(userInfo.getName())){
            userNamelength =  userInfo.getName().length();
        }
        int authorNamelength = 0;
        if(!TextUtils.isEmpty(p_userInfo.getName())){
            authorNamelength =  p_userInfo.getName().length();
        }

        spannableString = new SpannableString(sb.toString());
        spannableString.setSpan(clickableSpan1, 0, userNamelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //中间有 回复 两个字 + 2个空格
        spannableString.setSpan(clickableSpan2, userNamelength + 4, userNamelength + 4 + authorNamelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        helper.setText(R.id.textlimit,spannableString);
        ((TextView)helper.getView(R.id.textlimit)).setMovementMethod(LinkMovementMethod.getInstance());

    }



    public interface OnToSecondListener{
        void toSecond();
    }

    public OnToSecondListener mToSecondListener;

    public void setToSecondListener(OnToSecondListener toSecondListener) {
        mToSecondListener = toSecondListener;
    }
}





