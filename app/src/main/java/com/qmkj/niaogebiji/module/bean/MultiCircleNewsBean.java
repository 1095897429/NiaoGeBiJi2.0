package com.qmkj.niaogebiji.module.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-14
 * 描述:圈子的多实体 ，一个对象可以有多种类型的
 */
public class MultiCircleNewsBean extends BaseBean implements MultiItemEntity {

    private CircleBean mCircleBean;

    //类型 圈子为1 关注话题为2
    private int itemType;

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public CircleBean getCircleBean() {
        return mCircleBean;
    }

    public void setCircleBean(CircleBean circleBean) {
        mCircleBean = circleBean;
    }


    private TopicFocusBean mTopicFocusBean;

    public TopicFocusBean getTopicFocusBean() {
        return mTopicFocusBean;
    }

    public void setTopicFocusBean(TopicFocusBean topicFocusBean) {
        mTopicFocusBean = topicFocusBean;
    }
}
