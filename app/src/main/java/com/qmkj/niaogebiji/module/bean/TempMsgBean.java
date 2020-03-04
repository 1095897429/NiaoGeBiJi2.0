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
    private String content = "";
    //链接
    private String linkurl  = "";
    //链接标题
    private String linkTitle = "";
    //相册
    private List<MediaFile> imgPath;

    //拍照
    private List<MediaFile> imgPath2;

    //话题id
    private String topicId;

    //话题名称
    private String topicName;

    //是否是转发
    private int blog_type;

    //是否评论
    int blog_is_comment ;


    public int getBlog_type() {
        return blog_type;
    }

    public void setBlog_type(int blog_type) {
        this.blog_type = blog_type;
    }

    public int getBlog_is_comment() {
        return blog_is_comment;
    }

    public void setBlog_is_comment(int blog_is_comment) {
        this.blog_is_comment = blog_is_comment;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

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

    public List<MediaFile> getImgPath2() {
        return imgPath2;
    }

    public void setImgPath2(List<MediaFile> imgPath2) {
        this.imgPath2 = imgPath2;
    }
}
