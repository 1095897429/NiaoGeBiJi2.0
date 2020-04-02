package com.vhall.uilibs.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-30
 * 描述:
 */
public class DanMuEvent {

    private boolean isOpen;

    public DanMuEvent(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public boolean isOpen() {
        return isOpen;
    }
}
