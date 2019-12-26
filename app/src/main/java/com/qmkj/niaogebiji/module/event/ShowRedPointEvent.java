package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-19
 * 描述:显示红点
 */
public class ShowRedPointEvent {

    public String is_red;

    public ShowRedPointEvent(String is_red) {
        this.is_red = is_red;
    }

    public String getIs_red() {
        return is_red;
    }
}
