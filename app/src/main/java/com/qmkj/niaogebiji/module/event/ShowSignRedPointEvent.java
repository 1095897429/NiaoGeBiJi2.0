package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-19
 * 描述:显示签到红点
 */
public class ShowSignRedPointEvent {

    public String is_red;

    public ShowSignRedPointEvent(String is_red) {
        this.is_red = is_red;
    }

    public String getIs_red() {
        return is_red;
    }
}
