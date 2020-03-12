package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-17
 * 描述:
 */
public class MessageBean extends BaseBean {

    //内容
    private String text;
    //发布时间
    private String time;
    //标题
    private String myTitle;
    //快讯索引
    private String flash_id;

    //显示 头条 icon  top = 1 显示
    private String top;


    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public MessageBean(String text, String time, String myTitle, String flash_id) {
        this.text = text;
        this.time = time;
        this.myTitle = myTitle;
        this.flash_id = flash_id;
    }

    public String getFlash_id() {
        return flash_id;
    }

    public void setFlash_id(String flash_id) {
        this.flash_id = flash_id;
    }

    public String getMyTitle() {
        return myTitle;
    }

    public void setMyTitle(String myTitle) {
        this.myTitle = myTitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
