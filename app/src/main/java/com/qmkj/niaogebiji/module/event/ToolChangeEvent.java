package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-28
 * 描述:点击编辑上面 tag发送事件
 */
public class ToolChangeEvent {
    private int position;

    private String name;

    public ToolChangeEvent(int position, String name) {
        this.position = position;
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
