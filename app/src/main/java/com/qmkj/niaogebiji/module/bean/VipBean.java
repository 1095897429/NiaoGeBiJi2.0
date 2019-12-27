package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-27
 * 描述:
 */
public class VipBean extends BaseBean {
    private boolean is_vip;
    private boolean is_show;
    private String vip_last_time;

    public boolean isIs_vip() {
        return is_vip;
    }

    public void setIs_vip(boolean is_vip) {
        this.is_vip = is_vip;
    }

    public boolean isIs_show() {
        return is_show;
    }

    public void setIs_show(boolean is_show) {
        this.is_show = is_show;
    }

    public String getVip_last_time() {
        return vip_last_time;
    }

    public void setVip_last_time(String vip_last_time) {
        this.vip_last_time = vip_last_time;
    }
}
