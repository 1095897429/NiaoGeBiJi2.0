package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:圈子明细中点赞
 */
public class BlogPriaseEvent {
    private int  position;
    private String likeNum;
    private int stauts;

    public BlogPriaseEvent(int position,int stauts,String likeNum) {
        this.position = position;
        this.stauts = stauts;
        this.likeNum = likeNum;
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
}
