package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:主界面点击更多去快讯界面 - 具体的快讯位置
 */
public class FlashSpecificEvent {

    private String tag;

    public FlashSpecificEvent(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
