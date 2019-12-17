package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-14
 * 描述:客服微信图bean
 */
public class OfficialBean extends BaseBean {


    private String qrcode_url;
    //意见反馈
    private String question_url;

    private String about_us;
    //app推广合作:鸟哥笔记小秘书
    private String app_cooperation;


    public String getApp_cooperation() {
        return app_cooperation;
    }

    public void setApp_cooperation(String app_cooperation) {
        this.app_cooperation = app_cooperation;
    }

    public String getQuestion_url() {
        return question_url;
    }

    public void setQuestion_url(String question_url) {
        this.question_url = question_url;
    }

    public String getAbout_us() {
        return about_us;
    }

    public void setAbout_us(String about_us) {
        this.about_us = about_us;
    }

    public String getQrcode_url() {
        return qrcode_url;
    }

    public void setQrcode_url(String qrcode_url) {
        this.qrcode_url = qrcode_url;
    }
}
