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

    //有这个对象的时候设置type 为 2，其他情况可不设置1
    private CommentBean.FirstComment mFirstComment;

    //类型
    private int itemType;

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public CommentBean.FirstComment getFirstComment() {
        return mFirstComment;
    }

    public void setFirstComment(CommentBean.FirstComment firstComment) {
        mFirstComment = firstComment;
    }
}
