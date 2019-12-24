package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:圈子明细中点赞操作 + 评论操作
 */
public class BlogPriaseEvent {
    private int  position;
    //点赞数
    private String likeNum;
    //点赞状态
    private int stauts;

    //评论数
    private String commentNum;

    public BlogPriaseEvent(int position,int stauts,String likeNum,String commentNum) {
        this.position = position;
        this.stauts = stauts;
        this.likeNum = likeNum;
        this.commentNum = commentNum;
    }


    public int getPosition() {
        return position;
    }

    public String getLikeNum() {
        return likeNum;
    }

    public int getStauts() {
        return stauts;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }
}
