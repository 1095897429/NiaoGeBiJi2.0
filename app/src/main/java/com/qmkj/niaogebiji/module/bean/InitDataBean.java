package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-30
 * 描述:
 */
public class InitDataBean extends BaseBean {

    private String wechat_service_id;
    private String wechat_business_target_id;
    private String qrcode_url;
    private String app_cooperation;
    private String question_url;
    private String about_us;

    private String down_url;
    private String is_store_apply;
    private List<InitBean> position_arr;

    public String getWechat_service_id() {
        return wechat_service_id;
    }

    public void setWechat_service_id(String wechat_service_id) {
        this.wechat_service_id = wechat_service_id;
    }

    public String getWechat_business_target_id() {
        return wechat_business_target_id;
    }

    public void setWechat_business_target_id(String wechat_business_target_id) {
        this.wechat_business_target_id = wechat_business_target_id;
    }

    public String getQrcode_url() {
        return qrcode_url;
    }

    public void setQrcode_url(String qrcode_url) {
        this.qrcode_url = qrcode_url;
    }

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

    public String getDown_url() {
        return down_url;
    }

    public void setDown_url(String down_url) {
        this.down_url = down_url;
    }

    public String getIs_store_apply() {
        return is_store_apply;
    }

    public void setIs_store_apply(String is_store_apply) {
        this.is_store_apply = is_store_apply;
    }

    public List<InitBean> getPosition_arr() {
        return position_arr;
    }

    public void setPosition_arr(List<InitBean> position_arr) {
        this.position_arr = position_arr;
    }

    public static class InitBean extends BaseBean{
        private int id;
        private String pos;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPos() {
            return pos;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }
    }
}
