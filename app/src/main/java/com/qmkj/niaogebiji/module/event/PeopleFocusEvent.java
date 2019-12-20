package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-10
 * 描述:人脉取关 - 改变搜索里条目事件
 */
public class PeopleFocusEvent {

    private String uid;

    private int status;

    public PeopleFocusEvent(String uid, int status) {
        this.uid = uid;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getStatus() {
        return status;
    }
}
