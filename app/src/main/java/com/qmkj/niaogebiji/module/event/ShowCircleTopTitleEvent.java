package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-18
 * 描述:
 */
public class ShowCircleTopTitleEvent {

    public String data;

    public ShowCircleTopTitleEvent(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
