package com.qmkj.niaogebiji.module.adapter;

import androidx.annotation.Nullable;

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

                break;

            case NORMAL_IMG_TYPE:
                break;
                default:
        }
    }
}
