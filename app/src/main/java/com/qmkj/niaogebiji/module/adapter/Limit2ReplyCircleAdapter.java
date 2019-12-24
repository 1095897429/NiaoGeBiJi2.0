package com.qmkj.niaogebiji.module.adapter;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.CommentBeanNew;
import com.qmkj.niaogebiji.module.bean.User_info;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-17
 * 描述:最多回复两条评论 -- 圈子
 */
public class Limit2ReplyCircleAdapter extends BaseQuickAdapter<CommentBeanNew.SecondComment, BaseViewHolder> {

    public Limit2ReplyCircleAdapter(List<CommentBeanNew.SecondComment> data) {
        super(R.layout.item_limit2,data);
    }


    SpannableString spannableString ;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void convert(BaseViewHolder helper,CommentBeanNew.SecondComment item) {
        getReply(helper,item);
    }

    private void getReply(BaseViewHolder helper, CommentBeanNew.SecondComment item) {
        sb.setLength(0);
        //如果回复者 和 被回复者 一样，则不显示 【回复】
        User_info userInfo = item.getUser_info();
        User_info p_userInfo = item.getP_user_info();
        if(!TextUtils.isEmpty(userInfo.getUid()) &&  !userInfo.getUid().equals(p_userInfo.getUid())){
            sb.append(userInfo.getName()).append(" 回复 ").append(p_userInfo.getName())
                    .append(":").append(item.getComment());
        }else{
            sb.append(userInfo.getName()).append(":").append(item.getComment());

        }


        if(!TextUtils.isEmpty(userInfo.getName()) &&  !userInfo.getName().equals(p_userInfo.getName())){
            int userNamelength = userInfo.getName().length();
            int authorNamelength = p_userInfo.getName().length();
            spannableString = new SpannableString(sb.toString());
            ForegroundColorSpan fCs1 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_blue));
            ForegroundColorSpan fCs2 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_blue));
            spannableString.setSpan(fCs1, 0, userNamelength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            //中间有 回复 两个字 + 2个空格
            spannableString.setSpan(fCs2, userNamelength + 4, userNamelength + 4 + authorNamelength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }else{
            int userNamelength = userInfo.getName().length();
            spannableString = new SpannableString(sb.toString());
            ForegroundColorSpan fCs1 = new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_blue));
            spannableString.setSpan(fCs1, 0, userNamelength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        helper.setText(R.id.textlimit,spannableString);
    }
}