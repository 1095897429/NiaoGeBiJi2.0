package com.qmkj.niaogebiji.module.event;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-19
 * 描述:职业认证
 */
public class ProfessionEvent {

    private String data;
    private String type;

    public ProfessionEvent(String data,String type) {
        this.data = data;
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }
}
