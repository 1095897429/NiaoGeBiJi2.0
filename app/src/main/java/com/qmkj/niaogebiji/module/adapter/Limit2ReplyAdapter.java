package com.qmkj.niaogebiji.module.adapter;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.CommentCircleBean;
import com.qmkj.niaogebiji.module.bean.PeopleBean;
import com.qmkj.niaogebiji.module.bean.User_info;
import com.qmkj.niaogebiji.module.widget.NoLineCllikcSpan;
import com.socks.library.KLog;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-17
 * 描述:最多回复两条评论
 */
public class Limit2ReplyAdapter extends BaseQuickAdapter<CommentBean.FirstComment, BaseViewHolder> {


    //在哪条评论下操作
    private CommentBean.FirstComment fatherComment;

    public void setFatherComment(CommentBean.FirstComment fatherComment) {
        this.fatherComment = fatherComment;
    }

    public Limit2ReplyAdapter(List<CommentBean.FirstComment> data) {
        super(R.layout.item_limit2,data);
    }

    SpannableString spannableString ;
    StringBuilder sb = new StringBuilder();
    ClickableSpan clickableSpan1;
    ClickableSpan clickableSpan2;

    @Override
    protected void convert(BaseViewHolder helper, CommentBean.FirstComment item) {

        sb.setLength(0);

        if(!TextUtils.isEmpty(item.getRelatedid()) &&  !item.getUid().equals(fatherComment.getUid())){
            sb.append(item.getUsername()).append(" 回复 ").append(fatherComment.getReplyed_username())
                    .append(":").append(item.getMessage());
        }else {
            sb.append(item.getUsername()).append(":").append(item.getMessage());

        }



        if(!TextUtils.isEmpty(item.getRelatedid()) &&  !item.getRelatedid().equals(fatherComment.getUid())){

            NoLineCllikcSpan clickableSpan1 = new NoLineCllikcSpan() {
                @Override
                public void onClick(View widget) {
                    UIHelper.toUserInfoActivity(mContext,item.getUid());
                }
            };

            NoLineCllikcSpan clickableSpan2 = new NoLineCllikcSpan() {
                @Override
                public void onClick(View widget) {
                    KLog.d("tag","widget2");
                    UIHelper.toUserInfoActivity(mContext,item.getRelatedid());
                }
            };

            int userNamelength = item.getUsername().length();
            int authorNamelength = item.getReplyed_username().length();
            spannableString = new SpannableString(sb.toString());
            spannableString.setSpan(clickableSpan1, 0, userNamelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //中间有 回复 两个字 + 2个空格
            spannableString.setSpan(clickableSpan2, userNamelength + 4, userNamelength + 4 + authorNamelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);



        }else{
            NoLineCllikcSpan clickableSpan1 = new NoLineCllikcSpan() {
                @Override
                public void onClick(View widget) {
                    UIHelper.toUserInfoActivity(mContext,item.getUid());
                }
            };
            int userNamelength = item.getUsername().length();
            spannableString = new SpannableString(sb.toString());
            spannableString.setSpan(clickableSpan1, 0, userNamelength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        helper.setText(R.id.textlimit,spannableString);
        ((TextView)helper.getView(R.id.textlimit)).setMovementMethod(LinkMovementMethod.getInstance());

    }
}