package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-23
 * 描述:图片预览bean
 */
public class PicPreViewBean extends BaseBean {

    private String imgUrl;
    private boolean isShowMoreCount;
    //图片的总数量
    private int totalSize;


    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isShowMoreCount() {
        return isShowMoreCount;
    }

    public void setShowMoreCount(boolean showMoreCount) {
        isShowMoreCount = showMoreCount;
    }
}
