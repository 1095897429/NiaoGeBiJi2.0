package com.qmkj.niaogebiji.module.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.CommentBean;
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

    @Override
    protected void convert(BaseViewHolder helper,CommentBeanNew.SecondComment item) {
        StringBuilder sb = new StringBuilder();
        //如果回复者 和 被回复者 一样，则不显示 【回复】
        User_info userInfo = item.getUser_info();
        User_info p_userInfo = item.getP_user_info();
        if(!TextUtils.isEmpty(userInfo.getName()) &&  !userInfo.getName().equals(p_userInfo.getName())){
            sb.append(userInfo.getName()).append(" 回复 ").append(p_userInfo.getName())
                    .append(":").append(item.getComment());
        }else{
            sb.append(userInfo.getName()).append(":").append(item.getComment());
        }
        helper.setText(R.id.textlimit,sb.toString());
    }
}