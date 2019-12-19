package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-12
 * 描述:打招呼
 */
public class SayHiEvent {
    public String  content;

    public SayHiEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
