package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-17
 * 描述:动态信息bean
 */
public class PersonUserInfoBean extends BaseBean {

        private String uid;
        private String name;
        private String avatar;
        private String company_name;
        private String position;
        //企业认证状态：1-正常，2-未提交，3-审核中，4-未通过
        private String auth_com_status;
        private String pro_summary;
        private List<Badges> badge;
        private String blog_count;
        private int fans_count;
        private List<CircleBean> blog_list;
        private int follow_status;
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

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
        public String getAvatar() {
            return avatar;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }
        public String getCompany_name() {
            return company_name;
        }

        public void setPosition(String position) {
            this.position = position;
        }
        public String getPosition() {
            return position;
        }

        public void setAuth_com_status(String auth_com_status) {
            this.auth_com_status = auth_com_status;
        }
        public String getAuth_com_status() {
            return auth_com_status;
        }

        public void setPro_summary(String pro_summary) {
            this.pro_summary = pro_summary;
        }
        public String getPro_summary() {
            return pro_summary;
        }

    public List<Badges> getBadge() {
        return badge;
    }

    public void setBadge(List<Badges> badge) {
        this.badge = badge;
    }

    public void setBlog_count(String blog_count) {
            this.blog_count = blog_count;
        }
        public String getBlog_count() {
            return blog_count;
        }

        public void setFans_count(int fans_count) {
            this.fans_count = fans_count;
        }
        public int getFans_count() {
            return fans_count;
        }

        public void setFollow_status(int follow_status) {
            this.follow_status = follow_status;
        }
        public int getFollow_status() {
            return follow_status;
        }

    public List<CircleBean> getBlog_list() {
        return blog_list;
    }

    public void setBlog_list(List<CircleBean> blog_list) {
        this.blog_list = blog_list;
    }

    public static class Blog_list {
        private String id;
        private String uid;
        private String blog;
        private List<String> images;
        private String link;
        private String link_title;
        private String type;
        private String pid;
        private String like_num;
        private String show_num;
        private String sort;
        private String created_at;
        private String article_id;
        private String article_title;
        private String article_image;
        private User_info user_info;
        private String comment_num;
        private String share_url;
        private int is_like;
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

        public void setBlog(String blog) {
            this.blog = blog;
        }
        public String getBlog() {
            return blog;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
        public List<String> getImages() {
            return images;
        }

        public void setLink(String link) {
            this.link = link;
        }
        public String getLink() {
            return link;
        }

        public void setLink_title(String link_title) {
            this.link_title = link_title;
        }
        public String getLink_title() {
            return link_title;
        }

        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }
        public String getPid() {
            return pid;
        }

        public void setLike_num(String like_num) {
            this.like_num = like_num;
        }
        public String getLike_num() {
            return like_num;
        }

        public void setShow_num(String show_num) {
            this.show_num = show_num;
        }
        public String getShow_num() {
            return show_num;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }
        public String getSort() {
            return sort;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
        public String getCreated_at() {
            return created_at;
        }

        public void setArticle_id(String article_id) {
            this.article_id = article_id;
        }
        public String getArticle_id() {
            return article_id;
        }

        public void setArticle_title(String article_title) {
            this.article_title = article_title;
        }
        public String getArticle_title() {
            return article_title;
        }

        public void setArticle_image(String article_image) {
            this.article_image = article_image;
        }
        public String getArticle_image() {
            return article_image;
        }

        public void setUser_info(User_info user_info) {
            this.user_info = user_info;
        }
        public User_info getUser_info() {
            return user_info;
        }

        public void setComment_num(String comment_num) {
            this.comment_num = comment_num;
        }
        public String getComment_num() {
            return comment_num;
        }

        public void setShare_url(String share_url) {
            this.share_url = share_url;
        }
        public String getShare_url() {
            return share_url;
        }

        public void setIs_like(int is_like) {
            this.is_like = is_like;
        }
        public int getIs_like() {
            return is_like;
        }

    }



    public static class User_info {
        private String uid;
        private String name;
        private String avatar;
        private String company_name;
        private String position;
        private String auth_com_status;
        private String pro_summary;
        private List<Badges> badges;
        private String blog_count;
        private int fans_count;
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

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
        public String getAvatar() {
            return avatar;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }
        public String getCompany_name() {
            return company_name;
        }

        public void setPosition(String position) {
            this.position = position;
        }
        public String getPosition() {
            return position;
        }

        public void setAuth_com_status(String auth_com_status) {
            this.auth_com_status = auth_com_status;
        }
        public String getAuth_com_status() {
            return auth_com_status;
        }

        public void setPro_summary(String pro_summary) {
            this.pro_summary = pro_summary;
        }
        public String getPro_summary() {
            return pro_summary;
        }

        public void setBadges(List<Badges> badges) {
            this.badges = badges;
        }
        public List<Badges> getBadges() {
            return badges;
        }

        public void setBlog_count(String blog_count) {
            this.blog_count = blog_count;
        }
        public String getBlog_count() {
            return blog_count;
        }

        public void setFans_count(int fans_count) {
            this.fans_count = fans_count;
        }
        public int getFans_count() {
            return fans_count;
        }

    }


    public static class Badges {
        private String id;
        private String name;
        private String icon;
        private String desc;
        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
        public String getIcon() {
            return icon;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
        public String getDesc() {
            return desc;
        }

    }



}
