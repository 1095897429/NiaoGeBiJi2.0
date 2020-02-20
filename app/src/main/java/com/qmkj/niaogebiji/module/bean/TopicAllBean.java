package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-20
 * 描述:
 */
public class TopicAllBean extends BaseBean {

    private List<TopicBean> list;
    private int num;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<TopicBean> getList() {
        return list;
    }

    public void setList(List<TopicBean> list) {
        this.list = list;
    }
}
