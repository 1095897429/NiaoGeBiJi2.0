package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-10
 * 描述:搜索界面， 在某个界面点击了 搜索，就刷新此界面
 */
public class CurrentWordEvent {
    public int position;

    public CurrentWordEvent(int position){
        this.position = position;
    }
}
