package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-17
 * 描述:
 */
public class MessageBean extends BaseBean {

    private String text;
    private String mins;
    private String hour;
    //快讯索引
    private String flash_id;

    public String getFlash_id() {
        return flash_id;
    }

    public void setFlash_id(String flash_id) {
        this.flash_id = flash_id;
    }

    public MessageBean(String mins, String hour, String text,String flash_id) {
        this.text = text;
        this.mins = mins;
        this.hour = hour;
        this.flash_id = flash_id;
    }

    public MessageBean(String hour, String text,String flash_id) {
        this.text = text;
        this.mins = mins;
        this.hour = hour;
        this.flash_id = flash_id;
    }

    public String getMins() {
        return mins;
    }

    public void setMins(String mins) {
        this.mins = mins;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
