package com.qmkj.niaogebiji.module.adapter;

import android.text.TextPaint;
import android.widget.TextView;

import androidx.annotation.Nullable;

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
//
//
//        TextView chineseTv = helper.getView(R.id.author_name);
//        TextPaint paint = chineseTv.getPaint();
//        paint.setFakeBoldText(true);

    }
}

