package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-18
 * 描述:点击查看更多按钮事件
 */
public class LookMoreEvent {

    private int currentPosition;

    public LookMoreEvent(int currentPosition){
        this.currentPosition = currentPosition;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }
}
