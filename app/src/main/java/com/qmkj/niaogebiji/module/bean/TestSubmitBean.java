package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-20
 * 描述:用于修复学院测试完，分享数据没有更新的bug
 */
public class TestSubmitBean extends BaseBean {

    //分享数据
    private String share_title;
    private String share_content;
    //朋友圈分享内容
    private String moments_share_title;

    public String getShare_title() {
        return share_title;
    }

    public void setShare_title(String share_title) {
        this.share_title = share_title;
    }

    public String getShare_content() {
        return share_content;
    }

    public void setShare_content(String share_content) {
        this.share_content = share_content;
    }

    public String getMoments_share_title() {
        return moments_share_title;
    }

    public void setMoments_share_title(String moments_share_title) {
        this.moments_share_title = moments_share_title;
    }
}
