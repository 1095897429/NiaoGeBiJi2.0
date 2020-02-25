package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-02-25
 * 描述:
 */
public class AuthorDetailBean extends BaseBean {

    private AuthorDetail detail;

    public AuthorDetail getDetail() {
        return detail;
    }

    public void setDetail(AuthorDetail detail) {
        this.detail = detail;
    }

    public static class AuthorDetail extends BaseBean{
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
            private String hit_count_weekly;
            private String follow_count;
            private String memo;
            private int is_follow;
            private String user_name;
            private String user_avatar;
            private String share_url;
            private String share_title;
            private String share_summary;
            private String share_pic;
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

            public void setHit_count_weekly(String hit_count_weekly) {
                this.hit_count_weekly = hit_count_weekly;
            }
            public String getHit_count_weekly() {
                return hit_count_weekly;
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

            public void setUser_name(String user_name) {
                this.user_name = user_name;
            }
            public String getUser_name() {
                return user_name;
            }

            public void setUser_avatar(String user_avatar) {
                this.user_avatar = user_avatar;
            }
            public String getUser_avatar() {
                return user_avatar;
            }

            public void setShare_url(String share_url) {
                this.share_url = share_url;
            }
            public String getShare_url() {
                return share_url;
            }

            public void setShare_title(String share_title) {
                this.share_title = share_title;
            }
            public String getShare_title() {
                return share_title;
            }

            public void setShare_summary(String share_summary) {
                this.share_summary = share_summary;
            }
            public String getShare_summary() {
                return share_summary;
            }

            public void setShare_pic(String share_pic) {
                this.share_pic = share_pic;
            }
            public String getShare_pic() {
                return share_pic;
            }

    }
}
