package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;
import com.xzh.imagepicker.bean.MediaFile;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-20
 * 描述:草稿bean
 */
public class TempMsgBean extends BaseBean {

    //内容
    private String content;
    //链接
    private String linkurl;
    //链接标题
    private String linkTitle;
    //图片路径
    private List<MediaFile> imgPath;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLinkurl() {
        return linkurl;
    }

    public void setLinkurl(String linkurl) {
        this.linkurl = linkurl;
    }

    public List<MediaFile> getImgPath() {
        return imgPath;
    }

    public void setImgPath(List<MediaFile> imgPath) {
        this.imgPath = imgPath;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }
}
