package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-14
 * 描述:作者明细信息界面 展示小标题
 */
public class ShowTopAuthorEvent {

    public String data;

    public ShowTopAuthorEvent(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
