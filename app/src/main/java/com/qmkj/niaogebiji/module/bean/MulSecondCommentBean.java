package com.qmkj.niaogebiji.module.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-27
 * 描述:
 */
public class MulSecondCommentBean extends BaseBean implements MultiItemEntity {

    //文章评论 真实的
    private CommentBean.FirstComment mActicleComment;

    //圈子评论 真实的
    private CommentBeanNew mCircleComment;

    //类型
    private int itemType;

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public CommentBean.FirstComment getActicleComment() {
        return mActicleComment;
    }

    public void setActicleComment(CommentBean.FirstComment acticleComment) {
        mActicleComment = acticleComment;
    }

    public CommentBeanNew getCircleComment() {
        return mCircleComment;
    }

    public void setCircleComment(CommentBeanNew circleComment) {
        mCircleComment = circleComment;
    }
}
