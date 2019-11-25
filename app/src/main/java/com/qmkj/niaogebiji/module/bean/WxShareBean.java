package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-22
 * 描述:微信分享bean
 */
public class WxShareBean extends BaseBean {

    private String pic;
    private String share_url;
    private String share_title;
    private String share_summary;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getShare_title() {
        return share_title;
    }

    public void setShare_title(String share_title) {
        this.share_title = share_title;
    }

    public String getShare_summary() {
        return share_summary;
    }

    public void setShare_summary(String share_summary) {
        this.share_summary = share_summary;
    }
}
