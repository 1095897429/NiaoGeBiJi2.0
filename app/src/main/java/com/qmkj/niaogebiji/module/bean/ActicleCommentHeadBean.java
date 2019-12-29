package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-27
 * 描述:文章 二级评论上方的评论
 */
public class ActicleCommentHeadBean  extends BaseBean {

    private CommentBean.FirstComment data;

    public CommentBean.FirstComment getData() {
        return data;
    }

    public void setData(CommentBean.FirstComment data) {
        this.data = data;
    }
}
