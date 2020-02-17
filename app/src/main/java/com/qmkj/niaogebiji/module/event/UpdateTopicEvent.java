package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-14
 * 描述:话题选择
 */
public class UpdateTopicEvent {

    private String title;

    public UpdateTopicEvent(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
