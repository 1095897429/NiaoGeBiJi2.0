package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:首页下方的频道
 */
public class ChannelBean extends BaseBean {

    /**
     * chaid : 2
     * chaname : 娱乐
     */

    private String chaid;
    private String chaname;
    private String status;

    public ChannelBean() {
    }

    public ChannelBean(String chaid, String chaname) {
        this.chaid = chaid;
        this.chaname = chaname;
    }

    public String getChaid() {
        return chaid;
    }

    public void setChaid(String chaid) {
        this.chaid = chaid;
    }

    public String getChaname() {
        return chaname;
    }

    public void setChaname(String chaname) {
        this.chaname = chaname;
    }
}
