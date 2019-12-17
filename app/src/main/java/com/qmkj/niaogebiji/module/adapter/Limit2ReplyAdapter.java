package com.qmkj.niaogebiji.module.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.module.bean.CommentBean;
import com.qmkj.niaogebiji.module.bean.PeopleBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-17
 * 描述:最多回复两条评论
 */
public class Limit2ReplyAdapter extends BaseQuickAdapter<CommentBean.SecondComment, BaseViewHolder> {

    public Limit2ReplyAdapter(List<CommentBean.SecondComment> data) {
        super(R.layout.item_limit2,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentBean.SecondComment item) {

        StringBuilder sb = new StringBuilder();
        //如果回复者 和 被回复者 一样，则不显示 【回复】
        if(!TextUtils.isEmpty(item.getReplyed_username()) &&  !item.getReplyed_username().equals(item.getUsername())){
            sb.append(item.getUsername()).append(" 回复 ").append(item.getReplyed_username())
                    .append(":").append(item.getMessage());
        }else{
            sb.append(item.getUsername()).append(":").append(item.getMessage());
        }

        helper.setText(R.id.textlimit,sb.toString());
    }
}