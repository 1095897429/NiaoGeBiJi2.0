package com.vhall.uilibs.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-30
 * 描述:
 */
public class DocumentCloseEvent {
    private String type;

    public DocumentCloseEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
