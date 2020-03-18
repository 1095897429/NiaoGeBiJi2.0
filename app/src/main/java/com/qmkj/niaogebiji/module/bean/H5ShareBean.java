package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-18
 * 描述:h5分享的数据 -- 动态部分分享
 */
public class H5ShareBean extends BaseBean {

    private String type;
    private Params params;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public static class Params extends BaseBean {
        private String id;
        private String uid;
        private String blog;
        private String link;
        private String link_title;
        private String type;
        private String pid;
        private String link_num;
        private String show_num;
        private String sort;
        private String created_at;
        private String article_id;
        private String article_title;
        private String article_image;
        private String topic_id;
        private String comment_uid;

        private String comment_num;
        private String share_url;
        private String share_title;
        private String share_content;
        private String moments_share_title;

        private String share_icon;
        private int is_like;

        private List<String> images;

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getBlog() {
            return blog;
        }

        public void setBlog(String blog) {
            this.blog = blog;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getLink_title() {
            return link_title;
        }

        public void setLink_title(String link_title) {
            this.link_title = link_title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getLink_num() {
            return link_num;
        }

        public void setLink_num(String link_num) {
            this.link_num = link_num;
        }

        public String getShow_num() {
            return show_num;
        }

        public void setShow_num(String show_num) {
            this.show_num = show_num;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getArticle_id() {
            return article_id;
        }

        public void setArticle_id(String article_id) {
            this.article_id = article_id;
        }

        public String getArticle_title() {
            return article_title;
        }

        public void setArticle_title(String article_title) {
            this.article_title = article_title;
        }

        public String getArticle_image() {
            return article_image;
        }

        public void setArticle_image(String article_image) {
            this.article_image = article_image;
        }

        public String getTopic_id() {
            return topic_id;
        }

        public void setTopic_id(String topic_id) {
            this.topic_id = topic_id;
        }

        public String getComment_uid() {
            return comment_uid;
        }

        public void setComment_uid(String comment_uid) {
            this.comment_uid = comment_uid;
        }

        public String getComment_num() {
            return comment_num;
        }

        public void setComment_num(String comment_num) {
            this.comment_num = comment_num;
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

        public String getShare_icon() {
            return share_icon;
        }

        public void setShare_icon(String share_icon) {
            this.share_icon = share_icon;
        }

        public int getIs_like() {
            return is_like;
        }

        public void setIs_like(int is_like) {
            this.is_like = is_like;
        }
    }
}
