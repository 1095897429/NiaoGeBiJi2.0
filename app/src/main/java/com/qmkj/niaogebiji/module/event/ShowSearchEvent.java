package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-19
 * 描述:显示搜索图片
 */
public class ShowSearchEvent {

    public String data;

    public ShowSearchEvent(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
