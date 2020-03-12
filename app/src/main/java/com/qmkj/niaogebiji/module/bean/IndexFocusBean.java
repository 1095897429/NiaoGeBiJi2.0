package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-14
 * 描述:
 */
public class IndexFocusBean extends BaseBean {

    private List<Article_list> article_list;
    private List<Auther_list> auther_list;

    public List<Article_list> getArticle_list() {
        return article_list;
    }

    public void setArticle_list(List<Article_list> article_list) {
        this.article_list = article_list;
    }

    public List<Auther_list> getAuther_list() {
        return auther_list;
    }

    public void setAuther_list(List<Auther_list> auther_list) {
        this.auther_list = auther_list;
    }

    //首页关注上的作者列表
    public class Auther_list {

        private String id;
        private String uid;
        private String name;
        private String mobile;
        private String title;
        private String img;
        private String summary;
        private String status;
        private String type;
        private String created_at;
        private String updated_at;
        private String article_count;
        private String fav_count;
        private String up_count;
        private String subscribe_count;
        private String hit_count;
        private String follow_count;
        private String memo;
        private int is_follow;


        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
        public String getUid() {
            return uid;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
        public String getMobile() {
            return mobile;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

        public void setImg(String img) {
            this.img = img;
        }
        public String getImg() {
            return img;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
        public String getSummary() {
            return summary;
        }

        public void setStatus(String status) {
            this.status = status;
        }
        public String getStatus() {
            return status;
        }

        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
        public String getCreated_at() {
            return created_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
        public String getUpdated_at() {
            return updated_at;
        }

        public void setArticle_count(String article_count) {
            this.article_count = article_count;
        }
        public String getArticle_count() {
            return article_count;
        }

        public void setFav_count(String fav_count) {
            this.fav_count = fav_count;
        }
        public String getFav_count() {
            return fav_count;
        }

        public void setUp_count(String up_count) {
            this.up_count = up_count;
        }
        public String getUp_count() {
            return up_count;
        }

        public void setSubscribe_count(String subscribe_count) {
            this.subscribe_count = subscribe_count;
        }
        public String getSubscribe_count() {
            return subscribe_count;
        }

        public void setHit_count(String hit_count) {
            this.hit_count = hit_count;
        }
        public String getHit_count() {
            return hit_count;
        }

        public void setFollow_count(String follow_count) {
            this.follow_count = follow_count;
        }
        public String getFollow_count() {
            return follow_count;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }
        public String getMemo() {
            return memo;
        }

        public void setIs_follow(int is_follow) {
            this.is_follow = is_follow;
        }
        public int getIs_follow() {
            return is_follow;
        }

    }

    //作者的相关文章
    public static class Article_list {

        private String aid;
        private String title;
        private String author;
        private String author_id;
        private String pic;
        private String published_at;
        public void setAid(String aid) {
            this.aid = aid;
        }
        public String getAid() {
            return aid;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
        public String getAuthor() {
            return author;
        }

        public void setAuthor_id(String author_id) {
            this.author_id = author_id;
        }
        public String getAuthor_id() {
            return author_id;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }
        public String getPic() {
            return pic;
        }

        public void setPublished_at(String published_at) {
            this.published_at = published_at;
        }
        public String getPublished_at() {
            return published_at;
        }

    }



}
